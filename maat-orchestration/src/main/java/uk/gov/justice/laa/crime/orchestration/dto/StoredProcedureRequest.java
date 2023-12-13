package uk.gov.justice.laa.crime.orchestration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StoredProcedureRequest {

    private String dbPackageName;
    private String procedureName;
    private ApplicationDTO application;
    private UserDTO user;
}
