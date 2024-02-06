package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataApiService {

    @Qualifier("maatApiClient")
    private final RestAPIClient maatApiClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from MAAT Court Data Service: %s";

    public ApplicationDTO executeStoredProcedure(StoredProcedureRequest request) {
        ApplicationDTO response = maatApiClient.post(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getEndpoints().getCallStoredProcUrl(),
                Collections.emptyMap()
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public RepOrderDTO getRepOrderByRepId(Integer repId) {
        var response = maatApiClient.get(
                new ParameterizedTypeReference<RepOrderDTO>() {
                },
                configuration.getMaatApi().getEndpoints().getRepOrderUrl(),
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

}
