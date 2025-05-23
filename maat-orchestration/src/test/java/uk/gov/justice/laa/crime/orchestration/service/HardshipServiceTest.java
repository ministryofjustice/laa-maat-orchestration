package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.HardshipApiService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
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
        when(hardshipMapper.workflowRequestToPerformHardshipRequest(any(WorkflowRequest.class), anyBoolean()))
                .thenReturn(TestModelDataBuilder.getApiPerformHardshipRequest());
        hardshipService.create(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        verify(hardshipApiService).create(any(ApiPerformHardshipRequest.class));
    }

    @Test
    void givenWorkflowRequest_whenUpdateHardshipIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        when(hardshipMapper.workflowRequestToPerformHardshipRequest(any(WorkflowRequest.class), anyBoolean()))
                .thenReturn(TestModelDataBuilder.getApiPerformHardshipRequest());
        when(hardshipApiService.update(any(ApiPerformHardshipRequest.class)))
                .thenReturn(new ApiPerformHardshipResponse());
        hardshipService.update(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        verify(hardshipApiService).update(any(ApiPerformHardshipRequest.class));
        verify(hardshipMapper).performHardshipResponseToApplicationDTO(
                any(ApiPerformHardshipResponse.class), any(ApplicationDTO.class), any(CourtType.class)
        );
    }

    @Test
    void givenWorkflowRequest_whenRollbackHardshipIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.getHardshipReviewDTO();
        when(hardshipMapper.getHardshipReviewDTO(any(ApplicationDTO.class), any(CourtType.class)))
                .thenReturn(hardshipReviewDTO);
        doNothing().when(hardshipApiService).rollback(Constants.HARDSHIP_REVIEW_ID);
        hardshipService.rollback(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        verify(hardshipApiService).rollback(Constants.HARDSHIP_REVIEW_ID);
    }
}
