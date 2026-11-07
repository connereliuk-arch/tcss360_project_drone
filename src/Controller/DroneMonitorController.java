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
    private MonitorDashboard DashBoard;
    private TelemetryGenerator TeleGen;
    private AnomalyDetector AnomDetector;
    private Random random;



    private DroneMonitorController() {
        // Initialize all the stuff that we will be using
        DashBoard = new MonitorDashboard();
        // Initializing drones list as 3 drones
        drones = new ArrayList<Drone>(List.of(
                new Drone[]{
                        new Drone("Drone 1"),
                        new Drone("Drone 2"),
                        new Drone("Drone 3")
                }));
        // TODO Send the initialized arraylist drones to the dashboard.
        //  At the time of writing this there is no way for me to add or remove drones from the dashboard
        // Initialize random to be used for random change and value generation
        random = new Random();
        // Initialize Telemetry Generator and Anomaly Detector
        TeleGen = new TelemetryGenerator(random);
        AnomDetector = new AnomalyDetector();

    }


    /**
     * Updates all drones
     * @param theDrones
     */
    private Map<String, TelemetryData> updateAllDrones(Drone[] theDrones) {
        Map<String, TelemetryData> dronesData = new HashMap<>();
        for (Drone drone : theDrones) {
            String droneName = drone.getDroneId();
            TelemetryData OldData = drone.getData();
            TelemetryData NewData = TeleGen.generateTelemetry(OldData);
            drone.updateTelemetry(NewData);
            // Random chance for GPS spoofing
            final int gpsChance = random.nextInt(99) + 1;
            NewData = GPS_Spoofer(NewData, gpsChance);

            AnomDetector.detectAnomalies(OldData, NewData, drone.getDroneId());

            dronesData.put(droneName, NewData);
        }
        return dronesData;
    }

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
                        theOldData.altitude() + ((random.nextBoolean()) ? -120 : 120),
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
                        theOldData.latitude(), // needs to change this
                        theOldData.longitude(),
                        theOldData.orientation(),
                        theOldData.batteryLevel(),
                        theOldData.timestamp()
                );
            } else if (theChance <= 100) {
                spoofedTD = new TelemetryData(
                        theOldData.speed(),
                        theOldData.altitude() + ((random.nextBoolean()) ? -120 : +120),
                        theOldData.latitude(),
                        theOldData.longitude(), // needs to change this
                        theOldData.orientation(),
                        theOldData.batteryLevel(),
                        theOldData.timestamp()
                );
            }
        }
        return spoofedTD;

    }

}
