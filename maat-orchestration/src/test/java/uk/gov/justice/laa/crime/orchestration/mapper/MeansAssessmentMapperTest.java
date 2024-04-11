package uk.gov.justice.laa.crime.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.enums.AssessmentType;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.*;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getApplicationDTOForMeansAssessmentMapper;
import static uk.gov.justice.laa.crime.util.DateUtil.toLocalDateTime;

@ExtendWith(SoftAssertionsExtension.class)
class MeansAssessmentMapperTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    UserMapper userMapper = new UserMapper();
    MeansAssessmentMapper meansAssessmentMapper = new MeansAssessmentMapper(userMapper);

    @Test
    void givenApiFindHardshipResponseWithSection_whenFindHardshipResponseToHardshipDTOIsInvoked_thenMappingIsCorrect() {
        FinancialAssessmentDTO actual =
                meansAssessmentMapper.getMeansAssessmentResponseToFinancialAssessmentDto(
                        MeansAssessmentDataBuilder.getApiGetMeansAssessmentResponse(), Constants.APPLICANT_ID);
        FinancialAssessmentDTO expected = MeansAssessmentDataBuilder.getFinancialAssessmentDto();

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
//                .ignoringFields("incomeEvidence.applicantIncomeEvidenceList",
//                        "incomeEvidence.partnerIncomeEvidenceList",
//                        "incomeEvidence.extraEvidenceList",
//                        "full.sectionSummaries",
//                        "initial.sectionSummaries")
                .isEqualTo(expected);

        compareIncomeEvidenceFields(actual.getIncomeEvidence().getApplicantIncomeEvidenceList(),
                expected.getIncomeEvidence().getApplicantIncomeEvidenceList());
        compareIncomeEvidenceFields(actual.getIncomeEvidence().getPartnerIncomeEvidenceList(),
                expected.getIncomeEvidence().getPartnerIncomeEvidenceList());
        compareExtraEvidenceFields(actual.getIncomeEvidence().getExtraEvidenceList(),
                expected.getIncomeEvidence().getExtraEvidenceList());
        compareSectionSummariesFields(actual.getFull().getSectionSummaries(),
                expected.getFull().getSectionSummaries());
        compareSectionSummariesFields(actual.getInitial().getSectionSummaries(),
                expected.getInitial().getSectionSummaries());
    }

    private void compareSectionSummariesFields(Collection<AssessmentSectionSummaryDTO> actual, Collection<AssessmentSectionSummaryDTO> expected) {
        assertThat(actual).hasSameSizeAs(expected);
        AssessmentSectionSummaryDTO actualSectionSummaryDTO = actual.stream().findFirst().get();
        AssessmentSectionSummaryDTO expectedSectionSummaryDTO = expected.stream().findFirst().get();
        softly.assertThat(actualSectionSummaryDTO)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("assessmentDetail")
                .isEqualTo(expectedSectionSummaryDTO);
        softly.assertThat(actualSectionSummaryDTO.getAssessmentDetail().stream().findFirst())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("value.timestamp")
                .isEqualTo(expectedSectionSummaryDTO.getAssessmentDetail().stream().findFirst());
    }
    private void compareIncomeEvidenceFields(Collection<EvidenceDTO> actual, Collection<EvidenceDTO> expected) {
        assertThat(actual).hasSameSizeAs(expected);
        softly.assertThat(actual.stream().findFirst())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("value.timestamp")
                .isEqualTo(expected.stream().findFirst());
    }

    private void compareExtraEvidenceFields(Collection<ExtraEvidenceDTO> actual, Collection<ExtraEvidenceDTO> expected) {
        assertThat(actual).hasSameSizeAs(expected);
        softly.assertThat(actual.stream().findFirst())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("value.timestamp")
                .isEqualTo(expected.stream().findFirst());
    }

    @Test
    void givenValidWorkflowRequest_whenMeansAssessmentMapperIsInvokedForCreateAssessment_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder
                .buildWorkFlowRequest();
        ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest = meansAssessmentMapper
                .workflowRequestToCreateAssessmentRequest(workflowRequest);
        assertApiMeansAssessmentRequestAttributes(workflowRequest, apiCreateMeansAssessmentRequest);
        assertApiCreateMeansAssessmentRequestAttributes(workflowRequest, apiCreateMeansAssessmentRequest);
    }

    @Test
    void givenValidWorkflowRequest_whenMeansAssessmentMapperIsInvokedForUpdateAssessment_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder
                .buildWorkFlowRequest();
        ApiUpdateMeansAssessmentRequest apiUpdateMeansAssessmentRequest = meansAssessmentMapper
                .workflowRequestToUpdateAssessmentRequest(workflowRequest);
        assertApiMeansAssessmentRequestAttributes(workflowRequest, apiUpdateMeansAssessmentRequest);
        assertApiUpdateMeansAssessmentRequestAttributes(workflowRequest, apiUpdateMeansAssessmentRequest);

    }

    private void assertApiMeansAssessmentRequestAttributes(WorkflowRequest workflowRequest, ApiMeansAssessmentRequest apiMeansAssessmentRequest) {
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
        softly.assertThat(apiMeansAssessmentRequest.getAssessmentStatus().getStatus())
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

    private void assertApiUpdateMeansAssessmentRequestAttributes(WorkflowRequest workflowRequest, ApiUpdateMeansAssessmentRequest apiUpdateMeansAssessmentRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        AssessmentDTO assessmentDTO = applicationDTO.getAssessmentDTO();
        FinancialAssessmentDTO financialAssessmentDTO = assessmentDTO.getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();
        softly.assertThat(apiUpdateMeansAssessmentRequest.getFinancialAssessmentId())
                .isEqualTo(BigDecimal.valueOf(financialAssessmentDTO.getId()));
        softly.assertThat(apiUpdateMeansAssessmentRequest.getFullAssessmentDate())
                .isEqualTo(toLocalDateTime(fullAssessmentDTO.getAssessmentDate()));
        softly.assertThat(apiUpdateMeansAssessmentRequest.getOtherHousingNote())
                .isEqualTo(fullAssessmentDTO.getOtherHousingNote());
        softly.assertThat(apiUpdateMeansAssessmentRequest.getInitTotalAggregatedIncome())
                .isEqualTo(BigDecimal.valueOf(initialAssessmentDTO.getTotalAggregatedIncome()));
        softly.assertThat(apiUpdateMeansAssessmentRequest.getFullAssessmentNotes())
                .isEqualTo(fullAssessmentDTO.getAssessmentNotes());
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
    void givenValidMeansAssessmentResponse_whenMeansAssessmentMapperIsInvokedForFullAssessment_thenMappingIsCorrect() {
        ApplicationDTO applicationDTO = getApplicationDTOForMeansAssessmentMapper(true);
        FinancialAssessmentDTO financialAssessmentDTO = applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO();
        FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();
        ApiMeansAssessmentResponse apiMeansAssessmentResponse = MeansAssessmentDataBuilder.getApiMeansAssessmentResponse();

        meansAssessmentMapper.meansAssessmentResponseToApplicationDto(
                apiMeansAssessmentResponse,
                applicationDTO);

        softly.assertThat(applicationDTO.getRepId())
                .isEqualTo(apiMeansAssessmentResponse.getRepId().intValue());
        softly.assertThat(applicationDTO.getTimestamp().toLocalDateTime())
                .isEqualTo(apiMeansAssessmentResponse.getApplicationTimestamp());
        softly.assertThat(financialAssessmentDTO.getId())
                .isEqualTo(apiMeansAssessmentResponse.getAssessmentId().intValue());
        softly.assertThat(financialAssessmentDTO.getTimestamp().toLocalDateTime())
                .isEqualTo(apiMeansAssessmentResponse.getUpdated());
        softly.assertThat(fullAssessmentDTO.getResult())
                .isEqualTo(apiMeansAssessmentResponse.getFullResult());
        softly.assertThat(fullAssessmentDTO.getAdjustedLivingAllowance())
                .isEqualTo(apiMeansAssessmentResponse.getAdjustedLivingAllowance().doubleValue());
        softly.assertThat(fullAssessmentDTO.getResultReason())
                .isEqualTo(apiMeansAssessmentResponse.getFullResultReason());
        softly.assertThat(fullAssessmentDTO.getTotalAggregatedExpense())
                .isEqualTo(apiMeansAssessmentResponse.getTotalAggregatedExpense().doubleValue());
        softly.assertThat(fullAssessmentDTO.getTotalAnnualDisposableIncome())
                .isEqualTo(apiMeansAssessmentResponse.getTotalAnnualDisposableIncome().doubleValue());
        softly.assertThat(fullAssessmentDTO.getThreshold())
                .isEqualTo(apiMeansAssessmentResponse.getFullThreshold().doubleValue());
        softly.assertThat(fullAssessmentDTO.getSectionSummaries().size())
                .isEqualTo(1);
    }

    @Test
    void givenValidMeansAssessmentResponse_whenMeansAssessmentMapperIsInvokedForInitialAssessment_thenMappingIsCorrect() {
        ApiMeansAssessmentResponse apiMeansAssessmentResponse = MeansAssessmentDataBuilder.getApiMeansAssessmentResponse();
        ApplicationDTO applicationDTO = getApplicationDTOForMeansAssessmentMapper(false);
        FinancialAssessmentDTO financialAssessmentDTO = applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        Collection<AssessmentSectionSummaryDTO> sectionSummaries = initialAssessmentDTO.getSectionSummaries();
        AssessmentSectionSummaryDTO assessmentSectionSummaryDTO = sectionSummaries.iterator().next();
        List<ApiAssessmentSectionSummary> assessmentSectionSummaries = apiMeansAssessmentResponse.getAssessmentSectionSummary();
        ApiAssessmentSectionSummary assessmentSectionSummary = assessmentSectionSummaries.get(0);
        AssessmentDetailDTO assessmentDetail = assessmentSectionSummaryDTO.getAssessmentDetail().iterator().next();
        ApiAssessmentDetail apiAssessmentDetail = assessmentSectionSummary.getAssessmentDetails().get(0);
        ChildWeightingDTO childWeightingDTO = initialAssessmentDTO.getChildWeightings().iterator().next();
        ApiAssessmentChildWeighting apiAssessmentChildWeighting = apiMeansAssessmentResponse.getChildWeightings().get(0);

        meansAssessmentMapper.meansAssessmentResponseToApplicationDto(
                apiMeansAssessmentResponse,
                applicationDTO);

        softly.assertThat(applicationDTO.getRepId())
                .isEqualTo(apiMeansAssessmentResponse.getRepId().intValue());
        softly.assertThat(applicationDTO.getTimestamp().toLocalDateTime())
                .isEqualTo(apiMeansAssessmentResponse.getApplicationTimestamp());
        softly.assertThat(financialAssessmentDTO.getId())
                .isEqualTo(apiMeansAssessmentResponse.getAssessmentId().intValue());
        softly.assertThat(financialAssessmentDTO.getTimestamp().toLocalDateTime())
                .isEqualTo(apiMeansAssessmentResponse.getUpdated());
        softly.assertThat(financialAssessmentDTO.getFullAvailable())
                .isEqualTo(apiMeansAssessmentResponse.getFullAssessmentAvailable());
        softly.assertThat(initialAssessmentDTO.getLowerThreshold())
                .isEqualTo(apiMeansAssessmentResponse.getLowerThreshold().doubleValue());
        softly.assertThat(initialAssessmentDTO.getUpperThreshold())
                .isEqualTo(apiMeansAssessmentResponse.getUpperThreshold().doubleValue());
        softly.assertThat(initialAssessmentDTO.getTotalAggregatedIncome())
                .isEqualTo(apiMeansAssessmentResponse.getTotalAggregatedIncome().doubleValue());
        softly.assertThat(initialAssessmentDTO.getAdjustedIncomeValue())
                .isEqualTo(apiMeansAssessmentResponse.getAdjustedIncomeValue().doubleValue());
        softly.assertThat(initialAssessmentDTO.getResult())
                .isEqualTo(apiMeansAssessmentResponse.getInitResult());
        softly.assertThat(initialAssessmentDTO.getResultReason())
                .isEqualTo(apiMeansAssessmentResponse.getInitResultReason());
        softly.assertThat(initialAssessmentDTO.getCriteriaId())
                .isEqualTo(apiMeansAssessmentResponse.getCriteriaId().intValue());
        softly.assertThat(initialAssessmentDTO.getId())
                .isEqualTo(apiMeansAssessmentResponse.getAssessmentId().intValue());
        softly.assertThat(initialAssessmentDTO.getReviewType().getCode())
                .isEqualTo(apiMeansAssessmentResponse.getReviewType().getCode());
        softly.assertThat(sectionSummaries.size())
                .isEqualTo(assessmentSectionSummaries.size() );
        softly.assertThat(assessmentSectionSummaryDTO.getAnnualTotal())
                .isEqualTo(assessmentSectionSummary.getAnnualTotal().doubleValue());
        softly.assertThat(assessmentSectionSummaryDTO.getApplicantAnnualTotal())
                .isEqualTo(assessmentSectionSummary.getApplicantAnnualTotal().doubleValue());
        softly.assertThat(assessmentSectionSummaryDTO.getPartnerAnnualTotal())
                .isEqualTo(assessmentSectionSummary.getPartnerAnnualTotal().doubleValue());
        softly.assertThat(assessmentDetail.getId())
                .isEqualTo(apiAssessmentDetail.getId().longValue());
        softly.assertThat(childWeightingDTO.getId())
                .isEqualTo(apiAssessmentChildWeighting.getId().intValue());
    }

    @Test
    void givenInitialAssessment_whenApiRollbackMeansAssessmentResponseToApplicationDtoIsInvoked_thenMappingIsCorrect() {
        ApiRollbackMeansAssessmentResponse apiRollbackMeansAssessmentResponse =
                MeansAssessmentDataBuilder.getApiRollbackMeansAssessmentResponse(AssessmentType.INIT.getType());
        ApplicationDTO applicationDTO = getApplicationDTOForMeansAssessmentMapper(false);
        InitialAssessmentDTO initialAssessmentDTO = applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getInitial();
        meansAssessmentMapper.apiRollbackMeansAssessmentResponseToApplicationDto(
                apiRollbackMeansAssessmentResponse, applicationDTO);

        softly.assertThat(initialAssessmentDTO.getResult())
                .isEqualTo(apiRollbackMeansAssessmentResponse.getInitResult());
        softly.assertThat(initialAssessmentDTO.getAssessmnentStatusDTO().getStatus())
                .isEqualTo(apiRollbackMeansAssessmentResponse.getFassInitStatus().getStatus());
        softly.assertThat(initialAssessmentDTO.getAssessmnentStatusDTO().getDescription())
                .isEqualTo(apiRollbackMeansAssessmentResponse.getFassInitStatus().getDescription());
    }

    @Test
    void givenFullAssessment_whenApiRollbackMeansAssessmentResponseToApplicationDtoIsInvoked_thenMappingIsCorrect() {
        ApiRollbackMeansAssessmentResponse apiRollbackMeansAssessmentResponse =
                MeansAssessmentDataBuilder.getApiRollbackMeansAssessmentResponse(AssessmentType.FULL.getType());
        ApplicationDTO applicationDTO = getApplicationDTOForMeansAssessmentMapper(true);
        FullAssessmentDTO fullAssessmentDTO = applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getFull();
        meansAssessmentMapper.apiRollbackMeansAssessmentResponseToApplicationDto(
                apiRollbackMeansAssessmentResponse, applicationDTO);

        softly.assertThat(fullAssessmentDTO.getResult())
                .isEqualTo(apiRollbackMeansAssessmentResponse.getFullResult());
        softly.assertThat(fullAssessmentDTO.getAssessmnentStatusDTO().getStatus())
                .isEqualTo(apiRollbackMeansAssessmentResponse.getFassFullStatus().getStatus());
        softly.assertThat(fullAssessmentDTO.getAssessmnentStatusDTO().getDescription())
                .isEqualTo(apiRollbackMeansAssessmentResponse.getFassFullStatus().getDescription());
    }}
