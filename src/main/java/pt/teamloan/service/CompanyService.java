package pt.teamloan.service;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.mailer.MailTemplate;
import io.quarkus.qute.api.ResourcePath;
import pt.teamloan.authserver.AuthServerException;
import pt.teamloan.authserver.AuthServerResponse;
import pt.teamloan.authserver.AuthServerService;
import pt.teamloan.authserver.AuthServerUser;
import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.exception.GenericException;
import pt.teamloan.model.BusinessAreaEntity;
import pt.teamloan.model.CompanyEntity;
import pt.teamloan.utils.SecureKeyGenerator;
import pt.teamloan.utils.UUIDMapper;

@ApplicationScoped
public class CompanyService {
	
	@Inject
	AuthServerService authServerService;
	
	@Inject
	UUIDMapper uuidMapper;
	
	@ConfigProperty(name = "mail.email-verification.subject")
	String verificationMailSubject;
	
	@ConfigProperty(name = "mail.email-verification.link-format")
	String verificationMailLinkFormat;
	
	@ResourcePath("mails/email-verification")
	MailTemplate verificationEmailTemplate;
	
	@Transactional
	public CompletionStage<Void> register(@Valid CompanyEntity company) throws Exception {
		if(CompanyEntity.count("email", company.getEmail()) > 0) {
			throw new EntityAlreadyExistsException("Email already exists");
		}
		if(CompanyEntity.count("vat", company.getVat()) > 0) {
			throw new EntityAlreadyExistsException("VAT already exists");
		}
		company.setUuid(UUID.randomUUID());
		setBusinessAreaId(company);
		setActivationKey(company);
		company.persist();
		
		// create on keycloak
		 AuthServerResponse authServerCreateUserResponse = authServerService.createUser(new AuthServerUser(null, company.getVat(), company.getEmail(), company.getPassword(), company.getUuid().toString()));
		 String authSubjectUuid = authServerCreateUserResponse.getId();
		 company.setAuthSubjectUuid(authSubjectUuid);
		
		// send email verification with activation link. 
		// We are not waiting for the completion stage so the ongoing transaction can finish while the email is being sent.
		sendVerificationEmail(company);
		
		return CompletableFuture.completedFuture(null);
	}


	private String setActivationKey(CompanyEntity company) {
		company.setActivationKey(SecureKeyGenerator.generateUniqueSecureKey());
		company.setDtActivationKeyExpiresAt(Timestamp.from(Instant.now().plus(3, ChronoUnit.DAYS)));
		return company.getActivationKey();
	}
	
	
	public CompanyEntity getByUUID(String uuid) {
		return CompanyEntity.find("uuid", UUID.fromString(uuid)).singleResult();
	}

	@Transactional
	public CompanyEntity activate(String activationKey) throws Exception {
		CompanyEntity foundCompany = CompanyEntity.find("activationKey", activationKey).singleResult();
		if(!foundCompany.isEmailVerified() && !isActivationExpired(foundCompany)) {
			authServerService.updateEmailToVerified(foundCompany.getAuthSubjectUuid());
			foundCompany.setActivationKey(null);
			foundCompany.setDtActivationKeyExpiresAt(null);
			foundCompany.setEmailVerified(true);
			//foundCompany.persist();
		}
		return foundCompany;
	}


	private boolean isActivationExpired(CompanyEntity foundCompany) {
		return foundCompany.getDtActivationKeyExpiresAt().before(Timestamp.from(Instant.now()));
	}

	private CompletionStage<Void> sendVerificationEmail(CompanyEntity companyEntity) {
		String verificationMailLink = MessageFormat.format(verificationMailLinkFormat, companyEntity.getActivationKey());
		CompletionStage<Void> sendMailCompletionStage = verificationEmailTemplate.to(companyEntity.getEmail())
				.subject(verificationMailSubject).data("link", verificationMailLink).send();
		return sendMailCompletionStage;
	}

	private void setBusinessAreaId(CompanyEntity company) throws GenericException {
		if(company.getBusinessArea() != null && company.getBusinessArea().getUuid() != null) {
			UUID uuid = company.getBusinessArea().getUuid();
			company.setBusinessArea(null);
			Integer id = uuidMapper.mapToId(uuid, BusinessAreaEntity.class);
			BusinessAreaEntity businessArea = new BusinessAreaEntity();
			businessArea.setId(id);
			company.setBusinessArea(businessArea);
		}
	}



}
