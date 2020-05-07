package pt.teamloan.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.jboss.logmanager.Level;

import io.quarkus.mailer.MailTemplate;
import io.quarkus.panache.common.Sort;
import io.quarkus.qute.api.ResourcePath;
import pt.teamloan.config.MailConfig;
import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.model.CompanyEntity;
import pt.teamloan.model.ProspectEntity;

@ApplicationScoped
public class ProspectService {
	private static final int MINIMUM_INTERVAL_BETEEN_EMAILS = 3;

	private static final Logger LOGGER = Logger.getLogger(ProspectService.class.getName());

	@Inject
	MailConfig mailConfig;

	@ResourcePath("mails/prospect")
	MailTemplate prospectMailTemplate;

	@ResourcePath("mails/prospect-inform")
	MailTemplate prospectInformMailTemplate;

	@ResourcePath("mails/prospect-reinform")
	MailTemplate prospectReinformMailTemplate;

	@Transactional
	public CompletionStage<Void> registerProspect(@Valid ProspectEntity prospectEntity)
			throws EntityAlreadyExistsException {
		if (ProspectEntity.count("email", prospectEntity.getEmail()) > 0) {
			throw new EntityAlreadyExistsException("Email already exists");
		}
		prospectEntity.persist();
		CompletionStage<Void> sendMailCompletionStage = sendConfirmationEmail(prospectEntity);
		return sendMailCompletionStage;
	}

	private CompletionStage<Void> sendConfirmationEmail(ProspectEntity prospectEntity) {
		CompletionStage<Void> sendMailCompletionStage = prospectMailTemplate.to(prospectEntity.getEmail())
				.subject(mailConfig.getProspectMailSubject()).send();
		return sendMailCompletionStage;
	}

	/**
	 * Send the information email to all the prospects. This is why they have given
	 * us their email for.
	 * 
	 * @return
	 */
	public CompletionStage<Void> sendInformationEmails() {
		List<ProspectEntity> prospects = ProspectEntity.listAll(Sort.ascending("createdAt"));

		// filter invalid emails
		Stream<ProspectEntity> prospectValidEmailsStream = prospects.stream()
				.filter(p -> (!p.getEmail().contains("fake_") && p.getEmail().length() > 7));

		// Normalize and remove duplicates
		List<String> validEmails = prospectValidEmailsStream.map(p -> p.getEmail().toLowerCase()).distinct()
				.collect(Collectors.toList());

		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		int delayMinutes = 0;
		for (String email : validEmails) {
			int jitter = (int) Math.floor((Math.random() * 3d));
			delayMinutes += MINIMUM_INTERVAL_BETEEN_EMAILS + jitter;
			scheduler.schedule(createSendInformMailRunnable(email), delayMinutes, TimeUnit.MINUTES);
		}
		CompletableFuture<Void> cf = new CompletableFuture<Void>();
		cf.complete(null);
		return cf;
	}

	@Counted(name = "sentProspectInformMail", description = "How many mails have been sent to prospects.")
	public void sendProspectInformMail(String email) {
		LOGGER.info("SENDING EMAIL to: " + email);
		CompletionStage<Void> sendMailCompletionStage = prospectInformMailTemplate.to(email)
				.replyTo(mailConfig.getReplyTo()).subject(mailConfig.getProspectInformMailSubject()).send();
		sendMailCompletionStage.thenApply(f -> {
			LOGGER.info("SUCESS SENDING EMAIL to: " + email);
			return null;
		});
		sendMailCompletionStage.exceptionally(f -> {
			LOGGER.log(Level.ERROR, "ERROR SENDING EMAIL to: " + email);
			return null;
		});
	}

	private Runnable createSendInformMailRunnable(String email) {
		return new Runnable() {
			@Override
			public void run() {
				sendProspectInformMail(email);
			}
		};
	}

	public CompletionStage<Void> sendReinformEmails() {
		List<ProspectEntity> prospects = ProspectEntity.find("flReinformed", false).list();

		// filter invalid emails
		Stream<ProspectEntity> prospectValidEmailsStream = prospects.stream()
				.filter(p -> (!p.getEmail().contains("fake_") && p.getEmail().length() > 7));

		// Normalize and remove duplicates
		List<String> validEmails = prospectValidEmailsStream.map(p -> p.getEmail().toLowerCase()).distinct()
				.collect(Collectors.toList());

		List<CompanyEntity> registeredCompanies = CompanyEntity.listAll();
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		int delayMinutes = 0;
		for (String email : validEmails) {
			if (!registeredCompanies.stream().anyMatch(c -> c.getEmail().toLowerCase().equals(email))) {
				int jitter = (int) Math.floor((Math.random() * 3d));
				delayMinutes += MINIMUM_INTERVAL_BETEEN_EMAILS + jitter;
				scheduler.schedule(createSendReinformMailRunnable(email), delayMinutes, TimeUnit.SECONDS);
			}
		}
		CompletableFuture<Void> cf = new CompletableFuture<Void>();
		cf.complete(null);
		return cf;
	}

	private Runnable createSendReinformMailRunnable(String email) {
		return new Runnable() {
			@Override
			public void run() {
				sendProspectReinformMail(email);
			}
		};
	}

	@Counted(name = "sentProspectReinformMail", description = "How many mails have been sent to reinform prospects.")
	@Transactional
	public void sendProspectReinformMail(String email) {
		LOGGER.info("SENDING EMAIL to: " + email);
		CompletionStage<Void> sendMailCompletionStage = prospectReinformMailTemplate.to(email)
				.replyTo(mailConfig.getReplyTo()).subject(mailConfig.getProspectReinformMailSubject()).send();
		try {
			sendMailCompletionStage.toCompletableFuture().get();
			LOGGER.info("SUCESS SENDING EMAIL to: " + email);
			ProspectEntity reinformedProspect = ProspectEntity.find("email", email).singleResult();
			reinformedProspect.setFlReinformed(true);
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.log(Level.ERROR, "ERROR SENDING EMAIL to: " + email, e);
		}
	}
}
