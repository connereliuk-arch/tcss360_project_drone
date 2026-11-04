package Model;

import java.time.LocalDateTime;

public class Drone {

    private static final double START_SPEED = 0.0;
    private static final double START_ALTITUDE = 20.0;
    private static final double START_LATITUDE = 47.2454;
    private static final double START_LONGITUDE = -122.4385;
    private static final double START_ORIENTATION = 0.0;
    private static final double START_BATTERY = 100.0;

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

    public boolean updateTelemetry(final TelemetryData data) {
        // TO DO: isValidTelemetry(data) - only update if the data is valid
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

    // TO DO: private boolean isValidTelemetry(final TelemetryData data)
    //   - return false if data is null
    //   - return false if battery is below 0 or above 100
    //   - return false if altitude is negative
    //   - return false if latitude/longitude are out of range

} // end of Drone