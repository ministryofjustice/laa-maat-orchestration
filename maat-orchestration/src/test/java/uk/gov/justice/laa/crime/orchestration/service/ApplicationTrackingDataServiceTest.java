package uk.gov.justice.laa.crime.orchestration.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.RequestSource;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.ApplicationTrackingApiService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class ApplicationTrackingDataServiceTest {

    private static final Long APPLICATION_USN = 78943L;
    private static final Long FINANCIAL_ASSESSMENT_USN = 54321L;

    @Mock
    private ApplicationTrackingApiService applicationTrackingApiService;

    @Mock
    private ApplicationTrackingMapper applicationTrackingMapper;

    @InjectMocks
    private ApplicationTrackingDataService applicationTrackingDataService;

    @Test
    void givenAValidInput_whenSendTrackingOutputResultIsInvoked_thenApplicationTrackingApiServiceIsCalled() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = new RepOrderDTO();
        AssessmentType assessmentType = AssessmentType.IOJ;
        RequestSource requestSource = RequestSource.PASSPORT_IOJ;

        when(applicationTrackingMapper.build(
                        workflowRequest,
                        repOrderDTO,
                        assessmentType,
                        requestSource,
                        workflowRequest.getApplicationDTO().getUsn()))
                .thenReturn(new ApplicationTrackingOutputResult());

        applicationTrackingDataService.sendTrackingOutputResult(
                workflowRequest, repOrderDTO, assessmentType, requestSource);
        verify(applicationTrackingApiService).sendTrackingOutputResult(any(ApplicationTrackingOutputResult.class));
    }

    @Test
    void
            givenAHardshipRequestSource_whenSendTrackingOutputResultIsInvoked_thenItPassesTheFinancialAssessmentUsnToTheMapper() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.getApplicationDTO().setUsn(APPLICATION_USN);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .setUsn(FINANCIAL_ASSESSMENT_USN);
        RepOrderDTO repOrderDTO = new RepOrderDTO();
        AssessmentType assessmentType = AssessmentType.CCHARDSHIP;
        RequestSource requestSource = RequestSource.HARDSHIP;

        applicationTrackingDataService.sendTrackingOutputResult(
                workflowRequest, repOrderDTO, assessmentType, requestSource);

        verify(applicationTrackingMapper)
                .build(
                        workflowRequest,
                        repOrderDTO,
                        assessmentType,
                        requestSource,
                        workflowRequest
                                .getApplicationDTO()
                                .getAssessmentDTO()
                                .getFinancialAssessmentDTO()
                                .getUsn());
    }

    @Test
    void
            givenAMeansAssessmentRequestSource_whenSendTrackingOutputResultIsInvoked_thenItPassesTheFinancialAssessmentUsnToTheMapper() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.getApplicationDTO().setUsn(APPLICATION_USN);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .setUsn(FINANCIAL_ASSESSMENT_USN);
        RepOrderDTO repOrderDTO = new RepOrderDTO();
        AssessmentType assessmentType = AssessmentType.MEANS_INIT;
        RequestSource requestSource = RequestSource.MEANS_ASSESSMENT;

        applicationTrackingDataService.sendTrackingOutputResult(
                workflowRequest, repOrderDTO, assessmentType, requestSource);

        verify(applicationTrackingMapper)
                .build(
                        workflowRequest,
                        repOrderDTO,
                        assessmentType,
                        requestSource,
                        workflowRequest
                                .getApplicationDTO()
                                .getAssessmentDTO()
                                .getFinancialAssessmentDTO()
                                .getUsn());
    }

    @Test
    void
            givenAPassportIojRequestSource_whenSendTrackingOutputResultIsInvoked_thenItPassesTheApplicationUsnToTheMapper() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.getApplicationDTO().setUsn(APPLICATION_USN);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .setUsn(FINANCIAL_ASSESSMENT_USN);
        RepOrderDTO repOrderDTO = new RepOrderDTO();
        AssessmentType assessmentType = AssessmentType.IOJ;
        RequestSource requestSource = RequestSource.PASSPORT_IOJ;

        applicationTrackingDataService.sendTrackingOutputResult(
                workflowRequest, repOrderDTO, assessmentType, requestSource);

        verify(applicationTrackingMapper)
                .build(
                        workflowRequest,
                        repOrderDTO,
                        assessmentType,
                        requestSource,
                        workflowRequest.getApplicationDTO().getUsn());
    }

    @Test
    void givenNoUsn_whenSendTrackingOutputResult_thenNoTrackingInformationIsMappedOrSent() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.getApplicationDTO().setUsn(null);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .setUsn(null);
        RepOrderDTO repOrderDTO = new RepOrderDTO();
        AssessmentType assessmentType = AssessmentType.IOJ;
        RequestSource requestSource = RequestSource.PASSPORT_IOJ;

        applicationTrackingDataService.sendTrackingOutputResult(
                workflowRequest, repOrderDTO, assessmentType, requestSource);

        verify(applicationTrackingMapper, never()).build(any(), any(), any(), any(), any());
        verify(applicationTrackingApiService, never()).sendTrackingOutputResult(any());
    }
}
