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
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.SendToCCLFDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataApiService {

    private static final String RESPONSE_STRING = "Response from MAAT Court Data Service: {}";
    @Qualifier("maatApiClient")
    private final RestAPIClient maatApiClient;
    private final ServicesConfiguration configuration;

    public ApplicationDTO executeStoredProcedure(StoredProcedureRequest request) {
        ApplicationDTO response = maatApiClient.post(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getEndpoints().getCallStoredProcUrl(),
                Collections.emptyMap()
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public RepOrderDTO getRepOrderByRepId(Integer repId) {
        RepOrderDTO response = maatApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getEndpoints().getRepOrderUrl(),
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public UserSummaryDTO getUserSummary(String username) {
        UserSummaryDTO userSummaryDTO = maatApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getUserEndpoints().getUserSummaryUrl(),
                username
        );

        log.info(RESPONSE_STRING, userSummaryDTO);
        return userSummaryDTO;
    }

    public RepOrderDTO updateSendToCCLF(SendToCCLFDTO sendToCCLFDTO) {
        RepOrderDTO response = maatApiClient.put(sendToCCLFDTO,
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getEndpoints().getUpdateSendToCCLFUrl(),
                Map.of()
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public FinancialAssessmentDTO getFinancialAssessment(int financialAssessmentId) {
        FinancialAssessmentDTO financialAssessmentDTO = maatApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getEndpoints().getGetAssessmentUrl(),
                financialAssessmentId
        );
        log.info(RESPONSE_STRING, financialAssessmentDTO);
        return financialAssessmentDTO;
    }

}
