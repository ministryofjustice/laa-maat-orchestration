package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.service.PassportAssessmentService;

@Service
@RequiredArgsConstructor
public class PassportAssessmentOrchestrationService {

    private final PassportAssessmentService passportAssessmentService;

    public PassportedDTO find(int legacyId) {
        return passportAssessmentService.find(legacyId);
    }

}
