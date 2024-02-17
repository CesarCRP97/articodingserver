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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<IUserDetail> getUser(@PathVariable(value = "userId") Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @GetMapping
    public ResponseEntity<Page<IUser>> getUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "class", required = false) Optional<Long> clase,
            @RequestParam(name = "teacher", defaultValue = "false") boolean teacher,
            @RequestParam(name = "title", required = false) Optional<String> title) {

        return ResponseEntity.ok(userService.geAllUser(PageRequest.of(page, size), clase, teacher, title));

    }

    @PostMapping("/students")
    public ResponseEntity<CreatedRef> registerStudent(@RequestBody UserForm user) {
        return ResponseEntity.ok(new CreatedRef("users/" + userService.registerStudent(user)));
    }

    @PostMapping
    public ResponseEntity<CreatedRef> saveUser(@RequestBody UserForm user) {
        return ResponseEntity.ok(new CreatedRef("users/" + userService.save(user)));
    }

    @PostMapping("/list")
    public ResponseEntity<CreatedRef> saveUsers(@RequestBody List<UserForm> users) {
        userService.saveAll(users);
        return ResponseEntity.ok(new CreatedRef("users"));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<CreatedRef> updateUser(@PathVariable(value = "userId") Long userId,
                                                 @RequestBody UpdateUserForm updateUserForm) {
        return ResponseEntity.ok(new CreatedRef("users/" + userService.update(userId, updateUserForm)));
    }


}
