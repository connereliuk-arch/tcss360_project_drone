package Model;

import java.time.LocalDateTime;

public class SharpTurnCheck implements AnomalyCheck {
    /**
     * Max turn angle in degrees
     */
    private final double myMaxTurnAngleThreshold;

    public SharpTurnCheck(final double theThreshold) {
        myMaxTurnAngleThreshold = theThreshold;
    }

    @Override
    public AnomalyRecord detect(TelemetryData thePrevious, TelemetryData theCurrent, String theDroneId) {
        final LocalDateTime timestamp = LocalDateTime.now();
        AnomalyRecord record = null;

        if (Math.abs(theCurrent.orientation() - thePrevious.orientation()) > myMaxTurnAngleThreshold) {
            final String anomalyType = "TURN_ANGLE";
            final String details = "The drones turn angle has increased too much from "
                    + thePrevious.orientation() + " to " + theCurrent.orientation() + ".";

            record = new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details);
        }

        return record;
    }
}
