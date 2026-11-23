package Model;

import java.time.LocalDateTime;
import java.util.Random;

public class TelemetryGenerator {

    // Matches the Thread.sleep(500) in DroneMonitorApp
    private static final double SECONDS_PER_UPDATE = 0.5;
    private static final double MPS_PER_MPH = 0.44704;
    private static final double METERS_PER_DEGREE = 111_320.0;
    private static final double FULL_CIRCLE = 360.0;

    private static final double NORMAL_SPEED_CHANGE = 5.0;
    private static final double NORMAL_ALTITUDE_CHANGE = 3.0;
    private static final double NORMAL_TURN = 15.0;
    private static final double NORMAL_MAX_SPEED = 50.0;
    private static final double NORMAL_MAX_ALTITUDE = 110.0;
    private static final double BATTERY_DRAIN = 0.5;

    private static final double ANOMALY_CHANCE = 0.01;
    private static final double ANOMALY_MARGIN = 5.0;
    private static final int ANOMALY_TYPES = 3;

    private static final double MAX_ALTITUDE_CHANGE = 10.0;
    private static final double MAX_TURN_ANGLE = 45.0;
    private static final double MAX_SPEED_CHANGE = 20.0;
    private static final double MAX_GPS_CHANGE = 50.0;

    private static final int SPOOF_MIN_CHANCE = 97;
    private static final int SPOOF_ALTITUDE_MAX_CHANCE = 97;
    private static final int SPOOF_LATITUDE_MAX_CHANCE = 98;
    private static final double GPS_SPOOF_DISTANCE = 2 * MAX_GPS_CHANGE;

    /** Shared Random passed in from the controller. */
    private final Random random;

    public TelemetryGenerator(final Random random) {
        this.random = random;
    } // end of TelemetryGenerator

    public TelemetryData generateTelemetry(final TelemetryData data) {
        double speed = data.speed() + randomBetween(-NORMAL_SPEED_CHANGE, NORMAL_SPEED_CHANGE);
        speed = clamp(speed, 0, NORMAL_MAX_SPEED);

        double altitude = data.altitude()
                + randomBetween(-NORMAL_ALTITUDE_CHANGE, NORMAL_ALTITUDE_CHANGE);
        altitude = clamp(altitude, 0, NORMAL_MAX_ALTITUDE);

        double orientation = data.orientation() + randomBetween(-NORMAL_TURN, NORMAL_TURN);

        // Small chance to throw in an anomaly on purpose (GPS ones are in gpsSpoofer)
        if (random.nextDouble() < ANOMALY_CHANCE) {
            switch (random.nextInt(ANOMALY_TYPES)) {
                case 0 -> altitude = makeAltitudeAnomaly(data.altitude());
                case 1 -> orientation = makeTurnAnomaly(data.orientation());
                default -> speed = makeSpeedAnomaly(data.speed());
            }
        }

        orientation = wrapAngle(orientation);

        final double[] newPosition = move(data.latitude(), data.longitude(),
                speed, orientation);

        final double batteryLevel = Math.max(0, data.batteryLevel() - BATTERY_DRAIN);

        return new TelemetryData(speed, altitude, newPosition[0], newPosition[1],
                orientation, batteryLevel, LocalDateTime.now());
    } // end of generateTelemetry

    public TelemetryData gpsSpoofer(final TelemetryData data, final int chance) {
        // Only spoofs if chance is 97 or higher, otherwise the data stays the same.
        // TODO Discuss having the >= 97 check outside the method, to decide if it's called at all.
        if (chance < SPOOF_MIN_CHANCE) {
            return data;
        }

        double altitude = data.altitude();
        double latitude = data.latitude();
        double longitude = data.longitude();

        if (chance <= SPOOF_ALTITUDE_MAX_CHANCE) {
            // Chance of 97 results in altitude spoof
            altitude += randomSign() * (MAX_ALTITUDE_CHANGE + ANOMALY_MARGIN);
        } else if (chance <= SPOOF_LATITUDE_MAX_CHANCE) {
            // Chance of 98 results in latitude spoof
            latitude += randomSign() * GPS_SPOOF_DISTANCE / METERS_PER_DEGREE;
        } else {
            // Chance of 99 results in longitude spoof
            longitude += randomSign() * GPS_SPOOF_DISTANCE
                    / (METERS_PER_DEGREE * Math.cos(Math.toRadians(latitude)));
        }

        return new TelemetryData(data.speed(), altitude, latitude, longitude,
                data.orientation(), data.batteryLevel(), data.timestamp());
    } // end of gpsSpoofer

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

    private double makeAltitudeAnomaly(final double altitude) {
        final double change = MAX_ALTITUDE_CHANGE + ANOMALY_MARGIN;
        // Drop if there's room, otherwise jump up (still gets flagged)
        if (altitude - change >= 0) {
            return altitude - change;
        }
        return altitude + change;
    } // end of makeAltitudeAnomaly

    private double makeTurnAnomaly(final double orientation) {
        return orientation + randomSign() * (MAX_TURN_ANGLE + ANOMALY_MARGIN);
    } // end of makeTurnAnomaly

    private double makeSpeedAnomaly(final double speed) {
        final double change = MAX_SPEED_CHANGE + ANOMALY_MARGIN;
        // Slow down if there's room, otherwise speed up (still gets flagged)
        if (speed - change >= 0) {
            return speed - change;
        }
        return speed + change;
    } // end of makeSpeedAnomaly

    private double randomBetween(final double min, final double max) {
        return min + random.nextDouble() * (max - min);
    } // end of randomBetween

    private double randomSign() {
        if (random.nextBoolean()) {
            return 1;
        }
        return -1;
    } // end of randomSign

    private double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(max, value));
    } // end of clamp

    private double wrapAngle(final double angle) {
        final double wrapped = angle % FULL_CIRCLE;
        if (wrapped < 0) {
            return wrapped + FULL_CIRCLE;
        }
        return wrapped;
    } // end of wrapAngle

} // end of TelemetryGenerator