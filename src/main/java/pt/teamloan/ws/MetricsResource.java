package pt.teamloan.ws;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logmanager.Logger;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import pt.teamloan.model.CompanyEntity;
import pt.teamloan.service.CompanyService;
import pt.teamloan.service.metrics.MetricsService;
import pt.teamloan.service.metrics.RegistrationMetric;

@Path("/metrics")
@RequestScoped
@Bulkhead
@Timed
@Timeout(value = 5000)
public class MetricsResource {
	private static final Logger LOGGER = Logger.getLogger(MetricsResource.class.getName());

	@ResourcePath("metrics/metrics")
	Template metricsTemplate;

	@Inject
	MetricsService metricsService;

	@Inject
	CompanyService companyService;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance getRegistrationMetrics() {
		List<RegistrationMetric> registrationMetrics = metricsService.getRegistrationMetrics();
		Long totalRegistrations = registrationMetrics.stream().map(m -> m.getTotal())
				.reduce((accumulatedTotal, nextTotal) -> accumulatedTotal + nextTotal).get();
		return metricsTemplate.data("metrics", registrationMetrics).data("totalRegistrations", totalRegistrations);
	}
}