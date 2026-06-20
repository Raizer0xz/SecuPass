package com.example.SAG.Service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// ✅ MOCK evita levantar el servidor real y falla con Security si no hay BD
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SagServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}