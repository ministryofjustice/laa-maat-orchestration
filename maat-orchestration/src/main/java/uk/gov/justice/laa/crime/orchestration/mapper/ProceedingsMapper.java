package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.orchestration.common.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.common.model.orchestration.crown_court.*;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;

import uk.gov.justice.laa.crime.util.DateUtil;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.crime.util.DateUtil.toZonedDateTime;

@Component
@RequiredArgsConstructor
public class ProceedingsMapper extends CrownCourtMapper {

    private final UserMapper userMapper;

    public ApiUpdateApplicationRequest workflowRequestToUpdateApplicationRequest(
            WorkflowRequest workflowRequest) {

        ApplicationDTO application = workflowRequest.getApplicationDTO();
        ApiUpdateApplicationRequest updateApplicationRequest = new ApiUpdateApplicationRequest()
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withCaseType(CaseType.getFrom(application.getCaseDetailsDTO().getCaseType()))
                .withMagCourtOutcome(MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
                .withDecisionReason(DecisionReason.getFrom(application.getRepOrderDecision().getCode()))
                .withDecisionDate(DateUtil.toLocalDateTime(application.getDecisionDate()))
                .withCommittalDate(DateUtil.toLocalDateTime(application.getCommittalDate()))
                .withDateReceived(DateUtil.toLocalDateTime(application.getDateReceived()))
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

        ApiCrownCourtSummary ccpCrownCourtSummary =
                crownCourtSummaryDtoToApiCrownCourtSummary(crownCourtSummary);

        mapEvidenceDetailsToRequest(updateApplicationRequest, application);

        UserDTO userDTO = workflowRequest.getUserDTO();
        return updateApplicationRequest
                .withCrownCourtSummary(ccpCrownCourtSummary)
                .withApplicantHistoryId(NumberUtils.toInteger(application.getApplicantDTO().getApplicantHistoryId()))
                .withCrownRepId(NumberUtils.toInteger(crownCourtSummary.getCcRepId()))
                .withIsImprisoned(crownCourtSummary.getInPrisoned())
                .withUserSession(userMapper.userDtoToUserSession(userDTO));
    }

    private ApiCrownCourtSummary crownCourtSummaryDtoToApiCrownCourtSummary(CrownCourtSummaryDTO crownCourtSummary) {
        ApiCrownCourtSummary ccpCrownCourtSummary = new ApiCrownCourtSummary();
        ccpCrownCourtSummary.setRepOrderDecision(crownCourtSummary.getRepOrderDecision().getValue());
        ccpCrownCourtSummary.setRepOrderDate(DateUtil.toLocalDateTime(crownCourtSummary.getCcRepOrderDate()));
        ccpCrownCourtSummary.setRepType(crownCourtSummary.getCcRepType().getValue());
        ccpCrownCourtSummary.setSentenceOrderDate(DateUtil.toLocalDateTime(crownCourtSummary.getSentenceOrderDate()));
        ccpCrownCourtSummary.setRepId(NumberUtils.toInteger(crownCourtSummary.getCcRepId()));
        ccpCrownCourtSummary.setWithdrawalDate(DateUtil.toLocalDateTime(crownCourtSummary.getCcWithDrawalDate()));
        ccpCrownCourtSummary.setIsImprisoned(crownCourtSummary.getInPrisoned());
        ccpCrownCourtSummary.setIsWarrantIssued(crownCourtSummary.getBenchWarrantyIssued());

        if (null != crownCourtSummary.getEvidenceProvisionFee()) {
            ccpCrownCourtSummary.setEvidenceFeeLevel(
                    EvidenceFeeLevel.getFrom(crownCourtSummary.getEvidenceProvisionFee().getFeeLevel())
            );
        }

        if (null != crownCourtSummary.getOutcomeDTOs()) {
            ccpCrownCourtSummary.setCrownCourtOutcome(crownCourtSummaryDtoToCrownCourtOutcomes(crownCourtSummary));
        }

        return ccpCrownCourtSummary;
    }

    private CrownCourtSummaryDTO apiCrownCourtSummaryToCrownCourtSummaryDto(ApiCrownCourtSummary apiCrownCourtSummary) {
        return CrownCourtSummaryDTO.builder()
                .ccRepId(apiCrownCourtSummary.getRepId().longValue())
                .ccRepType(new SysGenString(apiCrownCourtSummary.getRepType()))
                .ccRepOrderDate(DateUtil.toDate(apiCrownCourtSummary.getRepOrderDate()))
                .sentenceOrderDate(DateUtil.toDate(apiCrownCourtSummary.getSentenceOrderDate()))
                .ccWithDrawalDate(DateUtil.toDate(apiCrownCourtSummary.getWithdrawalDate()))
                .repOrderDecision(new SysGenString(apiCrownCourtSummary.getRepOrderDecision()))
                .inPrisoned(apiCrownCourtSummary.getIsImprisoned())
                .benchWarrantyIssued(apiCrownCourtSummary.getIsWarrantIssued())
                .evidenceProvisionFee(
                        EvidenceFeeDTO.builder()
                                .feeLevel(apiCrownCourtSummary.getEvidenceFeeLevel().getFeeLevel())
                                .description(new SysGenString(
                                        apiCrownCourtSummary.getEvidenceFeeLevel().getDescription()))
                                .build())
                .outcomeDTOs(
                        apiRepOrderCrownCourtOutcomesToOutcomeDtos(apiCrownCourtSummary.getRepOrderCrownCourtOutcome()))
                .build();
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

    public ApplicationDTO updateApplicationResponseToApplicationDto(ApiUpdateApplicationResponse response,
                                                                    ApplicationDTO application) {

        application.setTimestamp(toZonedDateTime(response.getModifiedDateTime()));
        CrownCourtSummaryDTO crownCourtSummary = application.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO();
        crownCourtSummary.setCcRepOrderDate(DateUtil.toDate(response.getCrownRepOrderDate()));
        crownCourtSummary.setRepOrderDecision(new SysGenString(response.getCrownRepOrderDecision()));
        crownCourtSummary.setCcRepType(new SysGenString(response.getCrownRepOrderType()));

        return application;
    }

    public ApplicationDTO updateCrownCourtResponseToApplicationDto(ApiUpdateCrownCourtResponse response,
                                                                   ApplicationDTO application) {

        application.setTimestamp(toZonedDateTime(response.getModifiedDateTime()));
        application.getCrownCourtOverviewDTO().setCrownCourtSummaryDTO(
                apiCrownCourtSummaryToCrownCourtSummaryDto(response.getCrownCourtSummary())
        );
        return application;
    }

    private void mapEvidenceDetailsToRequest(ApiUpdateApplicationRequest request, ApplicationDTO application) {

        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        CapitalEquityDTO capitalEquityDTO = application.getCapitalEquityDTO();

        if (null != application && null != application.getEmploymentStatusDTO()) {
            request.setEmstCode(application.getEmploymentStatusDTO().getCode());
        }

        if (null != financialAssessmentDTO && null != financialAssessmentDTO.getIncomeEvidence()) {
            IncomeEvidenceSummaryDTO incomeEvidenceSummaryDTO = financialAssessmentDTO.getIncomeEvidence();
            request.setIncomeEvidenceReceivedDate(DateUtil.toLocalDateTime(incomeEvidenceSummaryDTO.getEvidenceReceivedDate()));
        }

       if (null != capitalEquityDTO && null != capitalEquityDTO.getCapitalEvidenceSummary()) {
            request.setCapitalEvidenceReceivedDate(
                    DateUtil.toLocalDateTime(capitalEquityDTO.getCapitalEvidenceSummary().getEvidenceReceivedDate()));
        }

       if (null != application.getCapitalEquityDTO() && null !=application.getCapitalEquityDTO().getCapitalOther()) {

           List<ApiCapitalEvidence> apiCapitalEvidenceList = application.getCapitalEquityDTO().getCapitalOther().stream()
                   .map(CapitalOtherDTO::getCapitalEvidence)
                   .flatMap(Collection::stream)
                   .map(x -> {
                       ApiCapitalEvidence apiCapitalEvidence = new ApiCapitalEvidence();
                       apiCapitalEvidence.setEvidenceType(x.getEvidenceTypeDTO().getEvidence());
                       apiCapitalEvidence.setDateReceived(DateUtil.toLocalDateTime(x.getDateReceived()));
                       return apiCapitalEvidence;
                   })
                   .collect(Collectors.toList());

           request.setCapitalEvidence(apiCapitalEvidenceList);
       }
    }

}
