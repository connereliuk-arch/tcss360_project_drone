package Model;

import java.time.LocalDateTime;

/**
 * The AnomalyRecord class represents an anomaly detected during drone
 * operation.
 *
 * It stores information about the drone associated with the anomaly,
 * the telemetry data recorded when the anomaly occurred, the timestamp
 * of detection, the type of anomaly, and a description of the detected
 * condition.
 *
 * AnomalyRecord objects provide a historical record of detected
 * anomalies and allow other components to retrieve information about
 * each recorded event.
 *
 * @author Dylan Browne
 * @version 1.0
 * @see TelemetryData
 * @see AnomalyDetector
 */
public record AnomalyRecord(String myDroneId,
                            TelemetryData myData,
                            LocalDateTime myTimeStamp,
                            String myAnomalyType,
                            String myDetails) {

    /** @return the unique identifier of the drone associated with the anomaly */
    public String droneId() { return myDroneId; }

    /** @return the data associated with the anomaly */
    public TelemetryData data() { return myData; }

    /** @return the timestamp when the anomaly was detected */
    public LocalDateTime timestamp() { return myTimeStamp; }

    /** @return the classification of the detected anomaly */
    public String anomalyType() { return myAnomalyType; }

    /** @return the description of the detected anomaly */
    public String details() { return myDetails; }

}
