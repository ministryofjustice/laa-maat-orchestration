package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.mapper.hardship.HardshipMapper;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class HardshipServiceTest {

    @Mock
    private HardshipApiService hardshipApiService;

    @Mock
    private HardshipMapper hardshipMapper;

    @InjectMocks
    private HardshipService hardshipService;

    @Test
    void givenHardshipReviewId_whenFindIsInvoked_thenApiServiceIsCalledAndResponseMapped() {
        when(hardshipApiService.find(Constants.HARDSHIP_REVIEW_ID))
                .thenReturn(TestModelDataBuilder.getApiFindHardshipResponse());
        hardshipService.find(Constants.HARDSHIP_REVIEW_ID);
        verify(hardshipMapper).findHardshipResponseToHardshipDto(any(ApiFindHardshipResponse.class));
    }

    @Test
    void givenWorkflowRequest_whenCreateHardshipIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        when(hardshipMapper.workflowRequestToPerformHardshipRequest(any(WorkflowRequest.class)))
                .thenReturn(TestModelDataBuilder.getApiPerformHardshipRequest());
        hardshipService.createHardship(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        verify(hardshipApiService).create(any(ApiPerformHardshipRequest.class));
    }

    @Test
    void givenWorkflowRequest_whenUpdateHardshipIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        when(hardshipMapper.workflowRequestToPerformHardshipRequest(any(WorkflowRequest.class)))
                .thenReturn(TestModelDataBuilder.getApiPerformHardshipRequest());
        when(hardshipApiService.update(any(ApiPerformHardshipRequest.class)))
                .thenReturn(new ApiPerformHardshipResponse());
        hardshipService.updateHardship(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        verify(hardshipApiService).update(any(ApiPerformHardshipRequest.class));
        verify(hardshipMapper).performHardshipResponseToApplicationDTO(any(ApiPerformHardshipResponse.class), any(ApplicationDTO.class));
    }

    @Test
    void givenWorkflowRequest_whenRollbackHardshipIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        when(hardshipMapper.workflowRequestToPerformHardshipRequest(any(WorkflowRequest.class)))
                .thenReturn(TestModelDataBuilder.getApiPerformHardshipRequest());
        when(hardshipApiService.rollback(any(ApiPerformHardshipRequest.class)))
                .thenReturn(new ApiPerformHardshipResponse());
        hardshipService.rollbackHardship(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        verify(hardshipApiService).rollback(any(ApiPerformHardshipRequest.class));
    }
}
