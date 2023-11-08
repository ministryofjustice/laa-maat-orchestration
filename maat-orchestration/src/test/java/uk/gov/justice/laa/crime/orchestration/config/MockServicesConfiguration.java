package uk.gov.justice.laa.crime.orchestration.config;

public class MockServicesConfiguration {

    public static ServicesConfiguration getConfiguration(int port) {

        String host = String.format("http://localhost:%s", port);
        ServicesConfiguration servicesConfiguration = new ServicesConfiguration();

        ServicesConfiguration.HardshipApi hardshipApiConfiguration = new ServicesConfiguration.HardshipApi();
        ServicesConfiguration.ContributionApi cccApiConfiguration = new ServicesConfiguration.ContributionApi();

        ServicesConfiguration.HardshipApi.HardshipEndpoints hardshipEndpoints =
                new ServicesConfiguration.HardshipApi.HardshipEndpoints(
                        "/hardship/{hardshipReviewId}",
                        "/hardship",
                        "/hardship",
                        "/hardship/rollback"
                );

        ServicesConfiguration.ContributionApi.ContributionEndpoints cccEndpoints =
                new ServicesConfiguration.ContributionApi.ContributionEndpoints(
                        "contribution/calculate-contribution",
                        "/contribution/request-transfer"
                );

        hardshipApiConfiguration.setBaseUrl(host);
        hardshipApiConfiguration.setHardshipEndpoints(hardshipEndpoints);

        cccApiConfiguration.setBaseUrl(host);
        cccApiConfiguration.setContributionEndpoints(cccEndpoints);

        servicesConfiguration.setOAuthEnabled(false);
        servicesConfiguration.setHardshipApi(hardshipApiConfiguration);
        servicesConfiguration.setContributionApi(cccApiConfiguration);

        return servicesConfiguration;
    }
}
