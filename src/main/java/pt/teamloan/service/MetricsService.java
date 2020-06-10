package pt.teamloan.service;

import java.text.Collator;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jboss.logmanager.Logger;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import io.quarkus.scheduler.Scheduled;
import pt.teamloan.config.MailConfig;
import pt.teamloan.service.pojos.RegistrationMetric;

@ApplicationScoped
public class MetricsService {

    private static final Logger LOGGER = Logger.getLogger(MetricsService.class.getName());

    @ResourcePath("admin/metrics/metrics")
    Template metricsTemplate;

    @Inject
    EntityManager em;

    @Inject
    Mailer mailer;

    @Inject
    MailConfig mailConfig;

    private static String REGISTRATION_METRICS_QUERY = "SELECT NEW pt.teamloan.service.pojos.RegistrationMetric(ba.name, c.intent, count(1))\n"
            + //
            "FROM CompanyEntity c\n" + //
            "JOIN c.businessArea ba\n" + //
            "WHERE c.name NOT LIKE '%[TEAMLOAN-TEST]%'\n" + // to filter test accounts
            "GROUP BY ba.name, c.intent";

    public List<RegistrationMetric> getRegistrationMetrics() {
        TypedQuery<RegistrationMetric> query = em.createQuery(REGISTRATION_METRICS_QUERY, RegistrationMetric.class);
        return query.getResultList();
    }

    public Long calculateTotalRegistrations(List<RegistrationMetric> registrationMetrics) {
        return registrationMetrics.stream().map(m -> m.getTotal())
                .reduce((accumulatedTotal, nextTotal) -> accumulatedTotal + nextTotal).get();
    }

    public TemplateInstance getRegistrationMetricsInHtml() {
        List<RegistrationMetric> registrationMetrics = getRegistrationMetrics();
        sortRegistrationsAlphabetically(registrationMetrics);
        Long totalRegistrations = calculateTotalRegistrations(registrationMetrics);
        return metricsTemplate.data("metrics", registrationMetrics).data("totalRegistrations", totalRegistrations);
    }

    private void sortRegistrationsAlphabetically(List<RegistrationMetric> registrationMetrics) {
        registrationMetrics.sort((rm1, rm2) -> Collator.getInstance().compare(rm1.getBusinessArea() + "_" + rm1.getIntent().name(), rm2.getBusinessArea() + "_" + rm2.getIntent().name()));
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void sendMetricsMail() {
        if (mailConfig.getMetricsEnabled()) {
            LOGGER.info("Running job to send registration metrics by email to: " + mailConfig.getMetricsTo());
            Mail mail = Mail.withHtml(null, mailConfig.getMetricsSubject(), getRegistrationMetricsInHtml().render());
            mail.setTo(mailConfig.getMetricsTo());
            mailer.send(mail);
        }
    }
}