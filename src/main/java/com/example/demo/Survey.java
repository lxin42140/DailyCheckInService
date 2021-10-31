package com.example.demo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public class Survey {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public java.util.Date dateCreated;

    @JsonFormat(pattern="HH:mm:ss")
    public java.util.Date timeOfCheckIn;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public java.util.Date dateAnswered;

    public List<Question> questions;
    public Integer score;

    @Override
    public String toString() {
        return "Date created: " + this.dateCreated.toString() + "\nTime to ask: " + this.timeOfCheckIn.toString();
    }
}
