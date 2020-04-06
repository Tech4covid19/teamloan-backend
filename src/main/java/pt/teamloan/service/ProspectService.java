package pt.teamloan.service;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.mailer.MailTemplate;
import pt.teamloan.db.ProspectEntity;
import pt.teamloan.exception.EntityAlreadyExistsException;

@ApplicationScoped
public class ProspectService {
	
	@ConfigProperty(name = "mail.prospect.subject")
	String prospectMailSubject;
	
	@Inject
	MailTemplate prospect;

	@Transactional
	public CompletionStage<Void> registerProspect(@Valid ProspectEntity prospectEntity) throws EntityAlreadyExistsException {
		if(ProspectEntity.count("email", prospectEntity.email) > 0) {
			throw new EntityAlreadyExistsException("Email already exists");
		}
		prospectEntity.persist();
		CompletionStage<Void> sendMailCompletionStage = prospect.to(prospectEntity.email)
				.subject(prospectMailSubject).send();
		return sendMailCompletionStage;
	}
}
