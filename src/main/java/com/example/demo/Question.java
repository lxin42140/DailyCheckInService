package com.example.demo;

import java.util.List;

public class Question {
    public String description;
    public List<QuestionOption> options;

    @Override
    public String toString() {
        String temp = this.description + '\n';
        for(int i = 0; i < this.options.size(); i++) {
            temp += "\n" + this.options.get(i).toString() + "\n";
        }
        return temp;
    }
}
