package com.gagreen.bowling.domain.bowling_center;

import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BowlingCenterService {

    private final BowlingCenterRepository repository;

    @GetMapping
    public List<BowlingCenterVo> searchList(BowlingCenterSearchDto dto) {
        Specification<BowlingCenterVo> spec = BowlingCenterSpecification.search(dto);

        return repository.findAll(spec);
    }

    public BowlingCenterVo getItem(Long centerId) {

        BowlingCenterVo center = repository.findById(centerId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 볼링장입니다."));


//        if (/*로그인된 상태라면?*/) {
//            boolean isMyFavorite = favoriteService.isRegistered(centerId,       /*로그인된 유저아이디*/);
//            center.setIsMyFavorite(isMyFavorite);
//
//            String myNote = noteService.getMyNote(centerId, /*로그인된 유저아이디*/);
//            center.setMyNote(myNote);
//        }

        return center;
    }
}
