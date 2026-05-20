package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepOrderService {

    private final MaatCourtDataService maatCourtDataService;

    public RepOrderDTO getRepOrder(WorkflowRequest workflowRequest) {
        int repId = workflowRequest.getApplicationDTO().getRepId().intValue();
        RepOrderDTO repOrderDTO = maatCourtDataService.findRepOrder(repId);

        if (repOrderDTO == null) {
            log.error("Could not find rep order with id: {}", repId);
            throw new MaatOrchestrationException(workflowRequest.getApplicationDTO());
        }

        return repOrderDTO;
    }

    public RepOrderDTO updateRepOrderDateModified(WorkflowRequest workflowRequest, LocalDateTime dateModified) {
        int repId = workflowRequest.getApplicationDTO().getRepId().intValue();
        Map<String, Object> fieldsToUpdate = Map.of("dateModified", dateModified);

        maatCourtDataService.updateRepOrder(repId, fieldsToUpdate);

        return getRepOrder(workflowRequest);
    }

    public RepOrderDTO updateRepOrderAssessmentDateCompleted(
            WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO, LocalDateTime dateCompleted) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        CaseType caseType = CaseType.getFrom(applicationDTO.getCaseDetailsDTO().getCaseType());
        CurrentStatus fullAssessmentStatus = CurrentStatus.getFrom(applicationDTO
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getFull()
                .getAssessmnentStatusDTO()
                .getStatus());

        if (!CaseType.EITHER_WAY.equals(caseType)
                || repOrderDTO.getAssessmentDateCompleted() == null
                        && CurrentStatus.COMPLETE.equals(fullAssessmentStatus)) {
            int repId = applicationDTO.getRepId().intValue();
            Map<String, Object> fieldsToUpdate = Map.of("assessmentDateCompleted", dateCompleted);

            maatCourtDataService.updateRepOrder(repId, fieldsToUpdate);
            return getRepOrder(workflowRequest);
        }

        return repOrderDTO;
    }
}
