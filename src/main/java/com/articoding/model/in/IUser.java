package com.articoding.model.in;

import com.articoding.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public interface IUser {

    Integer getId();

    String getUsername();

    boolean isEnabled();

    Set<Long> getLikedLevels();

    Set<Long> getLikedPlaylists();

    @JsonIgnore
    Role getRole();


    @JsonProperty("rol")
    default String getRol() {
        return getRole().getName();
    }
}
