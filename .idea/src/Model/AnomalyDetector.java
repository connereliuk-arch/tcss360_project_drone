package Model;

import java.util.ArrayList;
import java.util.List;

public class AnomalyDetector {
    private double myLowBatteryThreshold;

    private double myMaxAltitudeDrop;

    private double myMaxTurnAngle;

    private double myMaxSpeedChange;

    AnomalyDetector() {

    }

    public static List<AnomalyRecord> detectAnomalies(final TelemetryData thePrevious,
                                                      final TelemetryData theCurrent,
                                                      final String theDroneId) {
        List<AnomalyRecord> records = new ArrayList<>();


        return records;
    }
}
