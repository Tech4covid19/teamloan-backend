package pt.teamloan.test.resources;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.jboss.logmanager.Logger;
import org.testcontainers.containers.PostgreSQLContainer;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class PostgreSQLTestResource implements QuarkusTestResourceLifecycleManager {
	private static final Logger LOGGER = Logger.getLogger(PostgreSQLTestResource.class.getName());

	private PostgreSQLContainer db;

	@Override
	public Map<String, String> start() {
		try {
			int port = findAvailablePort();
			LOGGER.info("STARTING POSTGRESQL TEST DB!");
			db = new PostgreSQLContainer<>("postgres:11").withEnv("sslmode", "disable").withDatabaseName("test").withEnv("POSTGRES_HOST_AUTH_METHOD", "trust")
					.withUsername("test").withExposedPorts(port)
					.withCreateContainerCmdModifier(cmd -> {
						cmd.withHostName("localhost")
								.withPortBindings(new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(port)));
					});
			db.start();
			LOGGER.info("STARTED POSTGRESQL TEST DB! " + db.getJdbcUrl());

			Map<String, String> props = new HashMap<>();
			props.put("quarkus.datasource.url", db.getJdbcUrl());
			props.put("quarkus.hibernate-orm.database.generation", "drop-and-create");
			props.put("quarkus.datasource.username", "test");
			props.put("quarkus.datasource.driver", db.getDriverClassName());
			props.put("quarkus.datasource.password", "test");
			Thread.sleep(300000);
			System.out.println(props);
			return props;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error starting postgres container for tests.", e);
			throw new RuntimeException(e);
		}
	}

	private int findAvailablePort() throws IOException {
		ServerSocket serverSocket = new ServerSocket(0);
		int availablePort = serverSocket.getLocalPort();
		serverSocket.close();
		return availablePort;
	}

	@Override
	public void stop() {
		db.stop();
	}

	public Connection getConnection() throws Exception {
		Class.forName(db.getDriverClassName());
		return DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword());
	}
}
