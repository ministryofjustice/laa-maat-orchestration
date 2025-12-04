package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.IojAppeal;
import uk.gov.justice.laa.crime.common.model.ioj.IojAppealMetadata;
import uk.gov.justice.laa.crime.enums.IojAppealAssessor;
import uk.gov.justice.laa.crime.enums.IojAppealDecision;
import uk.gov.justice.laa.crime.enums.IojAppealDecisionReason;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJDecisionReasonDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.NewWorkReasonDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IojAppealMapper {

    private static final String SET_UP_RESULT_CASEWORKER_PASS = "GRANT";
    private static final String SET_UP_RESULT_CASEWORKER_FAIL = "REFUSED";
    private static final String SET_UP_RESULT_JUDGE = "REFER";
    private static final String ASSESSMENT_STATUS_DESCRIPTION = "Complete";
    private final UserMapper userMapper;

    public IOJAppealDTO apiGetIojAppealResponseToIojAppealDTO(ApiGetIojAppealResponse response) {

        AssessmentStatusDTO assessmentStatusDTO = AssessmentStatusDTO.builder()
                .status(AssessmentStatusDTO.COMPLETE)
                .description(ASSESSMENT_STATUS_DESCRIPTION)
                .build();

        // appealReason uses the NewWorkReason enum
        NewWorkReasonDTO newWorkReasonDTO = NewWorkReasonDTO.builder()
                .code(response.getAppealReason().getCode())
                .description(response.getAppealReason().getDescription())
                .type(response.getAppealReason().getType())
                .build();

        IOJDecisionReasonDTO iojDecisionReasonDTO = IOJDecisionReasonDTO.builder()
                .code(response.getDecisionReason().getCode())
                .description(response.getDecisionReason().getDescription())
                .build();

        String appealSetUpResult = null;

        if (response.getAppealAssessor().equals(IojAppealAssessor.CASEWORKER)) {
            if (response.getAppealDecision().equals(IojAppealDecision.PASS)) {
                appealSetUpResult = SET_UP_RESULT_CASEWORKER_PASS;
            } else if (response.getAppealDecision().equals(IojAppealDecision.FAIL)) {
                appealSetUpResult = SET_UP_RESULT_CASEWORKER_FAIL;
            }
        } else if (response.getAppealAssessor().equals(IojAppealAssessor.JUDGE)) {
            appealSetUpResult = SET_UP_RESULT_JUDGE;
        }

        Date receivedDate = Date.from(
                response.getReceivedDate().atZone(ZoneId.systemDefault()).toInstant());
        Date decisionDate = Date.from(
                response.getDecisionDate().atZone(ZoneId.systemDefault()).toInstant());

        return IOJAppealDTO.builder()
                .iojId(Long.valueOf(response.getLegacyAppealId()))
                .cmuId(Long.valueOf(response.getCaseManagementUnitId()))
                .receivedDate(receivedDate)
                .decisionDate(decisionDate)
                .appealSetUpResult(appealSetUpResult)
                .appealDecisionResult(response.getAppealDecision().toString())
                .notes(response.getNotes())
                .appealReason(iojDecisionReasonDTO)
                .assessmentStatusDTO(assessmentStatusDTO)
                .newWorkReasonDTO(newWorkReasonDTO)
                .build();
    }

    public ApiCreateIojAppealRequest mapIojAppealDtoToApiCreateIojAppealRequest(WorkflowRequest request) {

        IOJAppealDTO iojAppealDto =
                request.getApplicationDTO().getAssessmentDTO().getIojAppeal();

        boolean judicialReview = iojAppealDto.getAppealReason().getCode().equals(NewWorkReason.JR.getCode());

        LocalDateTime receivedDate = iojAppealDto
                .getReceivedDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime decisionDate = iojAppealDto
                .getDecisionDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        IojAppeal iojAppeal = new IojAppeal()
                .withReceivedDate(receivedDate)
                .withAppealReason(
                        NewWorkReason.getFrom(iojAppealDto.getNewWorkReasonDTO().getCode()))
                .withAppealAssessor(judicialReview ? IojAppealAssessor.JUDGE : IojAppealAssessor.CASEWORKER)
                .withAppealDecision(Enum.valueOf(IojAppealDecision.class, iojAppealDto.getAppealDecisionResult()))
                .withDecisionReason(IojAppealDecisionReason.getFrom(
                        iojAppealDto.getAppealReason().getCode()))
                .withNotes(iojAppealDto.getNotes())
                .withDecisionDate(decisionDate);

        IojAppealMetadata iojAppealMetadata = new IojAppealMetadata()
                .withLegacyApplicationId(request.getApplicationDTO().getRepId().intValue())
                .withCaseManagementUnitId(iojAppealDto.getCmuId().intValue())
                .withUserSession(userMapper.userDtoToUserSession(request.getUserDTO()));

        return new ApiCreateIojAppealRequest(iojAppeal, iojAppealMetadata);
    }

    public UserActionDTO getUserActionDTO(WorkflowRequest request) {
        NewWorkReason newWorkReason = NewWorkReason.getFrom(request.getApplicationDTO()
                .getAssessmentDTO()
                .getIojAppeal()
                .getNewWorkReasonDTO()
                .getCode());

        return userMapper.getUserActionDTO(request, Action.CREATE_IOJ, newWorkReason);
    }
}
