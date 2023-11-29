package uk.gov.justice.laa.crime.orchestration.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NumberUtilsTest {

    @Test
    void givenNullLong_whenToIntegerIsInvoked_thenNullIsReturned() {
        assertThat(NumberUtils.toInteger(null)).isNull();
    }

    @Test
    void givenLong_whenToIntegerIsInvoked_thenIntValueIsReturned() {
        assertThat(NumberUtils.toInteger(500L)).isEqualTo(500);
    }
}
