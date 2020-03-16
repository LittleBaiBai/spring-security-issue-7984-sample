package io.example;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.InternetProtocol;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ContainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final String CONTAINER_HOST = "localhost";
	private static final int UAA_CONTAINER_PORT = 40000;

	private static GenericContainer uaaContainer;

	@Override
	public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
		String uaaBaseUrl = String.format("http://%s:%s/uaa", CONTAINER_HOST, UAA_CONTAINER_PORT);

		try {
			Path uaaDockerfileFolder = Paths.get(ClassLoader.getSystemResource("uaa").toURI());
			uaaContainer = new GenericContainer(new ImageFromDockerfile().withFileFromPath(".", uaaDockerfileFolder))
					.withExposedPorts(UAA_CONTAINER_PORT)
					.withEnv("UAA_CONFIG_YAML", "{issuer.uri: \"" + uaaBaseUrl + "\"}")
					.waitingFor(Wait.forHttp("/uaa/info").forStatusCode(200).withStartupTimeout(Duration.ofMinutes(2)));

			uaaContainer.setPortBindings(Collections.singletonList(generatePortMapping(UAA_CONTAINER_PORT, 8080)));
			uaaContainer.start();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		TestPropertyValues values = TestPropertyValues.of("uaa.uri=" + uaaBaseUrl);
		values.applyTo(configurableApplicationContext);
	}

	private static String generatePortMapping(int externalPort, int internalPort) {
		return String.format("%d:%d/%s", externalPort, internalPort, InternetProtocol.TCP.toDockerNotation());
	}
}
