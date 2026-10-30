package Model;

import java.time.LocalDateTime;

public class AnomalyRecord {
    final String myDroneId;

    final TelemetryData myData;

    final LocalDateTime myTimeStamp;

    final String myAnomalyType;

    final String myDetails;

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

    public String getDroneId() { return myDroneId; }

    public LocalDateTime getTimestamp() { return myTimeStamp; }

    public String getAnomalyType() { return myAnomalyType; }

    public String getDetails() { return myDetails; }

}
