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

    @NotNull
    private CATApi catApi;

    @NotNull
    private EvidenceApi evidenceApi;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HardshipApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private String registrationId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContributionApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private String registrationId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrownCourtApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private String registrationId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CmaApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private String registrationId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaatApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private String registrationId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CATApi {

        @NotNull
        private String baseUrl;

        @NotNull
        private String registrationId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EvidenceApi {

        @NotNull
        private String baseUrl;

        @NotNull
        private String registrationId;
    }
}
