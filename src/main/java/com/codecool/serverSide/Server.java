package com.codecool.serverSide;

import com.codecool.model.EResult;
import com.codecool.model.Task;
import com.codecool.serverSide.exceptions.LackOfWorkersException;

import java.util.*;

public class Server {

    private List<Worker> availableWorkers = Collections.synchronizedList(new ArrayList<>());
    private Set<Worker> currentlyWorking = new HashSet<>();
    private List<Task> availableTasks = new ArrayList<>();

    public Server(int port) {
        WorkerRegistration registration = new WorkerRegistration(this, port);
        Thread registrationThread = new Thread(registration);
        registrationThread.start();
    }

    public void run() throws InterruptedException {
        int potentialPrime = 3;
        while (!Thread.currentThread().isInterrupted()) {
            boolean isPrime = isPrime(potentialPrime);
            printResult(potentialPrime, isPrime);
            potentialPrime += 2;
        }
    }

    private void printResult(int potentialPrime, boolean isPrime) {
        if (isPrime) {
            System.out.println(potentialPrime + " is Prime!");
        } else {
            System.out.println(potentialPrime + " is not Prime!");
        }
    }

    private boolean isPrime(int potentialPrime) throws InterruptedException {
        if (availableWorkers.isEmpty()) {
            waitForWorkers();
        }
        boolean isPrime = false;
        splitWorkIntoTasks(potentialPrime);
        while (!availableTasks.isEmpty()) {
            prepareCurrentlyWorking();
            try {
                assignTasks();
                isPrime = validateResult();
                if (!isPrime) {
                    availableTasks.clear();
                }
            } catch (LackOfWorkersException e) {
                removeDisconnectedWorkers();
                waitForWorkers();
            }
        }
        return isPrime;
    }

    private void prepareCurrentlyWorking() {
        for (Worker worker : availableWorkers) {
            if (!hasDisconnected(worker)) {
                currentlyWorking.add(worker);
            }
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
            iteration++;
        }
    }

    private void assignTasks() throws LackOfWorkersException {
        for (Worker worker : currentlyWorking) {
            if (availableTasks.isEmpty()) {
                return;
            }
            if (hasDisconnected(worker)) {
                throw new LackOfWorkersException();
            }
            worker.assignTask(availableTasks.remove(0));
        }
    }

    boolean validateResult() throws LackOfWorkersException {
        List<Worker> disconnected = new ArrayList<>();
        while (!this.currentlyWorking.isEmpty()) {
            boolean allWorkersFinished = true;
            boolean result = true;
            for (Worker worker : this.currentlyWorking) {
                if (hasDisconnected(worker)) {
                    disconnected.add(worker);
                }

                if (isStillWorking(worker)) {
                    allWorkersFinished = false;
                }

                if (numberIsNotPrime(worker)) {
                    result = false;
                }
            }

            handleDisconnectedWorkers(disconnected);

            if (allWorkersFinished) {
                return result;
            }
        }
        throw new LackOfWorkersException();
    }

    private void removeDisconnectedWorkers() {
        List<Worker> disconnected = new ArrayList<>();
        for (Worker worker : availableWorkers) {
            if (hasDisconnected(worker)) {
                disconnected.add(worker);
            }
        }
        handleDisconnectedWorkers(disconnected);
    }

    private void handleDisconnectedWorkers(List<Worker> disconnected) {
        for (Worker worker : disconnected) {
            availableTasks.add(worker.getTask());
            currentlyWorking.remove(worker);
            availableWorkers.remove(worker);
        }
    }

    private boolean numberIsNotPrime(Worker worker) {
        return worker.getResult().equals(EResult.Invalid);
    }

    private boolean hasDisconnected(Worker worker) {
        if (worker == null) {
            return true;
        }
        return worker.getResult().equals(EResult.Disconnected);
    }

    private boolean isStillWorking(Worker worker) {
        return worker.getResult().equals(EResult.InProgress);
    }

    synchronized void addWorker(Worker worker) {
        availableWorkers.add(worker);
        this.notify();
    }

    private synchronized void waitForWorkers() throws InterruptedException {
        while (availableWorkers.isEmpty()) {
            this.wait();
        }
    }
}
