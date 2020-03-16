package io.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = {ContainersInitializer.class})
@ActiveProfiles("test")
@TestPropertySource(properties = "server.port=8085")
class ControllerTest {

	@LocalServerPort
	private int port;

	private WebTestClient webTestClient;

	@BeforeEach
	void setUp() {
		webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	@Test
	@WithMockUser
	void callEndpointA() {
		webTestClient.get()
		             .uri("test/a")
		             .exchange()
		             .expectStatus()
		             .is2xxSuccessful()
		             .expectBody(String.class)
		             .isEqualTo("Hello Security!");
	}

	@Test
	@WithMockUser
	void callEndpointB() {
		webTestClient.get()
		             .uri("test/b")
		             .exchange()
		             .expectStatus()
		             .is2xxSuccessful()
		             .expectBody(String.class)
		             .isEqualTo("Hello Security!");
	}
}
