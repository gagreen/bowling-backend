package com.gagreen.bowling.domain.bowling_center;

import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterUpdateDto;
import com.gagreen.bowling.domain.favorite.FavoriteService;
import com.gagreen.bowling.domain.note.CenterNoteVo;
import com.gagreen.bowling.domain.note.NoteService;
import com.gagreen.bowling.domain.staff.StaffRepository;
import com.gagreen.bowling.domain.staff.StaffVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.exception.AuthenticationCredentialsNotFoundException;
import com.gagreen.bowling.exception.BadRequestException;
import com.gagreen.bowling.exception.ResourceNotFoundException;
import com.gagreen.bowling.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BowlingCenterService {

    private final BowlingCenterRepository repository;
    private final FavoriteService favoriteService;
    private final NoteService noteService;
    private final StaffRepository staffRepository;

    @GetMapping
    public Page<BowlingCenterVo> searchList(BowlingCenterSearchDto dto) {
        return repository.search(dto);
    }

    public BowlingCenterVo getItem(Long centerId) {
        return getItem(centerId, true);
    }

    public BowlingCenterVo getItem(Long centerId, boolean isWithUserData) {

        BowlingCenterVo center = repository.findById(centerId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 볼링장입니다."));

        if (isWithUserData) {
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
        }

        return center;
    }

    /**
     * 현재 로그인한 직원이 배정된 센터를 조회합니다.
     * @return 배정된 센터
     * @throws ResourceNotFoundException 직원이 존재하지 않는 경우
     * @throws BadRequestException 센터에 배정되지 않은 경우
     */
    public BowlingCenterVo getAssignedCenter() {
        StaffVo staff = SecurityUtil.getCurrentStaff();
        StaffVo persistedStaff = staffRepository.findById(staff.getId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 스태프입니다."));

        if (persistedStaff.getCenter() == null) {
            throw new BadRequestException("센터에 배정되지 않은 스태프입니다.");
        }

        return persistedStaff.getCenter();
    }

    /**
     * 현재 로그인한 직원이 배정된 볼링장 정보를 수정합니다.
     * @param dto 수정 요청 DTO
     * @return 수정된 볼링장 정보
     * @throws ResourceNotFoundException 직원이 존재하지 않거나 볼링장이 존재하지 않는 경우
     * @throws BadRequestException 센터에 배정되지 않은 경우
     */
    @Transactional
    public BowlingCenterVo updateAssignedCenter(BowlingCenterUpdateDto dto) {
        StaffVo staff = SecurityUtil.getCurrentStaff();
        StaffVo persistedStaff = staffRepository.findById(staff.getId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 스태프입니다."));

        if (persistedStaff.getCenter() == null) {
            throw new BadRequestException("센터에 배정되지 않은 스태프입니다.");
        }

        BowlingCenterVo center = persistedStaff.getCenter();

        if (dto.getName() != null) {
            center.setName(dto.getName());
        }
        if (dto.getState() != null) {
            center.setState(dto.getState());
        }
        if (dto.getCity() != null) {
            center.setCity(dto.getCity());
        }
        if (dto.getDistrict() != null) {
            center.setDistrict(dto.getDistrict());
        }
        if (dto.getDetailAddress() != null) {
            center.setDetailAddress(dto.getDetailAddress());
        }
        if (dto.getTelNumber() != null) {
            center.setTelNumber(dto.getTelNumber());
        }
        if (dto.getLaneCount() != null) {
            center.setLaneCount(dto.getLaneCount());
        }
        center.setUpdatedAt(Instant.now());

        return repository.save(center);
    }

    /**
     * 볼링장을 삭제합니다.
     * @param centerId 볼링장 ID
     * @throws ResourceNotFoundException 볼링장이 존재하지 않는 경우
     */
    @Transactional
    public void delete(Long centerId) {
        BowlingCenterVo center = repository.findById(centerId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 볼링장입니다."));

        repository.delete(center);
    }
}
