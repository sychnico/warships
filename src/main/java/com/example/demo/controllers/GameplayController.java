package com.example.demo.controllers;

import com.example.demo.dto.GameResultDto;
import com.example.demo.service.GameplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/gameplay")
public class GameplayController {

    private final GameplayService gameService;

    @PostMapping(path = "/shoot")
    public ResponseEntity<GameResultDto> makeMove(@RequestParam Long session,
                                                  @RequestParam String nickname,
                                                  @RequestParam int x,
                                                  @RequestParam int y) {
        try {
            return ResponseEntity.ok(gameService.shoot(session, nickname, x, y));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
