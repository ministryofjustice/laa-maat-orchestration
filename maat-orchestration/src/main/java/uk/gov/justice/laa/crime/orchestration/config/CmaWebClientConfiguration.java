package uk.gov.justice.laa.crime.orchestration.config;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import uk.gov.justice.laa.crime.orchestration.client.CmaApiClient;

import java.time.Duration;

@Configuration
public class CmaWebClientConfiguration {

    @Bean
    WebClient webClient(ServicesConfiguration servicesConfiguration,
                        ClientRegistrationRepository clientRegistrationRepository,
                        OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrationRepository, oAuth2AuthorizedClientRepository);
        oauth.setDefaultClientRegistrationId("cma");
        ConnectionProvider provider =
                ConnectionProvider.builder("custom")
                        .maxConnections(500)
                        .maxIdleTime(Duration.ofSeconds(20))
                        .maxLifeTime(Duration.ofSeconds(60))
                        .pendingAcquireTimeout(Duration.ofSeconds(60))
                        .evictInBackground(Duration.ofSeconds(120))
                        .build();

        System.out.println("Base URL: " + servicesConfiguration.getCmaApi().getBaseUrl());
        return WebClient.builder()
                .baseUrl(servicesConfiguration.getCmaApi().getBaseUrl())
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create(provider)
                                        .resolver(DefaultAddressResolverGroup.INSTANCE)
                                        .compress(true)
                                        .responseTimeout(Duration.ofSeconds(30))
                        )
                )
                .filter(oauth)
                .build();
    }

    @Bean
    CmaApiClient cmaClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builder(
                WebClientAdapter.forClient(webClient)).build();
        return httpServiceProxyFactory.createClient(CmaApiClient.class);
    }

}
