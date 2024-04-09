package com.bevans.kafka;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {
    @Test
    void shouldCallMain() {
        // given
        // when
        // then
        assertDoesNotThrow(() -> Main.main(null));
    }
}
