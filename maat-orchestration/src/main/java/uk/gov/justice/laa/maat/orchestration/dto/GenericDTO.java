package uk.gov.justice.laa.maat.orchestration.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public abstract class GenericDTO implements Serializable {
    private Timestamp timestamp;
    private Boolean selected;
    private Boolean mDirty;

    public GenericDTO() {
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSelected() {
        return this.selected != null ? this.selected : false;
    }

    public void setSelected(boolean selected) {
        this.selected = Boolean.valueOf(selected);
    }

    public boolean isDirty() {
        return this.mDirty != null ? this.mDirty : false;
    }

    public void setDirty(boolean dirty) {
        this.mDirty = Boolean.valueOf(dirty);
    }

    public abstract Object getKey();
}
