package com.gagreen.bowling.domain.bowling_center;

import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.favorite.FavoriteService;
import com.gagreen.bowling.domain.note.CenterNoteVo;
import com.gagreen.bowling.domain.note.NoteService;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.exception.AuthenticationCredentialsNotFoundException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import com.gagreen.bowling.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BowlingCenterService {

    private final BowlingCenterRepository repository;
    private final FavoriteService favoriteService;
    private final NoteService noteService;

    @GetMapping
    public Page<BowlingCenterVo> searchList(BowlingCenterSearchDto dto) {
        return repository.search(dto);
    }

    public BowlingCenterVo getItem(Long centerId) {

        BowlingCenterVo center = repository.findById(centerId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 볼링장입니다."));

        // 로그인된 사용자의 경우, 즐겨찾기 여부와 메모 정보를 추가
        try {
            UserVo user = SecurityUtil.getCurrentUser();
            boolean isMyFavorite = favoriteService.isRegistered(user, centerId);
            center.setIsMyFavorite(isMyFavorite);

            CenterNoteVo myNote = noteService.getNoteByUser(user, centerId);
            center.setMyNote(myNote.getContent());

        } catch (AuthenticationCredentialsNotFoundException e) {
            // nop
        }

        return center;
    }
}
