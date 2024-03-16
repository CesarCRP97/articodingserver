package com.articoding.service;

import com.articoding.RoleHelper;
import com.articoding.model.Playlist;
import com.articoding.model.User;
import com.articoding.model.in.ILevel;
import com.articoding.model.in.IPlaylist;
import com.articoding.model.in.PlaylistForm;
import com.articoding.repository.ClassRepository;
import com.articoding.repository.LevelRepository;
import com.articoding.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public class PlaylistService {


    @Autowired
    LevelRepository levelRepository;
    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    UserService userService;
    @Autowired
    LevelService levelService;
    @Autowired
    RoleHelper roleHelper;


    public Long createPlaylist(User actualUser, PlaylistForm playlistForm){
        return null;
    }

    public IPlaylist getPlaylist(User actualUser, Long playlistID){
        return null;
    }

    public Page<IPlaylist> getPlaylists(PageRequest pageRequest, Optional<Long> userId, Optional<String> title,
                                       Optional<String> owner, Optional<Long> playlistId, Optional<Boolean>publicPlaylists){
        Page<IPlaylist> page;
        User actualUser = userService.getActualUser();

        if(publicPlaylists.isPresent()){
            page = getPublicPlaylists(pageRequest, userId, title, owner, playlistId);
        }
        else{
            page = getOwnedLevels(pageRequest, title, actualUser);
        }

        return page;
    }


    private Page<IPlaylist> getPublicPlaylists(PageRequest pageRequest, Optional<Long> userId, Optional<String> title,
                                               Optional<String> owner, Optional<Long> playlistId) {
        Page<IPlaylist> page;
        if (title.isPresent()) {
            page = playlistRepository.findByPublicLevelTrueAndTitleContains(pageRequest, IPlaylist.class, title.get());
        } else {
            page = playlistRepository.findByPublicLevelTrue(pageRequest, IPlaylist.class);
        }
        return page;
    }

    private Page<IPlaylist> getOwnedLevels(PageRequest pageRequest, Optional<String> title, User actualUser) {
        if (roleHelper.isAdmin(actualUser)) {
            if (title.isPresent()) {
                return playlistRepository.findByTitleContains(pageRequest, title.get(), IPlaylist.class);
            } else {
                return playlistRepository.findBy(pageRequest, IPlaylist.class);
            }
        } else {
            /** Otherwise, returns only the levels created by the user */
            if (title.isPresent()) {
                return playlistRepository.findByOwnerAndActiveTrueAndTitleContains(actualUser, title.get(), pageRequest, IPlaylist.class);
            } else {
                return playlistRepository.findByOwnerAndActiveTrue(actualUser, pageRequest, IPlaylist.class);
            }
        }

    }


    //Todo - Sustituir PlaylistForm por UpdatePlaylistForm
    public Long updatePlaylist(PlaylistForm playlistForm, Long playlistId){return null;}





}
