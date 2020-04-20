package pt.teamloan.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MailConfig {
	
	@ConfigProperty(name = "mail.email-verification.subject")
	String verificationMailSubject;

	@ConfigProperty(name = "mail.email-verification.link-format")
	String verificationMailLinkFormat;

	@ConfigProperty(name = "mail.prospect.subject")
	String prospectMailSubject;

	@ConfigProperty(name = "mail.prospect-inform.subject")
	String prospectInformMailSubject;

	@ConfigProperty(name = "mail.reply-to")
	String replyTo;

	public String getVerificationMailSubject() {
		return verificationMailSubject;
	}

	public String getVerificationMailLinkFormat() {
		return verificationMailLinkFormat;
	}

	public String getProspectMailSubject() {
		return prospectMailSubject;
	}

	public String getProspectInformMailSubject() {
		return prospectInformMailSubject;
	}

	public String getReplyTo() {
		return replyTo;
	}

}
