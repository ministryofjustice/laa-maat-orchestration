package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.service.IojAppealService;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IojAppealsOrchestrationService {

    private final IojAppealService iojAppealService;

    public IOJAppealDTO find(int appealId) {
        return iojAppealService.find(appealId);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        iojAppealService.create(request);

        return request.getApplicationDTO();
    }
}
