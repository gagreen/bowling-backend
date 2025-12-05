package com.gagreen.bowling.domain.staff;

import com.gagreen.bowling.domain.user.UserRepository;
import com.gagreen.bowling.domain.user.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/staff-api/staffs")
public class StaffController {

    private final UserRepository userRepository;

    @GetMapping
    public List<UserVo> getUsers() {
        return userRepository.findAll();
    }
}
