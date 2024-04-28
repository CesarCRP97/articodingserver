package com.articoding.model.in;

import com.articoding.model.Level;
import com.articoding.model.articodingLevel.ACLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class LevelResponse {
    public Long id;
    public String title;
    public int likes;
    public int timesPlayed;
    public boolean publicLevel;
    public String imagePath;
    public int classRooms;
    public boolean active;
    public String ownerName;
    @JsonIgnore
    public String serializaArticodingLevel;

    public LevelResponse(Level level) {
        this.id = level.getId();
        this.title = level.getTitle();
        this.likes = level.getLikes();
        this.timesPlayed = level.getTimesPlayed();
        this.publicLevel = level.isPublicLevel();
        this.imagePath = level.getImagePath();
        this.classRooms = level.getClassRooms().size();
        this.active = level.isActive();
        this.ownerName = level.getOwner().getUsername();
        this.serializaArticodingLevel = level.getSerializaArticodingLevel();
    }

    public ACLevel getArticodingLevel() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(serializaArticodingLevel, ACLevel.class);
    }
}
