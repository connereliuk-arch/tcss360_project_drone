package Model;

import java.time.LocalDateTime;

public class SpeedChangeCheck implements AnomalyCheck {
    /**
     * Max speed change in mph
     */
    private final double myMaxSpeedChangeThreshold;

    public SpeedChangeCheck(final double theThreshold) {
        myMaxSpeedChangeThreshold = theThreshold;
    }

    @Override
    public AnomalyRecord detect(TelemetryData thePrevious, TelemetryData theCurrent, String theDroneId) {
        final LocalDateTime timestamp = LocalDateTime.now();
        AnomalyRecord record = null;

        if (Math.abs(thePrevious.speed() - theCurrent.speed()) >= myMaxSpeedChangeThreshold) {
            final String anomalyType = "SPEED_CHANGE";
            final String details = "Speed has changed too fast from "
                    + thePrevious.speed() + " to " + theCurrent.speed() + ".";

            record = new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details);
        }

        return record;
    }
}
