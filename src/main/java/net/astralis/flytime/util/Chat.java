package net.astralis.flytime.util;

public final class Chat {

    public static final String PREFIX = "§7[§bAstralisFly§7] §r";

    private Chat() {
    }

    public static String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        }

        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        if (minutes < 60) {
            return remainingSeconds > 0 ? minutes + "m " + remainingSeconds + "s" : minutes + "m";
        }

        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        StringBuilder builder = new StringBuilder();
        builder.append(hours).append("h");
        if (remainingMinutes > 0) {
            builder.append(" ").append(remainingMinutes).append("m");
        }
        if (remainingSeconds > 0) {
            builder.append(" ").append(remainingSeconds).append("s");
        }
        return builder.toString();
    }
}

