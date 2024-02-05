package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.enums.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    private final MaatCourtDataApiService maatCourtDataApiService;

    public ApplicationDTO invokeStoredProcedure(ApplicationDTO application, UserDTO user,
                                                StoredProcedure storedProcedure) {

        return maatCourtDataApiService.executeStoredProcedure(
                StoredProcedureRequest.builder()
                        .user(user)
                        .application(application)
                        .dbPackageName(storedProcedure.getPackageName())
                        .procedureName(storedProcedure.getProcedureName())
                        .build()
        );
    }

    public RepOrderDTO findRepOrder(Integer repId) {
        return maatCourtDataApiService.getRepOrderByRepId(repId);
    }

    public ApiFindHardshipResponse getHardship(Integer hardshipReviewId) {
        return maatCourtDataApiService.getHardship(hardshipReviewId);
    }

    public FinancialAssessmentDTO getFinancialAssessment(Integer financialAssessmentId) {
        return maatCourtDataApiService.getFinancialAssessment(financialAssessmentId);
    }
}
