package com.codecool.serverSide;

import com.codecool.model.EResult;
import com.codecool.serverSide.exceptions.LackOfWorkersException;
import com.codecool.serverSide.workers.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @Test
    void Should_ReturnTrue_When_AllWorkersResultsAreValid() throws LackOfWorkersException {
        Server server = new Server(8000, -1, -1);
        Worker w1 = new Worker(null);
        w1.setResult(EResult.Valid);
        Worker w2 = new Worker(null);
        w2.setResult(EResult.Valid);
        server.currentlyWorking.add(w1);
        server.currentlyWorking.add(w2);
        assertTrue(server.validateResult());
    }

    @Test
    void Should_ReturnFalse_When_OneOrMoreWorkersResultsAreInvalid() throws LackOfWorkersException {
        Server server = new Server(8001, -1, -1);
        Worker w1 = new Worker(null);
        w1.setResult(EResult.Valid);
        Worker w2 = new Worker(null);
        w2.setResult(EResult.Invalid);
        server.currentlyWorking.add(w1);
        server.currentlyWorking.add(w2);

        assertFalse(server.validateResult());
    }
}