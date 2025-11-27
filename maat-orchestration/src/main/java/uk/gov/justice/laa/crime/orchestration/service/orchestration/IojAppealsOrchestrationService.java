package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.IojAppealService;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IojAppealsOrchestrationService {

    private final AssessmentSummaryService assessmentSummaryService;

    private final IojAppealService iojAppealService;

    public IOJAppealDTO find(int appealId) {
        return iojAppealService.find(appealId);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        iojAppealService.create(request);

        // Call CCP determine-mags-rep-decision endpoint
        // Call CCC to calculate contributions - maybe call existing svc inside this repo?
        // Call the pre_update_cc_application stored procedure
        // Call CCP updateApplication crown court endpoint
        // Invoke matrix and correspondence SPs crown_court.xx_process_activity_and_get_correspondence

        AssessmentSummaryDTO assessmentSummaryDTO = assessmentSummaryService.getSummary(
                request.getApplicationDTO().getAssessmentDTO().getIojAppeal());
        assessmentSummaryService.updateApplication(request.getApplicationDTO(), assessmentSummaryDTO);

        return request.getApplicationDTO();
    }
}
