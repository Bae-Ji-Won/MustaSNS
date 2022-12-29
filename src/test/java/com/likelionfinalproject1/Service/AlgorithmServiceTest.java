package com.likelionfinalproject1.Service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmServiceTest {
    AlgorithmService algorithmService = new AlgorithmService();

    @Test
    void sumOfDigit(){
        assertEquals(6,algorithmService.sumOfDigit(123));
        assertEquals(0,algorithmService.sumOfDigit(0));
        assertEquals(15,algorithmService.sumOfDigit(456));
    }
}