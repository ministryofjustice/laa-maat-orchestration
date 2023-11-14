package uk.gov.justice.laa.maat.orchestration.util;

import uk.gov.justice.laa.maat.orchestration.dto.Currency;
import uk.gov.justice.laa.maat.orchestration.dto.SysGenCurrency;

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
