package pt.teamloan.service.metrics;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@ApplicationScoped
public class MetricsService {
    private static String REGISTRATION_METRICS_QUERY = "SELECT NEW pt.teamloan.service.metrics.RegistrationMetric(ba.name, c.intent, count(1))\n" + //
            "FROM CompanyEntity c\n" + //
            "JOIN c.businessArea ba\n" + //
            "GROUP BY ba.name, c.intent";

    @Inject
    EntityManager em;

    public List<RegistrationMetric> getRegistrationMetrics() {
        TypedQuery<RegistrationMetric> query = em.createQuery(REGISTRATION_METRICS_QUERY, RegistrationMetric.class);
        return query.getResultList();
    }
}