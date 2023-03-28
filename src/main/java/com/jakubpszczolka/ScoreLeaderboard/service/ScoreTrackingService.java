package com.jakubpszczolka.ScoreLeaderboard.service;

import com.jakubpszczolka.ScoreLeaderboard.model.Score;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ScoreTrackingService implements ScoreService {
    private static final String PATH_TO_SCORE_FILE = "src/main/resources/scores.csv";
    private static final int TOP_SCORES_TO_DISPLAY = 100;

    private final LinkedList<Score> scores = new LinkedList<>();

    public ScoreTrackingService() {
        scores.addAll(readScoresFromCsv());
    }

    @Override
    public Flux<Score> getTopScores() {
        log.info("Getting top 100 scores in descending order");
        return Flux.fromIterable(scores)
                .sort(Comparator.comparing(Score::getScoreValue).reversed())
                .take(TOP_SCORES_TO_DISPLAY);
    }

    @Override
    public void addOrUpdateScore(Score newScore) {
        Optional<Score> scoreOptional = scores.stream()
                .filter(score -> score.getUsername().equals(newScore.getUsername()))
                .findFirst();
        if (scoreOptional.isPresent()) {
            Score score = scoreOptional.get();
            updateScore(newScore, score);
        } else {
            addNewScore(newScore);
        }
    }

    private static void updateScore(Score newScore, Score score) {
        log.info("Updating score " + score + " with " + newScore);
        score.setScoreValue(score.getScoreValue() + newScore.getScoreValue());
        log.info("Result of the update " + score);
    }

    private void addNewScore(Score newScore) {
        log.info("Adding new score " + newScore);
        scores.add(newScore);
    }

    private List<Score> readScoresFromCsv() {
        log.info("Loading scores form csv file");
        List<Score> scoresFromFile = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PATH_TO_SCORE_FILE))) {
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
