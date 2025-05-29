package uk.gov.justice.laa.crime.orchestration.config;

import static uk.gov.justice.laa.crime.orchestration.common.Constants.MISSING_REGISTRATION_ID;

import io.github.resilience4j.retry.RetryRegistry;
import io.netty.resolver.DefaultAddressResolverGroup;
import java.time.Duration;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import uk.gov.justice.laa.crime.orchestration.client.ApplicationTrackingApiClient;
import uk.gov.justice.laa.crime.orchestration.client.CrimeMeansAssessmentApiClient;
import uk.gov.justice.laa.crime.orchestration.client.CrownCourtContributionsApiClient;
import uk.gov.justice.laa.crime.orchestration.client.CrownCourtProceedingApiClient;
import uk.gov.justice.laa.crime.orchestration.client.EvidenceApiClient;
import uk.gov.justice.laa.crime.orchestration.filter.Resilience4jRetryFilter;
import uk.gov.justice.laa.crime.orchestration.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.orchestration.client.HardshipApiClient;
import uk.gov.justice.laa.crime.orchestration.filter.WebClientFilters;

@Slf4j
@Configuration
@AllArgsConstructor
public class WebClientsConfiguration {
  public static final int MAX_IN_MEMORY_SIZE = 10485760;

  public static final String COURT_DATA_API_WEB_CLIENT_NAME = "maatCourtDataWebClient";
  public static final String EVIDENCE_API_WEB_CLIENT_NAME = "evidenceWebClient";
  public static final String CROWN_COURT_PROCEEDING_API_WEB_CLIENT_NAME = "crownCourtProceedingWebClient";
  public static final String HARDSHIP_SERVICE_WEB_CLIENT_NAME = "hardshipWebClient";
  public static final String CROWN_COURT_CONTRIBUTIONS_WEB_CLIENT_NAME = "crownCourtContributionsWebClient";
  public static final String CRIME_MEANS_ASSESSMENT_WEB_CLIENT_NAME = "crimeMeansAssessmentWebClient";
  public static final String APPLICATION_TRACKING_WEB_CLIENT_NAME = "applicationTrackingWebClient";
  

  @Bean
  WebClientCustomizer webClientCustomizer() {
    ConnectionProvider provider =
        ConnectionProvider.builder("custom")
            .maxConnections(500)
            .maxIdleTime(Duration.ofSeconds(20))
            .maxLifeTime(Duration.ofSeconds(60))
            .evictInBackground(Duration.ofSeconds(120))
            .pendingAcquireTimeout(Duration.ofSeconds(60))
            .build();

    return builder -> {
      builder.clientConnector(
          new ReactorClientHttpConnector(
              HttpClient.create(provider)
                  .resolver(DefaultAddressResolverGroup.INSTANCE)
                  .compress(true)
                  .responseTimeout(Duration.ofSeconds(30))
          )
      );
      builder.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
      builder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      builder.codecs(configurer -> configurer
          .defaultCodecs()
          .maxInMemorySize(MAX_IN_MEMORY_SIZE)
      );
    };
  }
  
  @Bean(COURT_DATA_API_WEB_CLIENT_NAME)
  WebClient maatCourtDataWebClient(WebClient.Builder webClientBuilder,
      ServicesConfiguration servicesConfiguration,
      ClientRegistrationRepository clientRegistrations,
      OAuth2AuthorizedClientRepository authorizedClients,
      RetryRegistry retryRegistry) {

    ServletOAuth2AuthorizedClientExchangeFilterFunction oauthFilter =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations,
            authorizedClients);

    String registrationId = servicesConfiguration.getMaatApi().getRegistrationId();
    Assert.notNull(registrationId, MISSING_REGISTRATION_ID);
    oauthFilter.setDefaultClientRegistrationId(registrationId);

    uk.gov.justice.laa.crime.orchestration.filter.Resilience4jRetryFilter retryFilter =
        new Resilience4jRetryFilter(retryRegistry, COURT_DATA_API_WEB_CLIENT_NAME);

    return webClientBuilder
        .baseUrl(servicesConfiguration.getMaatApi().getBaseUrl())
        .filters(filters -> configureFilters(filters, oauthFilter, retryFilter))
        .build();
  }

  @Bean(HARDSHIP_SERVICE_WEB_CLIENT_NAME)
  WebClient hardshipWebClient(WebClient.Builder webClientBuilder,
      ServicesConfiguration servicesConfiguration,
      ClientRegistrationRepository clientRegistrations,
      OAuth2AuthorizedClientRepository authorizedClients,
      RetryRegistry retryRegistry) {

    ServletOAuth2AuthorizedClientExchangeFilterFunction oauthFilter =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, authorizedClients);

    String registrationId = servicesConfiguration.getHardshipApi().getRegistrationId();
    Assert.notNull(registrationId, MISSING_REGISTRATION_ID);
    oauthFilter.setDefaultClientRegistrationId(registrationId);

    Resilience4jRetryFilter retryFilter =
        new Resilience4jRetryFilter(retryRegistry, HARDSHIP_SERVICE_WEB_CLIENT_NAME);

    return webClientBuilder
        .baseUrl(servicesConfiguration.getHardshipApi().getBaseUrl())
        .filters(filters -> configureFilters(filters, oauthFilter, retryFilter))
        .build();
  }

  @Bean(EVIDENCE_API_WEB_CLIENT_NAME)
  WebClient evidenceWebClient(WebClient.Builder webClientBuilder,
      ServicesConfiguration servicesConfiguration,
      ClientRegistrationRepository clientRegistrations,
      OAuth2AuthorizedClientRepository authorizedClients,
      RetryRegistry retryRegistry) {

    ServletOAuth2AuthorizedClientExchangeFilterFunction oauthFilter =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations,
            authorizedClients);

    String registrationId = servicesConfiguration.getEvidenceApi().getRegistrationId();
    Assert.notNull(registrationId, MISSING_REGISTRATION_ID);
    oauthFilter.setDefaultClientRegistrationId(registrationId);

    Resilience4jRetryFilter retryFilter =
        new Resilience4jRetryFilter(retryRegistry, EVIDENCE_API_WEB_CLIENT_NAME);

    return webClientBuilder
        .baseUrl(servicesConfiguration.getEvidenceApi().getBaseUrl())
        .filters(filters -> configureFilters(filters, oauthFilter, retryFilter))
        .build();
  }

  @Bean(CROWN_COURT_PROCEEDING_API_WEB_CLIENT_NAME)
  WebClient crownCourtProceedingWebClient(WebClient.Builder webClientBuilder,
      ServicesConfiguration servicesConfiguration,
      ClientRegistrationRepository clientRegistrations,
      OAuth2AuthorizedClientRepository authorizedClients,
      RetryRegistry retryRegistry) {

    ServletOAuth2AuthorizedClientExchangeFilterFunction oauthFilter =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations,
            authorizedClients);

    String registrationId = servicesConfiguration.getCrownCourtApi().getRegistrationId();
    Assert.notNull(registrationId, MISSING_REGISTRATION_ID);
    oauthFilter.setDefaultClientRegistrationId(registrationId);

    Resilience4jRetryFilter retryFilter =
        new Resilience4jRetryFilter(retryRegistry, CROWN_COURT_PROCEEDING_API_WEB_CLIENT_NAME);

    return webClientBuilder
        .baseUrl(servicesConfiguration.getCrownCourtApi().getBaseUrl())
        .filters(filters -> configureFilters(filters, oauthFilter, retryFilter))
        .build();
  }

  @Bean(CROWN_COURT_CONTRIBUTIONS_WEB_CLIENT_NAME)
  WebClient crownCourtContributionsWebClient(WebClient.Builder webClientBuilder,
      ServicesConfiguration servicesConfiguration,
      ClientRegistrationRepository clientRegistrations,
      OAuth2AuthorizedClientRepository authorizedClients,
      RetryRegistry retryRegistry) {

    ServletOAuth2AuthorizedClientExchangeFilterFunction oauthFilter =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations,
            authorizedClients);

    String registrationId = servicesConfiguration.getContributionApi().getRegistrationId();
    Assert.notNull(registrationId, MISSING_REGISTRATION_ID);
    oauthFilter.setDefaultClientRegistrationId(registrationId);

    Resilience4jRetryFilter retryFilter =
        new Resilience4jRetryFilter(retryRegistry, CROWN_COURT_CONTRIBUTIONS_WEB_CLIENT_NAME);

    return webClientBuilder
        .baseUrl(servicesConfiguration.getContributionApi().getBaseUrl())
        .filters(filters -> configureFilters(filters, oauthFilter, retryFilter))
        .build();
  }

  @Bean(CRIME_MEANS_ASSESSMENT_WEB_CLIENT_NAME)
  WebClient crimeMeansAssessmentWebClient(WebClient.Builder webClientBuilder,
      ServicesConfiguration servicesConfiguration,
      ClientRegistrationRepository clientRegistrations,
      OAuth2AuthorizedClientRepository authorizedClients,
      RetryRegistry retryRegistry) {

    ServletOAuth2AuthorizedClientExchangeFilterFunction oauthFilter =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations,
            authorizedClients);

    String registrationId = servicesConfiguration.getCmaApi().getRegistrationId();
    Assert.notNull(registrationId, MISSING_REGISTRATION_ID);
    oauthFilter.setDefaultClientRegistrationId(registrationId);

    Resilience4jRetryFilter retryFilter =
        new Resilience4jRetryFilter(retryRegistry, CRIME_MEANS_ASSESSMENT_WEB_CLIENT_NAME);

    return webClientBuilder
        .baseUrl(servicesConfiguration.getCmaApi().getBaseUrl())
        .filters(filters -> configureFilters(filters, oauthFilter, retryFilter))
        .build();
  }

  @Bean(APPLICATION_TRACKING_WEB_CLIENT_NAME)
  WebClient applicationTrackingWebClient(WebClient.Builder webClientBuilder,
      ServicesConfiguration servicesConfiguration,
      ClientRegistrationRepository clientRegistrations,
      OAuth2AuthorizedClientRepository authorizedClients,
      RetryRegistry retryRegistry) {

    ServletOAuth2AuthorizedClientExchangeFilterFunction oauthFilter =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations,
            authorizedClients);

    String registrationId = servicesConfiguration.getCatApi().getRegistrationId();
    Assert.notNull(registrationId, MISSING_REGISTRATION_ID);
    oauthFilter.setDefaultClientRegistrationId(registrationId);

    Resilience4jRetryFilter retryFilter =
        new Resilience4jRetryFilter(retryRegistry, APPLICATION_TRACKING_WEB_CLIENT_NAME);

    return webClientBuilder
        .baseUrl(servicesConfiguration.getCatApi().getBaseUrl())
        .filters(filters -> configureFilters(filters, oauthFilter, retryFilter))
        .build();
  }

  @Bean
  MaatCourtDataApiClient maatCourtDataApiClient(
      @Qualifier("maatCourtDataWebClient") WebClient maatCourtDataWebClient) {
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(maatCourtDataWebClient))
            .build();
    return httpServiceProxyFactory.createClient(MaatCourtDataApiClient.class);
  }

  @Bean
  HardshipApiClient hardshipApiClient(@Qualifier("hardshipWebClient") WebClient hardshipWebClient) {
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(hardshipWebClient))
            .build();
    return httpServiceProxyFactory.createClient(HardshipApiClient.class);
  }

  @Bean
  EvidenceApiClient evidenceApiClient(@Qualifier("evidenceWebClient") WebClient hardshipWebClient) {
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(hardshipWebClient))
            .build();
    return httpServiceProxyFactory.createClient(EvidenceApiClient.class);
  }

  @Bean
  CrownCourtContributionsApiClient crownCourtContributionsApiClient(
      @Qualifier("crownCourtContributionsWebClient") WebClient crownCourtContributionsWebClient) {
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(crownCourtContributionsWebClient))
            .build();
    return httpServiceProxyFactory.createClient(CrownCourtContributionsApiClient.class);
  }

  @Bean
  CrownCourtProceedingApiClient crownCourtProceedingApiClient(
      @Qualifier("crownCourtProceedingWebClient") WebClient crownCourtProceedingWebClient) {
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(crownCourtProceedingWebClient))
            .build();
    return httpServiceProxyFactory.createClient(CrownCourtProceedingApiClient.class);
  }

  @Bean
  CrimeMeansAssessmentApiClient crimeMeansAssessmentApiClient(
      @Qualifier("crimeMeansAssessmentWebClient") WebClient crimeMeansAssessmentWebClient) {
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(crimeMeansAssessmentWebClient))
            .build();
    return httpServiceProxyFactory.createClient(CrimeMeansAssessmentApiClient.class);
  }

  @Bean
  ApplicationTrackingApiClient applicationTrackingApiClient(
      @Qualifier("applicationTrackingWebClient") WebClient applicationTrackingWebClient) {
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(applicationTrackingWebClient))
            .build();
    return httpServiceProxyFactory.createClient(ApplicationTrackingApiClient.class);
  }

  private void configureFilters(List<ExchangeFilterFunction> filters,
      ServletOAuth2AuthorizedClientExchangeFilterFunction oauthFilter,
      ExchangeFilterFunction retryFilter) {
    filters.add(WebClientFilters.logRequestHeaders());
    filters.add(retryFilter);
    filters.add(oauthFilter);
    filters.add(WebClientFilters.errorResponseHandler());
    filters.add(WebClientFilters.handleNotFoundResponse());
    filters.add(WebClientFilters.logResponse());
  }
}