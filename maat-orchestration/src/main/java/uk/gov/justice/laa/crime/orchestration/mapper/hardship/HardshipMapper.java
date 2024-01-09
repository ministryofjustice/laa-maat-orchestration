package uk.gov.justice.laa.crime.orchestration.mapper.hardship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FrequenciesDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HRDetailDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HRDetailDescriptionDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HRDetailTypeDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HRProgressActionDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HRProgressDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HRProgressResponseDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HRReasonDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HRSectionDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HRSolicitorsCostsDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipOverviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.NewWorkReasonDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.enums.DeniedIncomeDetailCode;
import uk.gov.justice.laa.crime.orchestration.enums.ExtraExpenditureDetailCode;
import uk.gov.justice.laa.crime.orchestration.enums.Frequency;
import uk.gov.justice.laa.crime.orchestration.enums.HardshipReviewDetailCode;
import uk.gov.justice.laa.crime.orchestration.enums.HardshipReviewDetailReason;
import uk.gov.justice.laa.crime.orchestration.enums.HardshipReviewDetailType;
import uk.gov.justice.laa.crime.orchestration.enums.HardshipReviewProgressAction;
import uk.gov.justice.laa.crime.orchestration.enums.HardshipReviewProgressResponse;
import uk.gov.justice.laa.crime.orchestration.enums.HardshipReviewStatus;
import uk.gov.justice.laa.crime.orchestration.enums.NewWorkReason;
import uk.gov.justice.laa.crime.orchestration.mapper.UserMapper;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipDetail;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipProgress;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.model.hardship.DeniedIncome;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ExtraExpenditure;
import uk.gov.justice.laa.crime.orchestration.model.hardship.HardshipMetadata;
import uk.gov.justice.laa.crime.orchestration.model.hardship.HardshipProgress;
import uk.gov.justice.laa.crime.orchestration.model.hardship.HardshipReview;
import uk.gov.justice.laa.crime.orchestration.model.hardship.SolicitorCosts;
import uk.gov.justice.laa.crime.orchestration.util.DateUtil;
import uk.gov.justice.laa.crime.orchestration.util.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
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
                .withCmuId(NumberUtils.toInteger(application.getCaseManagementUnitDTO().getCmuId()))
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
                        .withItemCode(DeniedIncomeDetailCode.getFrom(detail.getReason().getReason()))
                ).toList();
    }

    private SolicitorCosts hrSolicitorCostsDtoToSolicitorCosts(HRSolicitorsCostsDTO solicitorsCosts) {
        if (solicitorsCosts.getSolicitorEstimatedTotalCost() != null) {
            return new SolicitorCosts()
                    .withVat(solicitorsCosts.getSolicitorVat())
                    .withRate(solicitorsCosts.getSolicitorRate())
                    // Converting from double to BigDecimal, truncate to 1 decimal place
                    .withHours(BigDecimal.valueOf(solicitorsCosts.getSolicitorHours())
                            .setScale(1, RoundingMode.DOWN))
                    .withDisbursements(solicitorsCosts.getSolicitorDisb())
                    .withEstimatedTotal(solicitorsCosts.getSolicitorEstimatedTotalCost());
        }
        return null;
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

    public void performHardshipResponseToApplicationDTO(ApiPerformHardshipResponse response,
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
        current.setDisposableIncomeAfterHardship(response.getPostHardshipDisposableIncome());
        current.setDisposableIncome(response.getDisposableIncome());
    }

    public HardshipReviewDTO findHardshipResponseToHardshipDto(ApiFindHardshipResponse response) {
        return HardshipReviewDTO.builder()
                .id(response.getId().longValue())
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
                    .reason(detailReason.getReason())
                    .build();
        }
        // Mimic Maat, create empty object
        return HRReasonDTO.builder().build();
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

    private HRSolicitorsCostsDTO solicitorCostsToHrSolicitorsCostsDto(SolicitorCosts solicitorCosts) {
        return HRSolicitorsCostsDTO.builder()
                .solicitorDisb(solicitorCosts.getDisbursements())
                .solicitorHours(solicitorCosts.getHours().doubleValue())
                .solicitorRate(solicitorCosts.getRate())
                .solicitorEstimatedTotalCost(solicitorCosts.getEstimatedTotal())
                .solicitorVat(solicitorCosts.getVat())
                .build();
    }
}
