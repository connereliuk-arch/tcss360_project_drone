package Model;

import java.time.LocalDateTime;
import java.util.Random;

public class TelemetryGenerator {

    /** Shared Random passed in from the controller. */
    private Random random;

    public TelemetryGenerator() {
        random = new Random();
    }

    public TelemetryGenerator(final Random random) {
        this.random = random;
    } // end of TelemetryGenerator

    public TelemetryData generateTelemetry(final TelemetryData data) {
        double speed = data.getSpeed();
        double altitude = data.getAltitude();
        double latitude = data.getLatitude();
        double longitude = data.getLongitude();
        double orientation = data.getOrientation();
        double batteryLevel = data.getBatteryLevel();

        // TO DO: nudge speed up or down by a small random amount
        // TO DO: nudge altitude up or down by a small random amount
        // TO DO: nudge orientation by a small random amount (keep 0 - 360)
        // TO DO: move latitude/longitude based on speed and orientation
        // TO DO: drain battery a little each update (never below 0)
        // TO DO: small random chance to create an anomaly (not GPS):
        //   - sudden altitude drop (>= 10m)
        //   - sharp turn (> 45 degrees)
        //   - speed spike (>= 20mph change)

        return new TelemetryData(speed, altitude, latitude, longitude,
                orientation, batteryLevel, LocalDateTime.now());
    } // end of generateTelemetry

} // end of TelemetryGenerator