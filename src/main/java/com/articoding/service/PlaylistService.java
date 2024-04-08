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
import com.articoding.model.in.PlaylistDTO;
import com.articoding.model.in.PlaylistForm;
import com.articoding.repository.LevelRepository;
import com.articoding.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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


    public Long createPlaylist(User actualUser, PlaylistForm playlistForm) {
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
        playlist.setLevels(levelsList);

        Playlist newPlaylist = playlistRepository.save(playlist);

        return newPlaylist.getId();
    }

    public IPlaylist getPlaylist(User actualUser, Long playlistID) {
        return playlistRepository.findById(playlistID, IPlaylist.class);
    }

    public Page<PlaylistDTO> getPlaylists(PageRequest pageRequest, Comparator<IPlaylist> comparator, Optional<Long> userId, Optional<Long> playlistId,
                                          Optional<Boolean> liked, Optional<Boolean> publicPlaylists, Optional<String> title,
                                          Optional<String> owner) {
        List<IPlaylist> playlists;
        User actualUser = userService.getActualUser();

        if (publicPlaylists.isPresent() && publicPlaylists.get()) {
            playlists = getPublicPlaylists(userId, title, owner, playlistId, liked);
        } else {
            playlists = getOwnedLevels(title, actualUser);
        }

        Page<IPlaylist> page = filteredPlaylistsToPage(pageRequest, comparator, playlists);
        return toPlaylistDTO(page);
    }


    private List<IPlaylist> getPublicPlaylists(Optional<Long> userId, Optional<String> title, Optional<String> owner,
                                               Optional<Long> playlistId, Optional<Boolean> liked) {
        Streamable<IPlaylist> playlists;

        if (liked.isPresent() && liked.get()) {
            Set<Long> likedIds = userService.getActualUser().getLikedPlaylists();
            playlists = playlistRepository.findByIdIn(likedIds, IPlaylist.class);
        } else playlists = playlistRepository.findBy(IPlaylist.class);

        if (title.isPresent()) {
            playlists = filterStreamable(playlists, playlistRepository.findByTitleContains(title.get(), IPlaylist.class));
        }
        if (playlistId.isPresent()) {
            playlists = playlists.filter(level -> level.getId().longValue() == playlistId.get());
        }
        //It uses a filter because we need direct access to the name of the owner
        if (owner.isPresent()) {
            playlists = playlists.filter(playlist -> Objects.equals(playlist.getOwner().getUsername(), owner.get()));
        }

        return playlists.stream().collect(Collectors.toList());
    }

    private List<IPlaylist> getOwnedLevels(Optional<String> title, User actualUser) {
        Streamable<IPlaylist> playlists;
        if (roleHelper.isAdmin(actualUser)) {
            if (title.isPresent()) {
                playlists = playlistRepository.findByTitleContains(title.get(), IPlaylist.class);
            } else {
                playlists = playlistRepository.findBy(IPlaylist.class);
            }
        } else {
            /** Otherwise, returns only the levels created by the user */
            if (title.isPresent()) {
                playlists = playlistRepository.findByOwnerAndEnabledTrueAndTitleContains(actualUser, title.get(), IPlaylist.class);
            } else {
                playlists = playlistRepository.findByOwnerAndEnabledTrue(actualUser, IPlaylist.class);
            }
        }
        return playlists.stream().collect(Collectors.toList());
    }

    public Long updatePlaylist(PlaylistForm playlistForm, Long playlistId) {

        Playlist playlistOld = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ErrorNotFound("Playlist does not exist", playlistId));

        User actualUser = userService.getActualUser();
        //probar si equals compara correctamente dos usuarios
        if (!playlistOld.getOwner().equals(actualUser) || !roleHelper.isAdmin(actualUser)) {
            throw new NotAuthorization("modificar el nivel " + playlistId);
        } else {
            if (playlistForm.getTitle() != null)
                playlistOld.setTitle(playlistForm.getTitle());
            if (playlistForm.getLevels() != null) {
                //If there is a new level not previously contained by the playlist, adds it.
                List<Level> levelList = playlistOld.getLevels();
                for (Long idLevel : playlistForm.getLevels()) {
                    /** Level exists */
                    Level level = levelRepository.findById(idLevel)
                            .orElseThrow(() -> new ErrorNotFound("Clase", idLevel));
                    if (!levelList.contains(level))
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
        u.deleteLikedPlaylist(playlistId);

        playlistRepository.save(playlist);
        return playlistId;
    }

    public long playPlaylist(PlaylistForm playlistForm, long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ErrorNotFound("level", playlistId));

        playlist.incrTimesPlayed();

        playlistRepository.save(playlist);
        return playlistId;
    }

    public PlaylistDTO toPlaylistDTO(IPlaylist playlist) {

        PlaylistDTO newPlaylist = new PlaylistDTO();
        newPlaylist.setId(playlist.getId());
        newPlaylist.setTitle(playlist.getTitle());
        newPlaylist.setOwner(playlist.getOwner());
        newPlaylist.setLikes(playlist.getLikes());
        newPlaylist.setTimesPlayed(playlist.getTimesPlayed());

        ArrayList<LevelWithImageDTO> newLevels = new ArrayList<>();

        for (ILevel level : playlist.getLevels()) {
            newLevels.add(levelService.toLevelWithImageDTO(level));
        }
        newPlaylist.setLevelsWithImage(newLevels);
        return newPlaylist;
    }

    private Page<PlaylistDTO> toPlaylistDTO(Page<IPlaylist> playlists) {
        return playlists.map(this::toPlaylistDTO);
    }

    private Streamable<IPlaylist> filterStreamable(Streamable<IPlaylist> s1, Streamable<IPlaylist> s2){
        Set<IPlaylist> set2 = new HashSet<>();
        s2.forEach(set2::add);
        return s1.filter(set2::contains);
    }

    private Page<IPlaylist> filteredPlaylistsToPage(PageRequest pageRequest, Comparator<IPlaylist> comparator, List<IPlaylist> filteredLiked) {

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), filteredLiked.size());

        filteredLiked.sort(comparator);

        List<IPlaylist> pageContent = filteredLiked.subList(start, end);

        return new PageImpl<>(pageContent, pageRequest, filteredLiked.size());
    }
}
