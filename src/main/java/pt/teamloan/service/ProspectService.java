package pt.teamloan.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.jboss.logmanager.Level;

import io.quarkus.mailer.MailTemplate;
import io.quarkus.panache.common.Sort;
import io.quarkus.qute.api.ResourcePath;
import pt.teamloan.config.MailConfig;
import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.model.ProspectEntity;

@ApplicationScoped
public class ProspectService {
	private static final Logger LOGGER = Logger.getLogger(ProspectService.class.getName());

	@Inject
	MailConfig mailConfig;
	
	@ResourcePath("mails/prospect")
	MailTemplate prospectMailTemplate;

	@ResourcePath("mails/prospect-inform")
	MailTemplate prospectInformMailTemplate;

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

		for (String email : validEmails) {
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
		CompletableFuture<Void> cf = new CompletableFuture<Void>();
		cf.complete(null);
		return cf;
	}
}
