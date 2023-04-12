package com.jakubpszczolka.ScoreLeaderboard.service;

import com.jakubpszczolka.ScoreLeaderboard.model.Score;
import com.jakubpszczolka.ScoreLeaderboard.storage.ScoreStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class ScoreTrackingService implements ScoreService {
    private static final int TOP_SCORES_TO_DISPLAY = 100;

    @Autowired
    private ScoreStorage scoreStorage;


    @Override
    public Flux<Score> getTopScores() {
        log.info("Getting top 100 scores in descending order");
        return Flux.fromIterable(scoreStorage.getAll())
                .take(TOP_SCORES_TO_DISPLAY);
    }

    @Override
    public void addOrUpdateScore(Score newScore) {
        for (int i = 0; i < scoreStorage.getAll().size(); i++) {
            Score existingScore = scoreStorage.getFromIndex(i);
            if (doseUsernameMatch(newScore, existingScore)) {
                updateScore(newScore, i);
                return;
            }
        }
        addNewScore(newScore);
    }

    private boolean doseUsernameMatch(Score newScore, Score existingScore) {
        return existingScore.getUsername().equals(newScore.getUsername());
    }

    private void updateScore(Score newScore, int index) {
        Score updatedScore = updateScoreValue(newScore, index);
        if (newScore.getScoreValue() > 0) {
            insertPositiveScoreUpdate(index, updatedScore);
        } else {
            insertNegativeScoreUpdate(index, updatedScore);
        }
    }

    private Score updateScoreValue(Score newScore, int index) {
        Score existingScore = scoreStorage.deleteFromIndex(index);
        log.info("Updating score " + existingScore + " with " + newScore + " at index " + index);
        existingScore.addToScore(newScore.getScoreValue());
        return existingScore;
    }

    private void insertPositiveScoreUpdate(int index, Score updatedScore) {
        for (int i = index - 1; i >= 0; i--) {
            Score nextScore = scoreStorage.getFromIndex(i);
            if (updatedScore.getScoreValue() < nextScore.getScoreValue()) {
                log.info("Result of the update " + updatedScore + " under new index " + (i + 1));
                scoreStorage.addAtIndex(i + 1, updatedScore);
                break;
            } else if (i == 0) {
                log.info("Result of the update " + updatedScore + " under new index " + i);
                scoreStorage.addAtIndex(i, updatedScore);

            }
        }
    }

    private void insertNegativeScoreUpdate(int index, Score updatedScore) {
        for (int i = index; i < scoreStorage.getAll().size(); i++) {
            Score nextScore = scoreStorage.getFromIndex(i);
            if (updatedScore.getScoreValue() >= nextScore.getScoreValue()) {
                log.info("Result of the update " + updatedScore + " under new index " + (i));
                scoreStorage.addAtIndex(i, updatedScore);
                break;
            } else if (i == scoreStorage.getAll().size() - 1) {
                scoreStorage.add(updatedScore);
                log.info("Result of the update " + updatedScore + " under new index " + (i + 1) );
                break;
            }
        }
    }

    private void addNewScore(Score newScore) {
        if (newScore.getScoreValue() < scoreStorage.getFromIndex(scoreStorage.getAll().size() - 1).getScoreValue()) {
            log.info("Adding new score at the bottom of the list");
            scoreStorage.add(newScore);
            return;
        }
        for (int i = 0; i < scoreStorage.getAll().size(); i++) {
            if (newScore.getScoreValue() >= scoreStorage.getFromIndex(i).getScoreValue()) {
                scoreStorage.addAtIndex(i, newScore);
                log.info("Adding new score " + newScore + " at index " + i);
                break;
            }
        }
    }
}
