package uk.gov.justice.laa.crime.orchestration.mapper.proceedings;

import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FullAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.InitialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CurrentStatus;
import uk.gov.justice.laa.crime.orchestration.enums.ReviewResult;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiFinancialAssessment;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiHardshipOverview;

class ApiFinancialAssessmentMapper {

    ApiFinancialAssessment applicationDtoToFinancialAssessment(ApplicationDTO application) {

        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        FullAssessmentDTO fullAssessment = financialAssessmentDTO.getFull();
        InitialAssessmentDTO initialAssessment = financialAssessmentDTO.getInitial();

        ApiFinancialAssessment assessment = new ApiFinancialAssessment();
        assessment
                .withInitResult(initialAssessment.getResult())
                .withInitStatus(CurrentStatus.getFrom(initialAssessment.getAssessmnentStatusDTO().getStatus()));

        if (financialAssessmentDTO.getFull().getAssessmentDate() != null) {
            assessment
                    .withFullResult(fullAssessment.getResult())
                    .withFullStatus(CurrentStatus.getFrom(fullAssessment.getAssessmnentStatusDTO().getStatus()));
        }

        HardshipReviewDTO crownHardship = financialAssessmentDTO.getHardship().getCrownCourtHardship();
        if (crownHardship.getId() != null) {
            assessment.withHardshipOverview(
                    new ApiHardshipOverview()
                            .withReviewResult(ReviewResult.getFrom(crownHardship.getReviewResult()))
                            .withAssessmentStatus(CurrentStatus.getFrom(crownHardship.getAsessmentStatus().getStatus()))
            );
        }
        return assessment;
    }
}
