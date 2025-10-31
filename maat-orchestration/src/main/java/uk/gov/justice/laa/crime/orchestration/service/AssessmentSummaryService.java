package uk.gov.justice.laa.crime.orchestration.service;

import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AssessmentSummaryService {

    public static final String INITIAL_ASSESSMENT = "Initial Assessment";
    public static final String FULL_MEANS_TEST = "Full Means Test";

    public AssessmentSummaryDTO getSummary(HardshipReviewDTO hardshipReviewDTO, CourtType courtType) {
        return AssessmentSummaryDTO.builder()
                .id(hardshipReviewDTO.getId())
                .status(hardshipReviewDTO.getAsessmentStatus().getStatus())
                .type(
                        courtType == CourtType.CROWN_COURT
                                ? "Hardship Review - Crown Court"
                                : "Hardship Review - Magistrate")
                .result(hardshipReviewDTO.getReviewResult())
                .assessmentDate(hardshipReviewDTO.getReviewDate())
                .build();
    }

    public void updateApplication(ApplicationDTO application, AssessmentSummaryDTO summaryDTO) {
        Collection<AssessmentSummaryDTO> assessmentSummary = application.getAssessmentSummary();
        assessmentSummary.stream()
                .filter(s -> s.getId().equals(summaryDTO.getId()))
                .findFirst()
                .ifPresentOrElse(
                        assessmentSummaryDTO -> {
                            assessmentSummaryDTO.setType(summaryDTO.getType());
                            assessmentSummaryDTO.setStatus(summaryDTO.getStatus());
                            assessmentSummaryDTO.setResult(summaryDTO.getResult());
                            assessmentSummaryDTO.setReviewType(summaryDTO.getReviewType());
                            assessmentSummaryDTO.setAssessmentDate(summaryDTO.getAssessmentDate());
                        },
                        () -> assessmentSummary.add(summaryDTO));
    }

    public AssessmentSummaryDTO getSummary(FinancialAssessmentDTO financialAssessmentDTO) {
        AssessmentSummaryDTO assessmentSummaryDTO = AssessmentSummaryDTO.builder()
                .id(financialAssessmentDTO.getId())
                .reviewType(financialAssessmentDTO.getInitial().getReviewType().getCode())
                .build();

        boolean isFullAvailable = Boolean.TRUE.equals(financialAssessmentDTO.getFullAvailable());
        String fullAssessmentStatus = isFullAvailable
                ? financialAssessmentDTO.getFull().getAssessmnentStatusDTO().getStatus()
                : "";
        if (StringUtils.isNotBlank(fullAssessmentStatus)) {
            assessmentSummaryDTO.setType(FULL_MEANS_TEST);
            assessmentSummaryDTO.setStatus(
                    CurrentStatus.getFrom(fullAssessmentStatus).getDescription());
            assessmentSummaryDTO.setResult(financialAssessmentDTO.getFull().getResult());
            assessmentSummaryDTO.setAssessmentDate(
                    financialAssessmentDTO.getFull().getAssessmentDate());
        } else {
            assessmentSummaryDTO.setType(INITIAL_ASSESSMENT);
            assessmentSummaryDTO.setStatus(CurrentStatus.getFrom(financialAssessmentDTO
                            .getInitial()
                            .getAssessmnentStatusDTO()
                            .getStatus())
                    .getDescription());
            assessmentSummaryDTO.setResult(financialAssessmentDTO.getInitial().getResult());
            assessmentSummaryDTO.setAssessmentDate(
                    financialAssessmentDTO.getInitial().getAssessmentDate());
        }
        return assessmentSummaryDTO;
    }
}
