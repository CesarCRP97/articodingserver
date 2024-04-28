package com.articoding.model.in;

import com.articoding.model.User;

import java.util.Set;

public class UserResponse {
    public Long id;
    public String username;
    public boolean enabled;
    public Set<Long> likedLevels;
    public Set<Long> likedPlaylists;


    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.enabled = user.isEnabled();
        this.likedLevels = user.getLikedLevels();
        this.likedPlaylists = user.getLikedPlaylists();
    }

}
