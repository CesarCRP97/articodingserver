package com.articoding.service;

import com.articoding.RoleHelper;
import com.articoding.error.ErrorNotFound;
import com.articoding.error.NotAuthorization;
import com.articoding.model.Level;
import com.articoding.model.Playlist;
import com.articoding.model.User;
import com.articoding.model.in.ILevel;
import com.articoding.model.in.IPlaylist;
import com.articoding.model.in.LevelWithImageDTO;
import com.articoding.model.in.PlaylistForm;
import com.articoding.model.in.PlaylistDTO;
import com.articoding.repository.LevelRepository;
import com.articoding.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
        return playlistRepository.findById(playlistID, IPlaylist.class);
    }

    public Page<PlaylistDTO> getPlaylists(PageRequest pageRequest, Optional<Long> userId, Optional<String> title,
                                       Optional<String> owner, Optional<Long> playlistId, Optional<Boolean>publicPlaylists){
        Page<IPlaylist> page;
        User actualUser = userService.getActualUser();

        if(publicPlaylists.isPresent()){
            page = getPublicPlaylists(pageRequest, userId, title, owner, playlistId);
        }
        else{
            page = getOwnedLevels(pageRequest, title, actualUser);
        }

        return toPlaylistDTO(page);
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

    public Long updatePlaylist(PlaylistForm playlistForm, Long playlistId){

        Playlist playlistOld = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ErrorNotFound("Playlist does not exist", playlistId));

        User actualUser = userService.getActualUser();
        //probar si equals compara correctamente dos usuarios
        if(!playlistOld.getOwner().equals(actualUser) || !roleHelper.isAdmin(actualUser)){
            throw new NotAuthorization("modificar el nivel " + playlistId);
        }
        else{
            if(playlistForm.getTitle() != null)
                playlistOld.setTitle(playlistForm.getTitle());
            if(playlistForm.getLevels() != null){
                //If there is a new level not previously contained by the playlist, adds it.
                List<Level> levelList = playlistOld.getLevels();
                for (Long idLevel : playlistForm.getLevels()) {
                    /** Level exists */
                    Level level = levelRepository.findById(idLevel)
                            .orElseThrow(() -> new ErrorNotFound("Clase", idLevel));
                    if(!levelList.contains(level))
                        levelList.add(level);

                }
                playlistOld.setLevels(levelList);
            }
        }

        return playlistRepository.save(playlistOld).getId();
    }


    public long likePlaylist(PlaylistForm playlistForm, long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ErrorNotFound("level", playlistId));
        playlist.incrLikes();
        User u = userService.getActualUser();
        u.addLikedPlaylist(playlistId);

        playlistRepository.save(playlist);
        return playlistId;
    }

    public long dislikePlaylist(PlaylistForm playlistForm, long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ErrorNotFound("level", playlistId));
        playlist.decrLikes();
        User u = userService.getActualUser();
        u.deleteLikedLevel(playlistId);

        playlistRepository.save(playlist);
        return playlistId;
    }

    public long playPlaylist(PlaylistForm playlistForm, long playlistId) {
        System.out.println("like level id: " + playlistId);
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ErrorNotFound("level", playlistId));
        playlist.increaseTimesPlayed();
        playlistRepository.save(playlist);
        return playlistId;
    }

    public PlaylistDTO toPlaylistDTO(IPlaylist playlist){

        PlaylistDTO newPlaylist = new PlaylistDTO();
        newPlaylist.setId(playlist.getId());
        newPlaylist.setTitle(playlist.getTitle());
        newPlaylist.setOwner(playlist.getOwner());

        ArrayList<LevelWithImageDTO> newLevels = new ArrayList<>();

        for(ILevel level : playlist.getLevels()){
            newLevels.add(levelService.toLevelWithImageDTO(level));
        }
        newPlaylist.setLevels(newLevels);
        return newPlaylist;
    }

    private Page<PlaylistDTO> toPlaylistDTO(Page<IPlaylist> playlists){
        return playlists.map(this::toPlaylistDTO);
    }

}
