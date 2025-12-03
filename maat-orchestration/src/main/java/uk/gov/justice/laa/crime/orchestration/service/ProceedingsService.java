package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiDetermineMagsRepDecisionRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateCrownCourtRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiDetermineMagsRepDecisionResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ProceedingsMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.ProceedingsApiService;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProceedingsService {

    private final ProceedingsMapper proceedingsMapper;
    private final ProceedingsApiService proceedingsApiService;
    private final FeatureDecisionService featureDecisionService;
    private final CCLFUpdateService cclfUpdateService;

    public ApplicationDTO determineMagsRepDecision(WorkflowRequest workflowRequest) {
        ApiDetermineMagsRepDecisionRequest apiDetermineMagsRepDecisionRequest =
                proceedingsMapper.workflowRequestToDetermineMagsRepDecisionRequest(workflowRequest);
        ApiDetermineMagsRepDecisionResponse determineMagsRepDecisionResponse =
                proceedingsApiService.determineMagsRepDecision(apiDetermineMagsRepDecisionRequest);

        /*
        If CCP validation on the case type and assessment status fails then it will return null,
        so do not update anything in this case in line with the old stored procedure.
         */
        if (determineMagsRepDecisionResponse.getDecisionResult() != null) {
            workflowRequest.setApplicationDTO(proceedingsMapper.determineMagsRepDecisionResponseToApplicationDto(
                    determineMagsRepDecisionResponse, workflowRequest.getApplicationDTO()));
        }

        return workflowRequest.getApplicationDTO();
    }

    public void updateApplication(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        ApiUpdateApplicationRequest apiUpdateApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateApplicationRequest(
                        request.getApplicationDTO(), request.getUserDTO());
        ApiUpdateApplicationResponse updateApplicationResponse =
                proceedingsApiService.updateApplication(apiUpdateApplicationRequest);
        proceedingsMapper.updateApplicationResponseToApplicationDto(
                updateApplicationResponse, request.getApplicationDTO());
        updateCCLF(request, repOrderDTO);
    }

    public ApplicationDTO updateCrownCourt(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        ApiUpdateCrownCourtRequest apiUpdateCrownCourtRequest =
                proceedingsMapper.workflowRequestToUpdateCrownCourtRequest(
                        request.getApplicationDTO(), request.getUserDTO());
        ApiUpdateCrownCourtOutcomeResponse updateCrownCourtResponse =
                proceedingsApiService.updateCrownCourt(apiUpdateCrownCourtRequest);

        request.setApplicationDTO(proceedingsMapper.updateCrownCourtResponseToApplicationDto(
                updateCrownCourtResponse, request.getApplicationDTO()));
        updateCCLF(request, repOrderDTO);
        return request.getApplicationDTO();
    }

    public void updateCCLF(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        if (featureDecisionService.isMaatPostAssessmentProcessingEnabled(request)) {
            cclfUpdateService.updateSendToCCLF(request, repOrderDTO);
        }
    }
}
