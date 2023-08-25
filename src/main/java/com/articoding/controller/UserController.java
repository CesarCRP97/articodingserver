package com.articoding.controller;

import com.articoding.model.UserForm;
import com.articoding.model.in.IUser;
import com.articoding.model.in.IUserDetail;
import com.articoding.model.in.UpdateUserForm;
import com.articoding.model.rest.CreatedRef;
import com.articoding.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<IUserDetail> getUser(@PathVariable(value="userId") Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @GetMapping
    public ResponseEntity<Page<IUser>> getUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "class", required=false) Optional<Long> clase,
            @RequestParam(name = "teacher", defaultValue = "false") boolean teacher)  {

        return ResponseEntity.ok(userService.geAllUser(PageRequest.of(page,size), clase, teacher));

    }

    @PostMapping
    public ResponseEntity<CreatedRef> saveUser(@RequestBody UserForm user) {
        return ResponseEntity.ok(new CreatedRef("users/" +userService.save(user)));
    }

    @PostMapping("/list")
    public ResponseEntity<CreatedRef> saveUsers(@RequestBody List<UserForm> users)  {
        userService.saveAll(users);
        return ResponseEntity.ok(new CreatedRef("users"));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<CreatedRef> updateUser(@PathVariable(value="userId") Long userId,
                                                  @RequestBody UpdateUserForm updateUserForm) {
        return ResponseEntity.ok(new CreatedRef("users/" + userService.update(userId, updateUserForm)));
    }


}
