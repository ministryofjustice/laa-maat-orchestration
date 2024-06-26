package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiRollbackMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiUpdateMeansAssessmentRequest;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansAssessmentApiService {

    @Qualifier("cmaApiClient")
    private final RestAPIClient cmaApiClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from CMA Service: %s";

    public ApiGetMeansAssessmentResponse find(Integer assessmentId) {
        ApiGetMeansAssessmentResponse response = cmaApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getCmaApi().getEndpoints().getFindUrl(),
                assessmentId
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public ApiMeansAssessmentResponse create(ApiCreateMeansAssessmentRequest request) {
        ApiMeansAssessmentResponse response = cmaApiClient.post(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getCmaApi().getEndpoints().getCreateUrl(),
                Collections.emptyMap()
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public ApiMeansAssessmentResponse update(ApiUpdateMeansAssessmentRequest request) {
        ApiMeansAssessmentResponse response = cmaApiClient.put(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getCmaApi().getEndpoints().getUpdateUrl(),
                Collections.emptyMap()
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public ApiRollbackMeansAssessmentResponse rollback(Long financialAssessmentId) {
        ApiRollbackMeansAssessmentResponse response = cmaApiClient.patch(
                "",
                new ParameterizedTypeReference<>() {
                },
                configuration.getCmaApi().getEndpoints().getRollbackUrl(),
                Collections.emptyMap(),
                financialAssessmentId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }
}
