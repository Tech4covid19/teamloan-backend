package pt.teamloan.service;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.mailer.MailTemplate;
import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.model.ProspectEntity;

@ApplicationScoped
public class ProspectService {
	
	@ConfigProperty(name = "mail.prospect.subject")
	String prospectMailSubject;
	
	@Inject
	MailTemplate prospect;

	@Transactional
	public CompletionStage<Void> registerProspect(@Valid ProspectEntity prospectEntity) throws EntityAlreadyExistsException {
		if(ProspectEntity.count("email", prospectEntity.getEmail()) > 0) {
			throw new EntityAlreadyExistsException("Email already exists");
		}
		prospectEntity.persist();
		CompletionStage<Void> sendMailCompletionStage = prospect.to(prospectEntity.getEmail())
				.subject(prospectMailSubject).send();
		return sendMailCompletionStage;
	}
}
