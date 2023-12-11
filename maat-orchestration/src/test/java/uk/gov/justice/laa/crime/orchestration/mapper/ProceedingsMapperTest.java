package uk.gov.justice.laa.crime.orchestration.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProceedingsMapperTest {

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
        // TODO: Generate ApiUpdateApplicationResponse test data
        ApiUpdateApplicationResponse updateApplicationResponse = TestModelDataBuilder.getApiUpdateApplicationResponse();
        // TODO: Generate ApplicationDTO test data - getCrownCourtSummaryDTO is already populated need to look at this
        ApplicationDTO applicationDTO = TestModelDataBuilder.getApplicationDTO(CourtType.CROWN_COURT);

        // TODO: Call updateApplicationResponseToApplicationDto passing in test data
        ApplicationDTO updatedApplicationDTO = proceedingsMapper.updateApplicationResponseToApplicationDto(
                updateApplicationResponse, applicationDTO);

        // TODO: Assert ApplicationDTO updated with data from ApiUpdateApplicationResponse
    }
}
