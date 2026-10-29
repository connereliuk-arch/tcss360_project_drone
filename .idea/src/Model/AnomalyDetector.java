package Model;

import java.util.ArrayList;
import java.util.List;

public class AnomalyDetector {
    private double myLowBatteryThreshold;

    private double myMaxAltitudeChange;

    private double myMaxTurnAngle;

    private double myMaxSpeedChange;

    AnomalyDetector() {
        myLowBatteryThreshold = 15; // percent
        myMaxAltitudeChange = 10; // meters
        myMaxTurnAngle = 45; // degrees
        myMaxSpeedChange = 20; // mph
    }

    public static List<AnomalyRecord> detectAnomalies(final TelemetryData thePrevious,
                                                      final TelemetryData theCurrent,
                                                      final String theDroneId) {
        List<AnomalyRecord> records = new ArrayList<>();


        return records;
    }

    public boolean checkLowBattery(final TelemetryData theData) {
        return theData.getBatteryLevel() <= myLowBatteryThreshold;
    }

    public boolean checkAltitudeChange(final TelemetryData thePrevious,
                                     final TelemetryData theCurrent) {
        return Math.abs(thePrevious.getAltitude() - theCurrent.getAltitude()) >= myMaxAltitudeDrop;
    }

    // Returns true when speed changed by 20 mph or more
    public boolean checkSpeedChange(final TelemetryData thePrevious,
                                     final TelemetryData theCurrent) {
        return Math.abs(thePrevious.getSpeed() - theCurrent.getSpeed()) >= myMaxSpeedChange;
    }
}
