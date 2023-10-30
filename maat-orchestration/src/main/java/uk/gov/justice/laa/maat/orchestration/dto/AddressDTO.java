package uk.gov.justice.laa.maat.orchestration.dto;

import org.apache.commons.lang3.StringUtils;

public class AddressDTO extends GenericDTO {
    private Long id;
    private String line1;
    private String line2;
    private String line3;
    private String city;
    private String postCode;
    private String county;
    private String country;

    @Override
    public Object getKey() {
        return getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        if (this.line1 != null) {
            if (!this.line1.equalsIgnoreCase((line1 != null) ? line1 : "")) {
                setDirty(true);
                this.line1 = line1;
            }
        } else {
            if (line1 != null) {
                setDirty(true);
                this.line1 = line1;
            }
        }
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        if (this.line2 != null) {
            if (!this.line2.equalsIgnoreCase((line2 != null) ? line2 : "")) {
                setDirty(true);
                this.line2 = line2;
            }
        } else {
            if (line2 != null) {
                setDirty(true);
                this.line2 = line2;
            }
        }
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        if (this.line3 != null) {
            if (!this.line3.equalsIgnoreCase((line3 != null) ? line3 : "")) {
                setDirty(true);
                this.line3 = line3;
            }
        } else {
            if (line3 != null) {
                setDirty(true);
                this.line3 = line3;
            }
        }
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        if (this.postCode != null) {
            if (!this.postCode.equalsIgnoreCase((postCode != null) ? postCode : "")) {
                setDirty(true);
                this.postCode = postCode;
            }
        } else {
            if (postCode != null) {
                setDirty(true);
                this.postCode = postCode;
            }
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (this.city != null) {
            if (!this.city.equalsIgnoreCase((city != null) ? city : "")) {
                setDirty(true);
                this.city = city;
            }
        } else {
            if (city != null) {
                setDirty(true);
                this.city = city;
            }
        }
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        if (this.county != null) {
            if (!this.county.equalsIgnoreCase((county != null) ? county : "")) {
                setDirty(true);
                this.county = county;
            }
        } else {
            if (county != null) {
                setDirty(true);
                this.county = county;
            }
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        if (this.country != null) {
            if (!this.country.equalsIgnoreCase((country != null) ? country : "")) {
                setDirty(true);
                this.country = country;
            }
        } else {
            if (country != null) {
                setDirty(true);
                this.country = country;
            }
        }
    }

    public static AddressDTO example(long idOffset) {
        AddressDTO dto = new AddressDTO();
        dto.setId(510 + idOffset);
        dto.setCity("Ashford");
        dto.setCountry("England");
        dto.setCounty("Kent");
        dto.setLine1("Address Line1: " + idOffset);
        dto.setLine2("Address Line2: " + idOffset);
        dto.setLine3("Address Line3: " + idOffset);
        dto.setPostCode("tn27 " + idOffset);
        return dto;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        // object must be Tested at this point
        AddressDTO addressDTO = (AddressDTO) obj;
        if (StringUtils.equals(this.getLine1(), addressDTO.getLine1())
                && StringUtils.equals(this.getLine2(), addressDTO.getLine2())
                && StringUtils.equals(this.getLine3(), addressDTO.getLine3())
                && StringUtils.equals(this.getPostCode(), addressDTO.getPostCode())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int) (this.id.longValue() ^ (this.id.longValue() >>> 32));
        return hash;
    }
}