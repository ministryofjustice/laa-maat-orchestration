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
        ServicesConfiguration.CATApi catApi = new ServicesConfiguration.CATApi();
        ServicesConfiguration.EvidenceApi evidenceApi = new ServicesConfiguration.EvidenceApi();

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
                        "/contribution/check-contribution-rule",
                        "/contribution/summaries"
                );

        ServicesConfiguration.CrownCourtApi.Endpoints crownCourtEndpoints =
                new ServicesConfiguration.CrownCourtApi.Endpoints(
                        "/proceedings",
                        "/proceedings/update-crown-court",
                        "/proceedings/determine-mags-rep-decision"
                );

        ServicesConfiguration.CmaApi.Endpoints cmaEndpoints =
                new ServicesConfiguration.CmaApi.Endpoints(
                        "/assessment/means/{financialAssessmentId}",
                        "/assessment/means",
                        "/assessment/means",
                        "/assessment/means/rollback/{financialAssessmentId}"
                );

        ServicesConfiguration.MaatApi.Endpoints maatEndpoints =
                new ServicesConfiguration.MaatApi.Endpoints(
                        "/assessment/means/execute-stored-procedure",
                        "/assessment/rep-orders/{repId}",
                        "/application/applicant/update-cclf",
                        "/assessment/financial-assessments/{financialAssessmentId}"
                );

        ServicesConfiguration.MaatApi.UserEndpoints userEndpoints =
                new ServicesConfiguration.MaatApi.UserEndpoints(
                        "/api/internal/v1/users/summary/{username}"
                );

        ServicesConfiguration.CATApi.Endpoints catEndpoints =  new ServicesConfiguration.CATApi.Endpoints(
                "/api/internal/v1/application-tracking-output-result"
        );

        ServicesConfiguration.EvidenceApi.Endpoints evidenceEndpoints = new ServicesConfiguration.EvidenceApi.Endpoints(
                "/api/internal/v1/evidence"
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
        maatApi.setUserEndpoints(userEndpoints);

        catApi.setEndpoints(catEndpoints);

        evidenceApi.setBaseUrl(host);
        evidenceApi.setEndpoints(evidenceEndpoints);

        servicesConfiguration.setHardshipApi(hardshipApi);
        servicesConfiguration.setContributionApi(contributionApi);
        servicesConfiguration.setCrownCourtApi(crownCourtApi);
        servicesConfiguration.setCmaApi(cmaApi);
        servicesConfiguration.setMaatApi(maatApi);
        servicesConfiguration.setCatApi(catApi);
        servicesConfiguration.setEvidenceApi(evidenceApi);

        return servicesConfiguration;
    }
}
