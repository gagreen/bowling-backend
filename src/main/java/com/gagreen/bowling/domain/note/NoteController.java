package com.gagreen.bowling.domain.note;

import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.note.dto.NoteWriteDto;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import com.gagreen.bowling.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api")
@Tag(name = "노트", description = "볼링장 노트 관리 API")
public class NoteController {

    private final NoteService noteService;

    @Operation(summary = "볼링장 노트 조회", description = "특정 볼링장에 작성한 노트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CenterNoteVo.class)))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/centers/{centerId}/notes")
    public CenterNoteVo get(@PathVariable Long centerId) {
        UserVo user = SecurityUtil.getCurrentUser();
        log.debug("노트 조회 요청 - userId: {}, centerId: {}", user.getId(), centerId);
        CenterNoteVo result = noteService.getNoteByUser(user, centerId);
        log.debug("노트 조회 완료 - userId: {}, centerId: {}", user.getId(), centerId);
        return result;
    }

    @Operation(summary = "노트 작성", description = "볼링장에 노트를 작성합니다.")
    @ApiResponse(responseCode = "200", description = "작성 성공")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/centers/{centerId}/notes")
    public void add(@PathVariable Long centerId, @Valid @RequestBody NoteWriteDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();
        log.info("노트 작성 요청 - userId: {}, centerId: {}", user.getId(), centerId);

        try {
            noteService.getNoteByUser(user, centerId);
            log.warn("노트 작성 실패 - 이미 존재함. userId: {}, centerId: {}", user.getId(), centerId);
            throw new BadRequestException("이미 노트가 작성된 볼링장입니다.");
        } catch (ResourceNotFoundException e ) {
            // nop
        }

        dto.setCenterId(centerId);
        // 메모 추가
        noteService.addNote(user, dto);
        log.info("노트 작성 완료 - userId: {}, centerId: {}", user.getId(), centerId);

        return;
    }

    @Operation(summary = "노트 수정", description = "작성한 노트를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/centers/{centerId}/notes")
    public void update(@PathVariable Long centerId, @Valid @RequestBody NoteWriteDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();
        log.info("노트 수정 요청 - userId: {}, centerId: {}", user.getId(), centerId);
        
        dto.setCenterId(centerId);
        // 메모 추가
        noteService.updateNote(user, dto);
        log.info("노트 수정 완료 - userId: {}, centerId: {}", user.getId(), centerId);

        return;
    }

    @Operation(summary = "노트 삭제", description = "작성한 노트를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/centers/{centerId}/notes")
    public void delete(@PathVariable Long centerId) {
        UserVo user = SecurityUtil.getCurrentUser();
        log.info("노트 삭제 요청 - userId: {}, centerId: {}", user.getId(), centerId);
        
        // 메모 제거
        noteService.deleteNote(user, centerId);
        log.info("노트 삭제 완료 - userId: {}, centerId: {}", user.getId(), centerId);
    }

    @Operation(summary = "노트 목록 조회", description = "작성한 노트 목록을 페이지네이션으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CenterNoteVo.class)))
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/notes")
    public Page<CenterNoteVo> getFavorites(BowlingCenterSearchDto dto) {
        UserVo user = SecurityUtil.getCurrentUser();
        log.debug("노트 목록 조회 요청 - userId: {}", user.getId());

        // 노트해놓은 볼링장 목록 조회
        Page<CenterNoteVo> notes = noteService.getNotesByUser(user, dto);
        log.debug("노트 목록 조회 완료 - userId: {}, 총 개수: {}", user.getId(), notes.getTotalElements());

        return notes;
    }
}
