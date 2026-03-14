package net.astralis.flytime.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerModelTest {

    @Test
    void startAndTickShouldConsumeTime() {
        PlayerModel model = new PlayerModel(2);

        model.startFlyTime();
        assertTrue(model.isEnabled());

        model.tick();
        assertEquals(1, model.getFlyTime());
        assertTrue(model.isEnabled());

        model.tick();
        assertEquals(0, model.getFlyTime());
        assertFalse(model.isEnabled());
    }

    @Test
    void removeFlyTimeShouldNotGoNegative() {
        PlayerModel model = new PlayerModel(10);

        model.removeFlyTime(100);

        assertEquals(0, model.getFlyTime());
        assertFalse(model.isEnabled());
    }
}

