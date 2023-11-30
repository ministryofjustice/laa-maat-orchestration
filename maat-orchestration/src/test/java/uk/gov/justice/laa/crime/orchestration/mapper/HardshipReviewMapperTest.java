package uk.gov.justice.laa.crime.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.model.hardship.*;

@ExtendWith(SoftAssertionsExtension.class)
class HardshipReviewMapperTest {

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
                hardshipMapper.workflowRequestToPerformHardshipRequest(workflowRequest);
        HardshipReview actualHardship = apiPerformHardshipRequest.getHardship();

        softly.assertThat(actualHardship.getCourtType())
                .isEqualTo(CourtType.CROWN_COURT);
    }

    @Test
    void givenWorkflowRequestWithMagsHardship_whenWorkflowRequestToPerformHardshipRequestIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);

        ApiPerformHardshipRequest apiPerformHardshipRequest =
                hardshipMapper.workflowRequestToPerformHardshipRequest(workflowRequest);
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

        HardshipProgress actualhardshipProgress = actualMetadata.getProgressItems().get(0);
        HardshipProgress expectedhardshipProgress = expectedMetadata.getProgressItems().get(0);
        checkHardshipProgress(actualhardshipProgress, expectedhardshipProgress);
    }

    @Test
    void givenPerformHardshipResponseAndCrownCourtType_whenPerformHardshipResponseToApplicationDTOIsInvoked_thenMappingIsCorrect() {
        ApplicationDTO applicationDTO =
                TestModelDataBuilder.getApplicationDTOWithBlankHardship(CourtType.CROWN_COURT);
        ApiPerformHardshipResponse response = TestModelDataBuilder.getApiPerformHardshipResponse();

        hardshipMapper.performHardshipResponseToApplicationDTO(response, applicationDTO);

        HardshipReviewDTO crownCourtHardship =
                applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship();

        checkHardshipDTO(response, crownCourtHardship);
    }

    @Test
    void givenPerformHardshipResponseAndMagsCourtType_whenPerformHardshipResponseToApplicationDTOIsInvoked_thenMappingIsCorrect() {
        ApplicationDTO applicationDTO =
                TestModelDataBuilder.getApplicationDTOWithBlankHardship(CourtType.MAGISTRATE);
        ApiPerformHardshipResponse response = TestModelDataBuilder.getApiPerformHardshipResponse();

        hardshipMapper.performHardshipResponseToApplicationDTO(response, applicationDTO);

        HardshipReviewDTO magCourtHardship =
                applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship();

        checkHardshipDTO(response, magCourtHardship);
    }

    private void checkHardshipDTO(ApiPerformHardshipResponse response, HardshipReviewDTO magCourtHardship) {
        softly.assertThat(magCourtHardship.getId())
                .isEqualTo(response.getHardshipReviewId().longValue());
        softly.assertThat(magCourtHardship.getReviewResult())
                .isEqualTo(Constants.TEST_HARDSHIP_REVIEW_RESULT);
        softly.assertThat(magCourtHardship.getDisposableIncomeAfterHardship())
                .isEqualTo(Constants.TEST_POST_HARDSHIP_DISPOSABLE_INCOME);
        softly.assertThat(magCourtHardship.getDisposableIncome())
                .isEqualTo(Constants.TEST_DISPOSABLE_INCOME);
    }


    private void checkHardshipProgress(HardshipProgress actualhardshipProgress, HardshipProgress other) {
        softly.assertThat(actualhardshipProgress.getAction())
                .isEqualTo(other.getAction());
        softly.assertThat(actualhardshipProgress.getResponse())
                .isEqualTo(other.getResponse());
        softly.assertThat(actualhardshipProgress.getDateTaken())
                .isEqualTo(other.getDateTaken());
        softly.assertThat(actualhardshipProgress.getDateCompleted())
                .isEqualTo(other.getDateCompleted());
        softly.assertThat(actualhardshipProgress.getDateRequired())
                .isEqualTo(other.getDateRequired());
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
}



































