package uk.gov.justice.laa.crime.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.enums.NewWorkReason;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.ApiIncomeEvidenceSummary;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.ApiMeansAssessmentRequest;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.orchestration.util.NumberUtils;

import java.util.List;

import static uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder.CRITERIA_DETAIL_ID;
import static uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder.SECTION;
import static uk.gov.justice.laa.crime.orchestration.util.DateUtil.toLocalDateTime;

@ExtendWith(SoftAssertionsExtension.class)
class MeansAssessmentMapperTest {
    UserMapper userMapper = new UserMapper();
    MeansAssessmentMapper meansAssessmentMapper = new MeansAssessmentMapper(userMapper);
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiFindHardshipResponseWithSection_whenFindHardshipResponseToHardshipDTOIsInvoked_thenMappingIsCorrect() {
        FinancialAssessmentDTO actual =
                meansAssessmentMapper.getMeansAssessmentResponseToFinancialAssessmentDto(
                        MeansAssessmentDataBuilder.getApiGetMeansAssessmentResponse(), Constants.APPLICANT_ID);
        FinancialAssessmentDTO expected = MeansAssessmentDataBuilder.getFinancialAssessmentDto();

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    void givenValidWorkflowRequest_whenMeansAssessmentMapperIsInvokedForCreateAssessment_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder
                .buildWorkFlowRequest();
        ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest = meansAssessmentMapper
                .workflowRequestToCreateAssessmentRequest(workflowRequest);
        assertApiMeansAssessmentRequest(workflowRequest, apiCreateMeansAssessmentRequest);
        assertApiCreateMeansAssessmentRequestAttributes(workflowRequest, apiCreateMeansAssessmentRequest);
    }

    @Test
    void givenValidWorkflowRequest_whenMeansAssessmentMapperIsInvokedForUpdateAssessment_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder
                .buildWorkFlowRequest();
        ApiUpdateMeansAssessmentRequest apiUpdateMeansAssessmentRequest = meansAssessmentMapper
                .workflowRequestToUpdateAssessmentRequest(workflowRequest);
        assertApiMeansAssessmentRequest(workflowRequest, apiUpdateMeansAssessmentRequest);
    }


    private void assertApiMeansAssessmentRequest(WorkflowRequest workflowRequest, ApiMeansAssessmentRequest apiMeansAssessmentRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        AssessmentDTO assessmentDTO = applicationDTO.getAssessmentDTO();
        FinancialAssessmentDTO financialAssessmentDTO = assessmentDTO.getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        AssessmentStatusDTO assessmentStatusDTO = initialAssessmentDTO.getAssessmnentStatusDTO();
        CaseManagementUnitDTO caseManagementUnitDTO = applicationDTO.getCaseManagementUnitDTO();
        CaseDetailDTO caseDetailsDTO = applicationDTO.getCaseDetailsDTO();
        ChildWeightingDTO childWeightingDTO = initialAssessmentDTO.getChildWeightings().iterator().next();
        CrownCourtOverviewDTO crownCourtOverviewDTO = applicationDTO.getCrownCourtOverviewDTO();
        CrownCourtSummaryDTO crownCourtSummaryDTO = crownCourtOverviewDTO.getCrownCourtSummaryDTO();
        ApplicantDTO applicantDTO = applicationDTO.getApplicantDTO();
        EmploymentStatusDTO employmentStatusDTO = applicantDTO.getEmploymentStatusDTO();

        ApiIncomeEvidenceSummary incomeEvidenceSummary = apiMeansAssessmentRequest.getIncomeEvidenceSummary();
        IncomeEvidenceSummaryDTO incomeEvidence = financialAssessmentDTO.getIncomeEvidence();

        softly.assertThat(apiMeansAssessmentRequest.getRepId())
                .isEqualTo(NumberUtils.toInteger(applicationDTO.getRepId()));
        softly.assertThat(apiMeansAssessmentRequest.getAssessmentStatus().getValue())
                .isEqualTo(assessmentStatusDTO.getStatus());

        softly.assertThat(apiMeansAssessmentRequest.getCmuId())
                .isEqualTo(caseManagementUnitDTO.getCmuId().intValue());
        softly.assertThat(apiMeansAssessmentRequest.getCaseType().getCaseType())
                .isEqualTo(caseDetailsDTO.getCaseType());
        softly.assertThat(apiMeansAssessmentRequest.getChildWeightings().get(0).getChildWeightingId())
                .isEqualTo(childWeightingDTO.getWeightingId().intValue());
        softly.assertThat(apiMeansAssessmentRequest.getChildWeightings().get(0).getNoOfChildren())
                .isEqualTo(childWeightingDTO.getNoOfChildren());
        softly.assertThat(apiMeansAssessmentRequest.getCrownCourtOverview().getAvailable())
                .isEqualTo(crownCourtOverviewDTO.getAvailable());
        softly.assertThat(apiMeansAssessmentRequest.getCrownCourtOverview().getCrownCourtSummary().getRepOrderDecision())
                .isEqualTo(crownCourtSummaryDTO.getRepOrderDecision().getValue());
        softly.assertThat(apiMeansAssessmentRequest.getSectionSummaries().size())
                .isEqualTo(1);
        softly.assertThat(incomeEvidenceSummary.getIncomeEvidenceNotes())
                .isEqualTo(incomeEvidence.getIncomeEvidenceNotes());
        softly.assertThat(apiMeansAssessmentRequest.getInitialAssessmentDate())
                .isEqualTo(toLocalDateTime(initialAssessmentDTO.getAssessmentDate()));
        softly.assertThat(apiMeansAssessmentRequest.getMagCourtOutcome()).
                isEqualTo(MagCourtOutcome.getFrom(applicationDTO.getMagsOutcomeDTO().getOutcome()));
        softly.assertThat(apiMeansAssessmentRequest.getUserSession()).
                isEqualTo(userMapper.userDtoToUserSession(workflowRequest.getUserDTO()));
        softly.assertThat(apiMeansAssessmentRequest.getNewWorkReason()).
                isEqualTo(NewWorkReason.getFrom(initialAssessmentDTO.getNewWorkReason().getCode()));
        softly.assertThat(apiMeansAssessmentRequest.getNewWorkReason()).
                isEqualTo(NewWorkReason.getFrom(initialAssessmentDTO.getNewWorkReason().getCode()));
        softly.assertThat(apiMeansAssessmentRequest.getHasPartner()).
                isEqualTo(applicationDTO.getApplicantHasPartner());
        softly.assertThat(apiMeansAssessmentRequest.getPartnerContraryInterest()).
                isEqualTo(Boolean.TRUE);
        softly.assertThat(apiMeansAssessmentRequest.getEmploymentStatus()).
                isEqualTo(employmentStatusDTO.getCode());
        softly.assertThat(apiMeansAssessmentRequest.getLaaTransactionId()).
                isNotEmpty();
        softly.assertThat(apiMeansAssessmentRequest.getOtherBenefitNote()).
                isEqualTo(initialAssessmentDTO.getOtherBenefitNote());
        softly.assertThat(apiMeansAssessmentRequest.getOtherIncomeNote()).
                isEqualTo(initialAssessmentDTO.getOtherIncomeNote());
    }

    private void assertApiCreateMeansAssessmentRequestAttributes(WorkflowRequest workflowRequest, ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        AssessmentDTO assessmentDTO = applicationDTO.getAssessmentDTO();
        FinancialAssessmentDTO financialAssessmentDTO = assessmentDTO.getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        softly.assertThat(apiCreateMeansAssessmentRequest.getUsn())
                .isEqualTo(applicationDTO.getUsn());
        softly.assertThat(apiCreateMeansAssessmentRequest.getReviewType().getCode())
                .isEqualTo(initialAssessmentDTO.getReviewType().getCode());
    }

    @Test
    void givenValidMeansAssessmentResponse_whenMeansAssessmentMapperIsInvoked_thenMappingIsCorrect() {
        ApplicationDTO applicationDTO = getApplicationDTO();
        meansAssessmentMapper.meansAssessmentResponseToApplicationDto(
                MeansAssessmentDataBuilder.getApiMeansAssessmentResponse(),
                applicationDTO);
    }
    private static ApplicationDTO getApplicationDTO() {
        ApplicationDTO applicationDTO = new ApplicationDTO();
        AssessmentDTO assessmentDTO = new AssessmentDTO();
        FinancialAssessmentDTO financialAssessmentDTO = new FinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = new InitialAssessmentDTO();
        initialAssessmentDTO.setReviewType(new ReviewTypeDTO());
        financialAssessmentDTO.setInitial(initialAssessmentDTO);
        AssessmentSectionSummaryDTO assessmentSectionSummaryDTO = new AssessmentSectionSummaryDTO();
        assessmentSectionSummaryDTO.setSection(SECTION);
        AssessmentDetailDTO assessmentDetailDTO = new AssessmentDetailDTO();
        assessmentDetailDTO.setCriteriaDetailsId(CRITERIA_DETAIL_ID.longValue());
        assessmentSectionSummaryDTO.setAssessmentDetail(List.of(assessmentDetailDTO));
        initialAssessmentDTO.setSectionSummaries(List.of(assessmentSectionSummaryDTO));
        ChildWeightingDTO childWeightingDTO = new ChildWeightingDTO();
        childWeightingDTO.setWeightingId(Long.valueOf(1234));
        initialAssessmentDTO.setChildWeightings(List.of(childWeightingDTO));
        assessmentDTO.setFinancialAssessmentDTO(financialAssessmentDTO);
        applicationDTO.setAssessmentDTO(assessmentDTO);
        return applicationDTO;
    }


}
