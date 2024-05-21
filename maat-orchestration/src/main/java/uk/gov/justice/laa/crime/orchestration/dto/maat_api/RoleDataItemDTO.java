package uk.gov.justice.laa.crime.orchestration.dto.maat_api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDataItemDTO implements Serializable {
    private String roleName;
    private String dataItem;
    private String enabled;
    private String insertAllowed;
    private String updateAllowed;
}