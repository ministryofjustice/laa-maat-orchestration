package uk.gov.justice.laa.maat.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.maat.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.maat.orchestration.dto.HRDetailDTO;
import uk.gov.justice.laa.maat.orchestration.dto.HRSectionDTO;
import uk.gov.justice.laa.maat.orchestration.dto.HRSolicitorsCostsDTO;
import uk.gov.justice.laa.maat.orchestration.dto.HardshipReviewDTO;
import uk.gov.justice.laa.maat.orchestration.enums.Frequency;
import uk.gov.justice.laa.maat.orchestration.enums.HardshipReviewDetailCode;
import uk.gov.justice.laa.maat.orchestration.enums.HardshipReviewDetailReason;
import uk.gov.justice.laa.maat.orchestration.enums.HardshipReviewDetailType;
import uk.gov.justice.laa.maat.orchestration.model.ApiFindHardshipResponse;
import uk.gov.justice.laa.maat.orchestration.model.ApiHardshipDetail;
import uk.gov.justice.laa.maat.orchestration.model.SolicitorCosts;
import uk.gov.justice.laa.maat.orchestration.util.DateUtil;

import java.util.Optional;

import static uk.gov.justice.laa.maat.orchestration.data.builder.TestModelDataBuilder.HARDSHIP_DETAIL_ID;

@ExtendWith(SoftAssertionsExtension.class)
class HardshipReviewMapperTest {

    HardshipReviewMapper mapper = new HardshipReviewMapper();

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiFindHardshipResponseWithSection_whenToDtoIsInvoked_thenDtoIsMapped() {
        ApiFindHardshipResponse hardship = TestModelDataBuilder.getApiFindHardshipResponse();
        HardshipReviewDTO reviewDTO = new HardshipReviewDTO();
        mapper.toDto(hardship, reviewDTO);

        validateCommonFields(hardship, reviewDTO);

        validateHRSection(reviewDTO);
    }

    @Test
    void givenApiFindHardshipResponse_whenToDtoIsInvoked_thenDtoIsMapped() {
        ApiFindHardshipResponse hardship = TestModelDataBuilder.getApiFindHardshipResponse();
        Optional<ApiHardshipDetail> optionalApiHardshipDetail = hardship.getReviewDetails().stream().findFirst();
        if (optionalApiHardshipDetail.isPresent()) {
            optionalApiHardshipDetail.get().setDetailReason(null);
            optionalApiHardshipDetail.get().setDetailCode(null);
        }
        HardshipReviewDTO reviewDTO = new HardshipReviewDTO();
        mapper.toDto(hardship, reviewDTO);

        validateCommonFields(hardship, reviewDTO);

        Optional<HRSectionDTO> optionalHRSectionDTO = reviewDTO.getSection().stream().findFirst();
        softly.assertThat(optionalHRSectionDTO.isPresent()).isTrue();
        if (optionalHRSectionDTO.isPresent()) {
            Optional<HRDetailDTO> optionalHRDetailDTO = optionalHRSectionDTO.get().getDetail().stream().findFirst();
            softly.assertThat(optionalHRDetailDTO.isPresent()).isTrue();
            if (optionalHRDetailDTO.isPresent()) {
                softly.assertThat(optionalHRDetailDTO.get().getReason()).isNull();
                softly.assertThat(optionalHRDetailDTO.get().getDetailDescription()).isNull();
            }
        }
    }

    private void validateCommonFields(ApiFindHardshipResponse hardship, HardshipReviewDTO reviewDTO) {
        softly.assertThat(reviewDTO.getReviewResult())
                .isEqualTo(hardship.getReviewResult().toString());
        softly.assertThat(reviewDTO.getReviewDate())
                .isEqualTo(DateUtil.toDate(hardship.getReviewDate()));
        softly.assertThat(reviewDTO.getId().intValue())
                .isEqualTo(hardship.getId());
        softly.assertThat(reviewDTO.getId().intValue())
                .isEqualTo(hardship.getId());
        softly.assertThat(reviewDTO.getCmuId().intValue())
                .isEqualTo(hardship.getCmuId());
        softly.assertThat(reviewDTO.getNotes())
                .isEqualTo(hardship.getNotes());
        softly.assertThat(reviewDTO.getDisposableIncome().doubleValue())
                .isEqualTo(hardship.getDisposableIncome().doubleValue());
        softly.assertThat(reviewDTO.getDecisionNotes())
                .isEqualTo(hardship.getDecisionNotes());
        softly.assertThat(reviewDTO.getDisposableIncomeAfterHardship().doubleValue())
                .isEqualTo(hardship.getDisposableIncomeAfterHardship().doubleValue());
        softly.assertThat(reviewDTO.getAsessmentStatus().getStatus())
                .isEqualTo(hardship.getStatus().getStatus());
        softly.assertThat(reviewDTO.getAsessmentStatus().getDescription())
                .isEqualTo(hardship.getStatus().getDescription());
        softly.assertThat(reviewDTO.getNewWorkReason().getCode())
                .isEqualTo(hardship.getNewWorkReason().getCode());
        softly.assertThat(reviewDTO.getNewWorkReason().getDescription())
                .isEqualTo(hardship.getNewWorkReason().getDescription());
        softly.assertThat(reviewDTO.getProgress().size())
                .isEqualTo(hardship.getReviewProgressItems().size());
        validateSolicitorCosts(reviewDTO.getSolictorsCosts(), hardship.getSolicitorCosts());
    }

    private void validateSolicitorCosts(HRSolicitorsCostsDTO hrSolicitorsCostsDTO, SolicitorCosts solicitorCosts) {
        softly.assertThat(hrSolicitorsCostsDTO.getSolicitorDisb().doubleValue())
                .isEqualTo(solicitorCosts.getDisbursements().doubleValue());
        softly.assertThat(hrSolicitorsCostsDTO.getSolicitorHours().intValue())
                .isEqualTo(solicitorCosts.getHours().intValue());
        softly.assertThat(hrSolicitorsCostsDTO.getSolicitorEstimatedTotalCost().doubleValue())
                .isEqualTo(solicitorCosts.getEstimatedTotal().doubleValue());
        softly.assertThat(hrSolicitorsCostsDTO.getSolicitorRate().doubleValue())
                .isEqualTo(solicitorCosts.getRate().doubleValue());
        softly.assertThat(hrSolicitorsCostsDTO.getSolicitorVat().doubleValue())
                .isEqualTo(solicitorCosts.getVat().doubleValue());
    }

    private void validateHRSection(HardshipReviewDTO reviewDTO) {
        Optional<HRSectionDTO> optionalHRSectionDTO = reviewDTO.getSection().stream().findFirst();
        softly.assertThat(optionalHRSectionDTO.isPresent()).isTrue();
        if (optionalHRSectionDTO.isPresent()) {
            HRSectionDTO hrSectionDTO = optionalHRSectionDTO.get();
            softly.assertThat(hrSectionDTO.getDetailType().getType())
                    .isEqualTo(HardshipReviewDetailType.EXPENDITURE.getType());
            softly.assertThat(hrSectionDTO.getDetailType().getDescription())
                    .isEqualTo("Extra Expenditure");
            Optional<HRDetailDTO> optionalHRDetailDTO = hrSectionDTO.getDetail().stream().findFirst();
            softly.assertThat(optionalHRDetailDTO.isPresent()).isTrue();
            if (optionalHRDetailDTO.isPresent()) {
                HRDetailDTO hrDetailDTO = optionalHRDetailDTO.get();
                softly.assertThat(hrDetailDTO.getAmountNumber().doubleValue())
                        .isEqualTo(TestModelDataBuilder.AMOUNT.doubleValue());
                softly.assertThat(hrDetailDTO.getId().intValue())
                        .isEqualTo(HARDSHIP_DETAIL_ID);
                softly.assertThat(hrDetailDTO.getOtherDescription())
                        .isEqualTo("Loan to family members");
                softly.assertThat(hrDetailDTO.getFrequency().getCode())
                        .isEqualTo(Frequency.TWO_WEEKLY.getCode());
                softly.assertThat(hrDetailDTO.getReason().getReason())
                        .isEqualTo(HardshipReviewDetailReason.COVERED_BY_LIVING_EXPENSE.getReason());
                softly.assertThat(hrDetailDTO.getReason().getId())
                        .isEqualTo(HardshipReviewDetailReason.COVERED_BY_LIVING_EXPENSE.getId());
                softly.assertThat(hrDetailDTO.getDetailDescription().getDescription())
                        .isEqualTo(HardshipReviewDetailCode.OTHER.getDescription());
                softly.assertThat(hrDetailDTO.isAccepted()).isTrue();
            }
        }
    }
}
