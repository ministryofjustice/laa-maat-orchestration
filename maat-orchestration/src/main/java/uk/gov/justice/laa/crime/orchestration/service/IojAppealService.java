package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.IojAppeal;
import uk.gov.justice.laa.crime.common.model.ioj.IojAppealMetadata;
import uk.gov.justice.laa.crime.enums.IojAppealAssessor;
import uk.gov.justice.laa.crime.enums.IojAppealDecision;
import uk.gov.justice.laa.crime.enums.IojAppealDecisionReason;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.IojAppealMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.UserMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IojAppealService {

    private final AssessmentApiService assessmentApiService;
    private final IojAppealMapper iojAppealMapper;
    private final UserMapper userMapper;

    public IOJAppealDTO find(int appealId) {
        ApiGetIojAppealResponse response = assessmentApiService.find(appealId);

        return iojAppealMapper.apiGetIojAppealResponseToIojAppealDTO(response);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        ApplicationDTO applicationDTO = request.getApplicationDTO();
        IOJAppealDTO iojAppealDto = applicationDTO.getAssessmentDTO().getIojAppeal();

        boolean judicialReview = iojAppealDto.getAppealReason().getCode().equals(NewWorkReason.JR.getCode());

        // Move the following to the mapper
        IojAppeal iojAppeal = new IojAppeal()
                .withReceivedDate(
                        LocalDateTime.from(iojAppealDto.getReceivedDate().toInstant()))
                .withAppealReason(
                        NewWorkReason.getFrom(iojAppealDto.getNewWorkReasonDTO().getCode()))
                .withAppealAssessor(judicialReview ? IojAppealAssessor.JUDGE : IojAppealAssessor.CASEWORKER)
                .withAppealDecision(Enum.valueOf(IojAppealDecision.class, iojAppealDto.getAppealDecisionResult()))
                .withDecisionReason(IojAppealDecisionReason.getFrom(
                        iojAppealDto.getAppealReason().getCode()))
                .withNotes(iojAppealDto.getNotes())
                .withDecisionDate(
                        LocalDateTime.from(iojAppealDto.getDecisionDate().toInstant()));

        IojAppealMetadata iojAppealMetadata = new IojAppealMetadata()
                .withLegacyApplicationId(request.getApplicationDTO().getRepId().intValue())
                .withCaseManagementUnitId(iojAppealDto.getCmuId().intValue())
                .withUserSession(userMapper.userDtoToUserSession(request.getUserDTO()));

        ApiCreateIojAppealRequest iojAppealRequest = new ApiCreateIojAppealRequest(iojAppeal, iojAppealMetadata);
        ApiCreateIojAppealResponse iojAppealResponse = assessmentApiService.create(iojAppealRequest);

        iojAppealDto.setIojId(iojAppealResponse.getLegacyAppealId().longValue());

        return applicationDTO;
    }
}
