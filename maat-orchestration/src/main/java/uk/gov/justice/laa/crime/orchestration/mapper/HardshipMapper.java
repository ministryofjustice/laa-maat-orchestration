package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.*;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipDetail;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipProgress;
import uk.gov.justice.laa.crime.orchestration.model.hardship.*;
import uk.gov.justice.laa.crime.orchestration.util.CurrencyUtil;
import uk.gov.justice.laa.crime.orchestration.util.DateUtil;
import uk.gov.justice.laa.crime.orchestration.util.NumberUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static uk.gov.justice.laa.crime.orchestration.util.CurrencyUtil.toCurrency;
import static uk.gov.justice.laa.crime.orchestration.util.CurrencyUtil.toSysGenCurrency;
import static uk.gov.justice.laa.crime.orchestration.util.DateUtil.toDate;

@Component
@RequiredArgsConstructor
public class HardshipMapper {

    private final UserMapper userMapper;

    public ApiPerformHardshipRequest workflowRequestToPerformHardshipRequest(WorkflowRequest workflowRequest) {
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
                .withExtraExpenditure(hrSectionDtosToExtraExpenditures(current.getSection()))
                .withDeniedIncome(hrSectionDtosToDeniedIncomes(current.getSection()))
                .withSolicitorCosts(hrSolicitorCostsDtoToSolicitorCosts(current.getSolictorsCosts()));

        HardshipMetadata metadata = new HardshipMetadata()
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withCmuId(NumberUtils.toInteger(current.getCmuId()))
                .withReviewStatus(HardshipReviewStatus.getFrom(current.getAsessmentStatus().getStatus()))
                .withUserSession(userMapper.userDtoToUserSession(userDTO))
                .withReviewReason(NewWorkReason.getFrom(current.getNewWorkReason().getCode()))
                .withNotes(current.getNotes())
                .withDecisionNotes(current.getDecisionNotes())
                .withFinancialAssessmentId(
                        NumberUtils.toInteger(application.getAssessmentDTO().getFinancialAssessmentDTO().getId())
                )
                .withHardshipReviewId(NumberUtils.toInteger(current.getId()))
                .withProgressItems(hrProgressListToHardshipProgressList(current.getProgress()));

        return new ApiPerformHardshipRequest()
                .withHardship(hardship)
                .withHardshipMetadata(metadata);
    }

    private List<ExtraExpenditure> hrSectionDtosToExtraExpenditures(Collection<HRSectionDTO> sections) {
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

    private List<DeniedIncome> hrSectionDtosToDeniedIncomes(Collection<HRSectionDTO> sections) {
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

    private SolicitorCosts hrSolicitorCostsDtoToSolicitorCosts(HRSolicitorsCostsDTO solicitorsCosts) {
        return new SolicitorCosts()
                .withVat(solicitorsCosts.getSolicitorVat())
                .withRate(solicitorsCosts.getSolicitorRate())
                .withHours(BigDecimal.valueOf(solicitorsCosts.getSolicitorHours()))
                .withDisbursements(solicitorsCosts.getSolicitorDisb())
                .withEstimatedTotal(solicitorsCosts.getSolicitorEstimatedTotalCost());
    }

    private List<HardshipProgress> hrProgressListToHardshipProgressList(Collection<HRProgressDTO> progressItems) {
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

    public ApplicationDTO performHardshipResponseToApplicationDTO(ApiPerformHardshipResponse response,
                                                                  ApplicationDTO application) {
        HardshipReviewDTO current;
        CourtType courtType = application.getCourtType();
        HardshipOverviewDTO hardshipOverview =
                application.getAssessmentDTO()
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
        return application;
    }

    public HardshipReviewDTO findHardshipResponseToHardshipDto(ApiFindHardshipResponse response) {
        return HardshipReviewDTO.builder()
                .id(response.getId().longValue())
                .reviewResult(response.getReviewResult().toString())
                .cmuId(response.getCmuId().longValue())
                .notes(response.getNotes())
                .decisionNotes(response.getDecisionNotes())
                .reviewDate(toDate(response.getReviewDate()))
                .disposableIncome(toSysGenCurrency(response.getDisposableIncome()))
                .disposableIncomeAfterHardship(toSysGenCurrency(response.getDisposableIncomeAfterHardship()))
                .newWorkReason(newWorkReasonToNewWorkReasonDto(response.getNewWorkReason()))
                .solictorsCosts(solicitorCostsToHrSolicitorsCostsDto(response.getSolicitorCosts()))
                .asessmentStatus(hardshipReviewStatusToAssessmentStatusDto(response.getStatus()))
                .progress(hardshipProgressListToHrProgressDtos(response.getReviewProgressItems()))
                .section(hardshipDetailsToHrSectionDTOs(response.getReviewDetails()))
                .build();
    }

    private List<HRSectionDTO> hardshipDetailsToHrSectionDTOs(List<ApiHardshipDetail> reviewDetails) {
        List<HRSectionDTO> hrSectionDTOList = new ArrayList<>();
        reviewDetails.stream()
                .collect(groupingBy(ApiHardshipDetail::getDetailType))
                .forEach((type, details) -> hrSectionDTOList.add(
                        HRSectionDTO.builder()
                                .detailType(hardshipReviewDetailTypeToHrDetailTypeDto(type))
                                .detail(details.stream()
                                                .map(apiHardshipDetail -> HRDetailDTO.builder()
                                                        .dateDue(
                                                                toDate(apiHardshipDetail.getDateDue()))
                                                        .id(apiHardshipDetail.getId()
                                                                    .longValue())
                                                        .accepted("Y".equals(
                                                                apiHardshipDetail.getAccepted()))
                                                        .amountNumber(
                                                                toCurrency(
                                                                        apiHardshipDetail.getAmount()))
                                                        .hrReasonNote(
                                                                apiHardshipDetail.getReasonNote())
                                                        .otherDescription(
                                                                apiHardshipDetail.getOtherDescription())
                                                        .detailDescription(
                                                                hardshipReviewDetailCodeToHrDetailDescriptionDto(
                                                                        apiHardshipDetail.getDetailCode()))
                                                        .frequency(
                                                                frequencyToFrequenciesDto(
                                                                        apiHardshipDetail.getFrequency()))
                                                        .reason(hardshipReviewDetailReasonToHrReasonDto(
                                                                apiHardshipDetail.getDetailReason()))
                                                        .build())
                                                .collect(Collectors.toList()))
                                .build()));
        return hrSectionDTOList;
    }

    private HRReasonDTO hardshipReviewDetailReasonToHrReasonDto(HardshipReviewDetailReason detailReason) {
        return HRReasonDTO.builder()
                .reason(detailReason.getReason())
                .build();
    }

    private FrequenciesDTO frequencyToFrequenciesDto(Frequency frequency) {
        return FrequenciesDTO.builder()
                .code(frequency.getCode())
                .description(frequency.getDescription())
                .annualWeighting((long) frequency.getAnnualWeighting())
                .build();
    }

    private HRDetailDescriptionDTO hardshipReviewDetailCodeToHrDetailDescriptionDto(
            HardshipReviewDetailCode detailCode) {
        return HRDetailDescriptionDTO.builder()
                .description(detailCode.getDescription())
                .code(detailCode.getCode())
                .build();
    }

    private HRDetailTypeDTO hardshipReviewDetailTypeToHrDetailTypeDto(HardshipReviewDetailType hrDetailType) {
        return HRDetailTypeDTO.builder()
                .type(hrDetailType.getType())
                .description(hrDetailType.getDescription())
                .build();
    }

    private List<HRProgressDTO> hardshipProgressListToHrProgressDtos(List<ApiHardshipProgress> reviewProgressItems) {
        return reviewProgressItems.stream()
                .map(item -> HRProgressDTO.builder()
                        .dateCompleted(toDate(item.getDateCompleted()))
                        .id(item.getId().longValue())
                        .dateRequested(toDate(item.getDateRequested()))
                        .dateRequired(toDate(item.getDateRequired()))
                        .progressAction(hardshipReviewProgressActionToHrProgressActionDto(item.getProgressAction()))
                        .progressResponse(
                                hardshipReviewProgressResponseToHrProgressResponseDto(item.getProgressResponse()))
                        .build()).collect(Collectors.toList());
    }

    private HRProgressActionDTO hardshipReviewProgressActionToHrProgressActionDto(
            HardshipReviewProgressAction hrProgressAction) {
        return HRProgressActionDTO.builder()
                .action(hrProgressAction.getAction())
                .description(hrProgressAction.getDescription())
                .build();
    }

    private HRProgressResponseDTO hardshipReviewProgressResponseToHrProgressResponseDto(
            HardshipReviewProgressResponse hrProgressResponse) {
        return HRProgressResponseDTO.builder()
                .response(hrProgressResponse.getResponse())
                .description(hrProgressResponse.getDescription())
                .build();
    }

    private AssessmentStatusDTO hardshipReviewStatusToAssessmentStatusDto(HardshipReviewStatus status) {
        return AssessmentStatusDTO.builder()
                .status(status.getStatus())
                .description(status.getDescription())
                .build();
    }

    private NewWorkReasonDTO newWorkReasonToNewWorkReasonDto(NewWorkReason newWorkReason) {
        return NewWorkReasonDTO.builder()
                .description(newWorkReason.getDescription())
                .code(newWorkReason.getCode())
                .build();
    }

    private HRSolicitorsCostsDTO solicitorCostsToHrSolicitorsCostsDto(
            uk.gov.justice.laa.crime.orchestration.model.SolicitorCosts solicitorCosts) {
        return HRSolicitorsCostsDTO.builder()
                .solicitorDisb(toCurrency(solicitorCosts.getDisbursements()))
                .solicitorHours(solicitorCosts.getHours().doubleValue())
                .solicitorRate(toCurrency(solicitorCosts.getRate()))
                .solicitorEstimatedTotalCost(toCurrency(solicitorCosts.getEstimatedTotal()))
                .solicitorVat(toCurrency(solicitorCosts.getVat()))
                .build();
    }
}
