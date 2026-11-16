package Model;

import java.time.LocalDateTime;

public record TelemetryData(double speed,
                            double altitude,
                            double latitude,
                            double longitude,
                            double orientation,
                            double batteryLevel,
                            LocalDateTime timestamp) {

} // end of TelemetryData