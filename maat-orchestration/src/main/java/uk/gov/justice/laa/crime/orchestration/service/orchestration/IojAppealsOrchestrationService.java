package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.IojAppealMapper;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationTrackingDataService;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.IojAppealService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IojAppealsOrchestrationService {

    private final AssessmentSummaryService assessmentSummaryService;
    private final IojAppealService iojAppealService;
    private final MaatCourtDataService maatCourtDataService;
    private final ContributionService contributionService;
    private final IojAppealMapper iojAppealMapper;
    private final ProceedingsService proceedingsService;
    private final RepOrderService repOrderService;
    private final WorkflowPreProcessorService workflowPreProcessorService;
    private final ApplicationTrackingMapper applicationTrackingMapper;
    private final ApplicationTrackingDataService applicationTrackingDataService;

    public IOJAppealDTO find(int appealId) {
        return iojAppealService.find(appealId);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        RepOrderDTO repOrderDto = repOrderService.getRepOrder(request);

        if (repOrderDto == null) {
            log.error("Could not find rep order for request {}", request);
            throw new MaatOrchestrationException(request.getApplicationDTO());
        }

        UserActionDTO userActionDTO = iojAppealMapper.getUserActionDTO(request);
        workflowPreProcessorService.preProcessRequest(request, repOrderDto, userActionDTO);

        iojAppealService.create(request);

        proceedingsService.determineMagsRepDecision(request);
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

        // Call Application Tracking Service endpoint
        ApplicationTrackingOutputResult applicationTrackingOutputResult =
                applicationTrackingMapper.build(request, repOrderDto);
        if (null != applicationTrackingOutputResult.getUsn()) {
            applicationTrackingDataService.sendTrackingOutputResult(applicationTrackingOutputResult);
        }

        return request.getApplicationDTO();
    }
}
