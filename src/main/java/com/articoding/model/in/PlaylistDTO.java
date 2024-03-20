package com.articoding.model.in;

import com.articoding.model.User;

import java.util.List;

public class PlaylistDTO {

    private Long id;

    private String title;

    private IUser owner;

    private int likes;

    private int timesPlayed;

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

    public IUser getOwner() {
        return owner;
    }

    public void setOwner(IUser owner) {
        this.owner = owner;
    }

    public int getLikes(){return this.likes;}

    public void setLikes(int likes){ this.likes = likes;}

    public int getTimesPlayed(){return this.timesPlayed;}

    public void setTimesPlayed(int timesPlayed){ this.timesPlayed = timesPlayed;}


    public List<LevelWithImageDTO> getLevels() {
        return levels;
    }

    public void setLevels(List<LevelWithImageDTO> levels) {
        this.levels = levels;
    }
}
