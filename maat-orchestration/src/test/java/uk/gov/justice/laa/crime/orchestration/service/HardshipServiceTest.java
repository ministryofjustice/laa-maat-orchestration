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
import uk.gov.justice.laa.crime.orchestration.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipRequest;

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
        when(hardshipApiService.find(Constants.TEST_HARDSHIP_REVIEW_ID))
                .thenReturn(TestModelDataBuilder.getApiFindHardshipResponse());
        hardshipService.find(Constants.TEST_HARDSHIP_REVIEW_ID);
        verify(hardshipMapper).findHardshipResponseToHardshipDto(any(ApiFindHardshipResponse.class));
    }

    @Test
    void givenWorkflowRequest_whenPerformHardshipIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        when(hardshipMapper.workflowRequestToPerformHardshipRequest(any(WorkflowRequest.class)))
                .thenReturn(TestModelDataBuilder.getApiPerformHardshipRequest());
        hardshipService.createHardship(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        verify(hardshipApiService).create(any(ApiPerformHardshipRequest.class));
    }
}
