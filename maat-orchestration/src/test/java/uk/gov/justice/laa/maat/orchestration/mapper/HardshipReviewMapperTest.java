package uk.gov.justice.laa.maat.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.maat.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.maat.orchestration.dto.HardshipReviewDTO;
import uk.gov.justice.laa.maat.orchestration.model.ApiFindHardshipResponse;
import uk.gov.justice.laa.maat.orchestration.util.DateUtil;

@ExtendWith(SoftAssertionsExtension.class)
class HardshipReviewMapperTest {

    HardshipReviewMapper mapper = new HardshipReviewMapper();

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiFindHardshipResponse_whenToDtoIsInvoked_thenDtoIsMapped() {
        ApiFindHardshipResponse hardship = TestModelDataBuilder.getApiFindHardshipResponse();
        HardshipReviewDTO reviewDTO = new HardshipReviewDTO();
        mapper.toDto(hardship, reviewDTO);

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
        softly.assertThat(reviewDTO.getSolictorsCosts().getSolicitorDisb().doubleValue())
                .isEqualTo(hardship.getSolicitorCosts().getDisbursements().doubleValue());
        softly.assertThat(reviewDTO.getSolictorsCosts().getSolicitorHours().intValue())
                .isEqualTo(hardship.getSolicitorCosts().getHours().intValue());
        softly.assertThat(reviewDTO.getSolictorsCosts().getSolicitorEstimatedTotalCost().doubleValue())
                .isEqualTo(hardship.getSolicitorCosts().getEstimatedTotal().doubleValue());
        softly.assertThat(reviewDTO.getSolictorsCosts().getSolicitorRate().doubleValue())
                .isEqualTo(hardship.getSolicitorCosts().getRate().doubleValue());
        softly.assertThat(reviewDTO.getSolictorsCosts().getSolicitorVat().doubleValue())
                .isEqualTo(hardship.getSolicitorCosts().getVat().doubleValue());
    }
}
