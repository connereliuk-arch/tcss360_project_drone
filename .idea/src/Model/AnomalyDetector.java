package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnomalyDetector {
    /**
     * Lower battery percent limit
     */
    private static final double LOW_BATTERY_THRESHOLD = 15;

    /**
     * Max altitude change in meters
     */
    private static final double MAX_ALTITUDE_CHANGE = 10;

    /**
     * Max turn angle in degrees
     */
    private static final double MAX_TURN_ANGLE = 45;

    /**
     * Max speed change in mph
     */
    private static final double MAX_SPEED_CHANGE = 20;

    public AnomalyDetector() { }

    public List<AnomalyRecord> detectAnomalies(final TelemetryData thePrevious,
                                                      final TelemetryData theCurrent,
                                                      final String theDroneId) {
        List<AnomalyRecord> records = new ArrayList<>();
        final LocalDateTime timestamp = LocalDateTime.now();

        // Check if battery is low
        if (checkLowBattery(theCurrent)) {
            final String anomalyType = "LOW_BATTERY";
            final String details = "Battery has depleted below " + LOW_BATTERY_THRESHOLD + "%. Current battery level: "
                                    + theCurrent.getBatteryLevel();

            records.add(new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details));
        }

        // Check if altitude has changed outside the limit
        if (checkAltitudeChange(thePrevious, theCurrent)) {
            final String anomalyType = "ALTITUDE_CHANGE";
            final String details = "Altitude level has moved too fast from "
                    + thePrevious.getAltitude() + " to " + theCurrent.getAltitude() + ".";

            records.add(new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details));
        }

        // Check if speed has changed outside the limit
        if (checkSpeedChange(thePrevious, theCurrent)) {
            final String anomalyType = "SPEED_CHANGE";
            final String details = "Speed has changed too fast from "
                    + thePrevious.getSpeed() + " to " + theCurrent.getSpeed() + ".";

            records.add(new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details));
        }

        return records;
    }

    public boolean checkLowBattery(final TelemetryData theData) {
        return theData.getBatteryLevel() <= LOW_BATTERY_THRESHOLD;
    }

    public boolean checkAltitudeChange(final TelemetryData thePrevious,
                                     final TelemetryData theCurrent) {
        return Math.abs(thePrevious.getAltitude() - theCurrent.getAltitude()) >= MAX_ALTITUDE_CHANGE;
    }

    // Returns true when speed changed by 20 mph or more
    public boolean checkSpeedChange(final TelemetryData thePrevious,
                                     final TelemetryData theCurrent) {
        return Math.abs(thePrevious.getSpeed() - theCurrent.getSpeed()) >= MAX_SPEED_CHANGE;
    }
}
