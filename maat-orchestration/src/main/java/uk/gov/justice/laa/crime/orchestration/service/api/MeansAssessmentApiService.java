package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.meansassessment.*;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansAssessmentApiService {

    @Qualifier("cmaApiClient")
    private final RestAPIClient cmaApiClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from Means Assessment Service: {}";
    private static final String REQUEST_STRING = "Request to Means Assessment Service: {}";

    public ApiGetMeansAssessmentResponse find(Integer assessmentId) {
        log.info(REQUEST_STRING, assessmentId);
        ApiGetMeansAssessmentResponse response = cmaApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getCmaApi().getEndpoints().getFindUrl(),
                assessmentId
        );

        log.info(RESPONSE_STRING, response);
        return response;
    }

    public ApiMeansAssessmentResponse create(ApiCreateMeansAssessmentRequest request) {
        log.info(REQUEST_STRING, request);
        ApiMeansAssessmentResponse response = cmaApiClient.post(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getCmaApi().getEndpoints().getCreateUrl(),
                Collections.emptyMap()
        );

        log.info(RESPONSE_STRING, response);
        return response;
    }

    public ApiMeansAssessmentResponse update(ApiUpdateMeansAssessmentRequest request) {
        log.info(REQUEST_STRING, request);
        ApiMeansAssessmentResponse response = cmaApiClient.put(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getCmaApi().getEndpoints().getUpdateUrl(),
                Collections.emptyMap()
        );

        log.info(RESPONSE_STRING, response);
        return response;
    }

    public ApiRollbackMeansAssessmentResponse rollback(Long financialAssessmentId) {
        log.info(REQUEST_STRING, financialAssessmentId);
        ApiRollbackMeansAssessmentResponse response = cmaApiClient.patch(
                "",
                new ParameterizedTypeReference<>() {
                },
                configuration.getCmaApi().getEndpoints().getRollbackUrl(),
                Collections.emptyMap(),
                financialAssessmentId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }
}
