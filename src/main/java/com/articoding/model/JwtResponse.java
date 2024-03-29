package com.articoding.model;

import java.io.Serializable;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;

    private final String role;

    //Temporal
    private Integer imageIndex = 0;

    public JwtResponse(String jwttoken, String role, Integer imageIndex) {
        this.role = role;
        this.jwttoken = jwttoken;
        this.imageIndex = imageIndex;
    }

    public String getToken() {
        return this.jwttoken;
    }

    public String getRole() {
        return role;
    }

    public Integer getImageIndex() {
        return imageIndex;
    }
}