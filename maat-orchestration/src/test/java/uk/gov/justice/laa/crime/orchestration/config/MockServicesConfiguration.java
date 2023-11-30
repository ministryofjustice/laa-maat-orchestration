package uk.gov.justice.laa.crime.orchestration.config;

public class MockServicesConfiguration {

    public static ServicesConfiguration getConfiguration(int port) {

        String host = String.format("http://localhost:%s", port);
        ServicesConfiguration servicesConfiguration = new ServicesConfiguration();

        ServicesConfiguration.HardshipApi hardshipApi = new ServicesConfiguration.HardshipApi();
        ServicesConfiguration.ContributionApi contributionApi = new ServicesConfiguration.ContributionApi();
        ServicesConfiguration.CrownCourtApi crownCourtApi = new ServicesConfiguration.CrownCourtApi();

        ServicesConfiguration.HardshipApi.Endpoints hardshipEndpoints =
                new ServicesConfiguration.HardshipApi.Endpoints(
                        "/hardship/{hardshipReviewId}",
                        "/hardship",
                        "/hardship",
                        "/hardship/rollback"
                );

        ServicesConfiguration.ContributionApi.Endpoints contributionEndpoints =
                new ServicesConfiguration.ContributionApi.Endpoints(
                        "contribution/calculate-contribution",
                        "/contribution/request-transfer",
                        "/contribution/check-contribution-rule"
                );

        ServicesConfiguration.CrownCourtApi.Endpoints crownCourtEndpoints =
                new ServicesConfiguration.CrownCourtApi.Endpoints(
                        "/proceedings",
                        "/proceedings/update-crown-court"
                );

        hardshipApi.setBaseUrl(host);
        hardshipApi.setHardshipEndpoints(hardshipEndpoints);

        contributionApi.setBaseUrl(host);
        contributionApi.setContributionEndpoints(contributionEndpoints);

        crownCourtApi.setBaseUrl(host);
        crownCourtApi.setCrownCourtEndpoints(crownCourtEndpoints);

        servicesConfiguration.setHardshipApi(hardshipApi);
        servicesConfiguration.setContributionApi(contributionApi);
        servicesConfiguration.setCrownCourtApi(crownCourtApi);

        return servicesConfiguration;
    }
}
