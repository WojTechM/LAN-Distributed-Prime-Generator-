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
                int baseNumber = Integer.parseInt(getBaseNumber(args));
                int taskRange = Integer.parseInt(getRangePerTask(args));
                Server server = new Server(port, baseNumber, taskRange);
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

        validatePort(args);

        if (mode.equalsIgnoreCase("server")) {
            validateServerArguments(args);
        }
    }

    private static void validateServerArguments(String[] args) throws IllegalArgumentException {
        if (args.length == 3) {
            return;
        }
        String startFromNumber = getBaseNumber(args);
        if (!isNumeric(startFromNumber)) {
            throw new IllegalArgumentException("Given base number is not numeric!");
        }

        String rangePerTask = getRangePerTask(args);
        if (!isNumeric(rangePerTask)) {
            throw new IllegalArgumentException("Given task range is not numeric!");
        }
    }


    private static void validatePort(String[] args) {
        String portString = getPort(args);
        if (!isNumeric(portString)) {
            throw new IllegalArgumentException("Port must be a number!");
        }
    }

    private static boolean isNumeric(String number) {
        try {
            Integer.valueOf(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidNumberOfArgs(String[] args) {
        int length = args.length;
        return length > 2 && length <= 5;
    }

    private static boolean isValidMode(String mode) {
        return mode.equalsIgnoreCase("client") || mode.equalsIgnoreCase("server");
    }

    private static String getMode(String[] args) {
        return args[0];
    }

    private static String getAddress(String[] args) {
        return args[1];
    }

    private static String getPort(String[] args) {
        return args[2];
    }

    private static String getBaseNumber(String[] args) {
        if (args.length == 3) {
            return "3";
        }
        return args[3];
    }

    private static String getRangePerTask(String[] args) {
        if (args.length == 3) {
            return "250";
        }
        return args[4];
    }
}
