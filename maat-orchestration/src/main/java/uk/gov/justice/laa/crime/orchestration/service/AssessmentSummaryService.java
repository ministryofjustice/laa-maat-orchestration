package uk.gov.justice.laa.crime.orchestration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.AssessmentType;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.util.Collection;
import java.util.Date;

@Slf4j
@Service
public class AssessmentSummaryService {

    public AssessmentSummaryDTO getSummary(HardshipReviewDTO hardshipReviewDTO, CourtType courtType) {
        return AssessmentSummaryDTO.builder()
                .id(hardshipReviewDTO.getId())
                .status(hardshipReviewDTO.getAsessmentStatus().getStatus())
                .type(courtType == CourtType.CROWN_COURT
                        ? "Hardship Review - Crown Court"
                        : "Hardship Review - Magistrate"
                )
                .result(hardshipReviewDTO.getReviewResult())
                .assessmentDate(hardshipReviewDTO.getReviewDate()).build();
    }

    public void updateApplication(ApplicationDTO application, AssessmentSummaryDTO summaryDTO) {
        Collection<AssessmentSummaryDTO> assessmentSummary = application.getAssessmentSummary();
        assessmentSummary.stream().filter(s -> s.getId().equals(summaryDTO.getId()))
                .findFirst().ifPresentOrElse(
                        assessmentSummaryDTO -> {
                            assessmentSummaryDTO.setType(summaryDTO.getType());
                            assessmentSummaryDTO.setStatus(summaryDTO.getStatus());
                            assessmentSummaryDTO.setResult(summaryDTO.getResult());
                            assessmentSummaryDTO.setReviewType(summaryDTO.getReviewType());
                            assessmentSummaryDTO.setAssessmentDate(summaryDTO.getAssessmentDate());
                        }, () -> assessmentSummary.add(summaryDTO)
                );
    }

    public AssessmentSummaryDTO getSummary(FinancialAssessmentDTO financialAssessmentDTO) {
        boolean isFullAssessment = AssessmentType.FULL.equals(AssessmentType.getFrom(financialAssessmentDTO.getAssessmentType()));
        String status = null;
        String type = null;
        String result = null;
        Date assessmentDate = null;

        if(isFullAssessment) {
            status = financialAssessmentDTO.getFassFullStatus();
            type = "Full Means Test";
            result = financialAssessmentDTO.getFullResult();
            assessmentDate = DateUtil.toDate(financialAssessmentDTO.getFullAssessmentDate());
        } else {
            status = financialAssessmentDTO.getFassInitStatus();
            type = "Initial Assessment";
            result = financialAssessmentDTO.getInitResult();
            assessmentDate = DateUtil.toDate(financialAssessmentDTO.getInitialAssessmentDate());
        }

        return AssessmentSummaryDTO.builder()
                .id(Long.valueOf(financialAssessmentDTO.getId()))
                .status(status)
                .type(type)
                .result(result)
                .assessmentDate(assessmentDate)
                .reviewType(financialAssessmentDTO.getRtCode())
                .build();
    }
}