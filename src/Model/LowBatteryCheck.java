package Model;

import java.time.LocalDateTime;

public class LowBatteryCheck implements AnomalyCheck {
    /**
     * Lower battery percent limit
     */
    private final double myLowBatteryThreshold;

    public LowBatteryCheck(final double theThreshold) {
        myLowBatteryThreshold = theThreshold;
    }

    @Override
    public AnomalyRecord detect(TelemetryData thePrevious, TelemetryData theCurrent, String theDroneId) {
        final LocalDateTime timestamp = LocalDateTime.now();
        AnomalyRecord record = null;

        if (theCurrent.batteryLevel() <= myLowBatteryThreshold) {
            final String anomalyType = "LOW_BATTERY";
            final String details = "Battery has depleted below " + myLowBatteryThreshold + "%. Current battery level: "
                    + theCurrent.batteryLevel();

            record = new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details);
        }

        return record;
    }
}
