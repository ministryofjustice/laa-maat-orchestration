package uk.gov.justice.laa.crime.orchestration.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;

@Component
public class ContributionMapper implements RequestMapper<ApiMaatCalculateContributionRequest, ApplicationDTO>,
        ResponseMapper<ApiMaatCalculateContributionResponse, ApplicationDTO> {

    @Override
    public ApiMaatCalculateContributionRequest fromDto(ApplicationDTO dto) {
        return null;
    }

    @Override
    public void toDto(ApiMaatCalculateContributionResponse model, ApplicationDTO dto) {

    }
}
