package Controller;
import Model.*;
import View.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main class that will actually run the loop.
 * @author Imran Ahmed
 */
public class DroneMonitorApp {

    // TODO look into doing runnable or something instead of using main

    static void main() {
        // Initialize all the stuff that we will be using
        MonitorDashboard DashBoard = new MonitorDashboard();
        // Initializing drones list as 3 drones
        List<Drone> drones = new ArrayList<>(List.of(
                new Drone[]{
                        new Drone("Drone 1"),
                        new Drone("Drone 2"),
                        new Drone("Drone 3")
                }));
        // TODO Send the initialized arraylist drones to the dashboard.
        //  At the time of writing this there is no way for me to add or remove drones from the dashboard
        // Initialize random to be used for random change and value generation
        Random random = new Random();
        // Initialize Telemetry Generator and Anomaly Detector
        TelemetryGenerator TeleGen = new TelemetryGenerator(random);
        AnomalyDetector AnomDetector = new AnomalyDetector();
        DroneMonitorController Controller = new DroneMonitorController(
                DashBoard, drones, TeleGen, AnomDetector, random
        );
        DashBoard.display();





    }

}
