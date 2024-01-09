package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    private final MaatCourtDataApiService maatCourtDataApiService;

    public ApplicationDTO invokeStoredProcedure(ApplicationDTO application, UserDTO user, String packageName,
                                                String procedureName) {

        return maatCourtDataApiService.executeStoredProcedure(
                StoredProcedureRequest.builder()
                        .application(application)
                        .user(user)
                        .dbPackageName(packageName)
                        .procedureName(procedureName)
                        .build()
        );
    }
}
