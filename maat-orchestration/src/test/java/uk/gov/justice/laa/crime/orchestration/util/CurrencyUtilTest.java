package uk.gov.justice.laa.crime.orchestration.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
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

    @Test
    void givenNull_whenToDoubleIsInvoked_NullIsReturned() {
        assertThat(CurrencyUtil.toDouble(null)).isNull();
    }

    @Test
    void givenAValidInput_whenToDoubleIsInvoked_ValidCurrencyIsReturned() {
        assertThat(CurrencyUtil.toDouble(1)).isNotNull();
    }

    @Test
    void testCurrencyUtilConstructorIsPrivate() throws NoSuchMethodException {
        Assertions.assertThat(CurrencyUtil.class.getDeclaredConstructors()).hasSize(1);
        Constructor<CurrencyUtil> constructor = CurrencyUtil.class.getDeclaredConstructor();
        Assertions.assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }
}
