package com.codecool.serverSide;

import com.codecool.model.Task;

import java.util.List;

public class Server {

    private List<Worker> workers;
    private List<Task> availableTasks;

    public Server(int port) {
        WorkerRegistration registration = new WorkerRegistration(this, port);
        Thread registrationThread = new Thread(registration);
        registrationThread.start();
    }

    public void run() {
        int potentialPrime = 3;
        boolean isPrime;
        while (!Thread.currentThread().isInterrupted()) {
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
                to = potentialPrime;
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

        return false;
    }

    void addWorker(Worker worker) {
        workers.add(worker);
    }
}
