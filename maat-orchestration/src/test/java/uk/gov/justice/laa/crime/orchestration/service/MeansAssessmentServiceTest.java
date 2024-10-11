package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.mapper.MeansAssessmentMapper;

import uk.gov.justice.laa.crime.orchestration.service.api.MeansAssessmentApiService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class MeansAssessmentServiceTest {

    @Mock
    private MeansAssessmentApiService meansAssessmentApiService;

    @Mock
    private MeansAssessmentMapper meansAssessmentMapper;

    @InjectMocks
    private MeansAssessmentService meansAssessmentService;

    @Test
    void givenFinancialAssessmentId_whenFindIsInvoked_thenApiServiceIsCalledAndResponseMapped() {
        when(meansAssessmentApiService.find(Constants.FINANCIAL_ASSESSMENT_ID))
                .thenReturn(MeansAssessmentDataBuilder.getApiGetMeansAssessmentResponse());
        meansAssessmentService.find(Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
        verify(meansAssessmentMapper).getMeansAssessmentResponseToFinancialAssessmentDto(any(
                ApiGetMeansAssessmentResponse.class), anyInt());
    }

    @Test
    void givenWorkflowRequest_whenCreateIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest = MeansAssessmentDataBuilder.getApiCreateMeansAssessmentRequest();
        when(meansAssessmentMapper.workflowRequestToCreateAssessmentRequest(any(WorkflowRequest.class)))
                .thenReturn(apiCreateMeansAssessmentRequest);
        meansAssessmentService.create(MeansAssessmentDataBuilder.buildWorkFlowRequest());
        verify(meansAssessmentApiService).create(apiCreateMeansAssessmentRequest);
    }

    @Test
    void givenWorkflowRequest_whenUpdateIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        ApiUpdateMeansAssessmentRequest apiUpdateMeansAssessmentRequest = MeansAssessmentDataBuilder.getApiUpdateMeansAssessmentRequest();
        when(meansAssessmentMapper.workflowRequestToUpdateAssessmentRequest(any(WorkflowRequest.class)))
                .thenReturn(apiUpdateMeansAssessmentRequest);
        meansAssessmentService.update(MeansAssessmentDataBuilder.buildWorkFlowRequest());
        verify(meansAssessmentApiService).update(apiUpdateMeansAssessmentRequest);
    }
}
