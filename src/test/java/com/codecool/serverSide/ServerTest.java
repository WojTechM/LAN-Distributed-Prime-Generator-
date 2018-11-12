package com.codecool.serverSide;

import com.codecool.model.EResult;
import com.codecool.serverSide.exceptions.LackOfWorkersException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @Test
    void Should_ReturnTrue_When_AllWorkersResultsAreValid() throws LackOfWorkersException {
        Server server = new Server(8000);
        Worker w1 = new Worker(null);
        w1.setResult(EResult.Valid);
        Worker w2 = new Worker(null);
        w2.setResult(EResult.Valid);
        server.addWorker(w1);
        server.addWorker(w2);

        assertTrue(server.validateResult());
    }

    @Test
    void Should_ReturnFalse_When_OneOrMoreWorkersResultsAreInvalid() throws LackOfWorkersException {
        Server server = new Server(8001);
        Worker w1 = new Worker(null);
        w1.setResult(EResult.Valid);
        Worker w2 = new Worker(null);
        w2.setResult(EResult.Invalid);
        server.addWorker(w1);
        server.addWorker(w2);

        assertFalse(server.validateResult());
    }
}