package uk.gov.justice.laa.maat.orchestration.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorDTO {
    String traceId;
    String code;
    String message;
}
