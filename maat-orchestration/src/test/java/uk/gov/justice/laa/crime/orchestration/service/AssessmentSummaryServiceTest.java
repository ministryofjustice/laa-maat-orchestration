package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith({MockitoExtension.class})
class AssessmentSummaryServiceTest {

    AssessmentSummaryService assessmentSummaryService = new AssessmentSummaryService();

    private HardshipReviewDTO buildHardshipDTO() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.getHardshipReviewDTO();
        hardshipReviewDTO.setId(Constants.ASSESSMENT_SUMMARY_ID.longValue());
        hardshipReviewDTO.setReviewDate(Constants.ASSESSMENT_SUMMARY_DATE);
        return hardshipReviewDTO;
    }

    @Test
    void givenHardshipReviewDTOAndCrownCourtType_whenGetSummaryIsInvoked_thenAssessmentSummaryIsReturned() {
        HardshipReviewDTO hardshipReviewDTO = buildHardshipDTO();
        AssessmentSummaryDTO actual = assessmentSummaryService.getSummary(hardshipReviewDTO, CourtType.CROWN_COURT);
        AssessmentSummaryDTO expected = TestModelDataBuilder.getAssessmentSummaryDTOFromHardship(CourtType.CROWN_COURT);
        assertThat(actual).isEqualTo(expected);


    }

    @Test
    void givenHardshipReviewDTOAndMagistratesCourtType_whenGetSummaryIsInvoked_thenAssessmentSummaryIsReturned() {
        HardshipReviewDTO hardshipReviewDTO = buildHardshipDTO();
        AssessmentSummaryDTO actual = assessmentSummaryService.getSummary(hardshipReviewDTO, CourtType.MAGISTRATE);
        AssessmentSummaryDTO expected = TestModelDataBuilder.getAssessmentSummaryDTOFromHardship(CourtType.MAGISTRATE);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenAssessmentSummaryWithIdExists_whenUpdateApplicationIsInvoked_thenAssessmentSummaryIsUpdated() {
        AssessmentSummaryDTO updatedSummary = TestModelDataBuilder.getAssessmentSummaryDTO();
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .assessmentSummary(List.of(AssessmentSummaryDTO.builder()
                                .id(Constants.ASSESSMENT_SUMMARY_ID.longValue())
                                .build()
                        )
                ).build();
        assessmentSummaryService.updateApplication(applicationDTO, updatedSummary);

        assertThat(applicationDTO.getAssessmentSummary())
                .asList()
                .hasSize(1);

        assertThat(applicationDTO.getAssessmentSummary().stream().toList().get(0))
                .usingRecursiveComparison()
                .isEqualTo(updatedSummary);
    }

    @Test
    void givenAssessmentSummaryWithIdDoesntExists_whenUpdateApplicationIsInvoked_thenAssessmentSummaryIsAdded() {
        AssessmentSummaryDTO newSummary = TestModelDataBuilder.getAssessmentSummaryDTO();
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .assessmentSummary(new ArrayList<>())
                .build();
        assessmentSummaryService.updateApplication(applicationDTO, newSummary);

        assertThat(applicationDTO.getAssessmentSummary())
                .asList()
                .hasSize(1);

        assertThat(applicationDTO.getAssessmentSummary().stream().toList().get(0))
                .usingRecursiveComparison()
                .isEqualTo(newSummary);
    }

    @Test
    void givenFinancialAssessmentDTOWithFullAssessmentType_whenGetSummaryIsInvoked_thenAssessmentSummaryIsReturned() {
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO();
        financialAssessmentDTO.setFullAvailable(true);
        financialAssessmentDTO.getFull().setAssessmentDate(Date.from(LocalDateTime.of(2022, 10, 15, 0, 0, 0)
                .toInstant(ZoneOffset.UTC)));
        AssessmentSummaryDTO actual = assessmentSummaryService.getSummary(financialAssessmentDTO);
        AssessmentSummaryDTO expected = TestModelDataBuilder.getAssessmentSummaryDTOFromFullFinancialAssessment();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenFinancialAssessmentDTOWithInitAssessmentType_whenGetSummaryIsInvoked_thenAssessmentSummaryIsReturned() {
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO();
        financialAssessmentDTO.setFullAvailable(false);
        AssessmentSummaryDTO actual = assessmentSummaryService.getSummary(financialAssessmentDTO);
        AssessmentSummaryDTO expected = TestModelDataBuilder.getAssessmentSummaryDTOFromInitFinancialAssessment();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenFinancialAssessmentDTOWithEmptyFullAssessmentStatus_whenGetSummaryIsInvoked_thenAssessmentSummaryIsReturned() {
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO();
        financialAssessmentDTO.setFullAvailable(true);
        financialAssessmentDTO.getFull().getAssessmnentStatusDTO().setStatus("");
        AssessmentSummaryDTO actual = assessmentSummaryService.getSummary(financialAssessmentDTO);
        AssessmentSummaryDTO expected = TestModelDataBuilder.getAssessmentSummaryDTOFromInitFinancialAssessment();
        assertThat(actual).isEqualTo(expected);
    }
}
