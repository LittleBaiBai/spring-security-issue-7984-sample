package io.example;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = {ContainersInitializer.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
		"server.port=8085",
		"service-b.url=http://localhost:" + ControllerTest.SERVICE_B_PORT,
})
class ControllerTest {

	static final int SERVICE_B_PORT = 10000;

	@LocalServerPort
	private int port;

	private WebTestClient webTestClient;

	private static WireMockServer wireMockServer;

	@BeforeAll
	static void setUpWireMock() {
		wireMockServer = new WireMockServer(options().port(SERVICE_B_PORT));
		wireMockServer.stubFor(get(urlPathEqualTo("/test/b")).willReturn(ok("Hello Security!")));
		wireMockServer.start();
	}

	@BeforeEach
	void setUp() {
		webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	@Test
	@WithMockUser
	void callServiceBWithBearerToken() {
		webTestClient.get()
		             .uri("test/a")
		             .exchange()
		             .expectStatus()
		             .is2xxSuccessful()
		             .expectBody(String.class)
		             .isEqualTo("Hello Security!");

		wireMockServer.verify(getRequestedFor(urlPathEqualTo("/test/b"))
			.withHeader("Authorization", matching("Bearer [\\w\\._-]+")));
	}

	@AfterAll
	static void tearDown() {
		wireMockServer.stop();
	}
}
