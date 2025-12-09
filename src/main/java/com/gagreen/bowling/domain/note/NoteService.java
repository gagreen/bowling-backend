package com.gagreen.bowling.domain.note;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterRepository;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.note.dto.NoteWriteDto;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final BowlingCenterRepository bowlingCenterRepository;

    public CenterNoteVo getNoteByUser(UserVo user, Long centerId) {
        return noteRepository.findByCenterIdAndUser(centerId, user)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 메모입니다."));
    }

    public void addNote(UserVo user, NoteWriteDto dto) {

        BowlingCenterVo bowlingCenter =  bowlingCenterRepository.findById(dto.getCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 볼링장입니다."));

        CenterNoteVo note = CenterNoteVo.builder()
                .center(bowlingCenter)
                .user(user)
                .content(dto.getContent())
                .build();

        noteRepository.save(note);
        return;
    }

    public void updateNote(UserVo user, NoteWriteDto dto) {
        CenterNoteVo item = getNoteByUser(user, dto.getCenterId());
        item.setContent(dto.getContent());

        noteRepository.save(item);
        return;
    }

    public void deleteNote(UserVo user, Long centerId) {
        CenterNoteVo item = getNoteByUser(user, centerId);

        noteRepository.delete(item);

        return;
    }

    public Page<CenterNoteVo> getNotesByUser(UserVo user, BowlingCenterSearchDto dto) {
        return noteRepository.findUserNotes(user, dto);
    }
}
