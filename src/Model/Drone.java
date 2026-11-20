package Model;

import java.time.LocalDateTime;

public class Drone {

    private static final double START_SPEED = 0.0;
    private static final double START_ALTITUDE = 20.0;
    private static final double START_LATITUDE = 47.2454;
    private static final double START_LONGITUDE = -122.4385;
    private static final double START_ORIENTATION = 0.0;
    private static final double START_BATTERY = 100.0;
    private static final double START_SPACING = 0.001;

    private static final double MIN_BATTERY = 0.0;
    private static final double MAX_BATTERY = 100.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MAX_LONGITUDE = 180.0;
    private static final double MAX_ORIENTATION = 360.0;

    /** How many drones have been made, so each one starts in a different spot. */
    private static int dronesCreated;

    /** This drone's unique ID. */
    private final String droneId;

    /** This drone's current telemetry. */
    private TelemetryData telemetry;

    public Drone(final String droneId) {
        this.droneId = droneId;

        // Each new drone starts a little further east (about 75m) than the last one
        // so they don't all sit on top of each other on the map.
        final double startLongitude = START_LONGITUDE + dronesCreated * START_SPACING;
        dronesCreated++;

        this.telemetry = new TelemetryData(START_SPEED, START_ALTITUDE,
                START_LATITUDE, startLongitude, START_ORIENTATION,
                START_BATTERY, LocalDateTime.now());
    } // end of Drone

    public Drone(final String droneId, final TelemetryData data) {
        this.droneId = droneId;
        this.telemetry = data;
    } // end of Drone

    /**
     * Constructor used for making shallow copy.
     * Technically they end up sharing a {@link TelemetryData} but since those are records and therefor immutable, that's probably fine.
     * @param other Drone to copy.
     */
    public Drone (final Drone other) {
        this.droneId = other.droneId;
        this.telemetry = other.telemetry;
    }


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
        return telemetry.speed();
    } // end of getSpeed

    public double getAltitude() {
        return telemetry.altitude();
    } // end of getAltitude

    public double getLatitude() {
        return telemetry.latitude();
    } // end of getLatitude

    public double getLongitude() {
        return telemetry.longitude();
    } // end of getLongitude

    public double getOrientation() {
        return telemetry.orientation();
    } // end of getOrientation

    public double getBatteryLevel() {
        return telemetry.batteryLevel();
    } // end of getBatteryLevel

    public TelemetryData getData() {
        return telemetry;
    } // end of getData

    private boolean isValidTelemetry(final TelemetryData data) {
        if (data == null) {
            return false;
        }
        if (data.speed() < 0) {
            return false;
        }
        if (data.altitude() < 0) {
            return false;
        }
        if (data.batteryLevel() < MIN_BATTERY || data.batteryLevel() > MAX_BATTERY) {
            return false;
        }
        if (Math.abs(data.latitude()) > MAX_LATITUDE) {
            return false;
        }
        if (Math.abs(data.longitude()) > MAX_LONGITUDE) {
            return false;
        }
        return data.orientation() >= 0 && data.orientation() < MAX_ORIENTATION;
    } // end of isValidTelemetry

} // end of Drone