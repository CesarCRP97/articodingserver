package com.articoding.model.in;

public class UpdateLevelForm {

    private String title;

    private Boolean publicLevel;

    private Boolean active;

    public UpdateLevelForm() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isPublicLevel() {
        return publicLevel;
    }

    public void setPublicLevel(Boolean publicLevel) {
        this.publicLevel = publicLevel;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
