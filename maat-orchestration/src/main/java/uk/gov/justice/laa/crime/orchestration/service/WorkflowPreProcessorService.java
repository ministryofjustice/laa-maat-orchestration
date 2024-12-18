package uk.gov.justice.laa.crime.orchestration.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CaseDetailDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.RepStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowPreProcessorService {
    private final ContributionService contributionService;
    private final MaatCourtDataService maatCourtDataService;
    private final ValidationService validationService;

    public WorkflowRequest preProcessRequest(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO, UserActionDTO userActionDTO) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        CaseDetailDTO caseDetailsDTO = applicationDTO.getCaseDetailsDTO();
        RepStatusDTO statusDTO = applicationDTO.getStatusDTO();

        validationService.validate(workflowRequest, repOrderDTO);

        UserSummaryDTO userSummaryDTO = Optional.ofNullable(userActionDTO.getUsername()).map(maatCourtDataService::getUserSummary).orElse(null);
        validationService.isUserActionValid(userActionDTO, userSummaryDTO);

        if (hasApplicationStatusChanged(repOrderDTO, caseDetailsDTO, statusDTO)) {
            workflowRequest.setApplicationDTO(contributionService.calculate(workflowRequest));
        }

        return workflowRequest;
    }

    private boolean hasApplicationStatusChanged(RepOrderDTO repOrderDTO, CaseDetailDTO caseDetailsDTO, RepStatusDTO repStatusDTO) {
        String caseType = Optional.ofNullable(caseDetailsDTO).map(CaseDetailDTO::getCaseType).orElse(null);
        String status = Optional.ofNullable(repStatusDTO).map(RepStatusDTO::getStatus).orElse(null);

        return CaseType.INDICTABLE.getCaseType().equals(caseType) && repOrderDTO != null
                && repOrderDTO.getRorsStatus() != null
                && !repOrderDTO.getRorsStatus().equalsIgnoreCase(status);
    }

    public void preProcessEvidenceRequest(UserActionDTO userActionDTO) {
        UserSummaryDTO userSummaryDTO = Optional.ofNullable(userActionDTO.getUsername()).map(maatCourtDataService::getUserSummary).orElse(null);
        validationService.isUserActionValid(userActionDTO, userSummaryDTO);
    }
}
