package com.gagreen.bowling.domain.note;

import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.note.dto.NoteWriteDto;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
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

    @PostMapping("/centers/{centerId}/notes")
    public void add(@AuthenticationPrincipal UserVo user,
                    @PathVariable Long centerId, @Valid @RequestBody NoteWriteDto dto) {
        try {
            noteService.getNoteByUser(user, centerId);
            throw new BadRequestException("이미 즐겨찾기에 추가된 볼링장입니다.");
        } catch (ResourceNotFoundException e ) {
            // nop
        }

        dto.setCenterId(centerId);
        // 메모 추가
        noteService.addNote(user, dto);

        return;
    }

    @PutMapping("/centers/{centerId}/notes")
    public void update(@AuthenticationPrincipal UserVo user,
                    @PathVariable Long centerId, @Valid @RequestBody NoteWriteDto dto) {
        dto.setCenterId(centerId);
        // 메모 추가
        noteService.updateNote(user, dto);

        return;
    }

    @DeleteMapping("/centers/{centerId}/notes")
    public void delete(@AuthenticationPrincipal UserVo user,
                       @PathVariable Long centerId) {
        // 메모 제거
        noteService.deleteNote(user, centerId);
    }

    @GetMapping("/notes")
    public Page<CenterNoteVo> getFavorites(@AuthenticationPrincipal UserVo user, BowlingCenterSearchDto dto) {
        // 즐겨찾기한 볼링장 목록 조회
        Page<CenterNoteVo> notes = noteService.getNotesByUser(user, dto);

        return notes;
    }
}
