package com.jakubpszczolka.ScoreLeaderboard.controller;

import com.jakubpszczolka.ScoreLeaderboard.model.Score;
import com.jakubpszczolka.ScoreLeaderboard.service.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/scores")
@Slf4j
public class ScoreTrackingController implements ScoreController{
    @Autowired
    private ScoreService scoreService;
    @Override
    @GetMapping("/top")
    public Flux<Score> getTopScores() {
        return scoreService.getTopScores();
    }

    @Override
    @PostMapping("/")
    public Mono<Void> addOrUpdateScore(@RequestBody Score score) {
        scoreService.addOrUpdateScore(score);
        return Mono.empty();
    }
}
