package Model;

import java.util.ArrayList;
import java.util.List;

public class AnomalyDetector {
    private double myLowBatteryThreshold;

    private double myMaxAltitudeDrop;

    private double myMaxTurnAngle;

    private double myMaxSpeedChange;

    AnomalyDetector() {
        myLowBatteryThreshold = 15; // percent
        myMaxAltitudeDrop = 20; // meters
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
}
