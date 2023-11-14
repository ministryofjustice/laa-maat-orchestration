package uk.gov.justice.laa.maat.orchestration.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.maat.orchestration.dto.*;
import uk.gov.justice.laa.maat.orchestration.enums.*;
import uk.gov.justice.laa.maat.orchestration.model.ApiFindHardshipResponse;
import uk.gov.justice.laa.maat.orchestration.model.ApiHardshipDetail;
import uk.gov.justice.laa.maat.orchestration.model.ApiHardshipProgress;
import uk.gov.justice.laa.maat.orchestration.model.SolicitorCosts;
import uk.gov.justice.laa.maat.orchestration.util.CurrencyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static uk.gov.justice.laa.maat.orchestration.util.CurrencyUtil.toCurrency;
import static uk.gov.justice.laa.maat.orchestration.util.CurrencyUtil.toSysGenCurrency;
import static uk.gov.justice.laa.maat.orchestration.util.DateUtil.toDate;

@Component
public class HardshipReviewMapper implements ResponseMapper<ApiFindHardshipResponse, HardshipReviewDTO> {

    @Override
    public void toDto(ApiFindHardshipResponse model, HardshipReviewDTO reviewDTO) {
        reviewDTO.setId(model.getId().longValue());
        reviewDTO.setReviewResult(model.getReviewResult().toString());
        reviewDTO.setCmuId(model.getCmuId().longValue());
        reviewDTO.setNotes(model.getNotes());
        reviewDTO.setDecisionNotes(model.getDecisionNotes());
        reviewDTO.setReviewDate(toDate(model.getReviewDate()));
        reviewDTO.setDisposableIncome(toSysGenCurrency(model.getDisposableIncome()));
        reviewDTO.setDisposableIncomeAfterHardship(toSysGenCurrency(model.getDisposableIncomeAfterHardship()));
        reviewDTO.setNewWorkReason(getNewWorkReasonDTO(model.getNewWorkReason()));
        reviewDTO.setSolictorsCosts(getSolicitorsCostsDTO(model.getSolicitorCosts()));
        reviewDTO.setAsessmentStatus(getAssessmentStatusDTO(model.getStatus()));
        reviewDTO.setProgress(getProgressDTO(model.getReviewProgressItems()));
        reviewDTO.setSection(getSectionDTO(model.getReviewDetails()));
//        reviewDTO.setSupplier(model.gets);
    }

    private List<HRSectionDTO> getSectionDTO(List<ApiHardshipDetail> reviewDetails) {
        List<HRSectionDTO> hrSectionDTOList = new ArrayList<>();
        reviewDetails.stream()
                .collect(groupingBy(ApiHardshipDetail::getDetailType))
                .forEach((type, details) -> hrSectionDTOList.add(HRSectionDTO.builder()
                        .detailType(getHRDetailType(type))
                        .detail(details.stream()
                                .map(apiHardshipDetail -> HRDetailDTO.builder()
                                        .dateDue(toDate(apiHardshipDetail.getDateDue()))
                                        .id(apiHardshipDetail.getId().longValue())
                                        .accepted("Y".equals(apiHardshipDetail.getAccepted()))
                                        .amountNumber(toCurrency(apiHardshipDetail.getAmount()))
//                                .dateReceived(item.get)
                                        .hrReasonNote(apiHardshipDetail.getReasonNote())
                                        .otherDescription(apiHardshipDetail.getOtherDescription())
                                        .detailDescription(getHRDetailDescriptionDTO(apiHardshipDetail.getDetailCode()))
                                        .frequency(getFrequencyDTO(apiHardshipDetail.getFrequency()))
                                        .reason(getHRReasonDTO(apiHardshipDetail.getDetailReason()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build()));
        return hrSectionDTOList;
    }

    private HRReasonDTO getHRReasonDTO(HardshipReviewDetailReason detailReason) {
        if (detailReason != null) {
            return HRReasonDTO.builder()
                    .reason(detailReason.getReason())
//                .id(detailReason.get)
//                .accepted(detailReason.)
                    .build();
        }
        return null;
    }

    private FrequenciesDTO getFrequencyDTO(Frequency frequency) {
        return FrequenciesDTO.builder()
                .code(frequency.getCode())
                .description(frequency.getDescription())
                .annualWeighting((long) frequency.getAnnualWeighting())
                .build();
    }

    private HRDetailDescriptionDTO getHRDetailDescriptionDTO(HardshipReviewDetailCode detailCode) {
        return HRDetailDescriptionDTO.builder()
                .description(detailCode.getDescription())
                .code(detailCode.getCode())
                .build();
    }

    private HRDetailTypeDTO getHRDetailType(HardshipReviewDetailType hrDetailType) {
        return HRDetailTypeDTO.builder()
                .type(hrDetailType.getType())
                .description(hrDetailType.getDescription())
                .build();
    }

    private List<HRProgressDTO> getProgressDTO(List<ApiHardshipProgress> reviewProgressItems) {
        return reviewProgressItems.stream()
                .map(item -> HRProgressDTO.builder()
                        .dateCompleted(toDate(item.getDateCompleted()))
                        .id(item.getId().longValue())
                        .dateRequested(toDate(item.getDateRequested()))
                        .dateRequired(toDate(item.getDateRequired()))
                        .progressAction(getProgressActionDTO(item.getProgressAction()))
                        .progressResponse(getProgressResponseDTO(item.getProgressResponse()))
                        .build()).collect(Collectors.toList());
    }

    private HRProgressActionDTO getProgressActionDTO(HardshipReviewProgressAction hrProgressAction) {
        return HRProgressActionDTO.builder()
                .action(hrProgressAction.getAction())
                .description(hrProgressAction.getDescription())
                .build();
    }

    private HRProgressResponseDTO getProgressResponseDTO(HardshipReviewProgressResponse hrProgressResponse) {
        return HRProgressResponseDTO.builder()
                .response(hrProgressResponse.getResponse())
                .description(hrProgressResponse.getDescription())
                .build();
    }

    private AssessmentStatusDTO getAssessmentStatusDTO(HardshipReviewStatus status) {
        return AssessmentStatusDTO.builder()
                .status(status.getStatus())
                .description(status.getDescription())
                .build();
    }

    private NewWorkReasonDTO getNewWorkReasonDTO(NewWorkReason newWorkReason) {
        return NewWorkReasonDTO.builder()
                .description(newWorkReason.getDescription())
                .code(newWorkReason.getCode())
                .build();
    }

    private HRSolicitorsCostsDTO getSolicitorsCostsDTO(SolicitorCosts solicitorCosts) {
        return HRSolicitorsCostsDTO.builder()
                .solicitorDisb(toCurrency(solicitorCosts.getDisbursements()))
                .solicitorHours(CurrencyUtil.toDouble(solicitorCosts.getHours()))
                .solicitorRate(toCurrency(solicitorCosts.getRate()))
                .solicitorEstimatedTotalCost(toCurrency(solicitorCosts.getEstimatedTotal()))
                .solicitorVat(toCurrency(solicitorCosts.getVat()))
                .build();
    }
}
