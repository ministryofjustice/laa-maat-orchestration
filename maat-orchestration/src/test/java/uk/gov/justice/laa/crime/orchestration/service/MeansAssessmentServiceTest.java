package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.mapper.MeansAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.ApiUpdateMeansAssessmentRequest;

import static org.mockito.ArgumentMatchers.any;
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
                .thenReturn(TestModelDataBuilder.getApiGetMeansAssessmentResponse());
        meansAssessmentService.find(Constants.FINANCIAL_ASSESSMENT_ID);
        verify(meansAssessmentMapper).getMeansAssessmentResponseToFinancialAssessmentDto(any(ApiGetMeansAssessmentResponse.class));
    }

    @Test
    void givenWorkflowRequest_whenCreateIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        when(meansAssessmentMapper.workflowRequestToCreateAssessmentRequest(any(WorkflowRequest.class)))
                .thenReturn(TestModelDataBuilder.getApiCreateMeansAssessmentRequest());
        meansAssessmentService.create(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        verify(meansAssessmentApiService).create(any(ApiCreateMeansAssessmentRequest.class));
    }

    @Test
    void givenWorkflowRequest_whenUpdateIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        when(meansAssessmentMapper.workflowRequestToUpdateAssessmentRequest(any(WorkflowRequest.class)))
                .thenReturn(TestModelDataBuilder.getApiUpdateMeansAssessmentRequest());
        meansAssessmentService.update(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        verify(meansAssessmentApiService).update(any(ApiUpdateMeansAssessmentRequest.class));
    }
}
