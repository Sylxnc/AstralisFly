package net.astralis.flytime.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatTest {

    @Test
    void formatTimeSecondsOnly() {
        assertEquals("45s", Chat.formatTime(45));
    }

    @Test
    void formatTimeMinutesAndSeconds() {
        assertEquals("2m 5s", Chat.formatTime(125));
    }

    @Test
    void formatTimeHoursMinutesSeconds() {
        assertEquals("1h 1m 1s", Chat.formatTime(3661));
    }
}

