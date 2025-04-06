package com.monisuea.numbergame;

public class NumberResponse {
    private String status;
    private String message;
    private Integer winnerNumber;
    private double prize;
    private int numStudents;
    private int numParticipants;
    private int numWinners; // 新增字段

    public NumberResponse(String status, String message, Integer winnerNumber, double prize, int numStudents, int numParticipants, int numWinners) {
        this.status = status;
        this.message = message;
        this.winnerNumber = winnerNumber;
        this.prize = prize;
        this.numStudents = numStudents;
        this.numParticipants = numParticipants;
        this.numWinners = numWinners; // 新增构造参数
    }

    // Getter 和 Setter 方法
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getWinnerNumber() {
        return winnerNumber;
    }

    public void setWinnerNumber(Integer winnerNumber) {
        this.winnerNumber = winnerNumber;
    }

    public double getPrize() {
        return prize;
    }

    public void setPrize(double prize) {
        this.prize = prize;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    public int getNumParticipants() {
        return numParticipants;
    }

    public void setNumParticipants(int numParticipants) {
        this.numParticipants = numParticipants;
    }

    public int getNumWinners() { // 新增 Getter 方法
        return numWinners;
    }

    public void setNumWinners(int numWinners) { // 新增 Setter 方法
        this.numWinners = numWinners;
    }
}