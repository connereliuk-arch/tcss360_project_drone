package Model;

import java.time.LocalDateTime;

public class AltitudeChangeCheck implements AnomalyCheck {
    /**
     * Max altitude change in meters
     */
    public final double myMaxAltitudeChangeThreshold;

    public AltitudeChangeCheck(final double theThreshold) {
        myMaxAltitudeChangeThreshold = theThreshold;
    }

    @Override
    public AnomalyRecord detect(TelemetryData thePrevious, TelemetryData theCurrent, String theDroneId) {
        final LocalDateTime timestamp = LocalDateTime.now();
        AnomalyRecord record = null;

        if (Math.abs(thePrevious.altitude() - theCurrent.altitude()) >= myMaxAltitudeChangeThreshold) {
            final String anomalyType = "ALTITUDE_CHANGE";
            final String details = "Altitude level has moved too fast from "
                    + thePrevious.altitude() + " to " + theCurrent.altitude() + ".";

            record = new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details);
        }

        return record;
    }
}
