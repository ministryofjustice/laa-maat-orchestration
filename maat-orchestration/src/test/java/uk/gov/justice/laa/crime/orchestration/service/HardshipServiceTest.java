package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.UserMapper;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class HardshipServiceTest {

    @Mock
    private HardshipApiService hardshipApiService;

    @Spy
    private UserMapper userMapper = new UserMapper();

    @Spy
    private HardshipMapper hardshipMapper = new HardshipMapper(userMapper);

    @InjectMocks
    private HardshipService hardshipService;

    @Test
    void givenHardshipReviewId_whenFindIsInvoked_thenApiServiceIsCalled() {
        when(hardshipApiService.find(Constants.TEST_HARDSHIP_REVIEW_ID))
                .thenReturn(TestModelDataBuilder.getApiFindHardshipResponse());
        HardshipReviewDTO actual = hardshipService.find(Constants.TEST_HARDSHIP_REVIEW_ID);
        HardshipReviewDTO expected = TestModelDataBuilder
                .getHardshipOverviewDTO(CourtType.MAGISTRATE)
                .getMagCourtHardship();
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    void givenWorkflowRequest_whenPerformHardshipIsInvoked_thenApiServiceIsCalled() {
        hardshipService.createHardship(TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE));
        verify(hardshipApiService).create(any(ApiPerformHardshipRequest.class));
    }
}
