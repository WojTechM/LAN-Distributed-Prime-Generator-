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

    Worker(Socket socket) {
        this.socket = socket;
        this.result = EResult.InProgress;
    }

    EResult getResult() {
        return result;
    }

    @Override
    public void run() {
        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Could not connect to socket.");
            result = EResult.Disconnected;
            return;
        }

        while (!result.equals(EResult.Disconnected)) {
            try {
                waitForTask();
                handleTask(inputStream, outputStream);
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                System.out.println("Connection error. Disconnecting.");
                result = EResult.Disconnected;
            }
        }
    }

    public void assignTask(Task task) {
        this.task = task;
    }

    private void waitForTask() throws InterruptedException {
        while (task == null) {
            System.out.println("Waiting for new task");
            this.wait();
        }
    }

    private void handleTask(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException {
        this.result = EResult.InProgress;
        outputStream.writeObject(this.task);
        this.result = (EResult) inputStream.readObject();
    }
}
