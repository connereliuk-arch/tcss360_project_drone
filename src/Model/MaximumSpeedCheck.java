package Model;

import java.time.LocalDateTime;

public class MaximumSpeedCheck implements AnomalyCheck {
    /**
     * Max speed change in mph
     */
    private final double myMaxSpeedThreshold;

    public MaximumSpeedCheck(final double theThreshold) {
        myMaxSpeedThreshold = theThreshold;
    }

    @Override
    public AnomalyRecord detect(TelemetryData thePrevious, TelemetryData theCurrent, String theDroneId) {
        final LocalDateTime timestamp = LocalDateTime.now();
        AnomalyRecord record = null;

        // Check if speed has changed outside the limit
        if (theCurrent.speed() > myMaxSpeedThreshold) {
            final String anomalyType = "HIGH_SPEED";
            final String details = "Speed is too fast. Current speed: "
                    + theCurrent.speed() + ".";

            record = new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details);
        }

        return record;
    }
}
