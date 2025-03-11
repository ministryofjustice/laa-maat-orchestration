package uk.gov.justice.laa.crime.orchestration.service.api;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
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
    private static final String REQUEST_STRING = "Request to MAAT Court Data Service: {}";
    @Qualifier("maatApiClient")
    private final RestAPIClient maatApiClient;
    private final ServicesConfiguration configuration;

    public ApplicationDTO executeStoredProcedure(StoredProcedureRequest request) {
        log.debug(REQUEST_STRING, request);
        ApplicationDTO response = maatApiClient.post(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getEndpoints().getCallStoredProcUrl(),
                Collections.emptyMap()
        );
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public RepOrderDTO getRepOrderByRepId(Integer repId) {
        log.info(REQUEST_STRING, repId);
        RepOrderDTO response = maatApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getEndpoints().getRepOrderUrl(),
                repId
        );
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public UserSummaryDTO getUserSummary(String username) {
        log.info(REQUEST_STRING, username);
        UserSummaryDTO userSummaryDTO = maatApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getUserEndpoints().getUserSummaryUrl(),
                username
        );

        log.debug(RESPONSE_STRING, userSummaryDTO);
        return userSummaryDTO;
    }

    public void updateSendToCCLF(SendToCCLFDTO sendToCCLFDTO) {
        log.debug(REQUEST_STRING, sendToCCLFDTO);
        maatApiClient.put(sendToCCLFDTO,
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getEndpoints().getUpdateSendToCCLFUrl(),
                Map.of()
        );
    }

    public FinancialAssessmentDTO getFinancialAssessment(int financialAssessmentId) {
        log.info(REQUEST_STRING, financialAssessmentId);
        FinancialAssessmentDTO financialAssessmentDTO = maatApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getEndpoints().getFinancialAssessmentUrl(),
                financialAssessmentId
        );
        log.debug(RESPONSE_STRING, financialAssessmentDTO);
        return financialAssessmentDTO;
    }

    public MaatApiAssessmentResponse updateFinancialAssessment(MaatApiUpdateAssessment maatApiUpdateAssessment) {
        log.debug(REQUEST_STRING, maatApiUpdateAssessment);
        MaatApiAssessmentResponse financialAssessmentDTO = maatApiClient.put(
                maatApiUpdateAssessment,
                new ParameterizedTypeReference<>() {},
                configuration.getMaatApi().getEndpoints().getFinancialAssessmentUrl(),
                Map.of()
        );
        log.debug(RESPONSE_STRING, financialAssessmentDTO);
        return financialAssessmentDTO;
    }

    public void patchRepOrder(int repOrderId, Map<String, Object> fieldsToUpdate) {
        log.debug(REQUEST_STRING, fieldsToUpdate);
        maatApiClient.patch(
            fieldsToUpdate,
            new ParameterizedTypeReference<>() {},
            configuration.getMaatApi().getEndpoints().getRepOrderUrl(),
            Collections.emptyMap(),
            repOrderId
        );
    }
}
