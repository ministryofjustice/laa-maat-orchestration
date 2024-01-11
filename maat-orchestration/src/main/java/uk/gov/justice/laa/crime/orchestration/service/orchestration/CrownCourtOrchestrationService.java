package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CrownCourtOverviewDTO;
import uk.gov.justice.laa.crime.orchestration.enums.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrownCourtOrchestrationService {

    private final ProceedingsService proceedingsService;
    private final MaatCourtDataService maatCourtDataService;

    public ApplicationDTO update(WorkflowRequest request) {
        ApplicationDTO application = request.getApplicationDTO();

        application = maatCourtDataService.invokeStoredProcedure(
                application, request.getUserDTO(), StoredProcedure.UPDATE_DBMS_TRANSACTION_ID
        );

        application = maatCourtDataService.invokeStoredProcedure(
                application, request.getUserDTO(), StoredProcedure.PRE_UPDATE_CHECKS
        );

        proceedingsService.updateCrownCourt(request);

        if (hasNewOutcome(application)) {
            application = maatCourtDataService.invokeStoredProcedure(
                    application, request.getUserDTO(), StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE
            );
        }

        application = maatCourtDataService.invokeStoredProcedure(
                application, request.getUserDTO(), StoredProcedure.UPDATE_CC_APPLICANT_AND_APPLICATION
        );

        application.setTransactionId(null);

        return application;
    }

    private boolean hasNewOutcome(ApplicationDTO application) {
        CrownCourtOverviewDTO crownCourtOverview = application.getCrownCourtOverviewDTO();
        if (null != crownCourtOverview.getCrownCourtSummaryDTO()
                && null != crownCourtOverview.getCrownCourtSummaryDTO().getOutcomeDTOs()) {
            return crownCourtOverview.getCrownCourtSummaryDTO().getOutcomeDTOs().stream()
                    .anyMatch(outcomeDTO -> null == outcomeDTO.getDateSet());
        }
        return false;
    }
}
