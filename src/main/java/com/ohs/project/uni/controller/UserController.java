package com.ohs.project.uni.controller;

import com.ohs.project.uni.dto.*;
import com.ohs.project.uni.entity.User;
import com.ohs.project.uni.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserAndTokenDTO> login(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(userService.login(loginDTO));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> save(@RequestBody CreateDTO createDTO) {
        userService.save(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<User> edit(@RequestBody EditDTO editDTO,
                                     Principal principal) {
        return ResponseEntity.ok().body(userService.edit(principal.getName(), editDTO));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> edit(@RequestBody PasswordDTO passwordDTO,
                                     Principal principal) {
        userService.editPassword(principal.getName(), passwordDTO);
        return ResponseEntity.ok().build();
    }
}
