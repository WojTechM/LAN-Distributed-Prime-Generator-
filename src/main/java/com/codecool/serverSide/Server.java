package com.codecool.serverSide;

import com.codecool.model.Task;

import java.util.List;

public class Server {

    private List<Worker> users;
    private List<Task> availableTasks;


    public Server(int port) {
        WorkerRegistration registration = new WorkerRegistration(this, port);
        Thread registrationThread = new Thread(registration);
        registrationThread.start();
    }

    public void run() {

    }

    private void splitWorkIntoTasks() {

    }

    private void assignTasks() {

    }

    private void validateResult() {

    }

    public void addWorker(Worker worker) {
        users.add(worker);
    }
}
