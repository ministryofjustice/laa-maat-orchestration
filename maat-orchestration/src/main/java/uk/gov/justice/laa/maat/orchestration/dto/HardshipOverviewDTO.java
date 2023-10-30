package uk.gov.justice.laa.maat.orchestration.dto;

public class HardshipOverviewDTO extends GenericDTO {
    private HardshipReviewDTO magCourtHardship;
    private HardshipReviewDTO crownCourtHardship;


    public HardshipOverviewDTO() {
        reset();
    }

    public void reset() {
        this.magCourtHardship = new HardshipReviewDTO();
        this.crownCourtHardship = new HardshipReviewDTO();
    }

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    public HardshipReviewDTO getMagCourthardShip() {
        return magCourtHardship;
    }

    public void setMagCourthardShip(HardshipReviewDTO magCourthardShip) {
        this.magCourtHardship = magCourthardShip;
    }

    public HardshipReviewDTO getCrownCourtHardship() {
        return crownCourtHardship;
    }

    public void setCrownCourtHardship(HardshipReviewDTO crownCourtHardship) {
        this.crownCourtHardship = crownCourtHardship;
    }

}