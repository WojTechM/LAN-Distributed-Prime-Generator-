package com.codecool.clientSide;

import com.codecool.model.EResult;
import com.codecool.model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    private Client testSubject = new Client("not part of the test", -1);

    @Test
    void Should_ReturnTrue_When_PassedPrime() {
        Task validatePrime = new Task(13, 2, 13);
        assertEquals(testSubject.partialCheck(validatePrime), EResult.Valid);
    }

    @Test
    void Should_ReturnFalse_When_PassedNonPrime() {
        Task validatePrime = new Task(12, 2, 13);
        assertEquals(testSubject.partialCheck(validatePrime), EResult.Invalid);
    }

    @Test
    void Should_ReturnTrue_When_PassedNonPrimeWithDivisorsOutOfRange() {
        Task validatePrime = new Task(15, 6, 14);
        assertEquals(testSubject.partialCheck(validatePrime), EResult.Valid);
    }

}
