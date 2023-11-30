package uk.gov.justice.laa.crime.orchestration.util;

public class CurrencyUtil {

    private CurrencyUtil() {
    }

    public static Double toDouble(Integer input) {
        return (input != null) ? input.doubleValue() : null;
    }
}
