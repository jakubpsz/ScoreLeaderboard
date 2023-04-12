package com.jakubpszczolka.ScoreLeaderboard.service;

import com.jakubpszczolka.ScoreLeaderboard.model.Score;
import com.jakubpszczolka.ScoreLeaderboard.storage.ListScoreStorage;
import com.jakubpszczolka.ScoreLeaderboard.storage.ScoreStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTest {

    @Spy
    private ListScoreStorage scoreStorage;
    @InjectMocks
    private ScoreTrackingService scoreService;

    @Captor
    private ArgumentCaptor<Score> scoreCaptor;
    @Captor
    private ArgumentCaptor<Integer> indexCaptor;


    @BeforeEach
    void setUp() {
        scoreStorage.add(new Score("Wojtek", 900));
        scoreStorage.add(new Score("John", 790));
        scoreStorage.add(new Score("Kate", 300));
        scoreStorage.add(new Score("Tsunami", 10));
    }
    @Test
    void getTopScores() {
        Flux<Score> topScores = scoreService.getTopScores();
        List<Score> result = topScores.toStream().toList();

        assertEquals("Wojtek", result.get(0).getUsername());
        assertEquals(900, result.get(0).getScoreValue());
        assertEquals("John", result.get(1).getUsername());
        assertEquals(790, result.get(1).getScoreValue());
        assertEquals("Kate", result.get(2).getUsername());
        assertEquals(300, result.get(2).getScoreValue());
        assertEquals("Tsunami", result.get(3).getUsername());
        assertEquals(10, result.get(3).getScoreValue());
    }

    @Test
    void addNewScore_atTheTopOfTheTable() {
        Score score = new Score("Tom", 1000);
        scoreService.addOrUpdateScore(score);

        verify(scoreStorage).addAtIndex(0, score);
        assertEquals("Tom", scoreStorage.getFromIndex(0).getUsername());
        assertEquals(1000, scoreStorage.getFromIndex(0).getScoreValue());
        assertEquals(5, scoreStorage.getAll().size());
    }

    @Test
    void addNewScore_atTheTopOfTheTable_edge() {
        Score score = new Score("Tom", 900);
        scoreService.addOrUpdateScore(score);

        verify(scoreStorage).addAtIndex(0, score);
        assertEquals("Tom", scoreStorage.getFromIndex(0).getUsername());
        assertEquals(900, scoreStorage.getFromIndex(0).getScoreValue());
        assertEquals(5, scoreStorage.getAll().size());
    }

    @Test
    void addNewScore_atTheBottomOfTheTable() {
        Score score = new Score("Tom", 5);
        scoreService.addOrUpdateScore(score);
        scoreStorage.getAll().forEach(System.out::println);

        verify(scoreStorage).add(score);
        assertEquals("Tom", scoreStorage.getFromIndex(4).getUsername());
        assertEquals(5, scoreStorage.getFromIndex(4).getScoreValue());
        assertEquals(5, scoreStorage.getAll().size());
    }

    @Test
    void addNewScore_atTheMiddleOfTheTable() {
        Score score = new Score("Tom", 600);
        scoreService.addOrUpdateScore(score);

        verify(scoreStorage).addAtIndex(2, score);
        assertEquals("Tom", scoreStorage.getFromIndex(2).getUsername());
        assertEquals(600, scoreStorage.getFromIndex(2).getScoreValue());
        assertEquals(5, scoreStorage.getAll().size());
    }

    @Test
    void addNewScore_atTheBottomOfTheTable_edge() {
        Score score = new Score("Tom", 10);
        scoreService.addOrUpdateScore(score);

        verify(scoreStorage).addAtIndex(3, score);
        assertEquals("Tom", scoreStorage.getFromIndex(3).getUsername());
        assertEquals(10, scoreStorage.getFromIndex(3).getScoreValue());
        assertEquals(5, scoreStorage.getAll().size());
    }

    @Test
    void updateScore_atTheMiddleOfTheTable_positive() {
        Score score = new Score("Kate", 550);
        scoreService.addOrUpdateScore(score);

        verify(scoreStorage).addAtIndex(eq(1), scoreCaptor.capture());
        assertEquals("Kate", scoreCaptor.getValue().getUsername());
        assertEquals(850, scoreCaptor.getValue().getScoreValue());
        assertEquals("Kate", scoreStorage.getFromIndex(1).getUsername());
        assertEquals(850, scoreStorage.getFromIndex(1).getScoreValue());
        assertEquals(4, scoreStorage.getAll().size());
    }

    @Test
    void updateScore_forTopSpotOfTheTable_positive() {
        Score score = new Score("Kate", 900);
        scoreService.addOrUpdateScore(score);

        verify(scoreStorage).addAtIndex(eq(0), scoreCaptor.capture());
        assertEquals("Kate", scoreCaptor.getValue().getUsername());
        assertEquals(1200, scoreCaptor.getValue().getScoreValue());
        assertEquals("Kate", scoreStorage.getFromIndex(0).getUsername());
        assertEquals(1200, scoreStorage.getFromIndex(0).getScoreValue());
        assertEquals(4, scoreStorage.getAll().size());
    }

    @Test
    void updateScore_fromBottomOfTheTable_positive() {
        Score score = new Score("Tsunami", 300);
        scoreService.addOrUpdateScore(score);

        verify(scoreStorage).addAtIndex(eq(2), scoreCaptor.capture());
        assertEquals("Tsunami", scoreCaptor.getValue().getUsername());
        assertEquals(310, scoreCaptor.getValue().getScoreValue());
        assertEquals("Tsunami", scoreStorage.getFromIndex(2).getUsername());
        assertEquals(310, scoreStorage.getFromIndex(2).getScoreValue());
        assertEquals(4, scoreStorage.getAll().size());
    }

    @Test
    void updateScore_fromBottomOfTheTable_positive_edge() {
        Score score = new Score("Tsunami", 290);
        scoreService.addOrUpdateScore(score);

        verify(scoreStorage).addAtIndex(eq(2), scoreCaptor.capture());
        assertEquals("Tsunami", scoreCaptor.getValue().getUsername());
        assertEquals(300, scoreCaptor.getValue().getScoreValue());
        assertEquals("Tsunami", scoreStorage.getFromIndex(2).getUsername());
        assertEquals(300, scoreStorage.getFromIndex(2).getScoreValue());
        assertEquals(4, scoreStorage.getAll().size());
    }

    @Test
    void updateScore_fromBottomOfTheTable_negative_edge() {
        Score score = new Score("Kate", -290);
        scoreService.addOrUpdateScore(score);

        verify(scoreStorage).addAtIndex(eq(2), scoreCaptor.capture());
        assertEquals("Kate", scoreCaptor.getValue().getUsername());
        assertEquals(10, scoreCaptor.getValue().getScoreValue());
        assertEquals("Kate", scoreStorage.getFromIndex(2).getUsername());
        assertEquals(10, scoreStorage.getFromIndex(2).getScoreValue());
        assertEquals(4, scoreStorage.getAll().size());
    }

    @Test
    void updateScore_fromTopOfTheTable_negative_() {
        Score score = new Score("Wojtek", -895);
        scoreService.addOrUpdateScore(score);

        assertEquals("Wojtek", scoreStorage.getFromIndex(3).getUsername());
        assertEquals(5, scoreStorage.getFromIndex(3).getScoreValue());
        assertEquals(4, scoreStorage.getAll().size());
    }
}