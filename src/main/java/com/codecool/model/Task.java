package com.codecool.model;

import java.io.Serializable;

public class Task implements Serializable {

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

    @Override
    public String toString() {
        return "Task{" +
                "potentialPrime=" + potentialPrime +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
