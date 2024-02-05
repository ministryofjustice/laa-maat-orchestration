package uk.gov.justice.laa.crime.orchestration.config;

public class MockServicesConfiguration {

    public static ServicesConfiguration getConfiguration(int port) {

        String host = String.format("http://localhost:%s", port);
        ServicesConfiguration servicesConfiguration = new ServicesConfiguration();

        ServicesConfiguration.HardshipApi hardshipApi = new ServicesConfiguration.HardshipApi();
        ServicesConfiguration.ContributionApi contributionApi = new ServicesConfiguration.ContributionApi();
        ServicesConfiguration.CrownCourtApi crownCourtApi = new ServicesConfiguration.CrownCourtApi();
        ServicesConfiguration.CmaApi cmaApi = new ServicesConfiguration.CmaApi();
        ServicesConfiguration.MaatApi maatApi = new ServicesConfiguration.MaatApi();

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
                        "/contribution/check-contribution-rule",
                        "/contribution/summaries"
                );

        ServicesConfiguration.CrownCourtApi.Endpoints crownCourtEndpoints =
                new ServicesConfiguration.CrownCourtApi.Endpoints(
                        "/proceedings",
                        "/proceedings/update-crown-court"
                );

        ServicesConfiguration.CmaApi.Endpoints cmaEndpoints =
                new ServicesConfiguration.CmaApi.Endpoints(
                        "/assessment/means/{financialAssessmentId}/applicantId/{applicantId}",
                        "/assessment/means",
                        "/assessment/means"
                );

        ServicesConfiguration.MaatApi.Endpoints maatEndpoints =
                new ServicesConfiguration.MaatApi.Endpoints(
                        "/assessment/means/execute-stored-procedure",
                        "/assessment/rep-orders/{repId}"
                );

        ServicesConfiguration.MaatApi.HardshipEndpoints maatHardshipEndpoints =
                new ServicesConfiguration.MaatApi.HardshipEndpoints(
                        "/hardship/{hardshipReviewId}"
                );

        ServicesConfiguration.MaatApi.FinancialAssessmentEndpoints financialAssessmentEndpoints =
                new ServicesConfiguration.MaatApi.FinancialAssessmentEndpoints(
                        "/financial-assessments/{financialAssessmentId}"
                );

        hardshipApi.setBaseUrl(host);
        hardshipApi.setEndpoints(hardshipEndpoints);

        contributionApi.setBaseUrl(host);
        contributionApi.setEndpoints(contributionEndpoints);

        crownCourtApi.setBaseUrl(host);
        crownCourtApi.setEndpoints(crownCourtEndpoints);

        cmaApi.setBaseUrl(host);
        cmaApi.setEndpoints(cmaEndpoints);

        maatApi.setBaseUrl(host);
        maatApi.setEndpoints(maatEndpoints);
        maatApi.setHardshipEndpoints(maatHardshipEndpoints);
        maatApi.setFinancialAssessmentEndpoints(financialAssessmentEndpoints);

        servicesConfiguration.setHardshipApi(hardshipApi);
        servicesConfiguration.setContributionApi(contributionApi);
        servicesConfiguration.setCrownCourtApi(crownCourtApi);
        servicesConfiguration.setCmaApi(cmaApi);
        servicesConfiguration.setMaatApi(maatApi);

        return servicesConfiguration;
    }
}
