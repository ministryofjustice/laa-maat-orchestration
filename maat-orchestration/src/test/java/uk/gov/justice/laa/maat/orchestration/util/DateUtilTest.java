package uk.gov.justice.laa.maat.orchestration.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DateUtilTest {

    @Test
    void givenAEmptyStringDate_whenToDateIsInvoked_thenReturnNull() {
        assertThat(DateUtil.toDate(null)).isNull();
    }

    @Test
    void givenAValidLocalDateTime_whenToDateIsInvoked_thenValidDateIsReturned() {
        LocalDateTime dateModified = LocalDateTime.now();
        assertThat(DateUtil.toDate(dateModified)).isNotNull();
    }
}
