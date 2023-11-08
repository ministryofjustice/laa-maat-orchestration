package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorDTO {
    String traceId;
    String code;
    String message;
}
