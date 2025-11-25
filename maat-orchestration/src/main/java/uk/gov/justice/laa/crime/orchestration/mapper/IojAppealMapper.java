package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.enums.IojAppealAssessor;
import uk.gov.justice.laa.crime.enums.IojAppealDecision;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJDecisionReasonDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.NewWorkReasonDTO;

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
}
