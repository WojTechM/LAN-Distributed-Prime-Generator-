package com.codecool.serverSide;

import com.codecool.model.EResult;
import com.codecool.model.Task;

import java.net.Socket;

public class Worker implements Runnable {

    private Task task;
    private Socket socket;
    private EResult result;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    public EResult getResult() {
        return result;
    }

    @Override
    public void run() {

    }

    public void assignTask(Task task) {

    }

    private void waitForTask() {

    }

    private void handleTask() {

    }
}
