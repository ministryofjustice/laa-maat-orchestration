package uk.gov.justice.laa.maat.orchestration.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CurrencyUtilTest {
    @Test
    void givenNull_whenToCurrencyIsInvoked_NullIsReturned() {
        assertThat(CurrencyUtil.toCurrency(null)).isNull();
    }

    @Test
    void givenAValidInput_whenToCurrencyIsInvoked_ValidCurrencyIsReturned() {
        assertThat(CurrencyUtil.toCurrency(BigDecimal.ONE)).isNotNull();
    }

    @Test
    void givenNull_whenToSysGenCurrencyIsInvoked_NullIsReturned() {
        assertThat(CurrencyUtil.toSysGenCurrency(null)).isNull();
    }

    @Test
    void givenAValidInput_whenToSysGenCurrencyIsInvoked_ValidCurrencyIsReturned() {
        assertThat(CurrencyUtil.toSysGenCurrency(BigDecimal.ONE)).isNotNull();
    }
}
