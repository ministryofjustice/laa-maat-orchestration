package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CaseDetailDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.RepStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowPreProcessorService {

    private final ContributionService contributionService;

    public WorkflowRequest  preProcessRequest(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        CaseDetailDTO caseDetailsDTO = applicationDTO.getCaseDetailsDTO();
        RepStatusDTO statusDTO = applicationDTO.getStatusDTO();
        String caseType = caseDetailsDTO != null ? caseDetailsDTO.getCaseType() : null;
        String status = statusDTO != null ? statusDTO.getStatus() : null;
        if(hasApplicationStatusChanged(repOrderDTO, caseType, status)) {
            workflowRequest.setApplicationDTO(contributionService.calculate(workflowRequest));
        }
        return workflowRequest;
    }

    private boolean hasApplicationStatusChanged(RepOrderDTO repOrderDTO, String caseType, String status) {
        return CaseType.INDICTABLE.getCaseType().equals(caseType) && repOrderDTO != null
                && repOrderDTO.getRorsStatus() != null
                && !repOrderDTO.getRorsStatus().equalsIgnoreCase(status);
    }

}
