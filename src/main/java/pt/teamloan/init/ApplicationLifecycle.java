package pt.teamloan.init;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.jboss.logmanager.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@OpenAPIDefinition(info = @Info(title = "backend API for TeamLoan", version = "2.0.0"), security = {
		@SecurityRequirement(name = "DEV"), @SecurityRequirement(name = "PROD") })
@ApplicationScoped
public class ApplicationLifecycle extends Application {
	private static final Logger LOGGER = Logger.getLogger(ApplicationLifecycle.class.getName());

	void onStart(@Observes StartupEvent ev) {
		LOGGER.info("Starting teamloan-backend API...");
	}

	void onStop(@Observes ShutdownEvent ev) {
		LOGGER.info("Shutting down teamloan-backend API...");
	}
}
