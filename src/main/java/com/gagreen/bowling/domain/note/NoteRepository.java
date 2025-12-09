package com.gagreen.bowling.domain.note;

import com.gagreen.bowling.domain.user.UserVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<CenterNoteVo, Long>, NoteCustomRepository {
    Optional<CenterNoteVo> findByCenterIdAndUser(Long centerId, UserVo user);
}
