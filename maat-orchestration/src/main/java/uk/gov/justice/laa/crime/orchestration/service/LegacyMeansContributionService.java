package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.enums.StoredProcedure;

@Slf4j
@Service
@RequiredArgsConstructor
public class LegacyMeansContributionService implements MeansContributionService {

    private final MaatCourtDataService maatCourtDataService;

    @Override
    public void processContributions(WorkflowRequest request) {
        // call post_processing_part1 and map the application
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(),
                request.getUserDTO(),
                StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1)
        );
    }
}
