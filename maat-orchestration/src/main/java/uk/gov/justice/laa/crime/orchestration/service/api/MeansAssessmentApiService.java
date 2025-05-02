package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.meansassessment.*;
import uk.gov.justice.laa.crime.orchestration.client.CrimeMeansAssessmentApiClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansAssessmentApiService {

    private final CrimeMeansAssessmentApiClient cmaApiClient;
    private static final String RESPONSE_STRING = "Response from Means Assessment Service: {}";
    private static final String REQUEST_STRING = "Request to Means Assessment Service: {}";

    public ApiGetMeansAssessmentResponse find(Integer assessmentId) {
        log.info(REQUEST_STRING, assessmentId);
        ApiGetMeansAssessmentResponse response = cmaApiClient.findMeansAssessment(assessmentId);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public ApiMeansAssessmentResponse create(ApiCreateMeansAssessmentRequest request) {
        log.debug(REQUEST_STRING, request);
        ApiMeansAssessmentResponse response = cmaApiClient.createMeansAssessment(request);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public ApiMeansAssessmentResponse update(ApiUpdateMeansAssessmentRequest request) {
        log.debug(REQUEST_STRING, request);
        ApiMeansAssessmentResponse response = cmaApiClient.updateMeansAssessment(request);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public ApiRollbackMeansAssessmentResponse rollback(Long financialAssessmentId) {
        log.info(REQUEST_STRING, financialAssessmentId);
        ApiRollbackMeansAssessmentResponse response = cmaApiClient.rollback(financialAssessmentId);
        log.info(RESPONSE_STRING, response);
        return response;
    }
}
