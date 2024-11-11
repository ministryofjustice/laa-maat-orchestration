package uk.gov.justice.laa.crime.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.proceeding.common.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.common.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateCrownCourtRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;

import uk.gov.justice.laa.crime.util.DateUtil;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class ProceedingsMapperTest {

    @Mock
    UserMapper userMapper;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @InjectMocks
    ProceedingsMapper proceedingsMapper;

    @Test
    void whenWorkflowRequestToUpdateApplicationRequestIsInvoked() {
        ApiUserSession userSession = TestModelDataBuilder.getApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);

        when(userMapper.userDtoToUserSession(any(UserDTO.class)))
                .thenReturn(userSession);

        ApiUpdateApplicationRequest expectedApplicationRequest = TestModelDataBuilder.getUpdateApplicationRequest();
        ApiUpdateApplicationRequest actualApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateApplicationRequest(workflowRequest.getApplicationDTO(), workflowRequest.getUserDTO());

        softly.assertThat(actualApplicationRequest)
                .usingRecursiveComparison()
                .isEqualTo(expectedApplicationRequest);
    }

    @Test
    void givenAEmptyInitStatus_whenWorkflowRequestToUpdateCrownCourtRequestIsInvoked_thenReturnEmptyInitStatus() {
        ApiUserSession userSession = TestModelDataBuilder.getApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);

        when(userMapper.userDtoToUserSession(any(UserDTO.class)))
                .thenReturn(userSession);

        InitialAssessmentDTO initialAssessmentDTO =
                workflowRequest.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getInitial();
        initialAssessmentDTO.getAssessmnentStatusDTO().setStatus(null);

        ApiUpdateCrownCourtRequest actualApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateCrownCourtRequest(workflowRequest.getApplicationDTO(), workflowRequest.getUserDTO());

        softly.assertThat(actualApplicationRequest.getFinancialAssessment().getInitStatus()).isNull();

        softly.assertAll();
    }

    @Test
    void givenAValidWorkflowRequest_whenWorkflowRequestToUpdateCrownCourtRequestIsInvoked_thenReturnCorrectCCRequest() {
        ApiUserSession userSession = TestModelDataBuilder.getApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);

        when(userMapper.userDtoToUserSession(any(UserDTO.class)))
                .thenReturn(userSession);

        ApiUpdateCrownCourtRequest expectedCrownCourtRequest = TestModelDataBuilder.getUpdateCrownCourtRequest();

        CrownCourtSummaryDTO crownCourtSummaryDTO =
                workflowRequest.getApplicationDTO().getCrownCourtOverviewDTO().getCrownCourtSummaryDTO();
        crownCourtSummaryDTO.setOutcomeDTOs(null);
        crownCourtSummaryDTO.setEvidenceProvisionFee(null);

        expectedCrownCourtRequest.getCrownCourtSummary().setEvidenceFeeLevel(null);
        expectedCrownCourtRequest.getCrownCourtSummary().setCrownCourtOutcome(emptyList());

        ApiUpdateCrownCourtRequest actualApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateCrownCourtRequest(workflowRequest.getApplicationDTO(), workflowRequest.getUserDTO());

        softly.assertThat(actualApplicationRequest)
                .usingRecursiveComparison()
                .isEqualTo(expectedCrownCourtRequest);
    }

    @Test
    void whenWorkflowRequestToUpdateApplicationRequestIsInvokedWithNullFields() {
        ApiUserSession userSession = TestModelDataBuilder.getApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);

        when(userMapper.userDtoToUserSession(any(UserDTO.class)))
                .thenReturn(userSession);

        ApiUpdateApplicationRequest expectedApplicationRequest = TestModelDataBuilder.getUpdateApplicationRequest();

        CrownCourtSummaryDTO crownCourtSummaryDTO =
                workflowRequest.getApplicationDTO().getCrownCourtOverviewDTO().getCrownCourtSummaryDTO();
        crownCourtSummaryDTO.setOutcomeDTOs(null);
        crownCourtSummaryDTO.setEvidenceProvisionFee(null);

        expectedApplicationRequest.getCrownCourtSummary().setEvidenceFeeLevel(null);
        expectedApplicationRequest.getCrownCourtSummary().setCrownCourtOutcome(emptyList());

        ApiUpdateApplicationRequest actualApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateApplicationRequest(workflowRequest.getApplicationDTO(), workflowRequest.getUserDTO());

        softly.assertThat(actualApplicationRequest)
                .usingRecursiveComparison()
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

        softly.assertThat(updatedApplicationDTO.getTimestamp().toLocalDateTime())
                .isEqualTo(updateApplicationResponse.getModifiedDateTime());

        softly.assertThat(updatedCrownCourtSummaryDTO.getCcRepOrderDate())
                .isEqualTo(DateUtil.toDate(updateApplicationResponse.getCrownRepOrderDate()));

        softly.assertThat(updatedCrownCourtSummaryDTO.getRepOrderDecision().getValue())
                .isEqualTo(updateApplicationResponse.getCrownRepOrderDecision());

        softly.assertThat(updatedCrownCourtSummaryDTO.getCcRepType().getValue())
                .isEqualTo(updateApplicationResponse.getCrownRepOrderType());
    }

    @Test
    void updateCrownCourtResponseToApplicationDtoIsInvoked() {
        ApiUpdateCrownCourtOutcomeResponse updateCrownCourtResponse = TestModelDataBuilder.getApiUpdateCrownCourtResponse();
        ApplicationDTO applicationDTO = TestModelDataBuilder.getApplicationDTO(CourtType.CROWN_COURT);

        updateCrownCourtResponse.getCrownCourtSummary().setRepOrderCrownCourtOutcome(
                List.of(TestModelDataBuilder.getApiRepOrderCrownCourtOutcome()));

        ApiCrownCourtSummary apiCrownCourtSummary = updateCrownCourtResponse.getCrownCourtSummary();

        ApplicationDTO application = proceedingsMapper.updateCrownCourtResponseToApplicationDto(
                updateCrownCourtResponse, applicationDTO);

        CrownCourtSummaryDTO crownCourtSummaryDTO =
                application.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO();

        softly.assertThat(application.getTimestamp().toLocalDateTime())
                .isEqualTo(updateCrownCourtResponse.getModifiedDateTime());

        softly.assertThat(crownCourtSummaryDTO.getCcRepId())
                .isEqualTo(apiCrownCourtSummary.getRepId().longValue());

        softly.assertThat(crownCourtSummaryDTO.getCcRepType().getValue())
                .isEqualTo(apiCrownCourtSummary.getRepType());

        softly.assertThat(crownCourtSummaryDTO.getCcRepOrderDate())
                .isEqualTo(DateUtil.toDate(apiCrownCourtSummary.getRepOrderDate()));

        softly.assertThat(crownCourtSummaryDTO.getRepOrderDecision().getValue())
                .isEqualTo(apiCrownCourtSummary.getRepOrderDecision());

        softly.assertThat(crownCourtSummaryDTO.getEvidenceProvisionFee().getFeeLevel())
                .isEqualTo(apiCrownCourtSummary.getEvidenceFeeLevel().getFeeLevel());

        checkOutcomes(crownCourtSummaryDTO.getOutcomeDTOs().stream().toList(), apiCrownCourtSummary);
    }

    void checkOutcomes(List<OutcomeDTO> outcomes, ApiCrownCourtSummary apiCrownCourtSummary) {
        var actualOutcome = outcomes.get(0);
        var originalOutcome = apiCrownCourtSummary.getRepOrderCrownCourtOutcome().get(0);

        softly.assertThat(actualOutcome.getOutcome())
                .isEqualTo(originalOutcome.getOutcome().getCode());
        softly.assertThat(actualOutcome.getDescription())
                .isEqualTo(originalOutcome.getOutcome().getDescription());
        softly.assertThat(actualOutcome.getDateSet())
                .isEqualTo(DateUtil.toDate(originalOutcome.getOutcomeDate()));
    }
}
