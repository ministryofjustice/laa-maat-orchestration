package uk.gov.justice.laa.crime.orchestration.dto.maat_api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendToCCLFDTO {
    @NotNull
    private Integer repId;
    @NotNull
    private Long applId;
    @NotNull
    private Long applHistoryId;
}
