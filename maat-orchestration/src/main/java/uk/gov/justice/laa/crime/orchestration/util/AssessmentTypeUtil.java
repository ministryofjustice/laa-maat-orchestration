package uk.gov.justice.laa.crime.orchestration.util;

import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FullAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.InitialAssessmentDTO;

import static uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType.MEANS_FULL;
import static uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType.MEANS_INIT;

public class AssessmentTypeUtil {

    private AssessmentTypeUtil() {

    }

    public static boolean isInitialAssessmentCompleted(WorkflowRequest request) {

        FinancialAssessmentDTO financialAssessmentDTO = request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessment = financialAssessmentDTO.getInitial();
        return (initialAssessment.getAssessmnentStatusDTO().getStatus().equals(CurrentStatus.COMPLETE.getStatus())
                && !initialAssessment.getResult().equals(AssessmentResult.FULL.getResult()));
    }

    public static boolean isFullAssessmentCompleted(WorkflowRequest request) {

        FinancialAssessmentDTO financialAssessmentDTO = request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO();
        FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();
        return (CurrentStatus.COMPLETE.getStatus().equals(fullAssessmentDTO.getAssessmnentStatusDTO().getStatus()));
    }

    public static boolean isInitCompletedAndFullAssessmentNotStarted(WorkflowRequest request) {

        FinancialAssessmentDTO financialAssessmentDTO = request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO();
        AssessmentStatusDTO assessmentStatus = financialAssessmentDTO.getFull().getAssessmnentStatusDTO();
        return (isInitialAssessmentCompleted(request) && StringUtils.isBlank(assessmentStatus.getStatus()));
    }

    public static boolean isAssessmentCompleted(WorkflowRequest request) {
        return (isInitialAssessmentCompleted(request) || isFullAssessmentCompleted(request));
    }

    public static ApplicationTrackingOutputResult.AssessmentType getAssessmentType(WorkflowRequest request) {

        return isFullAssessmentCompleted(request) ? MEANS_FULL : MEANS_INIT;
    }
}
