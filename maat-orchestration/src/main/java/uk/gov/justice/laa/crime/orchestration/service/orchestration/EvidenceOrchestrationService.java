package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.UserMapper;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.IncomeEvidenceService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EvidenceOrchestrationService {

    private final IncomeEvidenceService incomeEvidenceService;
    private final RepOrderService repOrderService;
    private final UserMapper userMapper;
    private final WorkflowPreProcessorService workflowPreProcessorService;
    private final ContributionService contributionService;

    public ApplicationDTO updateIncomeEvidence(WorkflowRequest workflowRequest) {

        RepOrderDTO repOrderDTO = repOrderService.getRepOrder(workflowRequest);
        preProcessRequest(workflowRequest, repOrderDTO);

        ApplicationDTO applicationDTO = incomeEvidenceService.updateEvidence(workflowRequest, repOrderDTO);

        boolean isUpliftChanged = hasUpliftChanged(applicationDTO, repOrderDTO);
        if (isUpliftChanged) {
            workflowRequest.setApplicationDTO(applicationDTO);
            return contributionService.calculate(workflowRequest);
        } else {
            return applicationDTO;
        }
    }

    private static boolean hasUpliftChanged(ApplicationDTO applicationDTO, RepOrderDTO repOrderDTO) {
        LocalDateTime defaultDateTime = LocalDateTime.of(9999, 12, 31, 0, 0, 0);
        Date defaultDate = DateUtil.toDate(defaultDateTime);
        var incomeEvidenceSummaryDTO = applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence();
        Date upliftAppliedDate = Objects.requireNonNullElse(incomeEvidenceSummaryDTO.getUpliftAppliedDate(), defaultDate);
        Date upliftRemovedDate = Objects.requireNonNullElse(incomeEvidenceSummaryDTO.getUpliftRemovedDate(), defaultDate);

        Integer financialAssessmentId = applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getId().intValue();
        FinancialAssessmentDTO financialAssessmentDTO = repOrderDTO.getFinancialAssessments()
                .stream()
                .filter(assessment -> assessment.getId().equals(financialAssessmentId))
                .findFirst().orElse(FinancialAssessmentDTO.builder().build());
        Date oldUpliftAppliedDate = DateUtil
                .toDate(Objects.requireNonNullElse(financialAssessmentDTO.getIncomeUpliftApplyDate(), defaultDateTime));
        Date oldUpliftRemovedDate = DateUtil
                .toDate(Objects.requireNonNullElse(financialAssessmentDTO.getIncomeUpliftRemoveDate(), defaultDateTime));

        return !upliftAppliedDate.equals(oldUpliftAppliedDate)
                || !upliftRemovedDate.equals(oldUpliftRemovedDate);
    }

    private void preProcessRequest(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        UserActionDTO userActionDTO = userMapper.getUserActionDTO(request, Action.MANAGE_INCOME_EVIDENCE, null);
        boolean isUpliftChanged = hasUpliftChanged(request.getApplicationDTO(), repOrderDTO);
        workflowPreProcessorService.preProcessEvidenceRequest(userActionDTO, isUpliftChanged);
    }
}
