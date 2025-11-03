package uk.gov.justice.laa.crime.orchestration.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getApplicationDTOForMeansAssessmentMapper;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getPassportedDTO;
import static uk.gov.justice.laa.crime.util.DateUtil.toLocalDateTime;

import uk.gov.justice.laa.crime.common.model.meansassessment.ApiAssessmentChildWeighting;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidenceSummary;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiRollbackMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.enums.AssessmentType;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicantDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentDetailDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSectionSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CaseDetailDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CaseManagementUnitDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ChildWeightingDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CrownCourtOverviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CrownCourtSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.EmploymentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.EvidenceDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ExtraEvidenceDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FullAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IncomeEvidenceSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.InitialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.util.DateUtil;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class MeansAssessmentMapperTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    UserMapper userMapper = new UserMapper();
    MeansAssessmentMapper meansAssessmentMapper = new MeansAssessmentMapper(userMapper);

    @Test
    void givenMeansAssessmentResponse_whenMappedToFinancialAssessmentDTO_thenMappingIsCorrect() {
        FinancialAssessmentDTO actual = meansAssessmentMapper.getMeansAssessmentResponseToFinancialAssessmentDto(
                MeansAssessmentDataBuilder.getApiGetMeansAssessmentResponse(), Constants.APPLICANT_ID);
        FinancialAssessmentDTO expected = MeansAssessmentDataBuilder.getFinancialAssessmentDto();

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    void givenWorkflowRequestAndAction_whenGetUserActionDtoIsInvokedDtoIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        UserActionDTO actual =
                meansAssessmentMapper.getUserActionDto(workflowRequest, TestModelDataBuilder.TEST_ACTION);
        UserActionDTO expected = TestModelDataBuilder.getUserActionDTO();

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    void givenValidWorkflowRequest_whenMeansAssessmentMapperIsInvokedForCreateAssessment_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest =
                meansAssessmentMapper.workflowRequestToCreateAssessmentRequest(workflowRequest);
        assertApiMeansAssessmentRequestAttributes(workflowRequest, apiCreateMeansAssessmentRequest);
        assertApiCreateMeansAssessmentRequestAttributes(workflowRequest, apiCreateMeansAssessmentRequest);
    }

    @Test
    void givenValidWorkflowRequest_whenMeansAssessmentMapperIsInvokedForUpdateAssessment_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        ApiUpdateMeansAssessmentRequest apiUpdateMeansAssessmentRequest =
                meansAssessmentMapper.workflowRequestToUpdateAssessmentRequest(workflowRequest);
        assertApiMeansAssessmentRequestAttributes(workflowRequest, apiUpdateMeansAssessmentRequest);
        assertApiUpdateMeansAssessmentRequestAttributes(workflowRequest, apiUpdateMeansAssessmentRequest);
    }

    private void assertApiMeansAssessmentRequestAttributes(
            WorkflowRequest workflowRequest, ApiMeansAssessmentRequest apiMeansAssessmentRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        AssessmentDTO assessmentDTO = applicationDTO.getAssessmentDTO();
        FinancialAssessmentDTO financialAssessmentDTO = assessmentDTO.getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        AssessmentStatusDTO assessmentStatusDTO = initialAssessmentDTO.getAssessmnentStatusDTO();
        CaseManagementUnitDTO caseManagementUnitDTO = applicationDTO.getCaseManagementUnitDTO();
        CaseDetailDTO caseDetailsDTO = applicationDTO.getCaseDetailsDTO();
        ChildWeightingDTO childWeightingDTO =
                initialAssessmentDTO.getChildWeightings().iterator().next();
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
        softly.assertThat(apiMeansAssessmentRequest
                        .getCrownCourtOverview()
                        .getCrownCourtSummary()
                        .getRepOrderDecision())
                .isEqualTo(crownCourtSummaryDTO.getRepOrderDecision().getValue());
        softly.assertThat(apiMeansAssessmentRequest.getSectionSummaries().size())
                .isEqualTo(1);
        softly.assertThat(incomeEvidenceSummary.getIncomeEvidenceNotes())
                .isEqualTo(incomeEvidence.getIncomeEvidenceNotes());
        softly.assertThat(apiMeansAssessmentRequest.getInitialAssessmentDate())
                .isEqualTo(toLocalDateTime(initialAssessmentDTO.getAssessmentDate()));
        softly.assertThat(apiMeansAssessmentRequest.getMagCourtOutcome())
                .isEqualTo(MagCourtOutcome.getFrom(
                        applicationDTO.getMagsOutcomeDTO().getOutcome()));
        softly.assertThat(apiMeansAssessmentRequest.getUserSession())
                .isEqualTo(userMapper.userDtoToUserSession(workflowRequest.getUserDTO()));
        softly.assertThat(apiMeansAssessmentRequest.getNewWorkReason())
                .isEqualTo(NewWorkReason.getFrom(
                        initialAssessmentDTO.getNewWorkReason().getCode()));
        softly.assertThat(apiMeansAssessmentRequest.getNewWorkReason())
                .isEqualTo(NewWorkReason.getFrom(
                        initialAssessmentDTO.getNewWorkReason().getCode()));
        softly.assertThat(apiMeansAssessmentRequest.getHasPartner()).isEqualTo(applicationDTO.getApplicantHasPartner());
        softly.assertThat(apiMeansAssessmentRequest.getPartnerContraryInterest())
                .isEqualTo(Boolean.TRUE);
        softly.assertThat(apiMeansAssessmentRequest.getEmploymentStatus()).isEqualTo(employmentStatusDTO.getCode());
        softly.assertThat(apiMeansAssessmentRequest.getLaaTransactionId()).isNotEmpty();
        softly.assertThat(apiMeansAssessmentRequest.getOtherBenefitNote())
                .isEqualTo(initialAssessmentDTO.getOtherBenefitNote());
        softly.assertThat(apiMeansAssessmentRequest.getOtherIncomeNote())
                .isEqualTo(initialAssessmentDTO.getOtherIncomeNote());
    }

    private void assertApiUpdateMeansAssessmentRequestAttributes(
            WorkflowRequest workflowRequest, ApiUpdateMeansAssessmentRequest apiUpdateMeansAssessmentRequest) {

        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        AssessmentDTO assessmentDTO = applicationDTO.getAssessmentDTO();
        FinancialAssessmentDTO financialAssessmentDTO = assessmentDTO.getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();

        softly.assertThat(apiUpdateMeansAssessmentRequest.getFinancialAssessmentId())
                .isEqualTo(financialAssessmentDTO.getId().intValue());
        softly.assertThat(apiUpdateMeansAssessmentRequest.getFullAssessmentDate())
                .isEqualTo(toLocalDateTime(fullAssessmentDTO.getAssessmentDate()));
        softly.assertThat(apiUpdateMeansAssessmentRequest.getOtherHousingNote())
                .isEqualTo(fullAssessmentDTO.getOtherHousingNote());
        softly.assertThat(apiUpdateMeansAssessmentRequest.getInitTotalAggregatedIncome())
                .isEqualTo(BigDecimal.valueOf(initialAssessmentDTO.getTotalAggregatedIncome()));
        softly.assertThat(apiUpdateMeansAssessmentRequest.getFullAssessmentNotes())
                .isEqualTo(fullAssessmentDTO.getAssessmentNotes());

        assertIncomeEvidenceAttributes(workflowRequest, apiUpdateMeansAssessmentRequest);
    }

    private void assertIncomeEvidenceAttributes(
            WorkflowRequest workflowRequest, ApiUpdateMeansAssessmentRequest apiUpdateMeansAssessmentRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        AssessmentDTO assessmentDTO = applicationDTO.getAssessmentDTO();
        IncomeEvidenceSummaryDTO incomeEvidenceSummary =
                assessmentDTO.getFinancialAssessmentDTO().getIncomeEvidence();

        Collection<ExtraEvidenceDTO> extraEvidenceList = incomeEvidenceSummary.getExtraEvidenceList();
        Collection<EvidenceDTO> partnerIncomeEvidenceList = incomeEvidenceSummary.getPartnerIncomeEvidenceList();
        Collection<EvidenceDTO> applicantIncomeEvidenceList = incomeEvidenceSummary.getApplicantIncomeEvidenceList();

        List<ApiIncomeEvidence> incomeEvidence = apiUpdateMeansAssessmentRequest.getIncomeEvidence();

        // Assert the total size of the income evidence list
        softly.assertThat(incomeEvidence)
                .hasSize(applicantIncomeEvidenceList.size()
                        + partnerIncomeEvidenceList.size()
                        + extraEvidenceList.size());

        // Helper method to assert income evidence
        BiConsumer<EvidenceDTO, ApiIncomeEvidence> assertCommonFields = (evidenceDTO, apiIncomeEvidence) -> {
            softly.assertThat(apiIncomeEvidence.getIncomeEvidence())
                    .isEqualTo(evidenceDTO.getEvidenceTypeDTO().getEvidence());

            softly.assertThat(apiIncomeEvidence.getDateReceived())
                    .isEqualTo(DateUtil.toLocalDateTime(evidenceDTO.getDateReceived()));

            softly.assertThat(apiIncomeEvidence.getOtherText()).isNull();
        };

        // Method to find the corresponding ApiIncomeEvidence
        Function<EvidenceDTO, ApiIncomeEvidence> findApiIncomeEvidence = evidenceDTO -> incomeEvidence.stream()
                .filter(item -> item.getId().equals(evidenceDTO.getId().intValue()))
                .findFirst()
                .orElse(null);

        applicantIncomeEvidenceList.forEach(evidenceDTO -> {
            ApiIncomeEvidence apiIncomeEvidence = incomeEvidence.stream()
                    .filter(item -> item.getId().equals(evidenceDTO.getId().intValue()))
                    .findFirst()
                    .orElse(null);

            softly.assertThat(apiIncomeEvidence.getApplicantId())
                    .isEqualTo(applicationDTO.getApplicantDTO().getId().intValue());

            assertCommonFields.accept(evidenceDTO, apiIncomeEvidence);
        });

        partnerIncomeEvidenceList.forEach(evidenceDTO -> {
            ApiIncomeEvidence apiIncomeEvidence = incomeEvidence.stream()
                    .filter(item -> item.getId().equals(evidenceDTO.getId().intValue()))
                    .findFirst()
                    .orElse(null);

            softly.assertThat(apiIncomeEvidence.getApplicantId())
                    .isEqualTo(NumberUtils.toInteger(TestModelDataBuilder.PARTNER_ID));

            assertCommonFields.accept(evidenceDTO, apiIncomeEvidence);
        });

        // Additional assertions for extra evidence list
        extraEvidenceList.forEach(extraEvidenceDTO -> {
            ApiIncomeEvidence apiIncomeEvidence = findApiIncomeEvidence.apply(extraEvidenceDTO);
            assertCommonFields.accept(extraEvidenceDTO, apiIncomeEvidence);

            softly.assertThat(apiIncomeEvidence.getMandatory()).isEqualTo("Y");

            softly.assertThat(apiIncomeEvidence.getAdhoc()).isEqualTo(extraEvidenceDTO.getAdhoc());

            softly.assertThat(apiIncomeEvidence.getOtherText()).isEqualTo(extraEvidenceDTO.getOtherText());
        });
    }

    private void assertApiCreateMeansAssessmentRequestAttributes(
            WorkflowRequest workflowRequest, ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        AssessmentDTO assessmentDTO = applicationDTO.getAssessmentDTO();
        FinancialAssessmentDTO financialAssessmentDTO = assessmentDTO.getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        softly.assertThat(apiCreateMeansAssessmentRequest.getUsn()).isEqualTo(applicationDTO.getUsn());
        softly.assertThat(apiCreateMeansAssessmentRequest.getReviewType().getCode())
                .isEqualTo(initialAssessmentDTO.getReviewType().getCode());
    }

    @Test
    void givenValidMeansAssessmentResponse_whenMeansAssessmentMapperIsInvoked_thenPassportDTOIsClearedOut() {
        ApplicationDTO applicationDTO = getApplicationDTOForMeansAssessmentMapper(true);
        applicationDTO.setPassportedDTO(getPassportedDTO());
        ApiMeansAssessmentResponse apiMeansAssessmentResponse =
                MeansAssessmentDataBuilder.getApiMeansAssessmentResponse();
        meansAssessmentMapper.meansAssessmentResponseToApplicationDto(apiMeansAssessmentResponse, applicationDTO);
        assertThat(applicationDTO.getPassportedDTO())
                .isEqualTo(PassportedDTO.builder().build());
    }

    @Test
    void givenValidMeansAssessmentResponse_whenMeansAssessmentMapperIsInvokedForFullAssessment_thenMappingIsCorrect() {
        ApplicationDTO applicationDTO = getApplicationDTOForMeansAssessmentMapper(true);
        FinancialAssessmentDTO financialAssessmentDTO =
                applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO();
        FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();
        ApiMeansAssessmentResponse apiMeansAssessmentResponse =
                MeansAssessmentDataBuilder.getApiMeansAssessmentResponse();

        meansAssessmentMapper.meansAssessmentResponseToApplicationDto(apiMeansAssessmentResponse, applicationDTO);

        softly.assertThat(applicationDTO.getRepId())
                .isEqualTo(apiMeansAssessmentResponse.getRepId().intValue());
        softly.assertThat(applicationDTO.getTimestamp().toLocalDateTime())
                .isEqualTo(apiMeansAssessmentResponse.getApplicationTimestamp());
        softly.assertThat(financialAssessmentDTO.getId())
                .isEqualTo(apiMeansAssessmentResponse.getAssessmentId().intValue());
        softly.assertThat(financialAssessmentDTO.getTimestamp().toLocalDateTime())
                .isEqualTo(apiMeansAssessmentResponse.getUpdated());
        softly.assertThat(financialAssessmentDTO.getDateCompleted())
                .isEqualTo(apiMeansAssessmentResponse.getDateCompleted());
        softly.assertThat(fullAssessmentDTO.getResult()).isEqualTo(apiMeansAssessmentResponse.getFullResult());
        softly.assertThat(fullAssessmentDTO.getAdjustedLivingAllowance())
                .isEqualTo(
                        apiMeansAssessmentResponse.getAdjustedLivingAllowance().doubleValue());
        softly.assertThat(fullAssessmentDTO.getResultReason())
                .isEqualTo(apiMeansAssessmentResponse.getFullResultReason());
        softly.assertThat(fullAssessmentDTO.getTotalAggregatedExpense())
                .isEqualTo(
                        apiMeansAssessmentResponse.getTotalAggregatedExpense().doubleValue());
        softly.assertThat(fullAssessmentDTO.getTotalAnnualDisposableIncome())
                .isEqualTo(apiMeansAssessmentResponse
                        .getTotalAnnualDisposableIncome()
                        .doubleValue());
        softly.assertThat(fullAssessmentDTO.getThreshold())
                .isEqualTo(apiMeansAssessmentResponse.getFullThreshold().doubleValue());
        softly.assertThat(fullAssessmentDTO.getSectionSummaries().size()).isEqualTo(1);
    }

    @Test
    void givenValidMeansAssessmentResponse_whenMeansAssessmentMapperIsInvoked_thenMappingIsCorrect() {
        ApiMeansAssessmentResponse apiMeansAssessmentResponse =
                MeansAssessmentDataBuilder.getApiMeansAssessmentResponse();
        ApplicationDTO applicationDTO = getApplicationDTOForMeansAssessmentMapper(false);
        FinancialAssessmentDTO financialAssessmentDTO =
                applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        Collection<AssessmentSectionSummaryDTO> sectionSummaries = initialAssessmentDTO.getSectionSummaries();
        AssessmentSectionSummaryDTO assessmentSectionSummaryDTO =
                sectionSummaries.iterator().next();
        List<ApiAssessmentSectionSummary> assessmentSectionSummaries =
                apiMeansAssessmentResponse.getAssessmentSectionSummary();
        ApiAssessmentSectionSummary assessmentSectionSummary = assessmentSectionSummaries.get(0);
        AssessmentDetailDTO assessmentDetail =
                assessmentSectionSummaryDTO.getAssessmentDetail().iterator().next();
        ApiAssessmentDetail apiAssessmentDetail =
                assessmentSectionSummary.getAssessmentDetails().get(0);
        ChildWeightingDTO childWeightingDTO =
                initialAssessmentDTO.getChildWeightings().iterator().next();
        ApiAssessmentChildWeighting apiAssessmentChildWeighting =
                apiMeansAssessmentResponse.getChildWeightings().get(0);

        IncomeEvidenceSummaryDTO incomeEvidence =
                applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence();
        incomeEvidence.getApplicantIncomeEvidenceList().add(new EvidenceDTO());

        meansAssessmentMapper.meansAssessmentResponseToApplicationDto(apiMeansAssessmentResponse, applicationDTO);

        softly.assertThat(applicationDTO.getRepId())
                .isEqualTo(apiMeansAssessmentResponse.getRepId().intValue());
        softly.assertThat(applicationDTO.getTimestamp().toLocalDateTime())
                .isEqualTo(apiMeansAssessmentResponse.getApplicationTimestamp());
        softly.assertThat(financialAssessmentDTO.getId())
                .isEqualTo(apiMeansAssessmentResponse.getAssessmentId().intValue());
        softly.assertThat(financialAssessmentDTO.getTimestamp().toLocalDateTime())
                .isEqualTo(apiMeansAssessmentResponse.getUpdated());
        softly.assertThat(financialAssessmentDTO.getDateCompleted())
                .isEqualTo(apiMeansAssessmentResponse.getDateCompleted());
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
        softly.assertThat(initialAssessmentDTO.getResult()).isEqualTo(apiMeansAssessmentResponse.getInitResult());
        softly.assertThat(initialAssessmentDTO.getResultReason())
                .isEqualTo(apiMeansAssessmentResponse.getInitResultReason());
        softly.assertThat(initialAssessmentDTO.getCriteriaId())
                .isEqualTo(apiMeansAssessmentResponse.getCriteriaId().intValue());
        softly.assertThat(initialAssessmentDTO.getId())
                .isEqualTo(apiMeansAssessmentResponse.getAssessmentId().intValue());
        softly.assertThat(initialAssessmentDTO.getReviewType().getCode())
                .isEqualTo(apiMeansAssessmentResponse.getReviewType().getCode());
        softly.assertThat(sectionSummaries.size()).isEqualTo(assessmentSectionSummaries.size());
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

        // Check that income evidence records are cleared - this will be populated during post-processing
        softly.assertThat(incomeEvidence.getExtraEvidenceList()).isEmpty();
        softly.assertThat(incomeEvidence.getPartnerIncomeEvidenceList()).isEmpty();
        softly.assertThat(incomeEvidence.getApplicantIncomeEvidenceList()).isEmpty();
    }

    @Test
    void givenInitialAssessment_whenApiRollbackMeansAssessmentResponseToApplicationDtoIsInvoked_thenMappingIsCorrect() {
        ApiRollbackMeansAssessmentResponse apiRollbackMeansAssessmentResponse =
                MeansAssessmentDataBuilder.getApiRollbackMeansAssessmentResponse(AssessmentType.INIT.getType());
        ApplicationDTO applicationDTO = getApplicationDTOForMeansAssessmentMapper(false);
        InitialAssessmentDTO initialAssessmentDTO =
                applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getInitial();
        meansAssessmentMapper.apiRollbackMeansAssessmentResponseToApplicationDto(
                apiRollbackMeansAssessmentResponse, applicationDTO);

        softly.assertThat(initialAssessmentDTO.getResult())
                .isEqualTo(apiRollbackMeansAssessmentResponse.getInitResult());
        softly.assertThat(initialAssessmentDTO.getAssessmnentStatusDTO().getStatus())
                .isEqualTo(
                        apiRollbackMeansAssessmentResponse.getFassInitStatus().getStatus());
        softly.assertThat(initialAssessmentDTO.getAssessmnentStatusDTO().getDescription())
                .isEqualTo(
                        apiRollbackMeansAssessmentResponse.getFassInitStatus().getDescription());
    }

    @Test
    void givenFullAssessment_whenApiRollbackMeansAssessmentResponseToApplicationDtoIsInvoked_thenMappingIsCorrect() {
        ApiRollbackMeansAssessmentResponse apiRollbackMeansAssessmentResponse =
                MeansAssessmentDataBuilder.getApiRollbackMeansAssessmentResponse(AssessmentType.FULL.getType());
        ApplicationDTO applicationDTO = getApplicationDTOForMeansAssessmentMapper(true);
        FullAssessmentDTO fullAssessmentDTO =
                applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getFull();
        meansAssessmentMapper.apiRollbackMeansAssessmentResponseToApplicationDto(
                apiRollbackMeansAssessmentResponse, applicationDTO);

        softly.assertThat(fullAssessmentDTO.getResult()).isEqualTo(apiRollbackMeansAssessmentResponse.getFullResult());
        softly.assertThat(fullAssessmentDTO.getAssessmnentStatusDTO().getStatus())
                .isEqualTo(
                        apiRollbackMeansAssessmentResponse.getFassFullStatus().getStatus());
        softly.assertThat(fullAssessmentDTO.getAssessmnentStatusDTO().getDescription())
                .isEqualTo(
                        apiRollbackMeansAssessmentResponse.getFassFullStatus().getDescription());
    }

    @Test
    void givenNullSectionSummaries_whenSectionSummariesBuilderIsInvoked_thenReturnsEmptyList() {
        List<ApiAssessmentSectionSummary> result = meansAssessmentMapper.sectionSummariesBuilder(null);
        softly.assertThat(result).isEmpty();
    }

    @Test
    void givenEmptySectionSummaries_whenSectionSummariesBuilderIsInvoked_thenReturnsEmptyList() {
        List<ApiAssessmentSectionSummary> result =
                meansAssessmentMapper.sectionSummariesBuilder(Collections.emptyList());
        softly.assertThat(result).isEmpty();
    }

    @Test
    void givenSectionSummariesContainingNullElements_whenSectionSummariesBuilderIsInvoked_thenSkipsNullElements() {
        List<AssessmentSectionSummaryDTO> input = Arrays.asList(null, null);
        List<ApiAssessmentSectionSummary> result = meansAssessmentMapper.sectionSummariesBuilder(input);
        softly.assertThat(result).isEmpty();
    }

    @Test
    void givenDtoWithAllNonNullValues_whenSectionSummariesBuilderIsInvoked_thenMapsAllFieldsCorrectly() {
        AssessmentSectionSummaryDTO dto = new AssessmentSectionSummaryDTO();
        dto.setAnnualTotal(1000.0);
        dto.setApplicantAnnualTotal(600.0);
        dto.setPartnerAnnualTotal(400.0);
        dto.setSection("Section A");
        dto.setAssessmentDetail(Collections.emptyList());

        List<ApiAssessmentSectionSummary> result =
                meansAssessmentMapper.sectionSummariesBuilder(Collections.singletonList(dto));
        softly.assertThat(result).hasSize(1);

        ApiAssessmentSectionSummary apiDto = result.get(0);
        softly.assertThat(apiDto.getAnnualTotal()).isEqualTo(BigDecimal.valueOf(1000.0));
        softly.assertThat(apiDto.getApplicantAnnualTotal()).isEqualTo(BigDecimal.valueOf(600.0));
        softly.assertThat(apiDto.getPartnerAnnualTotal()).isEqualTo(BigDecimal.valueOf(400.0));
        softly.assertThat(apiDto.getSection()).isEqualTo("Section A");
        softly.assertThat(apiDto.getAssessmentDetails()).isEmpty();
    }

    @Test
    void givenDtoWithSomeNullValues_whenSectionSummariesBuilderIsInvoked_thenMappingPreservesNulls() {
        AssessmentSectionSummaryDTO dto = new AssessmentSectionSummaryDTO();
        // annualTotal is null
        dto.setAnnualTotal(null);
        // applicantAnnualTotal is non-null
        dto.setApplicantAnnualTotal(500.0);
        // partnerAnnualTotal is null
        dto.setPartnerAnnualTotal(null);
        dto.setSection("Section B");
        dto.setAssessmentDetail(Collections.emptyList());

        List<ApiAssessmentSectionSummary> result =
                meansAssessmentMapper.sectionSummariesBuilder(Collections.singletonList(dto));
        softly.assertThat(result).hasSize(1);

        ApiAssessmentSectionSummary apiDto = result.get(0);
        softly.assertThat(apiDto.getAnnualTotal()).isNull();
        softly.assertThat(apiDto.getApplicantAnnualTotal()).isEqualTo(BigDecimal.valueOf(500.0));
        softly.assertThat(apiDto.getPartnerAnnualTotal()).isNull();
        softly.assertThat(apiDto.getSection()).isEqualTo("Section B");
        softly.assertThat(apiDto.getAssessmentDetails()).isEmpty();
    }

    @Test
    void givenMultipleDtoElements_whenSectionSummariesBuilderIsInvoked_thenMapsEachElementCorrectly() {
        AssessmentSectionSummaryDTO dto1 = new AssessmentSectionSummaryDTO();
        dto1.setAnnualTotal(1500.0);
        dto1.setApplicantAnnualTotal(800.0);
        dto1.setPartnerAnnualTotal(700.0);
        dto1.setSection("Section 1");
        dto1.setAssessmentDetail(Collections.emptyList());

        AssessmentSectionSummaryDTO dto2 = new AssessmentSectionSummaryDTO();
        dto2.setAnnualTotal(null);
        dto2.setApplicantAnnualTotal(null);
        dto2.setPartnerAnnualTotal(300.0);
        dto2.setSection("Section 2");
        dto2.setAssessmentDetail(Collections.emptyList());

        List<AssessmentSectionSummaryDTO> input = Arrays.asList(dto1, dto2);
        List<ApiAssessmentSectionSummary> result = meansAssessmentMapper.sectionSummariesBuilder(input);

        softly.assertThat(result).hasSize(2);

        ApiAssessmentSectionSummary apiDto1 = result.get(0);
        softly.assertThat(apiDto1.getAnnualTotal()).isEqualTo(BigDecimal.valueOf(1500.0));
        softly.assertThat(apiDto1.getApplicantAnnualTotal()).isEqualTo(BigDecimal.valueOf(800.0));
        softly.assertThat(apiDto1.getPartnerAnnualTotal()).isEqualTo(BigDecimal.valueOf(700.0));
        softly.assertThat(apiDto1.getSection()).isEqualTo("Section 1");
        softly.assertThat(apiDto1.getAssessmentDetails()).isEmpty();

        ApiAssessmentSectionSummary apiDto2 = result.get(1);
        softly.assertThat(apiDto2.getAnnualTotal()).isNull();
        softly.assertThat(apiDto2.getApplicantAnnualTotal()).isNull();
        softly.assertThat(apiDto2.getPartnerAnnualTotal()).isEqualTo(BigDecimal.valueOf(300.0));
        softly.assertThat(apiDto2.getSection()).isEqualTo("Section 2");
        softly.assertThat(apiDto2.getAssessmentDetails()).isEmpty();
    }
}
