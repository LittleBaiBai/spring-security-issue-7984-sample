package io.example;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
class TestController {

	private static final String API_CLIENT_ID = "b";

	private final WebClient webClient;

	TestController(ReactiveClientRegistrationRepository clientRegistrationRepository,
	               @Value("${server.port}") int port) {
		this.webClient = WebClient.builder()
		                          .filter(getOAuth2FilterFunction(clientRegistrationRepository))
		                          .baseUrl("http://localhost:" + port)
		                          .build();
	}

	@GetMapping("/test/a")
	Mono<String> endpointA() {
		return webClient.get()
		                .uri("/test/b")
		                .retrieve()
		                .toEntity(String.class)
						.map(HttpEntity::getBody);
	}

	@GetMapping("/test/b")
	Mono<String> endpointB() {
		return Mono.just("Hello Security!");
	}

	private ExchangeFilterFunction getOAuth2FilterFunction(
		ReactiveClientRegistrationRepository clientRegistrationRepository) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2FilterFunction = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
			clientRegistrationRepository,
			new UnAuthenticatedServerOAuth2AuthorizedClientRepository()
		);

		oauth2FilterFunction.setDefaultClientRegistrationId(API_CLIENT_ID);
		return oauth2FilterFunction;
	}
}
