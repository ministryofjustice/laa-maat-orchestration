package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.hardship.*;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static uk.gov.justice.laa.crime.util.DateUtil.toDate;
import static uk.gov.justice.laa.crime.util.DateUtil.toLocalDateTime;

@Component
@RequiredArgsConstructor
public class HardshipMapper {

    private final UserMapper userMapper;

    public ApiPerformHardshipRequest workflowRequestToPerformHardshipRequest(WorkflowRequest workflowRequest, boolean isCreate) {
        UserDTO userDTO = workflowRequest.getUserDTO();
        ApplicationDTO application = workflowRequest.getApplicationDTO();
        HardshipReviewDTO current = getHardshipReviewDTO(application, workflowRequest.getCourtType());

        HardshipReview hardship = new HardshipReview()
                .withCourtType(workflowRequest.getCourtType())
                .withTotalAnnualDisposableIncome(getTotalAnnualDisposableIncome(current, application, isCreate))
                .withReviewDate(toLocalDateTime(current.getReviewDate()))
                .withExtraExpenditure(hrSectionDtosToExtraExpenditures(current.getSection()))
                .withDeniedIncome(hrSectionDtosToDeniedIncomes(current.getSection()))
                .withSolicitorCosts(hrSolicitorCostsDtoToSolicitorCosts(current.getSolictorsCosts()));

        HardshipMetadata metadata = new HardshipMetadata()
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withCmuId(NumberUtils.toInteger(application.getCaseManagementUnitDTO().getCmuId()))
                .withReviewStatus(HardshipReviewStatus.getFrom(current.getAsessmentStatus().getStatus()))
                .withUserSession(userMapper.userDtoToUserSession(userDTO))
                .withReviewReason(NewWorkReason.getFrom(current.getNewWorkReason().getCode()))
                .withNotes(current.getNotes())
                .withDecisionNotes(current.getDecisionNotes())
                .withFinancialAssessmentId(application.getAssessmentDTO().getFinancialAssessmentDTO().getId())
                .withHardshipReviewId(current.getId());

        return new ApiPerformHardshipRequest()
                .withHardship(hardship)
                .withHardshipMetadata(metadata);
    }

    private BigDecimal getTotalAnnualDisposableIncome(HardshipReviewDTO current, ApplicationDTO applicationDTO, boolean isCreate) {
        return isCreate ?
                BigDecimal.valueOf(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getFull().getTotalAnnualDisposableIncome())
                : current.getDisposableIncome();
    }

    public HardshipReviewDTO getHardshipReviewDTO(ApplicationDTO application, CourtType courtType) {
        HardshipOverviewDTO hardshipOverview =
                application.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship();
        if (courtType == CourtType.MAGISTRATE) {
            return hardshipOverview.getMagCourtHardship();
        } else {
            return hardshipOverview.getCrownCourtHardship();
        }
    }

    private List<ExtraExpenditure> hrSectionDtosToExtraExpenditures(Collection<HRSectionDTO> sections) {
        return getDetailsStreamWithType(sections, HardshipReviewDetailType.EXPENDITURE)
                .map(detail -> new ExtraExpenditure()
                        .withAmount(detail.getAmountNumber())
                        .withFrequency(Frequency.getFrom(detail.getFrequency().getCode()))
                        .withAccepted(detail.isAccepted())
                        .withReasonCode(HardshipReviewDetailReason.getFrom(detail.getReason().getId().intValue()))
                        .withDescription(detail.getOtherDescription())
                        .withItemCode(ExtraExpenditureDetailCode.getFrom(detail.getDetailDescription().getCode()))
                ).toList();
    }

    private List<DeniedIncome> hrSectionDtosToDeniedIncomes(Collection<HRSectionDTO> sections) {
        return getDetailsStreamWithType(sections, HardshipReviewDetailType.INCOME)
                .map(detail -> new DeniedIncome()
                        .withAmount(detail.getAmountNumber())
                        .withFrequency(Frequency.getFrom(detail.getFrequency().getCode()))
                        .withAccepted(detail.isAccepted())
                        .withReasonNote(detail.getHrReasonNote())
                        .withDescription(detail.getOtherDescription())
                        .withItemCode(DeniedIncomeDetailCode.getFrom(detail.getDetailDescription().getCode()))
                ).toList();
    }

    private SolicitorCosts hrSolicitorCostsDtoToSolicitorCosts(HRSolicitorsCostsDTO solicitorsCosts) {
        if (solicitorsCosts.getSolicitorRate() != null && solicitorsCosts.getSolicitorHours() != null) {
            return new SolicitorCosts()
                    .withVat(solicitorsCosts.getSolicitorVat())
                    .withRate(solicitorsCosts.getSolicitorRate())
                    .withHours(BigDecimal.valueOf(solicitorsCosts.getSolicitorHours())
                            .setScale(2, RoundingMode.DOWN))
                    .withDisbursements(solicitorsCosts.getSolicitorDisb())
                    .withEstimatedTotal(solicitorsCosts.getSolicitorEstimatedTotalCost());
        }
        return null;
    }

    private Stream<HRDetailDTO> getDetailsStreamWithType(Collection<HRSectionDTO> sections,
                                                         HardshipReviewDetailType type) {
        return sections.stream()
                .filter(section -> HardshipReviewDetailType.getFrom(section.getDetailType().getType()) == type)
                .flatMap(section -> section.getDetail().stream());
    }

    public void performHardshipResponseToApplicationDTO(ApiPerformHardshipResponse response,
                                                        ApplicationDTO application, CourtType courtType) {
        HardshipReviewDTO current;
        HardshipOverviewDTO hardshipOverview =
                application.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship();
        if (courtType.equals(CourtType.MAGISTRATE)) {
            current = hardshipOverview.getMagCourtHardship();
        } else {
            current = hardshipOverview.getCrownCourtHardship();
        }
        current.setId(response.getHardshipReviewId());
        current.setReviewResult(response.getReviewResult().name());
        current.setDisposableIncomeAfterHardship(response.getPostHardshipDisposableIncome());
        current.setDisposableIncome(response.getDisposableIncome());
    }

    public HardshipReviewDTO findHardshipResponseToHardshipDto(ApiFindHardshipResponse response) {
        return HardshipReviewDTO.builder()
                .id(response.getId())
                .reviewResult(response.getReviewResult().toString())
                .cmuId(response.getCmuId().longValue())
                .notes(response.getNotes())
                .decisionNotes(response.getDecisionNotes())
                .reviewDate(toDate(response.getReviewDate()))
                .disposableIncome(response.getDisposableIncome())
                .disposableIncomeAfterHardship(response.getDisposableIncomeAfterHardship())
                .newWorkReason(newWorkReasonToNewWorkReasonDto(response.getNewWorkReason()))
                .solictorsCosts(solicitorCostsToHrSolicitorsCostsDto(response.getSolicitorCosts()))
                .asessmentStatus(hardshipReviewStatusToAssessmentStatusDto(response.getStatus()))
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
                                        .map(apiHardshipDetail ->
                                                HRDetailDTO.builder()
                                                        .dateDue(toDate(apiHardshipDetail.getDateDue()))
                                                        .id(apiHardshipDetail.getId().longValue())
                                                        .accepted("Y".equals(
                                                                apiHardshipDetail.getAccepted()))
                                                        .amountNumber(apiHardshipDetail.getAmount())
                                                        .hrReasonNote(apiHardshipDetail.getReasonNote())
                                                        .otherDescription(
                                                                apiHardshipDetail.getOtherDescription())
                                                        .detailDescription(
                                                                hardshipReviewDetailCodeToHrDetailDescriptionDto(
                                                                        apiHardshipDetail.getDetailCode())
                                                        )
                                                        .frequency(frequencyToFrequenciesDto(
                                                                apiHardshipDetail.getFrequency())
                                                        )
                                                        .reason(hardshipReviewDetailReasonToHrReasonDto(
                                                                apiHardshipDetail.getDetailReason()))
                                                        .build()
                                        )
                                        .collect(Collectors.toList()))
                                .build()));
        return hrSectionDTOList;
    }

    private HRReasonDTO hardshipReviewDetailReasonToHrReasonDto(HardshipReviewDetailReason detailReason) {
        if (detailReason != null) {
            return HRReasonDTO.builder()
                    .id((long) detailReason.getId())
                    .build();
        }
        // Mimic Maat, create empty object
        return HRReasonDTO.builder().build();
    }

    private FrequenciesDTO frequencyToFrequenciesDto(Frequency frequency) {
        return FrequenciesDTO.builder()
                .code(frequency.getCode())
                .description(frequency.getDescription())
                .annualWeighting((long) frequency.getWeighting())
                .build();
    }

    private HRDetailDescriptionDTO hardshipReviewDetailCodeToHrDetailDescriptionDto(
            HardshipReviewDetailCode detailCode) {
        if (detailCode != null) {
            return HRDetailDescriptionDTO.builder()
                    .description(detailCode.getDescription())
                    .code(detailCode.getCode())
                    .build();
        }
        return HRDetailDescriptionDTO.builder().build();
    }

    private HRDetailTypeDTO hardshipReviewDetailTypeToHrDetailTypeDto(HardshipReviewDetailType hrDetailType) {
        return HRDetailTypeDTO.builder()
                .type(hrDetailType.getType())
                .description(hrDetailType.getDescription())
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

    private HRSolicitorsCostsDTO solicitorCostsToHrSolicitorsCostsDto(SolicitorCosts solicitorCosts) {
        return HRSolicitorsCostsDTO.builder()
                .solicitorDisb(solicitorCosts.getDisbursements())
                .solicitorHours(NumberUtils.toDouble(solicitorCosts.getHours()))
                .solicitorRate(solicitorCosts.getRate())
                .solicitorEstimatedTotalCost(solicitorCosts.getEstimatedTotal())
                .solicitorVat(solicitorCosts.getVat())
                .build();
    }

    public UserActionDTO getUserActionDTO(WorkflowRequest request, Action action) {
        HardshipReviewDTO hardshipReviewDTO = getHardshipReviewDTO(request.getApplicationDTO(), request.getCourtType());
        NewWorkReason newWorkReason = NewWorkReason.getFrom(hardshipReviewDTO.getNewWorkReason().getCode());

        return userMapper.getUserActionDTO(request, action, newWorkReason);
    }
}
