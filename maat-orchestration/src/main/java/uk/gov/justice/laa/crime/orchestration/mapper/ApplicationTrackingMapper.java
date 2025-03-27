package uk.gov.justice.laa.crime.orchestration.mapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.tracking.*;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.CaseType;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.RequestSource;
import uk.gov.justice.laa.crime.common.model.tracking.Hardship.HardshipResult;
import uk.gov.justice.laa.crime.common.model.tracking.Hardship.HardshipType;
import uk.gov.justice.laa.crime.common.model.tracking.Ioj.IojAppealResult;
import uk.gov.justice.laa.crime.common.model.tracking.MeansAssessment.MeansAssessmentResult;
import uk.gov.justice.laa.crime.common.model.tracking.Passport.PassportResult;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

import java.util.Arrays;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.crime.util.DateUtil.toLocalDateTime;

@Component
public class ApplicationTrackingMapper {

    public ApplicationTrackingOutputResult build(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO,
                                                 AssessmentType assessmentType, RequestSource requestSource) {

        ApplicationTrackingOutputResult request = new ApplicationTrackingOutputResult();
        ApplicationDTO application = workflowRequest.getApplicationDTO();
        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        PassportedDTO passportedDTO = application.getPassportedDTO();
        RepOrderDecisionDTO repOrderDecisionDTO = application.getRepOrderDecision();
        CrownCourtOverviewDTO crownCourtOverviewDTO = application.getCrownCourtOverviewDTO();
        CrownCourtSummaryDTO crownCourtSummaryDTO = crownCourtOverviewDTO.getCrownCourtSummaryDTO();

        request.setUsn(financialAssessmentDTO.getUsn() != null ? financialAssessmentDTO.getUsn().intValue() : null);
        request.setMaatRef(application.getRepId().intValue());
        request.setActionKeyId(financialAssessmentDTO.getId().intValue());
        request.setCaseId(application.getCaseId());
        request.setCaseType(StringUtils.isBlank(application.getCaseDetailsDTO().getCaseType()) ? null :
                CaseType.fromValue(application.getCaseDetailsDTO().getCaseType()));
        request.setAssessmentId(financialAssessmentDTO.getId().intValue());
        request.setAssessmentType(assessmentType);
        request.setDwpResult(passportedDTO.getDwpResult());
        request.setRepDecision(repOrderDecisionDTO.getDescription() != null ? repOrderDecisionDTO.getDescription().getValue() : null);
        request.setCcRepDecision(crownCourtSummaryDTO.getRepOrderDecision() != null ? crownCourtSummaryDTO.getRepOrderDecision().getValue() : null);
        request.setMagsOutcome(application.getMagsOutcomeDTO().getOutcome());
        request.setRequestSource(requestSource);
        request.setUserCreated(workflowRequest.getUserDTO().getUserName());
        request.setIoj(buildIOJ(workflowRequest, repOrderDTO));
        request.setPassport(buildPassport(workflowRequest, repOrderDTO));
        request.setMeansAssessment(buildMeansAssessment(workflowRequest, repOrderDTO));
        request.setHardship(buildHardship(workflowRequest));

        return request;
    }

    protected Ioj buildIOJ(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO) {

        ApplicationDTO application = workflowRequest.getApplicationDTO();
        IOJAppealDTO iojAppealDTO = application.getAssessmentDTO().getIojAppeal();
        return new Ioj()
                .withIojId(null != iojAppealDTO.getIojId() ? iojAppealDTO.getIojId().intValue() : null)
                .withIojResult(application.getIojResult())
                .withIojReason(application.getIojResultNote())
                .withIojAppealResult(StringUtils.isBlank(iojAppealDTO.getAppealDecisionResult()) ? null :
                        IojAppealResult.fromValue(iojAppealDTO.getAppealDecisionResult()))
                .withIojAssessorName(getCapitalisedFullName(repOrderDTO.getUserCreatedEntity().getFirstName(), repOrderDTO.getUserCreatedEntity().getSurname()))
                .withAppCreatedDate(toLocalDateTime(application.getDateCreated()));

    }

    protected Passport buildPassport(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO) {

        ApplicationDTO application = workflowRequest.getApplicationDTO();
        PassportedDTO passportedDTO = application.getPassportedDTO();
        PassportAssessmentDTO passportAssessmentDTO = null;
        if (passportedDTO.getPassportedId() != null) {
            passportAssessmentDTO = repOrderDTO.getPassportAssessments()
                    .stream()
                    .filter(pa -> pa.getId() == passportedDTO.getPassportedId().intValue())
                    .findFirst()
                    .orElse(null);
        }

        return new Passport()
                .withPassportId(passportedDTO.getPassportedId() != null ? passportedDTO.getPassportedId().intValue() : null)
                .withPassportResult(StringUtils.isBlank(passportedDTO.getResult()) ? null :
                        PassportResult.fromValue(passportedDTO.getResult()))
                .withPassportAssessorName((null != passportAssessmentDTO) ?
                        getCapitalisedFullName(passportAssessmentDTO.getUserCreatedEntity().getFirstName(),
                                passportAssessmentDTO.getUserCreatedEntity().getSurname()) : null)
                .withPassportCreatedDate(passportedDTO.getDate() != null ? toLocalDateTime(passportedDTO.getDate()) : null);
    }

    protected MeansAssessment buildMeansAssessment(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO) {
        ApplicationDTO application = workflowRequest.getApplicationDTO();
        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        MeansAssessment assessment = new MeansAssessment();
        assessment.setMeansAssessmentId(financialAssessmentDTO.getId().intValue());
        uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO fa = repOrderDTO.getFinancialAssessments()
                .stream()
                .filter(pa -> pa.getId() == financialAssessmentDTO.getId().intValue())
                .findFirst()
                .orElse(null);
        assessment.setMeansAssessorName((null != fa) ? getCapitalisedFullName(fa.getUserCreatedEntity().getFirstName(),
                fa.getUserCreatedEntity().getSurname()) : null);

        if (financialAssessmentDTO.getFullAvailable() != null && financialAssessmentDTO.getFullAvailable()) {
            FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();
            assessment.setMeansAssessmentStatus(fullAssessmentDTO.getAssessmnentStatusDTO().getStatus());
            assessment.setMeansAssessmentResult(StringUtils.isBlank(fullAssessmentDTO.getResult()) ? null :
                    MeansAssessmentResult.fromValue(fullAssessmentDTO.getResult()));
            assessment.setMeansAssessmentCreatedDate(toLocalDateTime(fullAssessmentDTO.getAssessmentDate()));
        } else {
            InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
            assessment.setMeansAssessmentStatus(initialAssessmentDTO.getAssessmnentStatusDTO().getStatus());
            assessment.setMeansAssessmentResult(StringUtils.isBlank(initialAssessmentDTO.getResult()) ? null :
                    MeansAssessmentResult.fromValue(initialAssessmentDTO.getResult()));
            assessment.setMeansAssessmentCreatedDate(toLocalDateTime(initialAssessmentDTO.getAssessmentDate()));
        }

        return assessment;
    }

    protected Hardship buildHardship(WorkflowRequest workflowRequest) {

        ApplicationDTO application = workflowRequest.getApplicationDTO();
        HardshipOverviewDTO hardshipOverviewDTO = application.getAssessmentDTO().getFinancialAssessmentDTO().getHardship();
        HardshipReviewDTO hardshipReviewDTO = hardshipOverviewDTO.getCrownCourtHardship();

        return new Hardship()
                .withHardshipId(hardshipReviewDTO.getId() != null ? hardshipReviewDTO.getId().intValue() : null)
                .withHardshipResult(StringUtils.isBlank(hardshipReviewDTO.getReviewResult()) ? null :
                        HardshipResult.fromValue(hardshipReviewDTO.getReviewResult()))
                .withHardshipType(HardshipType.CCHARDSHIP);
    }

    public String getCapitalisedFullName(String... names) {
        return Arrays.stream(names)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(StringUtils.SPACE));
    }
}
