package com.jakubpszczolka.ScoreLeaderboard.controller;

import com.jakubpszczolka.ScoreLeaderboard.model.Score;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScoreController {
    Flux<Score> getTopScores();
    Mono<Void> addOrUpdateScore(Score score);
}
