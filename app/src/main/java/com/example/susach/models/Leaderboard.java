package com.example.susach.models;

public class Leaderboard {
    private String name;
    private int grade;
    private float grade10;
    private int totalTime; // Thời gian làm bài (giây)

    public Leaderboard() {

    }
    public Leaderboard(String name, int grade, float grade10) {
        this.name = name;
        this.grade = grade;
        this.grade10 = grade10;
        this.totalTime = 0;
    }
    
    public Leaderboard(String name, int grade, float grade10, int totalTime) {
        this.name = name;
        this.grade = grade;
        this.grade10 = grade10;
        this.totalTime = totalTime;
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

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    // Format thời gian từ giây sang mm:ss hoặc hh:mm:ss
    public String getFormattedTime() {
        int hours = totalTime / 3600;
        int minutes = (totalTime % 3600) / 60;
        int seconds = totalTime % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}


