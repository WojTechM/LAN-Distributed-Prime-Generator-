package com.codecool.serverSide.resultHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ValidationResultHandler {

    public void handleResult(int potentialPrime, boolean isPrime) {
        if (isPrime) {
            try {
                saveToFile(potentialPrime);
            } catch (IOException e) {
                System.out.println(potentialPrime);
            }
        }
    }

    private void saveToFile(int potentialPrime) throws IOException {
        FileWriter fw = new FileWriter("Primes.txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(String.valueOf(potentialPrime));
        bw.newLine();
        bw.close();
    }
}
