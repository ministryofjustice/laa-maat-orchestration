package uk.gov.justice.laa.crime.orchestration.config;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "services")
public class ServicesConfiguration {

    @NotNull
    private HardshipApi hardshipApi;

    @NotNull
    private boolean oAuthEnabled;

    @NotNull
    private ContributionApi contributionApi;

    @NotNull
    private CrownCourtApi crownCourtApi;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HardshipApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private HardshipEndpoints hardshipEndpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class HardshipEndpoints {
            @NotNull
            private String findUrl;

            @NotNull
            private String createUrl;

            @NotNull
            private String updateUrl;

            @NotNull
            private String rollbackUrl;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContributionApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private ContributionEndpoints contributionEndpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ContributionEndpoints {

            @NotNull
            private String calculateContributionUrl;

            @NotNull
            private String requestTransferUrl;

            @NotNull
            private String checkContributionRule;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrownCourtApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private CrownCourtEndpoints crownCourtEndpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CrownCourtEndpoints {

            @NotNull
            private String updateUrl;

        }
    }
}