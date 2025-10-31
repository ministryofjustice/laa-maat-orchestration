package uk.gov.justice.laa.crime.orchestration.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RestrictedField {
    APPEAL_CC_OUTCOME("appeal cc outcome"),
    APPEAL_CC_WITHDRAWAL_DATE("appeal cc withdrawal date"),
    APPEAL_RECEIVED_DATE("appeal received date"),
    APPEAL_SENTENCE_ORDER_DATE("appeal sentence order date"),
    APPEAL_TYPE("appeal type"),
    CAPITAL_ASSET_CONFIRMED("capital asset confirmed"),
    CAPITAL_DECLARED_CHARGES_CONFIRMED("capital declared charges confirmed"),
    CAPITAL_DECLARED_MORTGAGE_CONFIRMED("capital declared mortgage confirmed"),
    CAPITAL_UNDECLARED_PROPERTY("capital undeclared property"),
    CAPITAL_VERIFIED_MARKET_VALUE("capital verified market value"),
    CAPITAL_VERIFIED_MORTGAGE_CHARGES("capital verified mortgage charges"),
    CASE_TYPE("case type"),
    CROWN_COURT_REP_ORDER_NUMBER("crown court rep order number"),
    DATE_CAPITAL_ALLOWANCE_RESTORED("date capital allowance restored"),
    DATE_CAPITAL_ALLOWANCE_WITHHELD("date capital allowance withheld"),
    EQUITY_DECLARED_CHARGES_CONFIRMED("equity declared charges confirmed"),
    EQUITY_DECLARED_MORTGAGE_CONFIRMED("equity declared mortgage confirmed"),
    EQUITY_UNDECLARED_PROPERTY("equity undeclared property"),
    EQUITY_VERIFIED_MARKET_VALUE("equity verified market value"),
    EQUITY_VERIFIED_MORTGAGE_CHARGES("equity verified mortgage charges"),
    MAGISTRATE_COURT_OUTCOME("magistrate court outcome"),
    OFFENCE_TYPE("offence type"),
    REP_ORDER_STATUS("rep order status"),
    RESIDENTIAL_STATUS_CONFIRMED("residential status confirmed"),
    UNDECLARED_ASSET("undeclared asset"),
    UNDER_SPECIAL_INVESTIGATION("under special investigation"),
    UPLIFT_APPLIED("uplift applied"),
    UPLIFT_REMOVED("uplift removed"),
    VERIFIED_CAPITAL_AMOUNT("verified capital amount");

    private final String field;
}
