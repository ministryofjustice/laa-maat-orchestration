package uk.gov.justice.laa.crime.orchestration.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CrownCourtSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.util.DateUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProceedingsMapperTest {

    @Mock
    UserMapper userMapper;

    @InjectMocks
    ProceedingsMapper proceedingsMapper;

    @Test
    void whenWorkflowRequestToUpdateApplicationRequestIsInvoked() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);
        ApiUserSession userSession = TestModelDataBuilder.getApiUserSession();
        when(userMapper.userDtoToUserSession(any(UserDTO.class))).thenReturn(userSession);
        ApiUpdateApplicationRequest expectedApplicationRequest = TestModelDataBuilder.getUpdateApplicationRequest();

        ApiUpdateApplicationRequest actualApplicationRequest = proceedingsMapper.workflowRequestToUpdateApplicationRequest(workflowRequest);

        assertThat(actualApplicationRequest)
                .usingRecursiveComparison()
                .ignoringFields("laaTransactionId")
                .isEqualTo(expectedApplicationRequest);
    }

    @Test
    void whenUpdateApplicationResponseToApplicationDtoIsInvoked() {
        ApiUpdateApplicationResponse updateApplicationResponse = TestModelDataBuilder.getApiUpdateApplicationResponse();
        ApplicationDTO applicationDTO = TestModelDataBuilder.getApplicationDTO(CourtType.CROWN_COURT);

        ApplicationDTO updatedApplicationDTO = proceedingsMapper.updateApplicationResponseToApplicationDto(
                updateApplicationResponse, applicationDTO);

        CrownCourtSummaryDTO updatedCrownCourtSummaryDTO =
                updatedApplicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO();
        assertThat(updatedApplicationDTO.getTimestamp().toLocalDateTime())
                .isEqualTo(updateApplicationResponse.getModifiedDateTime());
        assertThat(updatedCrownCourtSummaryDTO.getCcRepOrderDate())
                .isEqualTo(DateUtil.toDate(updateApplicationResponse.getCrownRepOrderDate()));
        assertThat(updatedCrownCourtSummaryDTO.getRepOrderDecision().getValue())
                .isEqualTo(updateApplicationResponse.getCrownRepOrderDecision());
        assertThat(updatedCrownCourtSummaryDTO.getCcRepType().getValue())
                .isEqualTo(updateApplicationResponse.getCrownRepOrderType());
    }
}
