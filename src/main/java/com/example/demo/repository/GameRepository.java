package com.example.demo.repository;

import com.example.demo.enums.GameStatus;
import com.example.demo.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByStatus(GameStatus status);

    @Query(value = "select * from game_sessions gs where gs.status = 'WAITING_FOR_PLAYER'" +
            "and gs.type = 'PvP'", nativeQuery = true)
    List<GameSession> getAvailableSessions();

    @Query(value = "SELECT g.playerTwo.nickname FROM game_sessions g WHERE g.id = :sessionId", nativeQuery = true)
    String findSecondPlayerNicknameBySessionId(@Param("sessionId") Long sessionId);
}
