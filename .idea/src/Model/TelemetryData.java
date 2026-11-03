package Model;

import java.time.LocalDateTime;

public record TelemetryData(double speed,
                            double altitude,
                            double latitude,
                            double longitude,
                            double orientation,
                            double batteryLevel,
                            LocalDateTime timestamp) {

    // Note: records already make accessors like speed() and altitude()
    // automatically. The getX() methods below match the names in our UML.

    public double getSpeed() {
        return speed;
    } // end of getSpeed

    public double getAltitude() {
        return altitude;
    } // end of getAltitude

    public double getLatitude() {
        return latitude;
    } // end of getLatitude

    public double getLongitude() {
        return longitude;
    } // end of getLongitude

    public double getOrientation() {
        return orientation;
    } // end of getOrientation

    public double getBatteryLevel() {
        return batteryLevel;
    } // end of getBatteryLevel

    public LocalDateTime getTimestamp() {
        return timestamp;
    } // end of getTimestamp

} // end of TelemetryData