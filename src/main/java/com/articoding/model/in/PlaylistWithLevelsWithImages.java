package com.articoding.model.in;

import com.articoding.model.User;

import java.util.List;

public class PlaylistWithLevelsWithImages {

    private Long id;

    private String title;

    private User owner;

    List<LevelWithImageDTO> levels;

    public Long getId(){return id;}

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<LevelWithImageDTO> getLevels() {
        return levels;
    }

    public void setLevels(List<LevelWithImageDTO> levels) {
        this.levels = levels;
    }
}
