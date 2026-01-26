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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
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

        boolean appealSuccessful = Boolean.TRUE.equals(response.getAppealSuccessful());
        String appealDecision = (appealSuccessful ? IojAppealDecision.PASS : IojAppealDecision.FAIL).toString();

        if (response.getAppealAssessor().equals(IojAppealAssessor.CASEWORKER)) {
            if (appealSuccessful) {
                appealSetUpResult = SET_UP_RESULT_CASEWORKER_PASS;
            } else {
                appealSetUpResult = SET_UP_RESULT_CASEWORKER_FAIL;
            }
        } else if (response.getAppealAssessor().equals(IojAppealAssessor.JUDGE)) {
            appealSetUpResult = SET_UP_RESULT_JUDGE;
        }

        Date receivedDate = Date.from(response.getReceivedDate()
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC)
                .toInstant());
        Date decisionDate = Date.from(response.getDecisionDate()
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC)
                .toInstant());

        return IOJAppealDTO.builder()
                .iojId(Long.valueOf(response.getLegacyAppealId()))
                .cmuId(Long.valueOf(response.getCaseManagementUnitId()))
                .receivedDate(receivedDate)
                .decisionDate(decisionDate)
                .appealSetUpResult(appealSetUpResult)
                .appealDecisionResult(appealDecision)
                .notes(response.getNotes())
                .appealReason(iojDecisionReasonDTO)
                .assessmentStatusDTO(assessmentStatusDTO)
                .newWorkReasonDTO(newWorkReasonDTO)
                .build();
    }

    public ApiCreateIojAppealRequest mapIojAppealDtoToApiCreateIojAppealRequest(WorkflowRequest request) {

        IOJAppealDTO iojAppealDto =
                request.getApplicationDTO().getAssessmentDTO().getIojAppeal();

        boolean judicialReview = iojAppealDto.getNewWorkReasonDTO().getCode().equals(NewWorkReason.JR.getCode());

        LocalDate receivedDate = iojAppealDto
                .getReceivedDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate decisionDate = iojAppealDto
                .getDecisionDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        IojAppeal iojAppeal = new IojAppeal()
                .withReceivedDate(receivedDate)
                .withAppealReason(
                        NewWorkReason.getFrom(iojAppealDto.getNewWorkReasonDTO().getCode()))
                .withAppealAssessor(judicialReview ? IojAppealAssessor.JUDGE : IojAppealAssessor.CASEWORKER)
                .withAppealSuccessful(IojAppealDecision.PASS.toString().equals(iojAppealDto.getAppealDecisionResult()))
                .withDecisionReason(IojAppealDecisionReason.getFrom(
                        iojAppealDto.getAppealReason().getCode()))
                .withNotes(iojAppealDto.getNotes())
                .withDecisionDate(decisionDate);

        LocalDate applicationReceivedDate = request.getApplicationDTO()
                .getDateReceived()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        IojAppealMetadata iojAppealMetadata = new IojAppealMetadata()
                .withLegacyApplicationId(request.getApplicationDTO().getRepId().intValue())
                .withApplicationReceivedDate(applicationReceivedDate)
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
