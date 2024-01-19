package uk.gov.justice.laa.crime.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.AppealType;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static uk.gov.justice.laa.crime.util.DateUtil.toDate;
import static uk.gov.justice.laa.crime.util.DateUtil.toLocalDateTime;

@ExtendWith(SoftAssertionsExtension.class)
class ContributionMapperTest {

    ContributionMapper contributionMapper = new ContributionMapper();
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenValidWorkflowRequest_whenContributionMapperIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder
                .buildWorkFlowRequest();
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        CrownCourtOverviewDTO crownCourtOverviewDTO = applicationDTO.getCrownCourtOverviewDTO();
        ContributionsDTO contribution = crownCourtOverviewDTO.getContribution();
        CaseDetailDTO caseDetailsDTO = applicationDTO.getCaseDetailsDTO();
        OffenceDTO offenceDTO = applicationDTO.getOffenceDTO();
        AppealDTO appealDTO = crownCourtOverviewDTO.getAppealDTO();
        AppealTypeDTO appealTypeDTO = appealDTO.getAppealTypeDTO();

        ApiMaatCalculateContributionRequest apiMaatCalculateContributionRequest = contributionMapper
                .workflowRequestToMaatCalculateContributionRequest(workflowRequest);
        softly.assertThat(apiMaatCalculateContributionRequest.getUserCreated())
                .isEqualTo(workflowRequest.getUserDTO().getUserName());
        softly.assertThat(apiMaatCalculateContributionRequest.getRepId())
                .isEqualTo(NumberUtils.toInteger(applicationDTO.getRepId()));
        softly.assertThat(apiMaatCalculateContributionRequest.getApplId())
                .isEqualTo(NumberUtils.toInteger(applicationDTO.getApplicantDTO().getId()));
        softly.assertThat(apiMaatCalculateContributionRequest.getMagCourtOutcome())
                .isEqualTo(MagCourtOutcome.getFrom(applicationDTO.getMagsOutcomeDTO().getOutcome()));
        softly.assertThat(apiMaatCalculateContributionRequest.getCommittalDate())
                .isEqualTo(toLocalDateTime(applicationDTO.getCommittalDate()));
        softly.assertThat(apiMaatCalculateContributionRequest.getCaseType().getCaseType())
                .isEqualTo(caseDetailsDTO.getCaseType());
        softly.assertThat(apiMaatCalculateContributionRequest.getAssessments().size())
                .isGreaterThan(0);
        softly.assertThat(apiMaatCalculateContributionRequest.getContributionCap())
                .isEqualTo(BigDecimal.valueOf(offenceDTO.getContributionCap()));
        softly.assertThat(apiMaatCalculateContributionRequest.getContributionId())
                .isEqualTo(NumberUtils.toInteger(contribution.getId()));
        softly.assertThat(apiMaatCalculateContributionRequest.getMonthlyContributions())
                .isEqualTo(contribution.getMonthlyContribs());
        softly.assertThat(apiMaatCalculateContributionRequest.getEffectiveDate())
                .isEqualTo(toLocalDateTime(contribution.getEffectiveDate()));
        softly.assertThat(apiMaatCalculateContributionRequest.getUpfrontContributions())
                .isEqualTo(contribution.getUpfrontContribs());
        softly.assertThat(apiMaatCalculateContributionRequest.getRemoveContributions())
                .isEqualTo(applicationDTO.getStatusDTO().getRemoveContribs().toString());
        softly.assertThat(apiMaatCalculateContributionRequest.getMagCourtOutcome())
                .isEqualTo(MagCourtOutcome.getFrom(applicationDTO.getMagsOutcomeDTO().getOutcome()));
        softly.assertThat(apiMaatCalculateContributionRequest.getAppealType())
                .isEqualTo(AppealType.getFrom(appealTypeDTO.getCode()));
        softly.assertThat(apiMaatCalculateContributionRequest.getCrownCourtOutcome().size())
                .isGreaterThan(0);
        softly.assertThat(apiMaatCalculateContributionRequest.getLastOutcome().getOutcome())
                        .isEqualTo(CrownCourtAppealOutcome.SUCCESSFUL);
        softly.assertAll();

    }

    @Test
    void givenWorkflowRequestWithNoAppealOutcome_whenContributionMapperIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder
                .buildWorkFlowRequest();
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        CrownCourtOverviewDTO crownCourtOverviewDTO = applicationDTO.getCrownCourtOverviewDTO();
        crownCourtOverviewDTO.getCrownCourtSummaryDTO().setOutcomeDTOs(
                List.of(TestModelDataBuilder.getOutcomeDTO(CourtType.CROWN_COURT)));
        ContributionsDTO contribution = crownCourtOverviewDTO.getContribution();
        CaseDetailDTO caseDetailsDTO = applicationDTO.getCaseDetailsDTO();
        OffenceDTO offenceDTO = applicationDTO.getOffenceDTO();
        AppealDTO appealDTO = crownCourtOverviewDTO.getAppealDTO();
        AppealTypeDTO appealTypeDTO = appealDTO.getAppealTypeDTO();

        ApiMaatCalculateContributionRequest apiMaatCalculateContributionRequest = contributionMapper
                .workflowRequestToMaatCalculateContributionRequest(workflowRequest);
        softly.assertThat(apiMaatCalculateContributionRequest.getUserCreated())
                .isEqualTo(workflowRequest.getUserDTO().getUserName());
        softly.assertThat(apiMaatCalculateContributionRequest.getRepId())
                .isEqualTo(NumberUtils.toInteger(applicationDTO.getRepId()));
        softly.assertThat(apiMaatCalculateContributionRequest.getApplId())
                .isEqualTo(NumberUtils.toInteger(applicationDTO.getApplicantDTO().getId()));
        softly.assertThat(apiMaatCalculateContributionRequest.getMagCourtOutcome())
                .isEqualTo(MagCourtOutcome.getFrom(applicationDTO.getMagsOutcomeDTO().getOutcome()));
        softly.assertThat(apiMaatCalculateContributionRequest.getCommittalDate())
                .isEqualTo(toLocalDateTime(applicationDTO.getCommittalDate()));
        softly.assertThat(apiMaatCalculateContributionRequest.getCaseType().getCaseType())
                .isEqualTo(caseDetailsDTO.getCaseType());
        softly.assertThat(apiMaatCalculateContributionRequest.getAssessments().size())
                .isGreaterThan(0);
        softly.assertThat(apiMaatCalculateContributionRequest.getContributionCap())
                .isEqualTo(BigDecimal.valueOf(offenceDTO.getContributionCap()));
        softly.assertThat(apiMaatCalculateContributionRequest.getContributionId())
                .isEqualTo(NumberUtils.toInteger(contribution.getId()));
        softly.assertThat(apiMaatCalculateContributionRequest.getMonthlyContributions())
                .isEqualTo(contribution.getMonthlyContribs());
        softly.assertThat(apiMaatCalculateContributionRequest.getEffectiveDate())
                .isEqualTo(toLocalDateTime(contribution.getEffectiveDate()));
        softly.assertThat(apiMaatCalculateContributionRequest.getUpfrontContributions())
                .isEqualTo(contribution.getUpfrontContribs());
        softly.assertThat(apiMaatCalculateContributionRequest.getRemoveContributions())
                .isEqualTo(applicationDTO.getStatusDTO().getRemoveContribs().toString());
        softly.assertThat(apiMaatCalculateContributionRequest.getMagCourtOutcome())
                .isEqualTo(MagCourtOutcome.getFrom(applicationDTO.getMagsOutcomeDTO().getOutcome()));
        softly.assertThat(apiMaatCalculateContributionRequest.getAppealType())
                .isEqualTo(AppealType.getFrom(appealTypeDTO.getCode()));
        softly.assertThat(apiMaatCalculateContributionRequest.getCrownCourtOutcome().size())
                .isGreaterThan(0);
        softly.assertThat(apiMaatCalculateContributionRequest.getLastOutcome()).isNull();
        softly.assertAll();

    }

    @Test
    void givenValidApplicationDTO_whenContributionMapperIsInvoked_thenMappingIsCorrect() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.getApplicationDTO();
        ApiMaatCheckContributionRuleRequest request = contributionMapper.applicationDtoToCheckContributionRuleRequest(applicationDTO);
        softly.assertThat(request.getCaseType())
                .isEqualTo(CaseType.getFrom(applicationDTO.getCaseDetailsDTO().getCaseType()));
        softly.assertThat(request.getMagCourtOutcome())
                .isEqualTo(MagCourtOutcome.getFrom(applicationDTO.getMagsOutcomeDTO().getOutcome()));
        softly.assertThat(request.getCrownCourtOutcome().size())
                .isGreaterThan(0);
        softly.assertAll();
    }

    @Test
    void givenValidApiMaatCalculateContributionResponse_whenContributionMapperIsInvoked_thenMappingIsCorrect() {
        ApiMaatCalculateContributionResponse apiMaatCalculateContributionResponse = TestModelDataBuilder.
                getApiMaatCalculateContributionResponse();
        ContributionsDTO contributionsDTO = contributionMapper
                .maatCalculateContributionResponseToContributionsDto(apiMaatCalculateContributionResponse);
        softly.assertThat(contributionsDTO.getId())
                .isEqualTo(apiMaatCalculateContributionResponse.getContributionId().longValue());
        softly.assertThat(contributionsDTO.getCapped())
                .isEqualTo(apiMaatCalculateContributionResponse.getContributionCap());
        softly.assertThat(contributionsDTO.getCalcDate())
                .isEqualTo(toDate(apiMaatCalculateContributionResponse.getCalcDate()));
        softly.assertThat(contributionsDTO.getMonthlyContribs())
                .isEqualTo(apiMaatCalculateContributionResponse.getMonthlyContributions());
        softly.assertThat(contributionsDTO.getUpfrontContribs())
                .isEqualTo(apiMaatCalculateContributionResponse.getUpfrontContributions());
        softly.assertThat(contributionsDTO.getEffectiveDate())
                .isEqualTo(toDate(apiMaatCalculateContributionResponse.getEffectiveDate()));
        softly.assertAll();
    }

    @Test
    void givenValidContributionSummaries_whenContributionMapperIsInvoked_thenMappingIsCorrect() {
        List<ApiContributionSummary> apiContributionSummaryList = List.of(TestModelDataBuilder.getApiContributionSummary());
        ApiContributionSummary apiContributionSummary = apiContributionSummaryList.get(0);
        Collection<ContributionSummaryDTO> ContributionSummaryDTOs = contributionMapper
                .contributionSummaryToDto(apiContributionSummaryList);
        ContributionSummaryDTO contributionSummaryDTO = ContributionSummaryDTOs.iterator().next();

        softly.assertThat(ContributionSummaryDTOs.size())
                .isEqualTo(1);
        softly.assertThat(contributionSummaryDTO.getId())
                .isEqualTo(apiContributionSummary.getId().longValue());
        softly.assertThat(contributionSummaryDTO.getMonthlyContribs())
                .isEqualTo(apiContributionSummary.getMonthlyContributions().doubleValue());
        softly.assertThat(contributionSummaryDTO.getUpfrontContribs())
                .isEqualTo(apiContributionSummary.getUpfrontContributions().doubleValue());
        softly.assertThat(contributionSummaryDTO.getBasedOn())
                .isEqualTo(apiContributionSummary.getBasedOn());
        softly.assertThat(contributionSummaryDTO.getUpliftApplied())
                .isEqualTo(true);
        softly.assertThat(contributionSummaryDTO.getEffectiveDate())
                .isEqualTo(toDate(apiContributionSummary.getEffectiveDate()));
        softly.assertAll();
    }
}
