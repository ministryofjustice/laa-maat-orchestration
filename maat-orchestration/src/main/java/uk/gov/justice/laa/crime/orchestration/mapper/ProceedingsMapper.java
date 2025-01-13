package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.common.model.proceeding.common.*;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiDetermineMagsRepDecisionRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateCrownCourtRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.util.DateUtil;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.util.Collection;
import java.util.List;

import static uk.gov.justice.laa.crime.util.DateUtil.toZonedDateTime;

@Component
@RequiredArgsConstructor
public class ProceedingsMapper extends CrownCourtMapper {

    private final UserMapper userMapper;

    public ApiUpdateApplicationRequest workflowRequestToUpdateApplicationRequest(
            ApplicationDTO application, UserDTO userDTO) {

        ApiUpdateApplicationRequest request = new ApiUpdateApplicationRequest();
        mapToApiUpdateApplicationRequest(application, userDTO, request);

        return request;
    }

    private void mapToApiUpdateApplicationRequest(ApplicationDTO application, UserDTO userDTO, ApiUpdateApplicationRequest request) {

        request
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withCaseType(CaseType.getFrom(application.getCaseDetailsDTO().getCaseType()))
                .withMagCourtOutcome(MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
                .withDecisionReason(DecisionReason.getFrom(application.getRepOrderDecision().getCode()))
                .withDecisionDate(DateUtil.toLocalDateTime(application.getDecisionDate()))
                .withCommittalDate(DateUtil.toLocalDateTime(application.getCommittalDate()))
                .withDateReceived(DateUtil.toLocalDateTime(application.getDateReceived()))
                .withIojAppeal(
                        getIojAppeal(application)
                )
                .withPassportAssessment(applicationDtoToPassportAssessment(application))
                .withFinancialAssessment(applicationDtoToFinancialAssessment(application));

        CrownCourtOverviewDTO crownCourtOverview = application.getCrownCourtOverviewDTO();
        CrownCourtSummaryDTO crownCourtSummary = crownCourtOverview.getCrownCourtSummaryDTO();

        ApiCrownCourtSummary ccpCrownCourtSummary =
                crownCourtSummaryDtoToApiCrownCourtSummary(crownCourtSummary);

        request
                .withCrownCourtSummary(ccpCrownCourtSummary)
                .withApplicantHistoryId(NumberUtils.toInteger(application.getApplicantDTO().getApplicantHistoryId()))
                .withCrownRepId(NumberUtils.toInteger(crownCourtSummary.getCcRepId()))
                .withIsImprisoned(crownCourtSummary.getInPrisoned())
                .withUserSession(userMapper.userDtoToUserSession(userDTO));
    }

    private static ApiIOJSummary getIojAppeal(ApplicationDTO application) {
        return new ApiIOJSummary()
                .withDecisionResult(
                        application.getAssessmentDTO().getIojAppeal().getAppealDecisionResult()
                )
                .withIojResult(application.getIojResult());
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

    private void mapCrownCourtSummaryToApplication(ApiUpdateCrownCourtOutcomeResponse response, ApplicationDTO applicationDTO) {

        CrownCourtSummaryDTO crownCourtSummary = applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO();
        crownCourtSummary.setCcRepOrderDate(DateUtil.toDate(response.getCrownCourtSummary().getRepOrderDate()) );
        crownCourtSummary.setRepOrderDecision(new SysGenString(response.getCrownCourtSummary().getRepOrderDecision()));
        crownCourtSummary.setCcRepType(new SysGenString(response.getCrownCourtSummary().getRepType()));

        EvidenceFeeLevel evidenceFeeLevel = response.getCrownCourtSummary().getEvidenceFeeLevel();

        if (null != evidenceFeeLevel) {
            crownCourtSummary.getEvidenceProvisionFee().setFeeLevel(evidenceFeeLevel.getFeeLevel());
            crownCourtSummary.getEvidenceProvisionFee()
                    .setDescription(new SysGenString(evidenceFeeLevel.getDescription()));
        }

        List<OutcomeDTO> outcomeDTOList = response.getCrownCourtSummary().getRepOrderCrownCourtOutcome()
                .stream()
                .map(x -> {
                    OutcomeDTO outcomeDTO = new OutcomeDTO();
                    outcomeDTO.setOutcome(x.getOutcome().getCode());
                    outcomeDTO.setDescription(x.getOutcome().getDescription());
                    outcomeDTO.setDateSet(DateUtil.toDate(x.getOutcomeDate()));
                    return outcomeDTO;
                }).toList();

        crownCourtSummary.setOutcomeDTOs(outcomeDTOList);
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
        assessment.withInitResult(initialAssessment.getResult());
        assessment.withInitStatus(getCurrentStatus(initialAssessment.getAssessmnentStatusDTO().getStatus()));

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

    public ApplicationDTO updateCrownCourtResponseToApplicationDto(ApiUpdateCrownCourtOutcomeResponse response,
                                                                   ApplicationDTO application) {

        application.setTimestamp(toZonedDateTime(response.getModifiedDateTime()));
        mapCrownCourtSummaryToApplication(response, application);
        return application;
    }

    private void mapEvidenceDetailsToRequest(ApiUpdateCrownCourtRequest request, ApplicationDTO application) {

        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        CapitalEquityDTO capitalEquityDTO = application.getCapitalEquityDTO();
        ApplicantDTO applicantDTO = application.getApplicantDTO();

        if (null!= applicantDTO && null != applicantDTO.getEmploymentStatusDTO()) {
            request.setEmstCode(applicantDTO.getEmploymentStatusDTO().getCode());
        }

        if (null != financialAssessmentDTO && null != financialAssessmentDTO.getIncomeEvidence()) {
            IncomeEvidenceSummaryDTO incomeEvidenceSummaryDTO = financialAssessmentDTO.getIncomeEvidence();
            request.setIncomeEvidenceReceivedDate(DateUtil.toLocalDateTime(incomeEvidenceSummaryDTO.getEvidenceReceivedDate()));
        }

        if (null != capitalEquityDTO && null != capitalEquityDTO.getCapitalEvidenceSummary()) {
            request.setCapitalEvidenceReceivedDate(
                    DateUtil.toLocalDateTime(capitalEquityDTO.getCapitalEvidenceSummary().getEvidenceReceivedDate()));
        }

        if (null != application.getCapitalEquityDTO() && null != application.getCapitalEquityDTO().getCapitalOther()) {

            List<ApiCapitalEvidence> apiCapitalEvidenceList = application.getCapitalEquityDTO().getCapitalOther().stream()
                    .map(CapitalOtherDTO::getCapitalEvidence)
                    .flatMap(Collection::stream)
                    .map(x -> {
                        ApiCapitalEvidence apiCapitalEvidence = new ApiCapitalEvidence();
                        apiCapitalEvidence.setEvidenceType(x.getEvidenceTypeDTO().getEvidence());
                        apiCapitalEvidence.setDateReceived(DateUtil.toLocalDateTime(x.getDateReceived()));
                        return apiCapitalEvidence;
                    }).toList();

            request.setCapitalEvidence(apiCapitalEvidenceList);
        }
    }

    private static List<ApiCrownCourtOutcome> mapToApiCrownCourtOutcomes(CrownCourtSummaryDTO crownCourtSummary) {
        Collection<OutcomeDTO> outcomeDTOS = crownCourtSummary.getOutcomeDTOs();
        return outcomeDTOS.stream()
                .filter(outcomeDTO -> null == outcomeDTO.getDateSet())
                .map(ProceedingsMapper::getApiCrownCourtOutcome)
                .toList();
    }

    private static ApiCrownCourtOutcome getApiCrownCourtOutcome(OutcomeDTO outcomeDTO) {
        return new ApiCrownCourtOutcome()
                .withOutcome(CrownCourtOutcome.getFrom(outcomeDTO.getOutcome()))
                .withOutcomeType(outcomeDTO.getOutComeType())
                .withDateSet(DateUtil.toLocalDateTime(outcomeDTO.getDateSet()))
                .withDescription(outcomeDTO.getDescription());
    }


    public ApiUpdateCrownCourtRequest workflowRequestToUpdateCrownCourtRequest(
            ApplicationDTO application, UserDTO userDTO) {

        ApiUpdateCrownCourtRequest request = new ApiUpdateCrownCourtRequest();
        mapToApiUpdateApplicationRequest(application, userDTO, request);

        CrownCourtOverviewDTO crownCourtOverview = application.getCrownCourtOverviewDTO();
        CrownCourtSummaryDTO crownCourtSummary = crownCourtOverview.getCrownCourtSummaryDTO();

        if (null != crownCourtSummary.getOutcomeDTOs()) {
            request.getCrownCourtSummary().setCrownCourtOutcome(mapToApiCrownCourtOutcomes(crownCourtSummary));
        }
        mapEvidenceDetailsToRequest(request, application);

        return request;

    }

    CurrentStatus getCurrentStatus(String status) {
        return StringUtils.isNotBlank(status) ? CurrentStatus.getFrom(status) : null;
    }

    public ApiDetermineMagsRepDecisionRequest buildDetermineMagsRepDecision(ApplicationDTO application, UserDTO userDTO) {

        ApiDetermineMagsRepDecisionRequest request = new ApiDetermineMagsRepDecisionRequest();

        request.setRepId(application.getRepId().intValue());
        request.setCaseType(CaseType.getFrom(application.getCaseDetailsDTO().getCaseType()));
        request.setIojAppeal(getIojAppeal(application));
        request.setFinancialAssessment(applicationDtoToFinancialAssessment(application));
        request.setPassportAssessment(applicationDtoToPassportAssessment(application));
        request.setUserSession(userMapper.userDtoToUserSession(userDTO));

        return request;
    }
}
