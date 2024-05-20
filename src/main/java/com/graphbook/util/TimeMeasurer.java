package com.graphbook.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeMeasurer {
    
    public static LocalDateTime startMeasuring() {
        return LocalDateTime.now();
    }

    public static LocalDateTime endMeasuring() {
        return LocalDateTime.now();
    }

    public static String getTimePassedString(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        long totalHours = duration.toHoursPart();
        long totalMinutes = duration.toMinutesPart();
        long totalSeconds = duration.toSecondsPart();

        return totalHours + "h " + totalMinutes + "m " + totalSeconds + "s";
    }
}
