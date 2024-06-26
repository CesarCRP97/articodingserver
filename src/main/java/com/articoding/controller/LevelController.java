package com.articoding.controller;

import com.articoding.model.in.ILevel;
import com.articoding.model.in.LevelComparator;
import com.articoding.model.in.LevelForm;
import com.articoding.model.in.LevelWithImageDTO;
import com.articoding.model.in.UpdateLevelForm;
import com.articoding.model.rest.CreatedRef;
import com.articoding.service.LevelService;
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
import java.util.Comparator;
import java.util.Optional;

@RestController
@RequestMapping("/levels")
public class LevelController {

    @Autowired
    UserService userService;
    @Autowired
    LevelService levelService;

    @PostMapping
    public ResponseEntity<CreatedRef> createLevel(@RequestBody LevelForm levelForm) throws Exception {
        Long id = levelService.createLevel(userService.getActualUser(), levelForm);


        CreatedRef createdRef = new CreatedRef(String.format("/levels/%d", id));
        return ResponseEntity.ok(createdRef);
    }

    @GetMapping("/{levelId}")
    public ResponseEntity<LevelWithImageDTO> getLevel(@PathVariable(value = "levelId") Long levelId) throws IOException {
        ILevel level = levelService.getLevel(userService.getActualUser(), levelId);

        LevelWithImageDTO response = levelService.toLevelWithImageDTO(level);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<LevelWithImageDTO>> getLevels(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "class", required = false) Optional<Long> classId,
            @RequestParam(name = "user", required = false) Optional<Long> userId,
            @RequestParam(name = "publicLevels", required = false) Optional<Boolean> publicLevels,
            @RequestParam(name = "liked", required = false) Optional<Boolean> liked,
            @RequestParam(name = "title", required = false) Optional<String> title,
            @RequestParam(name = "owner", required = false) Optional<String> owner,
            @RequestParam(name = "levelid", required = false) Optional<Long> levelId,
            @RequestParam(name = "orderByLikes", required = false) Optional<Boolean> orderByLikes

    ) {
        boolean byLikes = orderByLikes.isPresent() && orderByLikes.get();
        Comparator<ILevel> comparator = new LevelComparator(byLikes);

        return ResponseEntity.ok(levelService.getLevels(PageRequest.of(page, size), comparator, classId, userId, publicLevels, liked, title, owner, levelId));
    }

    @PutMapping("/{levelId}")
    public ResponseEntity<CreatedRef> updateLevel(@RequestBody UpdateLevelForm levelForm,
                                                  @PathVariable(value = "levelId") Long levelId) {
        return ResponseEntity.ok(new CreatedRef("levels/" + levelService.updateLevel(levelForm, levelId)));
    }

    @PostMapping("/{levelId}/increaselikes")
    public ResponseEntity<CreatedRef> increaseLevelsLikes(@RequestBody LevelForm levelForm,
                                                          @PathVariable(value = "levelId") Long levelId) {
        return ResponseEntity.ok(new CreatedRef("levels/" + levelService.likeLevel(levelForm, levelId)));
    }

    @PostMapping("/{levelId}/decreaselikes")
    public ResponseEntity<CreatedRef> decreaseLevelsLikes(@RequestBody LevelForm levelForm,
                                                          @PathVariable(value = "levelId") Long levelId) {
        return ResponseEntity.ok(new CreatedRef("levels/" + levelService.dislikeLevel(levelForm, levelId)));
    }

    @PostMapping("/{levelId}/play")
    public ResponseEntity<CreatedRef> playLevel(@RequestBody LevelForm levelForm,
                                                @PathVariable(value = "levelId") Long levelId) {
        return ResponseEntity.ok(new CreatedRef("levels/" + levelService.playLevel(levelForm, levelId)));
    }

}
