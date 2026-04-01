package uk.gov.justice.laa.crime.orchestration.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.common.model.proceeding.common.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiDetermineMagsRepDecisionRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateCrownCourtRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiDetermineMagsRepDecisionResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CrownCourtSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.InitialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.OutcomeDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.SysGenString;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.ZoneId;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void givenWorkflowRequest_whenMappingDetermineMagsRepDecisionRequest_thenReturnsApiRequest() {
        mockApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();

        // Remove the crown hardship created above as we're only interested in the mags
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .setCrownCourtHardship(null);

        ApiDetermineMagsRepDecisionRequest expectedRequest = TestModelDataBuilder.getDetermineMagsRepDecisionRequest();
        ApiDetermineMagsRepDecisionRequest actualRequest =
                proceedingsMapper.workflowRequestToDetermineMagsRepDecisionRequest(workflowRequest);

        softly.assertThat(actualRequest.getRepId()).isEqualTo(expectedRequest.getRepId());
        softly.assertThat(actualRequest.getCaseType()).isEqualTo(expectedRequest.getCaseType());
        softly.assertThat(actualRequest.getPassportAssessment()).isEqualTo(expectedRequest.getPassportAssessment());
        softly.assertThat(actualRequest.getIojAppeal()).isEqualTo(expectedRequest.getIojAppeal());
        softly.assertThat(actualRequest.getFinancialAssessment()).isEqualTo(expectedRequest.getFinancialAssessment());
        softly.assertThat(actualRequest.getUserSession()).isEqualTo(expectedRequest.getUserSession());

        softly.assertAll();
    }

    @Test
    void givenNoFullAssessmentDate_whenMappingDetermineMagsRepDecisionRequest_thenDoesNotMapFullAssessment() {
        mockApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();

        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getFull()
                .setAssessmentDate(null);

        ApiDetermineMagsRepDecisionRequest actual =
                proceedingsMapper.workflowRequestToDetermineMagsRepDecisionRequest(workflowRequest);

        softly.assertThat(actual.getFinancialAssessment().getFullResult()).isNull();
        softly.assertThat(actual.getFinancialAssessment().getFullStatus()).isNull();
        softly.assertAll();
    }

    @Test
    void givenMagHardshipWithoutId_whenMappingDetermineMagsRepDecisionRequest_thenDoesNotMapHardshipOverview() {
        mockApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();

        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getMagCourtHardship()
                .setId(null);

        ApiDetermineMagsRepDecisionRequest actual =
                proceedingsMapper.workflowRequestToDetermineMagsRepDecisionRequest(workflowRequest);

        assertThat(actual.getFinancialAssessment().getHardshipOverview()).isNull();
    }

    @Test
    void givenCrownHardshipWithoutId_whenMappingUpdateApplicationRequest_thenDoesNotMapHardshipOverview() {
        mockApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);

        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getCrownCourtHardship()
                .setId(null);

        ApiUpdateApplicationRequest actual = proceedingsMapper.workflowRequestToUpdateApplicationRequest(
                workflowRequest.getApplicationDTO(), workflowRequest.getUserDTO());

        assertThat(actual.getFinancialAssessment().getHardshipOverview()).isNull();
    }

    @Test
    void givenNoPassportedId_whenMappingDetermineMagsRepDecisionRequest_thenReturnsNullPassportAssessment() {
        mockApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();

        workflowRequest.getApplicationDTO().getPassportedDTO().setPassportedId(null);

        ApiDetermineMagsRepDecisionRequest actual =
                proceedingsMapper.workflowRequestToDetermineMagsRepDecisionRequest(workflowRequest);

        assertThat(actual.getPassportAssessment()).isNull();
    }

    @Test
    void givenDetermineMagsRepDecisionResponse_whenMappingToApplicationDto_thenUpdatesApplication() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();

        ApiDetermineMagsRepDecisionResponse expectedResponse =
                TestModelDataBuilder.getDetermineMagsRepDecisionResponse();
        ApplicationDTO actualResponse = proceedingsMapper.determineMagsRepDecisionResponseToApplicationDto(
                expectedResponse, workflowRequest.getApplicationDTO());

        softly.assertThat(actualResponse.getRepOrderDecision().getCode())
                .isEqualTo(
                        expectedResponse.getDecisionResult().getDecisionReason().getCode());

        softly.assertThat(actualResponse.getRepOrderDecision().getDescription())
                .isEqualTo(new SysGenString(expectedResponse
                                .getDecisionResult()
                                .getDecisionReason()
                                .getDescription())
                        .getValue());

        softly.assertThat(actualResponse.getDecisionDate())
                .isEqualTo(expectedResponse
                        .getDecisionResult()
                        .getDecisionDate()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant());

        softly.assertAll();
    }

    @Test
    void givenWorkflowRequest_whenMappingUpdateApplicationRequest_thenReturnsApiUpdateApplicationRequest() {
        mockApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);
        ApiUpdateApplicationRequest expectedApplicationRequest = TestModelDataBuilder.getUpdateApplicationRequest();
        ApiUpdateApplicationRequest actualApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateApplicationRequest(
                        workflowRequest.getApplicationDTO(), workflowRequest.getUserDTO());

        assertThat(actualApplicationRequest).usingRecursiveComparison().isEqualTo(expectedApplicationRequest);
    }

    @Test
    void givenNullInitStatus_whenMappingUpdateCrownCourtRequest_thenReturnsNullInitStatus() {
        mockApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);
        InitialAssessmentDTO initialAssessmentDTO = workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getInitial();
        initialAssessmentDTO.getAssessmnentStatusDTO().setStatus(null);

        ApiUpdateCrownCourtRequest expectedApplicationRequest = TestModelDataBuilder.getUpdateCrownCourtRequest();
        expectedApplicationRequest.getFinancialAssessment().setInitStatus(null);

        ApiUpdateCrownCourtRequest actualApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateCrownCourtRequest(
                        workflowRequest.getApplicationDTO(), workflowRequest.getUserDTO());

        assertThat(actualApplicationRequest).usingRecursiveComparison().isEqualTo(expectedApplicationRequest);
    }

    @Test
    void givenValidWorkflowRequest_whenMappingUpdateCrownCourtRequest_thenReturnsCorrectRequest() {
        mockApiUserSession();
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);
        ApiUpdateCrownCourtRequest expectedCrownCourtRequest = TestModelDataBuilder.getUpdateCrownCourtRequest();
        ApiUpdateCrownCourtRequest actualApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateCrownCourtRequest(
                        workflowRequest.getApplicationDTO(), workflowRequest.getUserDTO());

        assertThat(actualApplicationRequest).usingRecursiveComparison().isEqualTo(expectedCrownCourtRequest);
    }

    @Test
    void givenUpdateApplicationResponse_whenMappingToApplicationDto_thenUpdatesApplication() {
        ApiUpdateApplicationResponse updateApplicationResponse = TestModelDataBuilder.getApiUpdateApplicationResponse();
        ApplicationDTO applicationDTO = TestModelDataBuilder.getApplicationDTO(CourtType.CROWN_COURT);

        ApplicationDTO updatedApplicationDTO =
                proceedingsMapper.updateApplicationResponseToApplicationDto(updateApplicationResponse, applicationDTO);

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

        softly.assertAll();
    }

    @Test
    void givenUpdateCrownCourtOutcomeResponse_whenMappingToApplicationDto_thenUpdatesApplication() {
        ApiUpdateCrownCourtOutcomeResponse updateCrownCourtResponse =
                TestModelDataBuilder.getApiUpdateCrownCourtResponse();
        ApplicationDTO applicationDTO = TestModelDataBuilder.getApplicationDTO(CourtType.CROWN_COURT);

        updateCrownCourtResponse
                .getCrownCourtSummary()
                .setRepOrderCrownCourtOutcome(List.of(TestModelDataBuilder.getApiRepOrderCrownCourtOutcome()));

        ApiCrownCourtSummary apiCrownCourtSummary = updateCrownCourtResponse.getCrownCourtSummary();

        ApplicationDTO application =
                proceedingsMapper.updateCrownCourtResponseToApplicationDto(updateCrownCourtResponse, applicationDTO);

        CrownCourtSummaryDTO crownCourtSummaryDTO =
                application.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO();

        softly.assertThat(application.getTimestamp().toLocalDateTime())
                .isEqualTo(updateCrownCourtResponse.getModifiedDateTime());

        softly.assertThat(crownCourtSummaryDTO.getCcRepId())
                .isEqualTo(apiCrownCourtSummary.getRepId().longValue());

        softly.assertThat(crownCourtSummaryDTO.getCcRepType().getValue()).isEqualTo(apiCrownCourtSummary.getRepType());

        softly.assertThat(crownCourtSummaryDTO.getCcRepOrderDate())
                .isEqualTo(DateUtil.toDate(apiCrownCourtSummary.getRepOrderDate()));

        softly.assertThat(crownCourtSummaryDTO.getRepOrderDecision().getValue())
                .isEqualTo(apiCrownCourtSummary.getRepOrderDecision());

        softly.assertThat(crownCourtSummaryDTO.getEvidenceProvisionFee().getFeeLevel())
                .isEqualTo(apiCrownCourtSummary.getEvidenceFeeLevel().getFeeLevel());

        checkOutcomes(crownCourtSummaryDTO.getOutcomeDTOs().stream().toList(), apiCrownCourtSummary);

        softly.assertAll();
    }

    void checkOutcomes(List<OutcomeDTO> outcomes, ApiCrownCourtSummary apiCrownCourtSummary) {
        var actualOutcome = outcomes.getFirst();
        var originalOutcome =
                apiCrownCourtSummary.getRepOrderCrownCourtOutcome().getFirst();

        softly.assertThat(actualOutcome.getOutcome())
                .isEqualTo(originalOutcome.getOutcome().getCode());
        softly.assertThat(actualOutcome.getDescription())
                .isEqualTo(originalOutcome.getOutcome().getDescription());
        softly.assertThat(actualOutcome.getDateSet()).isEqualTo(DateUtil.toDate(originalOutcome.getOutcomeDate()));
    }

    private void mockApiUserSession() {
        ApiUserSession userSession = TestModelDataBuilder.getApiUserSession();
        when(userMapper.userDtoToUserSession(any(UserDTO.class))).thenReturn(userSession);
    }

    @Test
    void givenNullStatus_whenGettingCurrentStatus_thenReturnsNull() {
        assertThat(proceedingsMapper.getCurrentStatus(null)).isNull();
    }

    @Test
    void givenBlankStatus_whenGettingCurrentStatus_thenReturnsNull() {
        assertThat(proceedingsMapper.getCurrentStatus("")).isNull();
    }

    @Test
    void givenValidStatus_whenGettingCurrentStatus_thenReturnsMappedStatus() {
        assertThat(proceedingsMapper.getCurrentStatus("COMPLETE")).isEqualTo(CurrentStatus.COMPLETE);
    }
}
