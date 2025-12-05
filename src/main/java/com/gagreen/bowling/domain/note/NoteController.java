package com.gagreen.bowling.domain.note;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterRepository;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api/my-notes")
public class NoteController {

//    private final BowlingCenterRepository repository;
//
//    @GetMapping
//    public List<BowlingCenterVo> searchList() {
//        // 이름, 주소 등으로 검색하기
//        return repository.findAll();
//    }
}
