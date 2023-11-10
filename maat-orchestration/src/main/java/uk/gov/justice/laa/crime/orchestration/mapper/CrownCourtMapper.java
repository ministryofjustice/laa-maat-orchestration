package uk.gov.justice.laa.crime.orchestration.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationResponse;

@Component
public class CrownCourtMapper implements RequestMapper<ApiUpdateApplicationRequest, ApplicationDTO>,
        ResponseMapper<ApiUpdateApplicationResponse, ApplicationDTO> {

    @Override
    public ApiUpdateApplicationRequest fromDto(ApplicationDTO dto) {
        return null;
    }

    @Override
    public void toDto(ApiUpdateApplicationResponse model, ApplicationDTO dto) {

    }
}
