package uk.gov.justice.laa.crime.orchestration.service.validation;

import org.junit.jupiter.api.Test;

import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiAssessment;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.LastOutcome;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ContributionValidationServiceTest {

    private static final ContributionValidationService CONTRIBUTION_VALIDATION_SERVICE = new ContributionValidationService();

    @Test
    void givenValidRequest_whenValidateIsInvoked_thenNoExceptionIsRaised() {
        ApiMaatCalculateContributionRequest maatCalculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();

        assertThat(CONTRIBUTION_VALIDATION_SERVICE.validate(maatCalculateContributionRequest)).isEmpty();
    }

    @Test
    void givenLastOutcomeIsNotAvailable_whenValidateIsInvoked_thenNoExceptionIsRaised() {
        ApiMaatCalculateContributionRequest maatCalculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();
        maatCalculateContributionRequest.setLastOutcome(null);
        assertThat(CONTRIBUTION_VALIDATION_SERVICE.validate(maatCalculateContributionRequest)).isEmpty();
    }

    @Test
    void givenEmptyOutDateSet_whenValidateIsInvoked_thenNoExceptionIsRaised() {
        ApiMaatCalculateContributionRequest maatCalculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();
        maatCalculateContributionRequest.getLastOutcome().setDateSet(null);

        assertThat(CONTRIBUTION_VALIDATION_SERVICE.validate(maatCalculateContributionRequest)).isEmpty();
    }

    @Test
    void givenIncorrectOutcomeDateSet_whenValidateIsInvoked_thenValidationExceptionIsRaised() {
        ApiMaatCalculateContributionRequest maatCalculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();
        LastOutcome lastOutcome = TestModelDataBuilder.buildLastOutcome();
        lastOutcome.setDateSet(LocalDateTime.now().plusDays(1));
        maatCalculateContributionRequest.setLastOutcome(lastOutcome);

        assertThatThrownBy(() -> CONTRIBUTION_VALIDATION_SERVICE.validate(maatCalculateContributionRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("The dateSet for lastOutcome is invalid");
    }

    @Test
    void givenNoCompleteAssessment_whenValidateIsInvoked_thenValidationExceptionIsRaised() {
        ApiMaatCalculateContributionRequest maatCalculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();
        ApiAssessment assessment = TestModelDataBuilder.buildAssessment();
        assessment.withStatus(CurrentStatus.IN_PROGRESS);
        maatCalculateContributionRequest.setAssessments(List.of(assessment));

        assertThatThrownBy(() -> CONTRIBUTION_VALIDATION_SERVICE.validate(maatCalculateContributionRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("There must be at least one COMPLETE assessment");
    }
}
