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
     * Highest a drone can fly in meters
     */
    private static final double MAX_ALTITUDE = 120;

    /**
     * Max turn angle in degrees
     */
    private static final double MAX_TURN_ANGLE = 45;

    /**
     * Max speed change in mph
     */
    private static final double MAX_SPEED_CHANGE = 20;

    /**
     * The max speed a drone can travel in mph
     */
    private static final double MAX_SPEED = 60;

    /**
     * The max distance a drone can travel between intervals in meters
     */
    private static final double MAX_GPS_THRESHOLD = 50;

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

        if (checkAltitudeHeight(theCurrent)) {
            final String anomalyType = "DRONE_ALTITUDE";
            final String details = "The drones altitude has exceeded the maximum altitude. Current altitude: "
                    + theCurrent.getAltitude();

            records.add(new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details));
        }

        // Check if speed has changed outside the limit
        if (checkSpeedChange(thePrevious, theCurrent)) {
            final String anomalyType = "SPEED_CHANGE";
            final String details = "Speed has changed too fast from "
                    + thePrevious.getSpeed() + " to " + theCurrent.getSpeed() + ".";

            records.add(new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details));
        }

        // Check if speed has changed outside the limit
        if (checkSpeed(theCurrent)) {
            final String anomalyType = "HIGH_SPEED";
            final String details = "Speed is too fast. Current speed: "
                    + theCurrent.getSpeed() + ".";

            records.add(new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details));
        }

        if (checkGpsChange(thePrevious, theCurrent)) {
            final String anomalyType = "GPS_CHANGE";
            final String details = "The GPS position has changed too much from ("
                    + thePrevious.getLatitude() + ", " + thePrevious.getLongitude() + ") to ("
                    + theCurrent.getLatitude() + ", " + theCurrent.getLongitude() + ").";

            records.add(new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details));
        }

        if (checkTurnAngle(thePrevious, theCurrent)) {
            final String anomalyType = "TURN_ANGLE";
            final String details = "The drones turn angle has increased too much from "
                    + thePrevious.getOrientation() + " to " + theCurrent.getOrientation() + ".";

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
    private boolean checkLowBattery(final TelemetryData theData) {
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
    private boolean checkAltitudeChange(final TelemetryData thePrevious,
                                     final TelemetryData theCurrent) {
        return Math.abs(thePrevious.getAltitude() - theCurrent.getAltitude()) >= MAX_ALTITUDE_CHANGE;
    }

    /**
     * Checks whether the current altitude of the drone is above the height limit of 120m
     *
     * @param theCurrent the drone's current telemetry data
     * @return true if the drones altitude rises above the limit; false otherwise
     */
    private boolean checkAltitudeHeight(final TelemetryData theCurrent) {
        return theCurrent.getAltitude() > MAX_ALTITUDE;
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
    private boolean checkSpeedChange(final TelemetryData thePrevious,
                                     final TelemetryData theCurrent) {
        return Math.abs(thePrevious.getSpeed() - theCurrent.getSpeed()) >= MAX_SPEED_CHANGE;
    }

    /**
     * Checks whether the drones current speed is greater than the maximum of 60 mph
     *
     * @param theCurrent the drone's current telemetry data
     * @return true if the drones speed is faster than 60 mph; false otherwise
     */
    private boolean checkSpeed(final TelemetryData theCurrent) {
        return theCurrent.getSpeed() > MAX_SPEED;
    }

    /**
     * Calculates the distance between two GPS coordinates using the Haversine formula
     *
     * @param thePrevious the drone's previous telemetry data
     * @param theCurrent the drone's current telemetry data
     * @return the distance between two points
     */
    private double calculateDistance(final TelemetryData thePrevious,
                                  final TelemetryData theCurrent) {
        double lat1 = thePrevious.getLatitude();
        double lon1 = thePrevious.getLongitude();
        double lat2 = theCurrent.getLatitude();
        double lon2 = theCurrent.getLongitude();

        // Convert degrees to radians
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        // Calculate differences
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        // Haversine formula
        double a = Math.min(1.0, Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2));

        // Calculate distance in meters
        double earthRadius = 6371000;

        return 2 * earthRadius * Math.asin(Math.sqrt(a));

    }

    /**
     * Checks if the change in GPS position is greater than the max threshold of 50m
     *
     * @param thePrevious the drone's previous telemetry data
     * @param theCurrent the drone's current telemetry data
     * @return true if the distance passes the threshold and false if the
     * change in distance is less.
     */
    private boolean checkGpsChange(final TelemetryData thePrevious,
                                  final TelemetryData theCurrent) {
        final double distanceTraveled = calculateDistance(thePrevious, theCurrent);

        return distanceTraveled > MAX_GPS_THRESHOLD;
    }

    /**
     * Checks whether the angle of the drone has exceeded 45 degrees in one telemetry change
     *
     * @param thePrevious the drone's previous telemetry data
     * @param theCurrent the drone's current telemetry data
     * @return true if the turn angle exceeds the maximum of 45 degree; false otherwise
     */
    private boolean checkTurnAngle(final TelemetryData thePrevious,
                                  final TelemetryData theCurrent) {
        return Math.abs(theCurrent.getOrientation() - thePrevious.getOrientation()) > MAX_TURN_ANGLE;

    }
}
