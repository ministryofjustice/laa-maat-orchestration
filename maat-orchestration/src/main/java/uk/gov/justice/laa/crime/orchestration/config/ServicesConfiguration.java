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
    private ContributionApi contributionApi;

    @NotNull
    private CrownCourtApi crownCourtApi;

    @NotNull
    private CmaApi cmaApi;

    @NotNull
    private MaatApi maatApi;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HardshipApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private Endpoints endpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Endpoints {
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
        private Endpoints endpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Endpoints {

            @NotNull
            private String calculateContributionUrl;

            @NotNull
            private String requestTransferUrl;

            @NotNull
            private String checkContributionRuleUrl;

            @NotNull
            private String contributionSummariesUrl;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrownCourtApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private Endpoints endpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Endpoints {

            @NotNull
            private String updateApplicationUrl;

            @NotNull
            private String updateCrownCourtUrl;

        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CmaApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private Endpoints endpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Endpoints {
            @NotNull
            private String findUrl;

            @NotNull
            private String createUrl;

            @NotNull
            private String updateUrl;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaatApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private Endpoints endpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Endpoints {
            @NotNull
            private String callStoredProcUrl;
            @NotNull
            private String repOrderUrl;

        }
    }
}