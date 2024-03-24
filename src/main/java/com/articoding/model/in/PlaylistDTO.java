package com.articoding.model.in;

import java.util.List;

public class PlaylistDTO {

    List<LevelWithImageDTO> levelsWithImage;
    private Long id;
    private String title;
    private IUser owner;
    private int likes;

    public Long getId() {
        return id;
    }

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

    public int getLikes() {
        return this.likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }



    public List<LevelWithImageDTO> getLevelsWithImage() {
        return levelsWithImage;
    }

    public void setLevelsWithImage(List<LevelWithImageDTO> levelsWithImage) {
        this.levelsWithImage = levelsWithImage;
    }
}
