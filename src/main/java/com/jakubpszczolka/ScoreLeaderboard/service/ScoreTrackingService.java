package com.jakubpszczolka.ScoreLeaderboard.service;

import com.jakubpszczolka.ScoreLeaderboard.model.Score;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class ScoreTrackingService implements ScoreService {
    private static final String PATH_TO_SCORE_FILE = "src/main/resources/scores.csv";
    private static final String PATH_TO_SCORE_TEST_FILE = "src/main/resources/test_scores.csv";

    private static final int TOP_SCORES_TO_DISPLAY = 100;

    private final LinkedList<Score> scores = new LinkedList<>();


    @Override
    public Flux<Score> getTopScores() {
        log.info("Getting top 100 scores in descending order");
        return Flux.fromIterable(scores)
                .take(TOP_SCORES_TO_DISPLAY);
    }

    @Override
    public void addOrUpdateScore(Score newScore) {
        for (int i = 0; i < scores.size(); i++) {
            Score existingScore = scores.get(i);
            if (doseUsernameMatch(newScore, existingScore)) {
                updateScore(newScore, i);
                return;
            }
        }
        addNewScore(newScore);
    }

    private static boolean doseUsernameMatch(Score newScore, Score existingScore) {
        return existingScore.getUsername().equals(newScore.getUsername());
    }

    private void updateScore(Score newScore, int index) {
        updateScoreValue(newScore, index);
        if (newScore.getScoreValue() > 0) {
            insertPositiveScoreUpdate(index);
        } else {
            insertNegativeScoreUpdate(index);
        }
    }

    private void updateScoreValue(Score newScore, int index) {
        Score existingScore = scores.get(index);
        log.info("Updating score " + existingScore + " with " + newScore + " at index " + index);
        existingScore.addToScore(newScore.getScoreValue());
        scores.set(index, existingScore);
    }

    private void insertPositiveScoreUpdate(int index) {
        for (int i = index - 1; i >= 0; i--) {
            Score updatedScore = scores.get(i + 1);
            Score nextScore = scores.get(i);
            if (updatedScore.getScoreValue() < nextScore.getScoreValue()) {
                log.info("Result of the update " + updatedScore + " under new index " + (i + 1));
                break;
            } else {
                if (i == 0) {
                    log.info("Result of the update " + updatedScore + " under new index " + i);
                }
                scores.set(i, updatedScore);
                scores.set(i + 1, nextScore);
            }
        }
    }

    private void insertNegativeScoreUpdate(int index) {
        for (int i = index + 1; i < scores.size(); i++) {
            Score updatedScore = scores.get(i - 1);
            Score nextScore = scores.get(i);
            if (updatedScore.getScoreValue() >= nextScore.getScoreValue()) {
                log.info("Result of the update " + updatedScore + " under new index " + (i - 1));
                break;
            } else {
                if (i == scores.size() - 1) {
                    log.info("Result of the update " + updatedScore + " under new index " + i);
                }
                scores.set(i, updatedScore);
                scores.set(i - 1, nextScore);
            }
        }
    }

    private void addNewScore(Score newScore) {
        for (int i = 0; i < scores.size(); i++) {
            if (newScore.getScoreValue() > scores.get(i).getScoreValue()) {
                scores.add(i, newScore);
                log.info("Adding new score " + newScore + " at index " + i);
                break;
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    private void setUp() {
        scores.addAll(readScoresFromCsv());
        scores.sort(Comparator.comparing(Score::getScoreValue).reversed());
        log.info("Set up complete");
    }

    private List<Score> readScoresFromCsv() {
        log.info("Loading scores form csv file");
        List<Score> scoresFromFile = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PATH_TO_SCORE_TEST_FILE))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                mapFileToScores(line, scoresFromFile);
            }
            log.info("Loaded scores from csv file successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scoresFromFile;
    }

    private void mapFileToScores(String line, List<Score> scoresFromFile) {
        String[] values = line.split(",");
        scoresFromFile.add(new Score(values[0], Integer.parseInt(values[1])));
    }
}
