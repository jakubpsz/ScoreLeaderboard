package com.jakubpszczolka.ScoreLeaderboard.storage;

import com.jakubpszczolka.ScoreLeaderboard.model.Score;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Repository
@Slf4j
public class ListScoreStorage implements ScoreStorage{
    private final LinkedList<Score> scores = new LinkedList<>();

    private static final String PATH_TO_SCORE_TEST_FILE = "src/main/resources/test_scores.csv";


    @Override
    public void add(int index, Score score) {
        scores.add(index, score);
    }

    @Override
    public List<Score> getAll() {
        return scores;
    }

    @Override
    public Score getFromIndex(int index) {
        return scores.get(index);
    }

    @Override
    public void setAtIndex(int index, Score score) {
        scores.set(index, score);
    }

    @Override
    public void addAtIndex(int index, Score score) {
        scores.add(index, score);
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
