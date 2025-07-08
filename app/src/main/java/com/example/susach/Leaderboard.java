package com.example.susach;

public class Leaderboard {
    private String name;
    private int grade;
    private float grade10;

    public Leaderboard() {

    }
    public Leaderboard(String name, int grade, float grade10) {
        this.name = name;
        this.grade = grade;
        this.grade10 = grade10;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public float getGrade10() {
        return grade10;
    }

    public void setGrade10(float grade10) {
        this.grade10 = grade10;
    }

}


