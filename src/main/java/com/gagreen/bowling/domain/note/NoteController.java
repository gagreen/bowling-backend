package com.gagreen.bowling.domain.note;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
