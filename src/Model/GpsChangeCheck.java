package Model;

import java.time.LocalDateTime;

public class GpsChangeCheck implements AnomalyCheck {
    /**
     * The max distance a drone can travel between intervals in meters
     */
    public final double myMaxGpsThreshold;

    public GpsChangeCheck(final double theThreshold) {
        myMaxGpsThreshold = theThreshold;
    }

    @Override
    public AnomalyRecord detect(TelemetryData thePrevious, TelemetryData theCurrent, String theDroneId) {
        final LocalDateTime timestamp = LocalDateTime.now();
        AnomalyRecord record = null;

        final double distanceTraveled = calculateDistance(thePrevious, theCurrent);

        if (distanceTraveled > myMaxGpsThreshold) {
            final String anomalyType = "GPS_CHANGE";
            final String details = "The GPS position has changed too much from ("
                    + thePrevious.latitude() + ", " + thePrevious.longitude() + ") to ("
                    + theCurrent.latitude() + ", " + theCurrent.longitude() + ").";

            record = new AnomalyRecord(theDroneId, theCurrent, timestamp, anomalyType, details);
        }

        return record;
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
        double lat1 = thePrevious.latitude();
        double lon1 = thePrevious.longitude();
        double lat2 = theCurrent.latitude();
        double lon2 = theCurrent.longitude();

        // Convert degrees to radians
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        // Calculate differences
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        // Haversine formula
        double a = Math.min(1.0, Math.max(0.0, Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2)));

        // Calculate distance in meters
        double earthRadius = 6371000;

        return 2 * earthRadius * Math.asin(Math.sqrt(a));
    }
}
