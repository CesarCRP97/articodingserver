package com.articoding.controller;

import com.articoding.model.in.UpdateLevelForm;
import com.articoding.model.rest.CreatedRef;
import com.articoding.model.in.ILevel;
import com.articoding.model.in.LevelForm;
import com.articoding.service.LevelService;
import com.articoding.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        Long id =  levelService.createLevel(userService.getActualUser(), levelForm);
        CreatedRef createdRef = new CreatedRef(String.format("/levels/%d", id));
        return ResponseEntity.ok(createdRef);
    }

    @GetMapping("/{levelId}")
    public ResponseEntity<ILevel> getLevel(@PathVariable(value="levelId") Long levelId) {
        return ResponseEntity.ok(levelService.getLevel(userService.getActualUser(), levelId));
    }

    @GetMapping
    public ResponseEntity<Page<ILevel>> getLevels(
            @RequestParam(name = "page", defaultValue = "0" ) int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "class",required=false) Optional<Long> classId,
            @RequestParam(name = "user", required=false) Optional<Long> userId
            ) {
        return ResponseEntity.ok(levelService.getLevels(PageRequest.of(page, size), classId, userId));
    }
    @PutMapping("/{levelId}")
    public ResponseEntity<CreatedRef> updateLevel(@RequestBody UpdateLevelForm levelForm,
                                           @PathVariable(value="levelId") Long levelId) {
        return ResponseEntity.ok(new CreatedRef("levels/" + levelService.updateLevel(levelForm, levelId)));
    }

}
