package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.orchestration.client.CrimeAssessmentApiClient;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.IojAppealMapper;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class IojAppealService {

    private final CrimeAssessmentApiClient crimeAssessmentApiClient;
    private final IojAppealMapper iojAppealMapper;
    private static final String REQUEST_STRING = "Request to Crime Assessment Service for IoJ Appeal ID: {}";
    private static final String RESPONSE_STRING = "Response from Crime Assessment Service: {}";

    public IOJAppealDTO find(Integer appealId) {
        log.debug(REQUEST_STRING, appealId);

        // 404s are intercepted by the WebClientFilters, so we re-throw the exception here to be
        // caught by our DefaultExceptionHandler
        ApiGetIojAppealResponse apiGetIojAppealResponse = Optional.ofNullable(
                        crimeAssessmentApiClient.getIojAppeal(appealId))
                .orElseThrow(() ->
                        new WebClientResponseException(HttpStatus.NOT_FOUND.value(), "Not found", null, null, null));

        log.debug(RESPONSE_STRING, apiGetIojAppealResponse);

        return iojAppealMapper.apiGetIojAppealResponseToIojAppealDTO(apiGetIojAppealResponse);
    }
}
