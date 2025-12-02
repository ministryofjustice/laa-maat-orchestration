package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.IojAppealService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IojAppealsOrchestrationService {

    private final AssessmentSummaryService assessmentSummaryService;
    private final IojAppealService iojAppealService;
    private final MaatCourtDataService maatCourtDataService;
    private final ContributionService contributionService;
    private final ProceedingsService proceedingsService;
    private final RepOrderService repOrderService;

    public IOJAppealDTO find(int appealId) {
        return iojAppealService.find(appealId);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        RepOrderDTO repOrderDto = repOrderService.getRepOrder(request);

        if (repOrderDto == null) {
            log.error("Could not find rep order for request {}", request);
            throw new MaatOrchestrationException(request.getApplicationDTO());
        }

        iojAppealService.create(request);

        proceedingsService.determineMagsRepDecisionResult(request);
        request.setApplicationDTO(contributionService.calculate(request));
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(), request.getUserDTO(), StoredProcedure.PRE_UPDATE_CC_APPLICATION));

        proceedingsService.updateApplication(request, repOrderDto);

        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(),
                request.getUserDTO(),
                StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE));

        AssessmentSummaryDTO assessmentSummaryDTO = assessmentSummaryService.getSummary(
                request.getApplicationDTO().getAssessmentDTO().getIojAppeal());
        assessmentSummaryService.updateApplication(request.getApplicationDTO(), assessmentSummaryDTO);

        return request.getApplicationDTO();
    }
}
