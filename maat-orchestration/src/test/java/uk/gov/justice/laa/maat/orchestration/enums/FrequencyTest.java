package uk.gov.justice.laa.maat.orchestration.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class FrequencyTest {

    @Test
    void givenABlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(Frequency.getFrom(null)).isNull();
    }

    @Test
    void givenValidResultString_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(Frequency.getFrom("MONTHLY")).isEqualTo(Frequency.MONTHLY);
    }

    @Test
    void valueOfCurrentStatusFromString_nullParameter_ReturnsNull() {
        assertThatThrownBy(
                () -> Frequency.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenValidInput_ValidateEnumValues() {
        assertThat("ANNUALLY").isEqualTo(Frequency.ANNUALLY.getCode());
        assertThat("Weekly").isEqualTo(Frequency.WEEKLY.getDescription());
        assertThat(26).isEqualTo(Frequency.TWO_WEEKLY.getAnnualWeighting());
    }

}