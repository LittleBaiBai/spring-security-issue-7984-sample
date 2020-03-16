package io.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SecurityConfiguration {

	@Bean
	public SecurityWebFilterChain actuator(ServerHttpSecurity httpSecurity) {
		// @formatter:off
		return httpSecurity
			.securityMatcher(new PathPatternParserServerWebExchangeMatcher("/test/**"))
			.httpBasic().disable()
			.csrf().disable()
			.oauth2ResourceServer().jwt().and().and()
			.authorizeExchange()
				.pathMatchers("/test/**").authenticated().and()
			.build();
		// @formatter:on
	}
}
