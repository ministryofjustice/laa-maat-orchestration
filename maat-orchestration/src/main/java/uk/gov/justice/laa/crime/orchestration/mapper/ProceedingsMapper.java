package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.*;
import uk.gov.justice.laa.crime.util.DateUtil;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.sql.Timestamp;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProceedingsMapper extends CrownCourtMapper {

    private final UserMapper userMapper;

    public ApiUpdateApplicationRequest workflowRequestToUpdateApplicationRequest(
            WorkflowRequest workflowRequest) {

        ApplicationDTO application = workflowRequest.getApplicationDTO();
        ApiUpdateApplicationRequest updateApplicationRequest = new ApiUpdateApplicationRequest()
                .withLaaTransactionId(UUID.randomUUID().toString())
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withCaseType(CaseType.getFrom(application.getCaseDetailsDTO().getCaseType()))
                .withMagCourtOutcome(MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
                .withDecisionReason(DecisionReason.getFrom(application.getRepOrderDecision().getCode()))
                .withDecisionDate(DateUtil.toLocalDateTime(application.getDecisionDate()))
                .withCommittalDate(DateUtil.toLocalDateTime(application.getCommittalDate()))
                .withDateReceived(DateUtil.toLocalDateTime(application.getDateReceived()))
                .withCrownCourtSummary(applicationDtoToCrownCourtSummary(application))
                .withIojAppeal(
                        new ApiIOJAppeal()
                                .withDecisionResult(
                                        application.getAssessmentDTO().getIojAppeal().getAppealDecisionResult()
                                )
                                .withIojResult(application.getIojResult())
                )
                .withPassportAssessment(applicationDtoToPassportAssessment(application))
                .withFinancialAssessment(applicationDtoToFinancialAssessment(application));

        CrownCourtOverviewDTO crownCourtOverview = application.getCrownCourtOverviewDTO();
        CrownCourtSummaryDTO crownCourtSummary = crownCourtOverview.getCrownCourtSummaryDTO();

        ApiCrownCourtSummary ccpCrownCourtSummary = new ApiCrownCourtSummary();
        ccpCrownCourtSummary.setRepOrderDecision(crownCourtSummary.getRepOrderDecision().getValue());
        ccpCrownCourtSummary.setRepOrderDate(DateUtil.toLocalDateTime(crownCourtSummary.getCcRepOrderDate()));
        ccpCrownCourtSummary.setRepType(crownCourtSummary.getCcRepType().getValue());
        ccpCrownCourtSummary.setSentenceOrderDate(DateUtil.toLocalDateTime(crownCourtSummary.getSentenceOrderDate()));
        ccpCrownCourtSummary.setRepId(NumberUtils.toInteger(crownCourtSummary.getCcRepId()));
        ccpCrownCourtSummary.setWithdrawalDate(DateUtil.toLocalDateTime(crownCourtSummary.getCcWithDrawalDate()));

        if (null != crownCourtSummary.getEvidenceProvisionFee()) {
            ccpCrownCourtSummary.setEvidenceFeeLevel(crownCourtSummary.getEvidenceProvisionFee().getFeeLevel());
        }

        if (null != crownCourtSummary.getOutcomeDTOs()) {
            ccpCrownCourtSummary.setCrownCourtOutcome(crownCourtSummaryDtoToCrownCourtOutcomes(crownCourtSummary));
        }

        UserDTO userDTO = workflowRequest.getUserDTO();
        return updateApplicationRequest
                .withCrownCourtSummary(ccpCrownCourtSummary)
                .withApplicantHistoryId(NumberUtils.toInteger(application.getApplicantDTO().getApplicantHistoryId()))
                .withCrownRepId(NumberUtils.toInteger(crownCourtSummary.getCcRepId()))
                .withIsImprisoned(crownCourtSummary.getInPrisoned())
                .withUserSession(userMapper.userDtoToUserSession(userDTO));
    }

    private ApiPassportAssessment applicationDtoToPassportAssessment(ApplicationDTO application) {
        PassportedDTO passported = application.getPassportedDTO();

        if (passported.getPassportedId() != null) {
            return new ApiPassportAssessment()
                    .withResult(passported.getResult())
                    .withStatus(CurrentStatus.getFrom(passported.getAssessementStatusDTO().getStatus()));
        }
        return null;
    }

    private ApiFinancialAssessment applicationDtoToFinancialAssessment(ApplicationDTO application) {

        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        FullAssessmentDTO fullAssessment = financialAssessmentDTO.getFull();
        InitialAssessmentDTO initialAssessment = financialAssessmentDTO.getInitial();

        ApiFinancialAssessment assessment = new ApiFinancialAssessment();
        assessment
                .withInitResult(initialAssessment.getResult())
                .withInitStatus(CurrentStatus.getFrom(initialAssessment.getAssessmnentStatusDTO().getStatus()));

        if (financialAssessmentDTO.getFull().getAssessmentDate() != null) {
            assessment
                    .withFullResult(fullAssessment.getResult())
                    .withFullStatus(CurrentStatus.getFrom(fullAssessment.getAssessmnentStatusDTO().getStatus()));
        }

        HardshipReviewDTO crownHardship = financialAssessmentDTO.getHardship().getCrownCourtHardship();
        if (crownHardship.getId() != null) {
            assessment.withHardshipOverview(
                    new ApiHardshipOverview()
                            .withReviewResult(ReviewResult.getFrom(crownHardship.getReviewResult()))
                            .withAssessmentStatus(CurrentStatus.getFrom(crownHardship.getAsessmentStatus().getStatus()))
            );
        }
        return assessment;
    }

    private ApiCrownCourtSummary applicationDtoToCrownCourtSummary(ApplicationDTO application) {
        CrownCourtSummaryDTO crownCourtSummary = application.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO();
        return new ApiCrownCourtSummary()
                .withRepId(NumberUtils.toInteger(crownCourtSummary.getCcRepId()))
                .withRepOrderDecision(crownCourtSummary.getRepOrderDecision().getValue())
                .withRepType(crownCourtSummary.getCcRepType().getValue())
                .withRepOrderDate(DateUtil.toLocalDateTime(crownCourtSummary.getCcRepOrderDate()))
                .withWithdrawalDate(DateUtil.toLocalDateTime(crownCourtSummary.getCcWithDrawalDate()))
                .withSentenceOrderDate(DateUtil.toLocalDateTime(crownCourtSummary.getSentenceOrderDate()))
                .withEvidenceFeeLevel(crownCourtSummary.getEvidenceProvisionFee().getFeeLevel());
    }

    public ApplicationDTO updateApplicationResponseToApplicationDto(ApiUpdateApplicationResponse response,
                                                                    ApplicationDTO application) {

        application.setTimestamp(Timestamp.valueOf(response.getModifiedDateTime()));
        CrownCourtSummaryDTO crownCourtSummary = application.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO();
        crownCourtSummary.setCcRepOrderDate(DateUtil.toDate(response.getCrownRepOrderDate()));
        crownCourtSummary.setRepOrderDecision(new SysGenString(response.getCrownRepOrderDecision()));
        crownCourtSummary.setCcRepType(new SysGenString(response.getCrownRepOrderType()));

        return application;
    }

}
