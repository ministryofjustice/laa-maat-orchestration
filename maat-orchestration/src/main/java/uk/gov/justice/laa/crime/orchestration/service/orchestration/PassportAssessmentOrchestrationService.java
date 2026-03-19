package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.service.PassportAssessmentService;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassportAssessmentOrchestrationService {

    private final PassportAssessmentService passportAssessmentService;

    public PassportedDTO find(int id) {
        return passportAssessmentService.find(id);
    }
}
