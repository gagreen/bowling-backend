package com.gagreen.bowling.domain.note;

import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.note.dto.NoteWriteDto;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import com.gagreen.bowling.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api")
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/centers/{centerId}/notes")
    public CenterNoteVo get(@PathVariable Long centerId) {
        UserVo user = SecurityUtil.getCurrentUser();
        return noteService.getNoteByUser(user, centerId);
    }

    @PostMapping("/centers/{centerId}/notes")
    public void add(@PathVariable Long centerId, @Valid @RequestBody NoteWriteDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();

        try {
            noteService.getNoteByUser(user, centerId);
            throw new BadRequestException("이미 노트가 작성된 볼링장입니다.");
        } catch (ResourceNotFoundException e ) {
            // nop
        }

        dto.setCenterId(centerId);
        // 메모 추가
        noteService.addNote(user, dto);

        return;
    }

    @PutMapping("/centers/{centerId}/notes")
    public void update(@PathVariable Long centerId, @Valid @RequestBody NoteWriteDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();

        dto.setCenterId(centerId);
        // 메모 추가
        noteService.updateNote(user, dto);

        return;
    }

    @DeleteMapping("/centers/{centerId}/notes")
    public void delete(@PathVariable Long centerId) {
        UserVo user = SecurityUtil.getCurrentUser();

        // 메모 제거
        noteService.deleteNote(user, centerId);
    }

    @GetMapping("/notes")
    public Page<CenterNoteVo> getFavorites(BowlingCenterSearchDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();

        // 노트해놓은 볼링장 목록 조회
        Page<CenterNoteVo> notes = noteService.getNotesByUser(user, dto);

        return notes;
    }
}
