package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.*;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.MeansAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.MeansAssessmentApiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansAssessmentService {

    private final MeansAssessmentApiService cmaApiService;
    private final MeansAssessmentMapper meansAssessmentMapper;

    public FinancialAssessmentDTO find(int assessmentId, int applicantId) {
        ApiGetMeansAssessmentResponse apiResponse = cmaApiService.find(assessmentId);
        return meansAssessmentMapper.getMeansAssessmentResponseToFinancialAssessmentDto(apiResponse, applicantId);
    }

    public void create(WorkflowRequest request) {
        ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest =
                meansAssessmentMapper.workflowRequestToCreateAssessmentRequest(request);
        ApiMeansAssessmentResponse apiMeansAssessmentResponse =
                cmaApiService.create(apiCreateMeansAssessmentRequest);
        meansAssessmentMapper.meansAssessmentResponseToApplicationDto(
                apiMeansAssessmentResponse, request.getApplicationDTO()
        );
    }

    public void update(WorkflowRequest request) {
        ApiUpdateMeansAssessmentRequest apiUpdateMeansAssessmentRequest =
                meansAssessmentMapper.workflowRequestToUpdateAssessmentRequest(request);
        ApiMeansAssessmentResponse apiMeansAssessmentResponse =
                cmaApiService.update(apiUpdateMeansAssessmentRequest);
        meansAssessmentMapper.meansAssessmentResponseToApplicationDto(
                apiMeansAssessmentResponse, request.getApplicationDTO()
        );
    }

    public void rollback(WorkflowRequest request) {
        FinancialAssessmentDTO financialAssessmentDTO = request.getApplicationDTO().getAssessmentDTO()
                .getFinancialAssessmentDTO();
        if (financialAssessmentDTO.getId() != null) {
            ApiRollbackMeansAssessmentResponse apiRollbackMeansAssessmentResponse =
                    cmaApiService.rollback(financialAssessmentDTO.getId());
            meansAssessmentMapper.apiRollbackMeansAssessmentResponseToApplicationDto(
                    apiRollbackMeansAssessmentResponse, request.getApplicationDTO());
        }
    }
}
