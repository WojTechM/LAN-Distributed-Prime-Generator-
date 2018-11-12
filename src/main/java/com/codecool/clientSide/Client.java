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
            inputStream = new ObjectInputStream(connection.getInputStream());
            outputStream = new ObjectOutputStream(connection.getOutputStream());

        } catch (IOException e) {
            System.out.println("Unable to connect with server.");
            return;
        }


        while (!Thread.currentThread().isInterrupted()) {
            try {
                Task task = (Task) inputStream.readObject();
                EResult result = partialCheck(task);
                outputStream.writeObject(result);
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
            e.printStackTrace();
        }
    }

    public EResult partialCheck(Task task) {
        boolean isPrime = false;
        int toCheck = task.getPotentialPrime();
        for(int i = task.getFrom(); i <= toCheck / 2; ++i)
        {

            if(toCheck % i == 0)
            {
                isPrime = true;
                break;
            }
        }

        return isPrime ? EResult.Valid : EResult.Invalid;
    }
}
