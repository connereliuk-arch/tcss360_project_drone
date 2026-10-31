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
public class AnomalyRecord {
    final String myDroneId;

    final TelemetryData myData;

    final LocalDateTime myTimeStamp;

    final String myAnomalyType;

    final String myDetails;


    /**
     * Constructs an AnomalyRecord containing information about a detected
     * drone anomaly.
     *
     * @param theDroneId the unique identifier of the drone associated with the anomaly
     * @param theData the telemetry data recorded when the anomaly occurred
     * @param theTimestamp the date and time when the anomaly was detected
     * @param theAnomalyType the classification of the detected anomaly
     * @param theDetails a description of the detected anomaly
     */
    public AnomalyRecord(String theDroneId,
                         TelemetryData theData,
                         LocalDateTime theTimestamp,
                         String theAnomalyType,
                         String theDetails) {
        myDroneId = theDroneId;
        myData = theData;
        myTimeStamp = theTimestamp;
        myAnomalyType = theAnomalyType;
        myDetails = theDetails;
    }

    /** @return the unique identifier of the drone associated with the anomaly */
    public String getDroneId() { return myDroneId; }

    /** @return the data associated with the anomaly */
    public TelemetryData getData() { return myData; }

    /** @return the timestamp when the anomaly was detected */
    public LocalDateTime getTimestamp() { return myTimeStamp; }

    /** @return the classification of the detected anomaly */
    public String getAnomalyType() { return myAnomalyType; }

    /** @return the description of the detected anomaly */
    public String getDetails() { return myDetails; }

}
