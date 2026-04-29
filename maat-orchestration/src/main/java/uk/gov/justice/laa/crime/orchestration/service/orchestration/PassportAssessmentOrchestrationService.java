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
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassportAssessmentOrchestrationService {

    private final PassportAssessmentService passportAssessmentService;
    private final PassportAssessmentMapper passportAssessmentMapper;
    private final RepOrderService repOrderService;
    private final ProceedingsService proceedingsService;
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

        passportAssessmentService.create(workflowRequest);

        // IF APP STATUS COMPLETE BUT WE CAN SAFELY ASSUME ALWAYS SUCH I BELIEVE
        // manage passport evidence - this doable??? no tickets for it and current create evidence seems geared for income evidence
        // check income evidence available - this is done in MAAT using PP flag for means assessment, do the same for passported???
        // determine mags rep decision
        proceedingsService.determineMagsRepDecision(workflowRequest);
        // check crown court actions - check crown repord - cap eq check - calc contribs - cc avail
        // update cc application
        // matrix process activity
        // get application correspondence
        // get assessments summary
    }
}
