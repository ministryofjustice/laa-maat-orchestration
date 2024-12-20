package uk.gov.justice.laa.crime.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.DeniedIncome;
import uk.gov.justice.laa.crime.common.model.hardship.ExtraExpenditure;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipMetadata;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipReview;
import uk.gov.justice.laa.crime.common.model.hardship.SolicitorCosts;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;

@ExtendWith(SoftAssertionsExtension.class)
class HardshipMapperTest {

    UserMapper userMapper = new UserMapper();
    HardshipMapper hardshipMapper = new HardshipMapper(userMapper);

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiFindHardshipResponseWithSection_whenFindHardshipResponseToHardshipDTOIsInvoked_thenMappingIsCorrect() {
        HardshipReviewDTO actual =
                hardshipMapper.findHardshipResponseToHardshipDto(TestModelDataBuilder.getApiFindHardshipResponse());
        HardshipReviewDTO expected = TestModelDataBuilder.getHardshipReviewDTO();

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    void givenWorkflowRequestWithCrownHardship_whenWorkflowRequestToPerformHardshipRequestIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);

        ApiPerformHardshipRequest apiPerformHardshipRequest =
                hardshipMapper.workflowRequestToPerformHardshipRequest(workflowRequest, true);
        HardshipReview actualHardship = apiPerformHardshipRequest.getHardship();

        softly.assertThat(actualHardship.getCourtType())
                .isEqualTo(CourtType.CROWN_COURT);
        softly.assertThat(actualHardship.getTotalAnnualDisposableIncome().doubleValue())
                .isEqualTo(workflowRequest.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getFull().getTotalAnnualDisposableIncome());
        softly.assertThat(actualHardship.getSolicitorCosts()).isNull();
    }

    @Test
    void givenWorkflowRequestWithMagsHardship_whenWorkflowRequestToPerformHardshipRequestIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);

        ApiPerformHardshipRequest apiPerformHardshipRequest =
                hardshipMapper.workflowRequestToPerformHardshipRequest(workflowRequest, false);
        HardshipReview actualHardship = apiPerformHardshipRequest.getHardship();

        ApiPerformHardshipRequest expected = TestModelDataBuilder.getApiPerformHardshipRequest();
        HardshipReview expectedHardship = expected.getHardship();

        softly.assertThat(actualHardship.getCourtType())
                .isEqualTo(CourtType.MAGISTRATE);
        softly.assertThat(actualHardship.getTotalAnnualDisposableIncome())
                .isEqualTo(expectedHardship.getTotalAnnualDisposableIncome());
        softly.assertThat(actualHardship.getReviewDate())
                .isEqualTo(expectedHardship.getReviewDate());

        ExtraExpenditure actualExtraExpenditure = expectedHardship.getExtraExpenditure().get(0);
        ExtraExpenditure expectedExtraExpenditure = actualHardship.getExtraExpenditure().get(0);
        checkExtraExpenditure(actualExtraExpenditure, expectedExtraExpenditure);

        DeniedIncome actualDeniedIncome = expectedHardship.getDeniedIncome().get(0);
        DeniedIncome expectedDeniedIncome = expectedHardship.getDeniedIncome().get(0);
        checkDeniedIncome(actualDeniedIncome, expectedDeniedIncome);

        SolicitorCosts actualSolicitorCosts = actualHardship.getSolicitorCosts();
        SolicitorCosts expectedSolicitorCosts = expectedHardship.getSolicitorCosts();
        checkSolicitorCosts(actualSolicitorCosts, expectedSolicitorCosts);

        HardshipMetadata actualMetadata = apiPerformHardshipRequest.getHardshipMetadata();
        HardshipMetadata expectedMetadata = apiPerformHardshipRequest.getHardshipMetadata();
        checkHardshipMetadata(actualMetadata, expectedMetadata);
    }

    @Test
    void givenPerformHardshipResponseAndCrownCourtType_whenPerformHardshipResponseToApplicationDTOIsInvoked_thenMappingIsCorrect() {
        ApplicationDTO applicationDTO =
                TestModelDataBuilder.getApplicationDTOWithBlankHardship(CourtType.CROWN_COURT);
        ApiPerformHardshipResponse response = TestModelDataBuilder.getApiPerformHardshipResponse();

        hardshipMapper.performHardshipResponseToApplicationDTO(response, applicationDTO, CourtType.CROWN_COURT);

        HardshipReviewDTO crownCourtHardship =
                applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship();

        checkHardshipDTO(response, crownCourtHardship);
    }

    @Test
    void givenPerformHardshipResponseAndMagsCourtType_whenPerformHardshipResponseToApplicationDTOIsInvoked_thenMappingIsCorrect() {
        ApplicationDTO applicationDTO =
                TestModelDataBuilder.getApplicationDTOWithBlankHardship(CourtType.MAGISTRATE);
        ApiPerformHardshipResponse response = TestModelDataBuilder.getApiPerformHardshipResponse();

        hardshipMapper.performHardshipResponseToApplicationDTO(response, applicationDTO, CourtType.MAGISTRATE);

        HardshipReviewDTO magCourtHardship =
                applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship();

        checkHardshipDTO(response, magCourtHardship);
    }

    private void checkHardshipDTO(ApiPerformHardshipResponse response, HardshipReviewDTO magCourtHardship) {
        softly.assertThat(magCourtHardship.getId())
                .isEqualTo(response.getHardshipReviewId().longValue());
        softly.assertThat(magCourtHardship.getReviewResult())
                .isEqualTo(Constants.HARDSHIP_REVIEW_RESULT);
        softly.assertThat(magCourtHardship.getDisposableIncomeAfterHardship())
                .isEqualTo(Constants.POST_HARDSHIP_DISPOSABLE_INCOME);
        softly.assertThat(magCourtHardship.getDisposableIncome())
                .isEqualTo(Constants.DISPOSABLE_INCOME);
    }

    private void checkHardshipMetadata(HardshipMetadata actualMetadata, HardshipMetadata other) {
        softly.assertThat(actualMetadata.getRepId())
                .isEqualTo(other.getRepId());
        softly.assertThat(actualMetadata.getCmuId())
                .isEqualTo(other.getCmuId());
        softly.assertThat(actualMetadata.getReviewStatus())
                .isEqualTo(other.getReviewStatus());
        softly.assertThat(actualMetadata.getUserSession())
                .isEqualTo(other.getUserSession());
        softly.assertThat(actualMetadata.getReviewReason())
                .isEqualTo(other.getReviewReason());
        softly.assertThat(actualMetadata.getNotes())
                .isEqualTo(other.getNotes());
        softly.assertThat(actualMetadata.getDecisionNotes())
                .isEqualTo(other.getDecisionNotes());
        softly.assertThat(actualMetadata.getFinancialAssessmentId())
                .isEqualTo(other.getFinancialAssessmentId());
        softly.assertThat(actualMetadata.getHardshipReviewId())
                .isEqualTo(other.getHardshipReviewId());
    }

    private void checkSolicitorCosts(SolicitorCosts actualSolicitorCosts, SolicitorCosts other) {
        softly.assertThat(actualSolicitorCosts.getRate())
                .isEqualTo(other.getRate());
        softly.assertThat(actualSolicitorCosts.getVat())
                .isEqualTo(other.getVat());
        softly.assertThat(actualSolicitorCosts.getHours())
                .isEqualTo(other.getHours());
        softly.assertThat(actualSolicitorCosts.getDisbursements())
                .isEqualTo(other.getDisbursements());
        softly.assertThat(actualSolicitorCosts.getEstimatedTotal())
                .isEqualTo(other.getEstimatedTotal());
    }

    private void checkDeniedIncome(DeniedIncome actualDeniedIncome, DeniedIncome other) {
        softly.assertThat(actualDeniedIncome.getAmount())
                .isEqualTo(other.getAmount());
        softly.assertThat(actualDeniedIncome.getFrequency())
                .isEqualTo(other.getFrequency());
        softly.assertThat(actualDeniedIncome.getAccepted())
                .isEqualTo(other.getAccepted());
        softly.assertThat(actualDeniedIncome.getReasonNote())
                .isEqualTo(other.getReasonNote());
        softly.assertThat(actualDeniedIncome.getDescription())
                .isEqualTo(other.getDescription());
        softly.assertThat(actualDeniedIncome.getItemCode())
                .isEqualTo(other.getItemCode());
    }

    private void checkExtraExpenditure(ExtraExpenditure actualExtraExpenditure, ExtraExpenditure other) {
        softly.assertThat(actualExtraExpenditure.getAmount())
                .isEqualTo(other.getAmount());
        softly.assertThat(actualExtraExpenditure.getFrequency())
                .isEqualTo(other.getFrequency());
        softly.assertThat(actualExtraExpenditure.getDescription())
                .isEqualTo(other.getDescription());
        softly.assertThat(actualExtraExpenditure.getAccepted())
                .isEqualTo(other.getAccepted());
        softly.assertThat(actualExtraExpenditure.getReasonCode())
                .isEqualTo(other.getReasonCode());
        softly.assertThat(actualExtraExpenditure.getItemCode())
                .isEqualTo(other.getItemCode());
    }

    @Test
    void givenWorkflowRequestAndAction_whengetUserActionDTOIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        UserActionDTO actual =
                hardshipMapper.getUserActionDTO(workflowRequest, TestModelDataBuilder.TEST_ACTION);
        UserActionDTO expected = TestModelDataBuilder.getUserActionDTO();

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }
}
