package com.bevans.kafka.connect.springboot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ConnectSpringBootApplicationTest {
    @Test
    void shouldCallMainMethodOnApplication() {
        // given
        // when
        // then
        assertDoesNotThrow(() -> ConnectSpringBootApplication.main(new String[]{}));
    }
}
