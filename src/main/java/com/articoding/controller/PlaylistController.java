package com.articoding.controller;

import com.articoding.model.in.IPlaylist;
import com.articoding.model.in.PlaylistDTO;
import com.articoding.model.in.PlaylistForm;
import com.articoding.model.rest.CreatedRef;
import com.articoding.service.PlaylistService;
import com.articoding.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    @Autowired
    UserService userService;
    @Autowired
    PlaylistService playlistService;


    @PostMapping
    public ResponseEntity<CreatedRef> createPlaylist(@RequestBody PlaylistForm playlistForm) throws Exception {
        System.out.print("Create playlist");
        Long id = playlistService.createPlaylist(userService.getActualUser(), playlistForm);

        CreatedRef createdRef = new CreatedRef(String.format("/playlists/%d", id));
        return ResponseEntity.ok(createdRef);
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<IPlaylist> getPlaylist(@PathVariable(value = "playlistId") Long playlistId) throws IOException {
        IPlaylist playlist = playlistService.getPlaylist(userService.getActualUser(), playlistId);
        playlist.getLevels();

        return ResponseEntity.ok(playlist);
    }

    @GetMapping
    public ResponseEntity<Page<PlaylistDTO>> getPlaylists(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "user", required = false) Optional<Long> userId,
            @RequestParam(name = "playlist", required = false) Optional<Long> playlistId,
            @RequestParam(name = "liked", required = false) Optional<Boolean> liked,
            @RequestParam(name = "publicPlaylists", required = false) Optional<Boolean> publicPlaylist,
            @RequestParam(name = "title", required = false) Optional<String> title,
            @RequestParam(name = "owner", required = false) Optional<String> owner
            //@RequestParam(name = "orderByLikes", required = false) Optional<Boolean> orderByLikes

    ) {
        String s;
        //if(orderByLikes.isPresent() && orderByLikes.get()) s = "likes";
        //else s = "timesPlayed";
        //Sort sort = Sort.by(Sort.Direction.DESC, s);
        return ResponseEntity.ok(playlistService.getPlaylists(PageRequest.of(page, size), userId, playlistId, liked, publicPlaylist,  title, owner));
    }

    @PutMapping("/{playlistId}")
    public ResponseEntity<CreatedRef> updateLevel(@RequestBody PlaylistForm playlistForm,
                                                  @PathVariable(value = "playlistId") Long playlistId) {
        return ResponseEntity.ok(new CreatedRef("playlists/" + playlistService.updatePlaylist(playlistForm, playlistId)));
    }

    @PostMapping("/{playlistId}/increaselikes")
    public ResponseEntity<CreatedRef> increasePlaylistLikes(@RequestBody PlaylistForm playlistForm,
                                                          @PathVariable(value = "playlistId") Long playlistId) {
        return ResponseEntity.ok(new CreatedRef("playlists/" + playlistService.likePlaylist(playlistForm, playlistId)));
    }

    @PostMapping("/{levelId}/decreaselikes")
    public ResponseEntity<CreatedRef> decreasePlaylistLikes(@RequestBody PlaylistForm playlistForm,
                                                          @PathVariable(value = "playlistId") Long playlistId) {
        return ResponseEntity.ok(new CreatedRef("playlists/" + playlistService.dislikePlaylist(playlistForm, playlistId)));
    }

    @PostMapping("/{levelId}/play")
    public ResponseEntity<CreatedRef> playPlaylist(@RequestBody PlaylistForm playlistForm,
                                                          @PathVariable(value = "playlistId") Long playlistId) {
        return ResponseEntity.ok(new CreatedRef("playlists/" + playlistService.playPlaylist(playlistForm, playlistId)));
    }


}
