package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    private final MaatCourtDataApiService maatCourtDataApiService;

    public ApplicationDTO invokeStoredProcedure(ApplicationDTO application, UserDTO user,
                                                StoredProcedure storedProcedure) {

        log.info("Invoking stored procedure : {}.{} for rep-id : {}",
                storedProcedure.getPackageName(),
                storedProcedure.getProcedureName(),
                application.getRepId());

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
    
    public UserSummaryDTO getUserSummary(String userName) {
        return maatCourtDataApiService.getUserSummary(userName);
    }

    public FinancialAssessmentDTO getFinancialAssessment(int financialAssessmentId) {return maatCourtDataApiService.getFinancialAssessment(financialAssessmentId);}

}
