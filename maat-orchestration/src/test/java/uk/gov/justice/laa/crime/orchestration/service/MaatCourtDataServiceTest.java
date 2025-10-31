package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static uk.gov.justice.laa.crime.util.FileUtils.readFileToString;

import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith({MockitoExtension.class})
class MaatCourtDataServiceTest {

    public static final String TEST_FILE_PATH_REPORDER_DTO_JSON = "data/dto/reporder_dto.json";

    @Mock
    private MaatCourtDataApiService maatCourtDataApiService;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Test
    void givenValidRequest_whenInvokeStoredProcedureIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        maatCourtDataService.invokeStoredProcedure(
                new ApplicationDTO(), new UserDTO(), StoredProcedure.PROCESS_ACTIVITY);
        verify(maatCourtDataApiService).executeStoredProcedure(any(StoredProcedureRequest.class));
    }

    @Test
    void givenValidRequest_whenFindRepOrderIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        maatCourtDataService.findRepOrder(1000);
        verify(maatCourtDataApiService).getRepOrderByRepId(anyInt());
    }

    @Test
    void givenValidRequest_whenGetUserSummaryIsInvoked_thenRequestIsMappedAndApiServiceIsCalled() {
        maatCourtDataService.getUserSummary("test");
        verify(maatCourtDataApiService).getUserSummary(anyString());
    }

    @Test
    void givenValidResponseJson_whenFindRepOrderIsInvoked_ValidRepOrderDTOIsReturned() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        RepOrderDTO repOrderDTO =
                mapper.readValue(readFileToString(TEST_FILE_PATH_REPORDER_DTO_JSON), RepOrderDTO.class);
        assertThat(repOrderDTO.getId()).isEqualTo(5788163);
    }

    @Test
    void givenValidReqest_whenUpdateRepOrderDateModifiedIsInvoked_thenApiServiceIsCalled() {
        Map<String, Object> fieldsToUpdate = Map.of("dateModified", LocalDateTime.now());

        maatCourtDataService.updateRepOrderDateModified(1234, fieldsToUpdate);
        verify(maatCourtDataApiService).patchRepOrder(1234, fieldsToUpdate);
    }
}
