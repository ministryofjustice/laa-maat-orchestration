package uk.gov.justice.laa.crime.orchestration.dto.maat_api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureToggleDTO {

    private Integer id;
    private String username;
    private String featureName;
    private String action;
}
