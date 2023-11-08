package uk.gov.justice.laa.crime.orchestration.util;

import uk.gov.justice.laa.crime.orchestration.dto.maat.Currency;
import uk.gov.justice.laa.crime.orchestration.dto.maat.SysGenCurrency;

import java.math.BigDecimal;

public class CurrencyUtil {

    private CurrencyUtil() {}

    public static Currency toCurrency(BigDecimal input) {
        return (input != null) ? new Currency(input.doubleValue()) : null;
    }

    public static Double toDouble(Integer input) {
        return (input != null) ? input.doubleValue() : null;
    }

    public static SysGenCurrency toSysGenCurrency(BigDecimal input) {
        return (input != null) ? new SysGenCurrency(input.doubleValue()) : null;
    }
}
