package com.articoding.model.in;

import java.util.List;

public class PlaylistForm {

    public boolean active;
    private String title;
    private List<Long> levels;

    public PlaylistForm() {
    }

    public List<Long> getLevels() {
        return levels;
    }

    public void setLevels(List<Long> levels) {
        this.levels = levels;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
