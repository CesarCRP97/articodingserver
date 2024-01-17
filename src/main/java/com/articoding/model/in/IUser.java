package com.articoding.model.in;

import com.articoding.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface IUser {

    public Integer getId();

    public String getUsername();

    public boolean isEnabled();
    @JsonIgnore
    public Role getRole();


    //TODO-Aclarar si hace algo, getRol->getRole
    @JsonProperty("role")
    default String getRol() {
        return getRole().getName();
    }
}
