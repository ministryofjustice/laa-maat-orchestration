package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiRollbackMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.orchestration.client.CrimeMeansAssessmentApiClient;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansAssessmentApiService {

    private final CrimeMeansAssessmentApiClient cmaApiClient;
    private static final String REQUEST_STRING = "Request to Means Assessment Service: {}";

    public ApiGetMeansAssessmentResponse find(Integer assessmentId) {
        log.info(REQUEST_STRING, assessmentId);
        return cmaApiClient.findMeansAssessment(assessmentId);
    }

    public ApiMeansAssessmentResponse create(ApiCreateMeansAssessmentRequest request) {
        log.debug(REQUEST_STRING, request);
        return cmaApiClient.createMeansAssessment(request);
    }

    public ApiMeansAssessmentResponse update(ApiUpdateMeansAssessmentRequest request) {
        log.debug(REQUEST_STRING, request);
        return cmaApiClient.updateMeansAssessment(request);
    }

    public ApiRollbackMeansAssessmentResponse rollback(Integer financialAssessmentId) {
        log.info(REQUEST_STRING, financialAssessmentId);
        return cmaApiClient.rollback(financialAssessmentId);
    }
}
