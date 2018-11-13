package com.codecool.serverSide.workers;

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
        this.result = EResult.New;
    }

    public EResult getResult() {
        synchronized (this) {
            return result;
        }
    }

    public Task getTask() {
        return task;
    }

    public void setResult(EResult result) {
        this.result = result;
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

    public synchronized void assignTask(Task task) {
        this.task = task;
        this.result = EResult.InProgress;
        this.notify();
    }

    private synchronized void waitForTask() throws InterruptedException {
        while (task == null) {
            this.wait();
        }
    }

    private void handleTask(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException {
        synchronized (this) {
            outputStream.writeObject(this.task);
            this.result = (EResult) inputStream.readObject();
            this.task = null;
        }
    }

    public boolean givenNumberIsNotPrime() {
        return this.result.equals(EResult.Invalid);
    }

    public boolean isStillWorking() {
        return this.result.equals(EResult.InProgress);
    }
}
