package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnomalyDetector {
    private double myLowBatteryThreshold;

    private double myMaxAltitudeChange;

    private double myMaxTurnAngle;

    private double myMaxSpeedChange;

    public AnomalyDetector() {
        myLowBatteryThreshold = 15; // percent
        myMaxAltitudeChange = 10; // meters
        myMaxTurnAngle = 45; // degrees
        myMaxSpeedChange = 20; // mph
    }

    public List<AnomalyRecord> detectAnomalies(final TelemetryData thePrevious,
                                                      final TelemetryData theCurrent,
                                                      final String theDroneId) {
        List<AnomalyRecord> records = new ArrayList<>();
        final LocalDateTime timestamp = LocalDateTime.now();

        // Check if battery is low
        if (checkLowBattery(theCurrent)) {
            final String anomalyType = "LOW_BATTERY";
            final String details = "Battery has depleted below " + myLowBatteryThreshold + "%. Current battery level: "
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
        return theData.getBatteryLevel() <= myLowBatteryThreshold;
    }

    public boolean checkAltitudeChange(final TelemetryData thePrevious,
                                     final TelemetryData theCurrent) {
        return Math.abs(thePrevious.getAltitude() - theCurrent.getAltitude()) >= myMaxAltitudeChange;
    }

    // Returns true when speed changed by 20 mph or more
    public boolean checkSpeedChange(final TelemetryData thePrevious,
                                     final TelemetryData theCurrent) {
        return Math.abs(thePrevious.getSpeed() - theCurrent.getSpeed()) >= myMaxSpeedChange;
    }
}
