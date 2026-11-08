package Controller;
import java.util.*;

import Model.*;
import View.*;

/**
 *
 * @author Imran Ahmed
 *
 * */
public class DroneMonitorController {


    // For now, just hard-coding it to have 3 drones.
    private List<Drone> drones;
    final private MonitorDashboard DashBoard;
    final private TelemetryGenerator TeleGen;
    final private AnomalyDetector AnomDetector;
    final private Random random;



    public DroneMonitorController(
            final MonitorDashboard theDash,
            final List<Drone> theDrones,
            final TelemetryGenerator theTeleGen,
            final AnomalyDetector theAnomDetector,
            final Random theRand
            ) {
        DashBoard = theDash;
        drones = theDrones;
        TeleGen = theTeleGen;
        AnomDetector = theAnomDetector;
        random = theRand;
    }

    // TODO I don't like that updateAllDrones is handling both drone updating and anomaly detection.
    //  Split it into two methods somehow.
    /**
     * Updates all drones and checks for anomalies.
     * @return list of the anomalies detected.
     */
    private List<AnomalyRecord> updateAllDrones() {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        for (Drone drone : drones) {
            TelemetryData OldData = drone.getData();
            TelemetryData NewData = TeleGen.generateTelemetry(OldData);
            drone.updateTelemetry(NewData);
            // Random chance for GPS spoofing
            final int gpsChance = random.nextInt(99) + 1;
            NewData = GPS_Spoofer(NewData, gpsChance);

            // Check for anomalies
            List<AnomalyRecord> anomaliesForThisDrone = AnomDetector.detectAnomalies(OldData, NewData, drone.getDroneId());
            anomalies.addAll(anomaliesForThisDrone);
        }
        return anomalies;
    }


    // TODO Should GPS_Spoofer be in TeleGen?
    /**
     *
     * @param theOldData The input data to modify.
     * @param theChance The specific chance rolled to determine which kind of GPS spoof will occur.
     * @return New {@link TelemetryData} with the GPS data spoofed.
     */
    private TelemetryData GPS_Spoofer(final TelemetryData theOldData, final int theChance) {
        // spoofedTD is initialized to theOldData and only changed if theChance is > 82.
        // TODO Discuss having the > 82 check outside the function, to decide if the function is called at all.
        // TODO Determine how much the lat and long should change in GPS_Spoofer.
        TelemetryData spoofedTD = theOldData;
        // Chance value between 82 and 88 results in altitude spoof
        if (theChance >= 82) {
            if (theChance <= 88) {
                spoofedTD = new TelemetryData(
                        theOldData.speed(),
                        theOldData.altitude() + ((random.nextBoolean())
                                ? AnomalyDetector.MAX_ALTITUDE_CHANGE
                                : -AnomalyDetector.MAX_ALTITUDE_CHANGE),
                        theOldData.latitude(),
                        theOldData.longitude(),
                        theOldData.orientation(),
                        theOldData.batteryLevel(),
                        theOldData.timestamp()
                );
                // Chance value between 88 and 94 results in latitude spoof
            } else if (theChance <= 94) {
                spoofedTD = new TelemetryData(
                        theOldData.speed(),
                        theOldData.altitude(),
                        theOldData.latitude() + ((random.nextBoolean())
                                ? AnomalyDetector.MAX_GPS_THRESHOLD
                                : -AnomalyDetector.MAX_GPS_THRESHOLD),
                        theOldData.longitude(),
                        theOldData.orientation(),
                        theOldData.batteryLevel(),
                        theOldData.timestamp()
                );
            } else if (theChance <= 100) {
                spoofedTD = new TelemetryData(
                        theOldData.speed(),
                        theOldData.altitude(),
                        theOldData.latitude(),
                        theOldData.longitude() + ((random.nextBoolean())
                                ? AnomalyDetector.MAX_GPS_THRESHOLD
                                : -AnomalyDetector.MAX_GPS_THRESHOLD),
                        theOldData.orientation(),
                        theOldData.batteryLevel(),
                        theOldData.timestamp()
                );
            }
        }
        return spoofedTD;

    }

}
