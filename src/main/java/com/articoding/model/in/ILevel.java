package com.articoding.model.in;

import com.articoding.model.articodingLevel.ACLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public interface ILevel {

    Integer getId();

    String getTitle();

    int getLikes();

    int getTimesPlayed();

    boolean isPublicLevel();

    String getImagePath();

    @Value("#{target.classRooms.size()}")
    int getClassRooms();

    boolean isActive();

    IUser getOwner();

    @JsonIgnore
    String getSerializaArticodingLevel();

    default ACLevel getArticodingLevel() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(getSerializaArticodingLevel(), ACLevel.class);
    }


}
