package pt.teamloan.service;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.mailer.MailTemplate;
import io.quarkus.qute.api.ResourcePath;
import pt.teamloan.authserver.AuthServerException;
import pt.teamloan.authserver.AuthServerResponse;
import pt.teamloan.authserver.AuthServerService;
import pt.teamloan.authserver.AuthServerUser;
import pt.teamloan.config.MailConfig;
import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.exception.TeamLoanException;
import pt.teamloan.model.BusinessAreaEntity;
import pt.teamloan.model.CompanyEntity;
import pt.teamloan.utils.SecureKeyGenerator;
import pt.teamloan.utils.UUIDMapper;

@ApplicationScoped
public class CompanyService {
	private static final Logger LOGGER = Logger.getLogger(CompanyService.class.getName());
	
	@Inject
	MailConfig mailConfig;

	@Inject
	AuthServerService authServerService;

	@Inject
	UUIDMapper uuidMapper;

	@ResourcePath("mails/email-verification")
	MailTemplate verificationEmailTemplate;

	@ResourcePath("mails/forgot-password")
	MailTemplate forgotPasswordEmailTemplate;

	@Transactional
	public CompletionStage<Void> register(@Valid CompanyEntity company) throws Exception {
		if (CompanyEntity.count("email", company.getEmail()) > 0) {
			throw new EntityAlreadyExistsException("Email already exists");
		}
		if (CompanyEntity.count("vat", company.getVat()) > 0) {
			throw new EntityAlreadyExistsException("VAT already exists");
		}
		company.setUuid(UUID.randomUUID());
		setBusinessAreaId(company);
		setActivationKey(company);
		company.persist();

		// create on keycloak
		AuthServerResponse authServerCreateUserResponse = authServerService.createUser(new AuthServerUser(null,
				company.getVat(), company.getEmail(), company.getPassword(), company.getUuid().toString()));
		String authSubjectUuid = authServerCreateUserResponse.getId();
		company.setAuthSubjectUuid(authSubjectUuid);

		// send email verification with activation link.
		// We are not waiting for the completion stage so the ongoing transaction can
		// finish while the email is being sent.
		sendVerificationEmail(company);

		return CompletableFuture.completedFuture(null);
	}

	public CompanyEntity getByUUID(String uuid) {
		return CompanyEntity.find("uuid", UUID.fromString(uuid)).singleResult();
	}

	@Transactional
	public CompanyEntity activate(String activationKey) throws Exception {
		CompanyEntity foundCompany = CompanyEntity.find("activationKey", activationKey).singleResult();
		if (!foundCompany.isEmailVerified() && !isDateExpired(foundCompany.getDtActivationKeyExpiresAt())) {
			authServerService.updateEmailToVerified(foundCompany.getAuthSubjectUuid());
			foundCompany.setActivationKey(null);
			foundCompany.setDtActivationKeyExpiresAt(null);
			foundCompany.setEmailVerified(true);
		}
		return foundCompany;
	}

	@Transactional
	public void forgotPassword(@NotBlank @Email String email) {
		Optional<CompanyEntity> companyOptional = CompanyEntity.find("email", email).singleResultOptional();
		if(companyOptional.isPresent()){
			CompanyEntity company = companyOptional.get();
			setResetPasswordKey(company);
			sendForgotPasswordEmail(company);
		} else {
			LOGGER.warn("No company for forgot password provided email: " + email);
		}
	}

	@Transactional
	public CompanyEntity resetPassword(@NotBlank String resetPasswordKey,
			@NotBlank @Size(min = 5, max = 128) String newPassword) throws AuthServerException, TeamLoanException {
		CompanyEntity foundCompany = CompanyEntity.find("resetPasswordKey", resetPasswordKey).singleResult();
		if (isDateExpired(foundCompany.getDtResetPasswordKeyExpiresAt())) {
			throw new TeamLoanException("The reset password key has already expired.");
		} else {
			authServerService.resetPassword(foundCompany.getAuthSubjectUuid(), newPassword);
			foundCompany.setResetPasswordKey(null);
			foundCompany.setDtResetPasswordKeyExpiresAt(null);
			if (!foundCompany.isEmailVerified()) {
				authServerService.updateEmailToVerified(foundCompany.getAuthSubjectUuid());
				foundCompany.setEmailVerified(true);
			}
		}
		return foundCompany;
	}

	private String setActivationKey(CompanyEntity company) {
		company.setActivationKey(SecureKeyGenerator.generateUniqueSecureKey());
		company.setDtActivationKeyExpiresAt(Timestamp.from(Instant.now().plus(3, ChronoUnit.DAYS)));
		return company.getActivationKey();
	}

	private boolean isDateExpired(Timestamp date) {
		return date.before(Timestamp.from(Instant.now()));
	}

	private CompletionStage<Void> sendVerificationEmail(CompanyEntity companyEntity) {
		String verificationMailLink = MessageFormat.format(mailConfig.getVerificationMailLinkFormat(),
				companyEntity.getActivationKey());
		CompletionStage<Void> sendMailCompletionStage = verificationEmailTemplate.to(companyEntity.getEmail())
				.replyTo(mailConfig.getReplyTo()).subject(mailConfig.getVerificationMailSubject())
				.data("link", verificationMailLink).send();
		return sendMailCompletionStage;
	}

	private void setBusinessAreaId(CompanyEntity company) throws TeamLoanException {
		if (company.getBusinessArea() != null && company.getBusinessArea().getUuid() != null) {
			UUID uuid = company.getBusinessArea().getUuid();
			company.setBusinessArea(null);
			Integer id = uuidMapper.mapToId(uuid, BusinessAreaEntity.class);
			BusinessAreaEntity businessArea = new BusinessAreaEntity();
			businessArea.setId(id);
			company.setBusinessArea(businessArea);
		}
	}

	private String setResetPasswordKey(CompanyEntity foundCompany) {
		foundCompany.setResetPasswordKey(SecureKeyGenerator.generateUniqueSecureKey());
		foundCompany.setDtResetPasswordKeyExpiresAt(Timestamp.from(Instant.now().plus(3, ChronoUnit.DAYS)));
		return foundCompany.getResetPasswordKey();
	}

	private CompletionStage<Void> sendForgotPasswordEmail(CompanyEntity companyEntity) {
		String mailLink = MessageFormat.format(mailConfig.getForgotPasswordMailLinkFormat(),
				companyEntity.getResetPasswordKey());
		CompletionStage<Void> sendMailCompletionStage = forgotPasswordEmailTemplate.to(companyEntity.getEmail())
				.replyTo(mailConfig.getReplyTo()).subject(mailConfig.getForgotPasswordMailSubject())
				.data("link", mailLink).send();
		return sendMailCompletionStage;
	}
}
