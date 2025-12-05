package com.gagreen.bowling.domain.note;

import com.gagreen.bowling.domain.favorite.FavoriteCenterVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<CenterNoteVo, Long> {

}
