package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.UserMapper;
import uk.gov.justice.laa.crime.orchestration.service.IncomeEvidenceService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;

@Service
@RequiredArgsConstructor
public class EvidenceOrchestrationService {

    private final IncomeEvidenceService incomeEvidenceService;
    private final RepOrderService repOrderService;
    private final UserMapper userMapper;
    private final WorkflowPreProcessorService workflowPreProcessorService;

    public ApplicationDTO updateIncomeEvidence(WorkflowRequest workflowRequest) {

        preProcessRequest(workflowRequest);

        RepOrderDTO repOrderDTO = repOrderService.getRepOrder(workflowRequest);
        return incomeEvidenceService.updateEvidence(workflowRequest, repOrderDTO);
    }

    private void preProcessRequest(WorkflowRequest request) {
        UserActionDTO userActionDTO = userMapper.getUserActionDTO(request, Action.MANAGE_INCOME_EVIDENCE, null);
        workflowPreProcessorService.preProcessEvidenceRequest(userActionDTO);
    }
}
