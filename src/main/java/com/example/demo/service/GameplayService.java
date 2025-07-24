package com.example.demo.service;

import com.example.demo.dto.GameResultDto;
import com.example.demo.enums.MoveResult;
import com.example.demo.model.GameSession;
import com.example.demo.model.Player;
import com.example.demo.repository.BattleFieldRepository;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GameplayService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final BattleFieldRepository battleFieldRepository;

    public GameResultDto shoot(Long sessionId, String nickname, int x, int y) {

        String secondNickname = gameRepository.findSecondPlayerNicknameBySessionId(sessionId);
        MoveResult moveResult = null;

        //проверки
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("Nickname cannot be empty");
        }
        if (x < 0 || x > 9 || y < 0 || y > 9) {
            throw new IllegalArgumentException("Coordinates must be between 0 and 9");
        }


        //удар игрока
        int cellCode = battleFieldRepository.get(secondNickname, x, y);

        if (cellCode > 1) {
            //уже стрелял
            throw new IllegalStateException("Cell already attacked");
        }
        else {
            if (cellCode == 0){
                //мимо
                moveResult = MoveResult.MISS;
            }
            else if (cellCode == 1){
                //попал
                moveResult = MoveResult.HIT;
            }
            //пометить
            battleFieldRepository.set(secondNickname, x, y, cellCode+2);
        }

        //проверка победы игрока
        if (!battleFieldRepository.hasAliveShips(secondNickname)){
            //победа
            finishGameWithWinner(sessionId, nickname);
            return new GameResultDto(MoveResult.WIN, true, nickname, -1, -1);
        }

        //удар бота
        int botHitX = 0;
        int botHitY = 0;

        // выбор клетки
        for (int i = 0; i < 10; i++){
            for (int j = 0; j < 10; j++){
                if (battleFieldRepository.get(nickname, i, j) == 1){
                    botHitX = i;
                    botHitY = j;
                }
            }
        }

        cellCode = battleFieldRepository.get(nickname, botHitX, botHitY);
        battleFieldRepository.set(nickname, botHitX, botHitY, cellCode+2);

        //проверка победы бота
        if (!battleFieldRepository.hasAliveShips(nickname)){
            //поражение
            finishGameWithWinner(sessionId, secondNickname);
            return new GameResultDto(MoveResult.LOSE, true, "bot", botHitX, botHitY);
        }

        return new GameResultDto(moveResult, false, null, botHitX, botHitY);
    }

    @Transactional
    private void finishGameWithWinner(Long gameSessionId, String winnerNickname) {
        GameSession gameSession = gameRepository.findById(gameSessionId)
                .orElseThrow(() -> new EntityNotFoundException("Game session not found"));

        // Проверяем, что игрок с таким никнеймом существует
        Player winner = playerRepository.findByNickname(winnerNickname);
        if (winner == null) {
            throw new EntityNotFoundException("Player not found");
        }

        // Проверяем, что победитель является участником игры
        if (!winner.equals(gameSession.getPlayerOne()) &&
                !winner.equals(gameSession.getPlayerTwo())) {
            throw new IllegalArgumentException("The winner is not a participant of this game");
        }

        gameSession.finishGame(winner);
        gameRepository.save(gameSession);
    }

}
