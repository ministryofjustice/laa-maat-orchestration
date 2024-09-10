package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.enums.StoredProcedure;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModernMeansContributionService implements MeansContributionService {

    private final ContributionService contributionService;
    private final MaatCourtDataService maatCourtDataService;

    @Override
    public void processContributions(WorkflowRequest request) {
        log.info("Before calling ASSESSMENT_POST_PROCESSING_PART_1_C3");
        // call post_processing_part_1_c3 and map the application
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(),
                request.getUserDTO(),
                StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1_C3)
        );
        // call pre_update_cc_application with the calculated contribution and map the application
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                contributionService.calculate(request),
                request.getUserDTO(),
                StoredProcedure.PRE_UPDATE_CC_APPLICATION)
        );
        log.info("MeansAssessmentOrchestrationService.PRE_UPDATE_CC_APPLICATION = " + request);
    }
}
