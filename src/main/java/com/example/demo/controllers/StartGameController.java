package com.example.demo.controllers;

import com.example.demo.enums.GameType;
import com.example.demo.model.GameSession;
import com.example.demo.service.StartGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/game")
@RequiredArgsConstructor
public class StartGameController {

    private final StartGameService gameService;

    @PostMapping("/start-pve")
    public ResponseEntity<GameSession> startGame(@RequestParam String nickname) {
        return ResponseEntity.ok(gameService.startGame(nickname, GameType.PvE));
    }

}
