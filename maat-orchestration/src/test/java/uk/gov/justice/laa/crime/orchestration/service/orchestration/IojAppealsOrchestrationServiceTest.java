package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.service.IojAppealService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class IojAppealsOrchestrationServiceTest {
    private static final int EXISTING_APPEAL_ID = 1;

    @Mock
    private IojAppealService iojAppealService;

    @InjectMocks
    private IojAppealsOrchestrationService iojAppealsOrchestrationService;

    @Test
    void givenAppealId_whenFindIsInvoked_thenIojAppealServiceIsCalled() {
        when(iojAppealService.find(EXISTING_APPEAL_ID)).thenReturn(new IOJAppealDTO());

        iojAppealsOrchestrationService.find(EXISTING_APPEAL_ID);

        verify(iojAppealService).find(EXISTING_APPEAL_ID);
    }
}
