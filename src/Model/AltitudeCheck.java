package Model;

import java.time.LocalDateTime;

public class AltitudeCheck implements AnomalyCheck {
    /**
     * Highest a drone can fly in meters
     */
    private final double myMaxAltitudeThreshold;

    public AltitudeCheck(final double theThreshold) {
        myMaxAltitudeThreshold = theThreshold;
    }

    @Override
    public AnomalyRecord detect(TelemetryData thePrevious, TelemetryData theCurrent, String theDroneId) {
        final LocalDateTime timestamp = LocalDateTime.now();
        AnomalyRecord record = null;

        if (theCurrent.altitude() > myMaxAltitudeThreshold) {
            final String anomalyType = "DRONE_ALTITUDE";
            final String details = "The drones altitude has exceeded the maximum altitude. Current altitude: "
                    + theCurrent.altitude();

            record = new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details);
        }

        return record;
    }
}
