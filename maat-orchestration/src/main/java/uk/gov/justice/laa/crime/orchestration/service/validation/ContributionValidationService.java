package uk.gov.justice.laa.crime.orchestration.service.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.LastOutcome;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionValidationService {

    public Optional<Void> validate(ApiMaatCalculateContributionRequest maatCalculateContributionRequest) {
        log.debug("Performing validation against calculate contributions request");
        LastOutcome lastOutcome = maatCalculateContributionRequest.getLastOutcome();
        if (lastOutcome != null && lastOutcome.getDateSet() != null
                    && lastOutcome.getDateSet().isAfter(LocalDateTime.now())) {
                throw new ValidationException("The dateSet for lastOutcome is invalid");
            }

        boolean isNoCompletedAssessment = maatCalculateContributionRequest.getAssessments()
                .stream()
                .filter(assessment -> assessment.getStatus() == CurrentStatus.COMPLETE)
                .toList()
                .isEmpty();
        if (isNoCompletedAssessment) {
            throw new ValidationException("There must be at least one COMPLETE assessment");
        }
        return Optional.empty();
    }
}
