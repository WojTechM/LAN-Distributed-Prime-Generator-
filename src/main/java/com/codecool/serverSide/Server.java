package com.codecool.serverSide;

import com.codecool.model.EResult;
import com.codecool.model.Task;

import java.util.ArrayList;
import java.util.List;

public class Server {

    private List<Worker> workers = new ArrayList<>();
    private List<Task> availableTasks = new ArrayList<>();

    public Server(int port) {
        WorkerRegistration registration = new WorkerRegistration(this, port);
        Thread registrationThread = new Thread(registration);
        registrationThread.start();
    }

    public void run() throws InterruptedException {
        int potentialPrime = 3;
        boolean isPrime;
        while (!Thread.currentThread().isInterrupted()) {
            if (workers.isEmpty()) {
                waitForWorkers();
            }
            splitWorkIntoTasks(potentialPrime);
            assignTasks();
            isPrime = validateResult();
            if (isPrime) {
                System.out.println(potentialPrime);
            }
            potentialPrime += 2;
        }
    }

    private void splitWorkIntoTasks(int potentialPrime) {
        boolean reachedEnd = false;
        int iteration = 0;
        while (!reachedEnd) {
            int from = iteration * 100;
            int to = from + 100;
            if (to > potentialPrime) {
                to = potentialPrime - 1;
                reachedEnd = true;
            }
            Task task = new Task(potentialPrime, from, to);
            availableTasks.add(task);
        }
    }

    private void assignTasks() {
        for(Worker worker : workers) {
            if (availableTasks.isEmpty()) {
                return;
            }
            worker.assignTask(availableTasks.remove(0));
        }
    }

    private boolean validateResult() {

        while (true) {
            boolean allWorkersFinished = true;
            boolean result = true;
            for(Worker worker : workers) {
                if (isStillWorking(worker)) {
                    allWorkersFinished = false;
                }

                if (numberIsNotPrime(worker)) {
                    result = false;
                }
            }
            if (allWorkersFinished) {
                return result;
            }
        }
    }

    private boolean numberIsNotPrime(Worker worker) {
        return worker.getResult().equals(EResult.Invalid);
    }

    private boolean isStillWorking(Worker worker) {
        return worker.getResult().equals(EResult.InProgress);
    }

    synchronized void addWorker(Worker worker) {
        workers.add(worker);
        this.notify();
    }

    private synchronized void waitForWorkers() throws InterruptedException {
        while (workers.isEmpty()) {
            this.wait();
        }
    }
}
