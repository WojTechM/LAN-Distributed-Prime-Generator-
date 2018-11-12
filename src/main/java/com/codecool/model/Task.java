package com.codecool.model;

public class Task {

    private int potentialPrime;
    private int from;
    private int to;

    public Task(int potentialPrime, int from, int to) {
        this.potentialPrime = potentialPrime;
        this.from = from;
        this.to = to;
    }

    public int getPotentialPrime() {
        return potentialPrime;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
