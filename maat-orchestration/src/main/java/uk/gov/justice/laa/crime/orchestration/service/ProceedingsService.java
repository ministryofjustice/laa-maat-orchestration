package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateCrownCourtRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiDetermineMagsRepDecisionResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FullAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.InitialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ProceedingsMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.ProceedingsApiService;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProceedingsService {

    private final ProceedingsMapper proceedingsMapper;
    private final ProceedingsApiService proceedingsApiService;
    private final FeatureDecisionService featureDecisionService;
    private final CCLFUpdateService cclfUpdateService;

    public void updateApplication(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        ApiUpdateApplicationRequest apiUpdateApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateApplicationRequest(request.getApplicationDTO(),
                                                                            request.getUserDTO()
                );
        ApiUpdateApplicationResponse updateApplicationResponse =
                proceedingsApiService.updateApplication(apiUpdateApplicationRequest);
        proceedingsMapper.updateApplicationResponseToApplicationDto(
                updateApplicationResponse, request.getApplicationDTO()
        );
        updateCCLF(request, repOrderDTO);
    }

    public ApplicationDTO updateCrownCourt(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        ApiUpdateCrownCourtRequest apiUpdateCrownCourtRequest =
                proceedingsMapper.workflowRequestToUpdateCrownCourtRequest(request.getApplicationDTO(),
                                                                           request.getUserDTO()
                );
        ApiUpdateCrownCourtOutcomeResponse updateCrownCourtResponse =
                proceedingsApiService.updateCrownCourt(apiUpdateCrownCourtRequest);

        request.setApplicationDTO(
                proceedingsMapper.updateCrownCourtResponseToApplicationDto(
                        updateCrownCourtResponse, request.getApplicationDTO()
                )
        );
        updateCCLF(request, repOrderDTO);
        return request.getApplicationDTO();
    }

    public void updateCCLF(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        if (featureDecisionService.isMaatPostAssessmentProcessingEnabled(request)) {
            cclfUpdateService.updateSendToCCLF(request, repOrderDTO);
        }
    }

    public ApiDetermineMagsRepDecisionResponse determineMagsRepDecision(WorkflowRequest request) {

        ApiDetermineMagsRepDecisionResponse repDecisionResponse = null;

        if (canInvokeMagsRepDecision(request.getApplicationDTO())) {

            repDecisionResponse = proceedingsApiService.determineMagsRepDecision(
                    proceedingsMapper.buildDetermineMagsRepDecision(request.getApplicationDTO(), request.getUserDTO()));
            proceedingsMapper.mapDecisionResultToApplicationDTO(request.getApplicationDTO(), repDecisionResponse);

        }

        return repDecisionResponse;
    }

    boolean canInvokeMagsRepDecision(ApplicationDTO application) {

        if (Set.of(CaseType.INDICTABLE.getCaseType(), CaseType.SUMMARY_ONLY.getCaseType(), CaseType.EITHER_WAY.getCaseType())
                .contains(application.getCaseDetailsDTO().getCaseType())){

            FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
            InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
            FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();

            if ((initialAssessmentDTO.getAssessmnentStatusDTO().getStatus().equals(CurrentStatus.COMPLETE.getStatus())
                    && initialAssessmentDTO.getResult().equals(AssessmentResult.FULL.getResult())) ||
                    fullAssessmentDTO.getAssessmnentStatusDTO().getStatus().equals(CurrentStatus.COMPLETE.getStatus()) ) {
                return true;
            }

        }
        return false;
    }
}
