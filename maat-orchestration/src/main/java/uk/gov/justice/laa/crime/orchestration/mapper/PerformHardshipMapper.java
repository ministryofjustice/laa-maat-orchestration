package uk.gov.justice.laa.crime.orchestration.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.*;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.orchestration.model.hardship.*;
import uk.gov.justice.laa.crime.orchestration.util.CurrencyUtil;
import uk.gov.justice.laa.crime.orchestration.util.DateUtil;
import uk.gov.justice.laa.crime.orchestration.util.NumberUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PerformHardshipMapper implements RequestMapper<ApiPerformHardshipRequest, WorkflowRequestDTO>,
        ResponseMapper<ApiPerformHardshipResponse, ApplicationDTO> {

    @Override
    public ApiPerformHardshipRequest fromDto(WorkflowRequestDTO workflowRequest) {
        HardshipReviewDTO current;
        UserDTO userDTO = workflowRequest.getUserDTO();
        ApplicationDTO application = workflowRequest.getApplicationDTO();
        CourtType courtType = application.getCourtType();
        HardshipOverviewDTO hardshipOverview =
                application.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship();
        if (courtType == CourtType.MAGISTRATE) {
            current = hardshipOverview.getMagCourtHardship();
        } else {
            current = hardshipOverview.getCrownCourtHardship();
        }
        HardshipReview hardship = new HardshipReview()
                .withCourtType(courtType)
                .withTotalAnnualDisposableIncome(current.getDisposableIncome())
                .withReviewDate(DateUtil.toLocalDateTime(current.getReviewDate()))
                .withExtraExpenditure(mapExtraExpenditure(current.getSection()))
                .withDeniedIncome(mapDeniedIncome(current.getSection()))
                .withSolicitorCosts(mapSolicitorCosts(current.getSolictorsCosts()));

        HardshipMetadata metadata = new HardshipMetadata()
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withCmuId(NumberUtils.toInteger(current.getCmuId()))
                .withReviewStatus(HardshipReviewStatus.getFrom(current.getAsessmentStatus().getStatus()))
                .withUserSession(
                        new ApiUserSession()
                                .withUserName(userDTO.getUserName())
                                .withSessionId(userDTO.getUserSession())
                )
                .withReviewReason(NewWorkReason.getFrom(current.getNewWorkReason().getCode()))
                .withNotes(current.getNotes())
                .withDecisionNotes(current.getDecisionNotes())
                .withFinancialAssessmentId(
                        NumberUtils.toInteger(application.getAssessmentDTO().getFinancialAssessmentDTO().getId())
                )
                .withHardshipReviewId(NumberUtils.toInteger(current.getId()))
                .withProgressItems(mapProgressItems(current.getProgress()));

        return new ApiPerformHardshipRequest()
                .withHardship(hardship)
                .withHardshipMetadata(metadata);
    }

    private List<ExtraExpenditure> mapExtraExpenditure(Collection<HRSectionDTO> sections) {
        return getDetailsStreamWithType(sections, HardshipReviewDetailType.EXPENDITURE)
                .map(detail -> new ExtraExpenditure()
                        .withAmount(detail.getAmountNumber())
                        .withFrequency(Frequency.getFrom(detail.getFrequency().getCode()))
                        .withAccepted(detail.isAccepted())
                        .withReasonCode(HardshipReviewDetailReason.getFrom(detail.getReason().getReason()))
                        .withDescription(detail.getOtherDescription())
                        .withItemCode(ExtraExpenditureDetailCode.getFrom(detail.getDetailDescription().getCode()))
                ).collect(Collectors.toList());
    }

    private List<DeniedIncome> mapDeniedIncome(Collection<HRSectionDTO> sections) {
        return getDetailsStreamWithType(sections, HardshipReviewDetailType.INCOME)
                .map(detail -> new DeniedIncome()
                        .withAmount(detail.getAmountNumber())
                        .withFrequency(Frequency.getFrom(detail.getFrequency().getCode()))
                        .withAccepted(detail.isAccepted())
                        .withReasonNote(detail.getHrReasonNote())
                        .withDescription(detail.getOtherDescription())
                        .withItemCode(DeniedIncomeDetailCode.getFrom(detail.getReason().getReason()))
                ).collect(Collectors.toList());
    }

    private SolicitorCosts mapSolicitorCosts(HRSolicitorsCostsDTO solicitorsCosts) {
        return new SolicitorCosts()
                .withVat(solicitorsCosts.getSolicitorVat())
                .withRate(solicitorsCosts.getSolicitorRate())
                .withHours(BigDecimal.valueOf(solicitorsCosts.getSolicitorHours()))
                .withDisbursements(solicitorsCosts.getSolicitorDisb())
                .withEstimatedTotal(solicitorsCosts.getSolicitorEstimatedTotalCost());
    }

    private List<HardshipProgress> mapProgressItems(Collection<HRProgressDTO> progressItems) {
        return progressItems.stream()
                .map(item -> new HardshipProgress()
                        .withAction(HardshipReviewProgressAction.getFrom(
                                item.getProgressAction().getAction())
                        )
                        .withResponse(HardshipReviewProgressResponse.getFrom(
                                item.getProgressResponse().getResponse())
                        )
                        .withDateTaken(DateUtil.toLocalDateTime(item.getDateRequested()))
                        .withDateCompleted(DateUtil.toLocalDateTime(item.getDateCompleted()))
                        .withDateRequired(DateUtil.toLocalDateTime(item.getDateRequired()))
                ).collect(Collectors.toList());
    }

    private Stream<HRDetailDTO> getDetailsStreamWithType(Collection<HRSectionDTO> sections,
                                                         HardshipReviewDetailType type) {
        return sections.stream()
                .filter(section -> HardshipReviewDetailType.getFrom(section.getDetailType().getType()) == type)
                .flatMap(section -> section.getDetail().stream());
    }

    @Override
    public void toDto(ApiPerformHardshipResponse response, ApplicationDTO application) {
        HardshipReviewDTO current;
        CourtType courtType = application.getCourtType();
        HardshipOverviewDTO hardshipOverview =
                application.
                        getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship();
        if (courtType.equals(CourtType.MAGISTRATE)) {
            current = hardshipOverview.getMagCourtHardship();
        } else {
            current = hardshipOverview.getCrownCourtHardship();
        }
        current.setId(response.getHardshipReviewId().longValue());
        current.setReviewResult(response.getReviewResult().name());
        current.setDisposableIncomeAfterHardship(
                CurrencyUtil.toSysGenCurrency(response.getPostHardshipDisposableIncome()));
        current.setDisposableIncome(
                CurrencyUtil.toSysGenCurrency(response.getDisposableIncome()));
    }
}
