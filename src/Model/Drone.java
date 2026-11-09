package Model;

import java.time.LocalDateTime;

public class Drone {

    private static final double START_SPEED = 0.0;
    private static final double START_ALTITUDE = 20.0;
    private static final double START_LATITUDE = 47.2454;
    private static final double START_LONGITUDE = -122.4385;
    private static final double START_ORIENTATION = 0.0;
    private static final double START_BATTERY = 100.0;

    private static final double MIN_BATTERY = 0.0;
    private static final double MAX_BATTERY = 100.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MAX_LONGITUDE = 180.0;
    private static final double MAX_ORIENTATION = 360.0;

    /** This drone's unique ID. */
    private final String droneId;

    /** This drone's current telemetry. */
    private TelemetryData telemetry;

    public Drone(final String droneId) {
        this.droneId = droneId;
        this.telemetry = new TelemetryData(START_SPEED, START_ALTITUDE,
                START_LATITUDE, START_LONGITUDE, START_ORIENTATION,
                START_BATTERY, LocalDateTime.now());

        // TO DO: give each drone a slightly different starting position
        // so they don't all sit on top of each other on the map.
    } // end of Drone

    public Drone(final String droneId, final TelemetryData data) {
        this.droneId = droneId;
        this.telemetry = data;
    } // end of Drone

    public boolean updateTelemetry(final TelemetryData data) {
        if (!isValidTelemetry(data)) {
            return false;
        }
        telemetry = data;
        return true;
    } // end of updateTelemetry

    public String getDroneId() {
        return droneId;
    } // end of getDroneId

    public double getSpeed() {
        return telemetry.getSpeed();
    } // end of getSpeed

    public double getAltitude() {
        return telemetry.getAltitude();
    } // end of getAltitude

    public double getLatitude() {
        return telemetry.getLatitude();
    } // end of getLatitude

    public double getLongitude() {
        return telemetry.getLongitude();
    } // end of getLongitude

    public double getOrientation() {
        return telemetry.getOrientation();
    } // end of getOrientation

    public double getBatteryLevel() {
        return telemetry.getBatteryLevel();
    } // end of getBatteryLevel

    public TelemetryData getData() {
        return telemetry;
    } // end of getData

    private boolean isValidTelemetry(final TelemetryData data) {
        if (data == null) {
            return false;
        }
        if (data.getSpeed() < 0) {
            return false;
        }
        if (data.getAltitude() < 0) {
            return false;
        }
        if (data.getBatteryLevel() < MIN_BATTERY || data.getBatteryLevel() > MAX_BATTERY) {
            return false;
        }
        if (Math.abs(data.getLatitude()) > MAX_LATITUDE) {
            return false;
        }
        if (Math.abs(data.getLongitude()) > MAX_LONGITUDE) {
            return false;
        }
        return data.getOrientation() >= 0 && data.getOrientation() < MAX_ORIENTATION;
    } // end of isValidTelemetry

} // end of Drone