package Controller;
import Model.*;
import View.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Main class that will actually run the loop.
 * @author Imran Ahmed
 */
public class DroneMonitorApp {

    // TODO look into doing runnable or something instead of using main.


    /**
     * Main method that runs the whole thing.
     * Currently, this initializes all classes and whatnot then starts a loop for the display.
     *
     */
    static void main() throws InterruptedException, SQLException {
        // Initialize all the stuff that we will be using
        MonitorDashboard DashBoard = new MonitorDashboard();
        // Initializing drones list as 3 drones
        List<Drone> drones = new ArrayList<>(List.of(
                new Drone[]{
                        new Drone("Drone 1"),
                        new Drone("Drone 2"),
                        new Drone("Drone 3")
                }));
        // oldDrones exists to get previous data after drone update for comparison in anomaly detection.
        List<Drone> oldDrones = copyOfDrones(drones);
        Map<String, TelemetryData> droneDataMap = new HashMap<>();
        // Fill droneDataMap with the initialized drones
        for (Drone drone : drones) {
            droneDataMap.put(drone.getDroneId(), drone.getData());
        }

        // Initialize random to be used for random change and value generation
        Random random = new Random();
        // Initialize Telemetry Generator and Anomaly Detector
        TelemetryGenerator TeleGen = new TelemetryGenerator(random);
        AnomalyDetector AnomDetector = new AnomalyDetector();


        AnomalyRepository AnomRepo = new AnomalyRepository("jdbc:sqlite:drone_monitor.db");
        AnomRepo.initializeDatabase();

        DashBoard.display();
        DashBoard.updateDroneTelemetry(droneDataMap);

        // TODO make the main while loop pausable and with delays
        // This is a temporary variable being used just so the loop can run for demoing and testing.
        boolean running = true;
        do {
            try {
                // Initialize list of anomalies that will be sent to DashBoard.
                List<AnomalyRecord> anomalies = new ArrayList<>();
                // Iterate through list of drones.
                for (int i = 0; i < drones.size(); i++) {

                    Drone drone = drones.get(i);
                    Drone oldDrone = oldDrones.get(i);

                    TelemetryData newData = updateDrone(drone, TeleGen, random);
                    // Replace the data corresponding to the droneID in droneDataMap
                    // This should also add any new drones to the map
                    droneDataMap.put(drone.getDroneId(), newData);
                    // Check for anomalies
                    List<AnomalyRecord> anomaliesForThisDrone = AnomDetector.detectAnomalies(
                            oldDrone.getData(),
                            newData,
                            drone.getDroneId());
                    anomalies.addAll(anomaliesForThisDrone);
                }


                // Have dashboard show the new anomalies if there are any.
                if (!anomalies.isEmpty()) {
                    DashBoard.refreshAnomalyLog(anomalies);
                }
                for (AnomalyRecord anomaly : anomalies) {
                    AnomRepo.saveAnomaly(anomaly);
                }


                // Give DashBoard the new data
                DashBoard.updateDroneTelemetry(droneDataMap);
                // Update oldDrones for it to be used in the next iteration of the loop
                oldDrones = copyOfDrones(drones);

                // This is just here to display the changes as text until the map functions
                System.out.println(droneDataMap);
                // Currently using Thread.sleep because it's the simplest but will look into alternatives.
                // TODO Look into alternative ways to cause delays between loop iterations.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new InterruptedException("a");
            }
        } while (running);





    }

    /**
     * Updates the given {@link Drone}.
     * @return {@link TelemetryData} that has the new data and also possible GPS spoofing.
     */
    static public TelemetryData updateDrone(final Drone theDrone, final TelemetryGenerator theTeleGen, final Random theRandom) {
        String ID = theDrone.getDroneId();
        TelemetryData oldData = theDrone.getData();
        TelemetryData newData = theTeleGen.generateTelemetry(oldData);
        theDrone.updateTelemetry(newData);
        // Random chance for GPS spoofing
        final int gpsChance = theRandom.nextInt(99) + 1;
        newData = theTeleGen.gpsSpoofer(newData, gpsChance);
        return newData;

    }

    /**
     * Helper method for getting a copy of a {@link Drone} list.
     * Makes use of {@link Drone}'s shallow copy constructor. Since the only object that would share reference is a record
     * we don't need to worry about the shared reference causing issues.
     * @param theDrones The list of drones to copy.
     * @return The copied list of {@link Drone}s
     */
    private static List<Drone> copyOfDrones(final List<Drone> theDrones) {
        List<Drone> toReturn = new ArrayList<>();
        for (Drone drone : theDrones) {
            toReturn.add(new Drone(drone));
        }
        return toReturn;
    }

}
