package com.codecool.clientSide;

import com.codecool.model.EResult;
import com.codecool.model.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Runnable {

    private String address;
    private int port;
    private int previousTask = -1;

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        Socket connection;
        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;
        try {
            connection = new Socket(address, port);
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(connection.getInputStream());

        } catch (IOException e) {
            System.out.println("Unable to connect with server.");
            return;
        }


        while (!Thread.currentThread().isInterrupted()) {
            try {
                Task task = (Task) inputStream.readObject();
                EResult result = partialCheck(task);
                outputStream.writeObject(result);
                showProgress(task);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Could not load task. Shutting down connection.");
                break;
            }
        }

        closeConnection(outputStream, connection);
    }

    private void closeConnection(ObjectOutputStream outputStream, Socket connection) {
        try {
            outputStream.writeObject(EResult.Disconnected);
            connection.close();
        } catch (IOException e) {
            System.out.println("Connection closed.");
        }
    }

    public EResult partialCheck(Task task) {
        boolean isPrime = true;
        int toCheck = task.getPotentialPrime();
        int start = getStart(task);
        int limit = getLimit(task);
        for (int i = start; i <= limit; ++i) {
            if (toCheck % i == 0) {
                isPrime = false;
                break;
            }
        }
        return isPrime ? EResult.Valid : EResult.Invalid;
    }

    private int getStart(Task task) {
        if (task.getFrom() < 2) {
            return 2;
        }
        return task.getFrom();
    }

    private int getLimit(Task task) {
        int defaultLimit = task.getPotentialPrime() / 2;
        int requested = task.getTo();
        if (defaultLimit < requested) {
            return defaultLimit;
        }
        return requested;
    }

    private void showProgress(Task task) {
        System.out.println(task);
    }
}
