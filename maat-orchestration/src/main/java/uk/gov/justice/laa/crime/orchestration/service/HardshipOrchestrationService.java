package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipOrchestrationService implements AssessmentOrchestrator<HardshipReviewDTO> {

    public HardshipReviewDTO find(int assessmentId) {
        return null;
    }

    public ApplicationDTO create(ApplicationDTO application) {


        return application;
    }

    public ApplicationDTO update(ApplicationDTO application) {

        return application;
    }

}
