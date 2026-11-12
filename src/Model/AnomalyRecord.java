package Model;

import java.time.LocalDateTime;

/**
 * The AnomalyRecord class represents an anomaly detected during drone
 * operation.
 * <p>
 * It stores information about the drone associated with the anomaly,
 * the telemetry data recorded when the anomaly occurred, the timestamp
 * of detection, the type of anomaly, and a description of the detected
 * condition.
 * <p>
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
                            LocalDateTime myTimestamp,
                            String myAnomalyType,
                            String myDetails)
{}
