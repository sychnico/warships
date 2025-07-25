package com.example.demo.service;

import com.example.demo.enums.GameType;
import com.example.demo.model.GameSession;
import com.example.demo.model.Player;
import com.example.demo.repository.BattleFieldRepository;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class StartGameService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final BattleFieldRepository battleFieldRepository;

    public GameSession startGame(String userName, GameType type) {

        Player player = playerRepository.findByNickname(userName);
        if (player == null) {
            player = new Player();
            player.setNickname(userName);
            player = playerRepository.save(player);
        }
        GameSession gameSession = new GameSession();
        gameSession.setPlayerOne(player);
        gameSession.setType(type);
        gameSession.setCreatedAt(LocalDateTime.now());
        GameSession savedSession = gameRepository.save(gameSession);

        int[][] field = generateBattlefield();

        battleFieldRepository.saveField(userName, field);

        if (type == GameType.PvE){

            String botName = userName + "_bot";
            Player bot = playerRepository.findByNickname(botName);
            if (bot == null) {
                bot = new Player();
                bot.setNickname(botName);
                bot = playerRepository.save(bot);
            }
            gameSession.setPlayerTwo(bot);
            savedSession = gameRepository.save(gameSession);

            int[][] botField = generateBattlefield();
            battleFieldRepository.saveField(botName, botField);

        }

        return gameSession;
    }

    private static int[][] generateBattlefield() {
        int[][] field = new int[10][10];
        int[] shipSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        Random random = new Random();

        for (int size : shipSizes) {
            boolean placed = false;

            while (!placed) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);
                boolean horizontal = random.nextBoolean();

                if (canPlaceShip(field, x, y, size, horizontal)) {
                    placeShip(field, x, y, size, horizontal);
                    placed = true;
                }
            }
        }
        return field;
    }

    private static boolean canPlaceShip(int[][] field, int x, int y, int size, boolean horizontal) {
        // Проверяем границы поля
        if (horizontal && x + size > 10) return false;
        if (!horizontal && y + size > 10) return false;

        // Проверяем клетки и соседние клетки
        for (int i = -1; i <= size; i++) {
            for (int j = -1; j <= 1; j++) {
                int checkX = horizontal ? x + i : x + j;
                int checkY = horizontal ? y + j : y + i;

                if (checkX >= 0 && checkX < 10 && checkY >= 0 && checkY < 10) {
                    if (field[checkY][checkX] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void placeShip(int[][] field, int x, int y, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            if (horizontal) {
                field[y][x + i] = 1;
            } else {
                field[y + i][x] = 1;
            }
        }
    }

}
