package com.example.demo;

public class QuestionOption {
    public String optionName;
    public Integer optionScore;
    public Boolean isSelected;

    @Override
    public String toString() {
        return this.optionName + ": " + this.optionScore;
    }
}
