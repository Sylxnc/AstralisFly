package net.astralis.flytime.models;

public class PlayerModel {

    private long flyTime; // Sekunden
    private boolean enabled;

    public PlayerModel(long flyTime) {
        this.flyTime = flyTime;
        this.enabled = false;
    }

    public void startFlyTime() {
        if (flyTime <= 0) return;
        this.enabled = true;
    }

    public void stopFlyTime() {
        this.enabled = false;
    }

    public void tick() {
        if (!enabled) return;

        if (flyTime > 0) {
            flyTime--;
        }

        if (flyTime <= 0) {
            flyTime = 0;
            enabled = false;
        }
    }

    public boolean hasFlyTime() {
        return flyTime > 0;
    }

    public long getFlyTime() {
        return flyTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void addFlyTime(long seconds) {
        this.flyTime += seconds;
    }

    public void removeFlyTime(long seconds) {
        this.flyTime -= seconds;
        if (this.flyTime < 0) {
            this.flyTime = 0;
            this.enabled = false;
        }
    }
}
