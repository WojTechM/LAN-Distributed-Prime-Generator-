package com.codecool;

import com.codecool.clientSide.Client;
import com.codecool.serverSide.Server;

public class App {

    public static void main(String[] args) {
        try {
            validateArguments(args);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        String mode = getMode(args).toLowerCase();
        String address = getAddress(args);
        int port = Integer.parseInt(getPort(args));
        switch (mode) {
            case "client":
                Runnable client = new Client(address, port);
                new Thread(client).start();
                break;
            case "server":
                Server server = new Server(port);
                try {
                    server.run();
                } catch (InterruptedException e) {
                    System.out.println("Server shutting down.");
                }
                break;
        }
    }

    private static void validateArguments(String[] args) throws IllegalArgumentException {
        if (!isValidNumberOfArgs(args)) {
            throw new IllegalArgumentException("Command signature should look in a following way: java App mode address port");
        }
        String mode = getMode(args);
        if (!isValidMode(mode)) {
            throw new IllegalArgumentException("Available modes are: 'client' and 'server'");
        }

        try {
            String portString = getPort(args);
            Integer.valueOf(portString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port must be a number!");
        }
    }

    private static boolean isValidNumberOfArgs(String[] args) {
        int length = args.length;
        return length == 2 || length == 3;
    }

    private static String getMode(String[] args) {
        return args[0];
    }

    private static boolean isValidMode(String mode) {
        return mode.equalsIgnoreCase("client") || mode.equalsIgnoreCase("server");
    }

    private static String getPort(String[] args) {
        if (args.length == 2) {
            return args[1];
        }
        return args[2];
    }

    private static String getAddress(String[] args) {
        if (args.length == 2) {
            return "localhost";
        }
        return args[1];
    }
}
