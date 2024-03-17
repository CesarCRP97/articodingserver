package com.articoding.model.in;

import com.articoding.model.articodingLevel.ACLevel;

import java.util.List;

public class LevelForm {

    public Boolean active;
    private String title;
    private List<Long> classes;
    private ACLevel articodingLevel;
    private boolean publicLevel;

    private byte[] image;

    public LevelForm() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Long> getClasses() {
        return classes;
    }

    public void setClasses(List<Long> classes) {
        this.classes = classes;
    }

    public ACLevel getArticodingLevel() {
        return articodingLevel;
    }

    public void setArticodingLevel(ACLevel articodingLevel) {
        this.articodingLevel = articodingLevel;
    }

    public boolean isPublicLevel() {
        return publicLevel;
    }

    public void setPublicLevel(boolean publicLevel) {
        this.publicLevel = publicLevel;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
