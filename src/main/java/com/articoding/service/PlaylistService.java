package com.articoding.service;

import com.articoding.RoleHelper;
import com.articoding.error.ErrorNotFound;
import com.articoding.error.NotAuthorization;
import com.articoding.model.ClassRoom;
import com.articoding.model.Level;
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
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
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
        Playlist playlist = new Playlist();
        List<Level> levelsList = new ArrayList<>();

        for (Long idLevel : playlistForm.getLevels()) {
            /** Level exists */
            Level level = levelRepository.findById(idLevel)
                    .orElseThrow(() -> new ErrorNotFound("Clase", idLevel));


            levelsList.add(level);
        }
        playlist.setTitle(playlistForm.getTitle());
        playlist.setOwner(actualUser);
        playlist.setEnabled(true);

        Playlist newPlaylist = playlistRepository.save(playlist);

        return newPlaylist.getId();
    }

    public IPlaylist getPlaylist(User actualUser, Long playlistID){

        Playlist playlist = playlistRepository.findById(playlistID, Playlist.class);

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
            page = playlistRepository.findByTitleContains(pageRequest, title.get(), IPlaylist.class);
        } else {
            page = playlistRepository.findBy(pageRequest, IPlaylist.class);
        }
        return page;
    }

    private Page<IPlaylist> getOwnedLevels(PageRequest pageRequest, Optional<String> title, User actualUser) {
        Page<IPlaylist> page;
        if (roleHelper.isAdmin(actualUser)) {
            if (title.isPresent()) {
                page = playlistRepository.findByTitleContains(pageRequest, title.get(), IPlaylist.class);
            } else {
                page =  playlistRepository.findBy(pageRequest, IPlaylist.class);
            }
        } else {
            /** Otherwise, returns only the levels created by the user */
            if (title.isPresent()) {
                page = playlistRepository.findByOwnerAndEnabledTrueAndTitleContains(actualUser, title.get(), pageRequest, IPlaylist.class);
            } else {
                page = playlistRepository.findByOwnerAndEnabledTrue(actualUser, pageRequest, IPlaylist.class);
            }
        }
        return page;
    }


    //Todo - Sustituir PlaylistForm por UpdatePlaylistForm
    public Long updatePlaylist(PlaylistForm playlistForm, Long playlistId){return null;}





}
