package com.jakubpszczolka.ScoreLeaderboard.storage;

import com.jakubpszczolka.ScoreLeaderboard.model.Score;

import java.util.List;

public interface ScoreStorage {
    void add(int index, Score score);
    List<Score> getAll();

    Score getFromIndex(int index);

    void setAtIndex(int index, Score score);

    void addAtIndex(int index, Score score);
}