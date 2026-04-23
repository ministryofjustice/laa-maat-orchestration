package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.PassportAssessmentService;

import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassportAssessmentOrchestrationService {

    private final PassportAssessmentService passportAssessmentService;
    private final PassportAssessmentMapper passportAssessmentMapper;
    private final RepOrderService repOrderService;
    private final WorkflowPreProcessorService workflowPreProcessorService;

    public PassportedDTO find(int id) {
        return passportAssessmentService.find(id);
    }

    public ApplicationDTO create(WorkflowRequest workflowRequest) {
        RepOrderDTO repOrderDTO = repOrderService.getRepOrder(workflowRequest);
        if (repOrderDTO == null) {
            log.error("Could not find rep order for request {}", workflowRequest);
            throw new MaatOrchestrationException(workflowRequest.getApplicationDTO());
        }
        UserActionDTO userActionDTO = passportAssessmentMapper.getUserActionDTO(workflowRequest);

        workflowPreProcessorService.preProcessPassportRequest(workflowRequest, repOrderDTO, userActionDTO);
    }
}
