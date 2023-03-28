package com.jakubpszczolka.ScoreLeaderboard.service;

import com.jakubpszczolka.ScoreLeaderboard.model.Score;
import reactor.core.publisher.Flux;

public interface ScoreService {
    Flux<Score> getTopScores();
    void addOrUpdateScore(Score score);
}