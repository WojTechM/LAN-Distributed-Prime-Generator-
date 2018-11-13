package com.codecool.serverSide.workers;

import com.codecool.serverSide.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WorkerRegistration implements Runnable {

    private final Server server;
    private final int port;

    public WorkerRegistration(Server server, int port) {
        this.server = server;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Thread currentThread = Thread.currentThread();
            while (!currentThread.isInterrupted()) {
                Socket socket = serverSocket.accept();
                Worker worker = new Worker(socket);
                new Thread(worker).start();
                this.server.addWorker(worker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
