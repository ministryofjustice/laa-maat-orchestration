package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.Date;

public class NoteDTO extends GenericDTO {

    private static final long serialVersionUID = -6856254823228346707L;

    private Long noteId;
    private Long fdcId;
    private UserDTO author;
    private String userCreated;
    private Date dateCreated;
    private String note;

    public NoteDTO() {

        reset();
    }

    @Override
    public Object getKey() {

        return getNoteId();

    }


    public void reset() {

        this.noteId = null;
        this.fdcId = null;
        this.dateCreated = null;
        this.userCreated = null;
        this.note = null;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(String userCreated) {
        this.userCreated = userCreated;
    }

    public Long getFdcId() {
        return fdcId;
    }

    public void setFdcId(Long fdcId) {
        this.fdcId = fdcId;
    }


}
