package com.gagreen.bowling.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api/users")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public List<UserVo> getUsers() {
        return userRepository.findAll();
    }



}
