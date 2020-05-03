package pt.teamloan.config;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MailConfig {

	@ConfigProperty(name = "mail.email-verification.subject")
	String verificationMailSubject;

	@ConfigProperty(name = "mail.email-verification.link-format")
	String verificationMailLinkFormat;

	@ConfigProperty(name = "mail.forgot-password.subject")
	String forgotPasswordMailSubject;

	@ConfigProperty(name = "mail.forgot-password.link-format")
	String forgotPasswordMailLinkFormat;

	@ConfigProperty(name = "mail.prospect.subject")
	String prospectMailSubject;

	@ConfigProperty(name = "mail.prospect-inform.subject")
	String prospectInformMailSubject;

	@ConfigProperty(name = "mail.metrics.enabled", defaultValue = "true")
	Boolean metricsEnabled;

	@ConfigProperty(name = "mail.metrics.subject")
	String metricsSubject;

	@ConfigProperty(name = "mail.metrics.to")
	List<String> metricsTo;

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

	public Boolean getMetricsEnabled() {
		return metricsEnabled;
	}

	public String getMetricsSubject() {
		return metricsSubject;
	}

	public List<String> getMetricsTo() {
		return metricsTo;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public String getForgotPasswordMailSubject() {
		return forgotPasswordMailSubject;
	}

	public void setForgotPasswordMailSubject(String forgotPasswordMailSubject) {
		this.forgotPasswordMailSubject = forgotPasswordMailSubject;
	}

	public String getForgotPasswordMailLinkFormat() {
		return forgotPasswordMailLinkFormat;
	}

	public void setForgotPasswordMailLinkFormat(String forgotPasswordMailLinkFormat) {
		this.forgotPasswordMailLinkFormat = forgotPasswordMailLinkFormat;
	}

}
