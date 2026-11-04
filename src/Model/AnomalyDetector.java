package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The AnomalyDetector class monitors drone telemetry data to identify
 * unusual behavior and potential safety concerns.
 *
 * It compares previous and current telemetry readings against predefined
 * thresholds to detect anomalies related to battery levels, altitude
 * changes, and speed changes.
 *
 * When an anomaly is detected, an AnomalyRecord is created containing
 * the drone ID, current telemetry data, timestamp, anomaly type, and
 * a description of the detected condition.
 *
 * The detector uses predefined thresholds for determining when
 * telemetry readings are considered anomalous.
 *
 * @author Dylan Browne
 * @version 1.0
 * @see TelemetryData
 * @see AnomalyRecord
 */
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

    /**
     * Detects anomalies in the drone's current telemetry data by comparing
     * it against the previous telemetry data.
     *
     * Checks for low battery, significant altitude changes, and significant
     * speed changes. Creates an AnomalyRecord for each detected anomaly.
     *
     * @param thePrevious the drone's previous telemetry data
     * @param theCurrent the drone's current telemetry data
     * @param theDroneId the unique identifier of the drone being monitored
     * @return a list of AnomalyRecord objects representing detected anomalies;
     *         returns an empty list if no anomalies are detected
     */
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

    /**
     * Checks whether the drone's current battery level is at or below
     * the configured low battery threshold.
     *
     * @param theData the telemetry data containing the current battery level
     * @return true if the battery level is at or below the threshold;
     *         false otherwise
     */
    public boolean checkLowBattery(final TelemetryData theData) {
        return theData.getBatteryLevel() <= LOW_BATTERY_THRESHOLD;
    }

    /**
     * Checks whether the difference between the drone's previous and current
     * altitude meets or exceeds the configured maximum altitude change.
     *
     * @param thePrevious the drone's previous telemetry data
     * @param theCurrent the drone's current telemetry data
     * @return true if the absolute altitude difference meets or exceeds
     *         the configured threshold; false otherwise
     */
    public boolean checkAltitudeChange(final TelemetryData thePrevious,
                                     final TelemetryData theCurrent) {
        return Math.abs(thePrevious.getAltitude() - theCurrent.getAltitude()) >= MAX_ALTITUDE_CHANGE;
    }

    /**
     * Checks whether the difference between the drone's previous and current
     * speed meets or exceeds the configured maximum speed change.
     *
     * @param thePrevious the drone's previous telemetry data
     * @param theCurrent the drone's current telemetry data
     * @return true if the absolute speed difference meets or exceeds
     *         the configured threshold; false otherwise
     */
    // Returns true when speed changed by 20 mph or more
    public boolean checkSpeedChange(final TelemetryData thePrevious,
                                     final TelemetryData theCurrent) {
        return Math.abs(thePrevious.getSpeed() - theCurrent.getSpeed()) >= MAX_SPEED_CHANGE;
    }
}
