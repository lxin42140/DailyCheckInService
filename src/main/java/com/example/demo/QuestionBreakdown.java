package com.example.demo;

import java.util.HashMap;

public class QuestionBreakdown {
    Question question;
    HashMap<String, Integer> scoreBreakdown;

    public Question getQuestion() {
        return this.question;
    }

    public HashMap<String, Integer> getScoreBreakDown() {
        return this.scoreBreakdown;
    }
}
