package com.gagreen.bowling.domain.note;


import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.user.UserVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoteCustomRepository {
    Page<CenterNoteVo> findUserNotes(UserVo user, BowlingCenterSearchDto dto);

}
