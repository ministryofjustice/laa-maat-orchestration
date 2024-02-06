package uk.gov.justice.laa.crime.orchestration.dto.validation;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ErrorDTO {
    String traceId;
    String code;
    String message;
    List<String> messageList;
}
