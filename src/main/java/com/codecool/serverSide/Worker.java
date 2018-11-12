package com.codecool.serverSide;

import com.codecool.model.EResult;
import com.codecool.model.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Worker implements Runnable {

    private Task task;
    private Socket socket;
    private EResult result;

    public Worker(Socket socket) {
        this.socket = socket;
        this.result = EResult.InProgress;
    }

    public EResult getResult() {
        return result;
    }

    @Override
    public void run() {
        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Could not connect to socket.");
            result = EResult.Disconnected;
            return;
        }

        while (!result.equals(EResult.Disconnected)) {
            waitForTask();
            handleTask(inputStream, outputStream);
        }
    }

    public void assignTask(Task task) {
        this.task = task;
    }

    private void waitForTask() {

    }

    private void handleTask(ObjectInputStream inputStream, ObjectOutputStream outputStream) {

    }
}
