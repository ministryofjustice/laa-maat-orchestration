package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

import static org.mockito.ArgumentMatchers.any;
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
                "ASSESSMENTS",
                "TEST_SP"
        );
        verify(maatCourtDataApiService).executeStoredProcedure(any(StoredProcedureRequest.class));
    }
}
