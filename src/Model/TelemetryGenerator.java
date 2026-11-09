package Model;

import java.time.LocalDateTime;
import java.util.Random;

public class TelemetryGenerator {

    private static final double SECONDS_PER_UPDATE = 1.0;
    private static final double MPS_PER_MPH = 0.44704;
    private static final double METERS_PER_DEGREE = 111_320.0;
    private static final double FULL_CIRCLE = 360.0;

    private static final double NORMAL_SPEED_CHANGE = 5.0;
    private static final double NORMAL_ALTITUDE_CHANGE = 3.0;
    private static final double NORMAL_TURN = 15.0;
    private static final double BATTERY_DRAIN = 0.5;

    /** Shared Random passed in from the controller. */
    private final Random random;

    public TelemetryGenerator(final Random random) {
        this.random = random;
    } // end of TelemetryGenerator

    public TelemetryData generateTelemetry(final TelemetryData data) {
        final double speed = data.getSpeed() + randomBetween(-NORMAL_SPEED_CHANGE, NORMAL_SPEED_CHANGE);

        final double altitude = data.getAltitude()
                + randomBetween(-NORMAL_ALTITUDE_CHANGE, NORMAL_ALTITUDE_CHANGE);

        double orientation = data.getOrientation() + randomBetween(-NORMAL_TURN, NORMAL_TURN);

        // TODO: there should be limiters for weird numbers
        // TODO: small chance to add anomalies (needs Dylan's constants to be public)

        orientation = wrapAngle(orientation);

        final double[] newPosition = move(data.getLatitude(), data.getLongitude(),
                speed, orientation);

        final double batteryLevel = data.getBatteryLevel() - BATTERY_DRAIN;

        return new TelemetryData(speed, altitude, newPosition[0], newPosition[1],
                orientation, batteryLevel, LocalDateTime.now());
    } // end of generateTelemetry

    private double[] move(final double latitude, final double longitude,
                          final double speed, final double orientation) {
        final double distance = speed * MPS_PER_MPH * SECONDS_PER_UPDATE;
        final double heading = Math.toRadians(orientation);

        // cos for the north/south part, sin for the east/west part
        final double northMeters = distance * Math.cos(heading);
        final double eastMeters = distance * Math.sin(heading);

        final double newLatitude = latitude + northMeters / METERS_PER_DEGREE;
        // Longitude lines get closer together the further you are from the equator
        final double newLongitude = longitude
                + eastMeters / (METERS_PER_DEGREE * Math.cos(Math.toRadians(latitude)));

        return new double[] {newLatitude, newLongitude};
    } // end of move

    private double randomBetween(final double min, final double max) {
        return min + random.nextDouble() * (max - min);
    } // end of randomBetween

    private double wrapAngle(final double angle) {
        final double wrapped = angle % FULL_CIRCLE;
        if (wrapped < 0) {
            return wrapped + FULL_CIRCLE;
        }
        return wrapped;
    } // end of wrapAngle

} // end of TelemetryGenerator