package uk.gov.justice.laa.crime.orchestration.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberUtils {

    public static Integer toInteger(Long value) {
        return (value != null) ? value.intValue() : null;
    }
}
