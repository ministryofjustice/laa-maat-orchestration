package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.enums.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class MaatCourtDataServiceTest {

    @Mock
    private MaatCourtDataApiService maatCourtDataApiService;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;


    @Test
    void givenValidRequest_whenInvokeStoredProcedureIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        maatCourtDataService.invokeStoredProcedure(
                new ApplicationDTO(),
                new UserDTO(),
                StoredProcedure.PROCESS_ACTIVITY
        );
        verify(maatCourtDataApiService).executeStoredProcedure(any(StoredProcedureRequest.class));
    }

    @Test
    void givenValidRequest_whenFindRepOrderIsInvoked_thenGetRepOrderByRepIdIsCalled() {
        maatCourtDataService.findRepOrder(1000);
        verify(maatCourtDataApiService).getRepOrderByRepId(anyInt());
    }

    @Test
    void givenValidRequest_whenGetHardshipIsInvoked_thenGetHardshipIsCalled() {
        maatCourtDataService.getHardship(1000);
        verify(maatCourtDataApiService).getHardship(anyInt());
    }

    @Test
    void givenValidRequest_whenGetFinancialAssessmentIsInvoked_thenGetFinancialAssessmentIsCalled() {
        maatCourtDataService.getFinancialAssessment(1000);
        verify(maatCourtDataApiService).getFinancialAssessment(anyInt());

    }
}
