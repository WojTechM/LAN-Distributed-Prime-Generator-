package com.codecool.serverSide;

import com.codecool.model.EResult;
import com.codecool.model.Task;
import com.codecool.serverSide.exceptions.LackOfWorkersException;
import com.codecool.serverSide.resultHandler.ValidationResultHandler;
import com.codecool.serverSide.workers.Worker;
import com.codecool.serverSide.workers.WorkerRegistration;

import java.util.*;

public class Server {

    private List<Worker> availableWorkers = Collections.synchronizedList(new ArrayList<>());
    Set<Worker> currentlyWorking = new HashSet<>();
    private List<Task> availableTasks = new ArrayList<>();
    private int potentialPrime;
    private int taskRange;

    public Server(int port, int startFromNumber, int taskRange) {
        WorkerRegistration registration = new WorkerRegistration(this, port);
        Thread registrationThread = new Thread(registration);
        registrationThread.start();
        this.potentialPrime = startFromNumber;
        this.taskRange = taskRange;
    }

    public void run() throws InterruptedException {
        ValidationResultHandler resultHandler = new ValidationResultHandler();
        while (!Thread.currentThread().isInterrupted()) {
            boolean isPrime = checkIfIsPrime(potentialPrime);
            resultHandler.handleResult(potentialPrime, isPrime);
            potentialPrime += 2;
        }
    }

    private boolean checkIfIsPrime(int potentialPrime) throws InterruptedException {
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
            int from = iteration * taskRange;
            int to = from + taskRange;
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
                System.out.println(worker.getTask() + "  " + worker.getResult());
                if (hasDisconnected(worker)) {
                    disconnected.add(worker);
                }

                if (worker.isStillWorking()) {
                    allWorkersFinished = false;
                }

                if (worker.givenNumberIsNotPrime()) {
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

    private boolean hasDisconnected(Worker worker) {
        if (worker == null) {
            return true;
        }
        return worker.getResult().equals(EResult.Disconnected);
    }

    public synchronized void addWorker(Worker worker) {
        availableWorkers.add(worker);
        this.notify();
    }

    private synchronized void waitForWorkers() throws InterruptedException {
        while (availableWorkers.isEmpty()) {
            this.wait();
        }
    }
}
