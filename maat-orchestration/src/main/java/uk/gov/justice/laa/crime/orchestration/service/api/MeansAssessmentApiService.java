package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiRollbackMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.orchestration.client.CrimeMeansAssessmentApiClient;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeansAssessmentApiService {

    private final CrimeMeansAssessmentApiClient cmaApiClient;

    public ApiGetMeansAssessmentResponse find(Integer assessmentId) {
        return cmaApiClient.findMeansAssessment(assessmentId);
    }

    public ApiMeansAssessmentResponse create(ApiCreateMeansAssessmentRequest request) {
        return cmaApiClient.createMeansAssessment(request);
    }

    public ApiMeansAssessmentResponse update(ApiUpdateMeansAssessmentRequest request) {
        return cmaApiClient.updateMeansAssessment(request);
    }

    public ApiRollbackMeansAssessmentResponse rollback(Integer financialAssessmentId) {
        return cmaApiClient.rollback(financialAssessmentId);
    }
}
