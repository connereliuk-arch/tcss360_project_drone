package Controller;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import Model.*;
import View.*;


public class DroneMonitorController {

    // For now, just hard-coding it to have 3 drones.
    private Drone[] drones;
    private MonitorDashboard DashBoard;
    private TelemetryGenerator TeleGen;
    private AnomalyDetector AnomDetector;
    private Random random;


    static void main() {
        // Boolean value for if the while loop should play, starts as true
        boolean playing = true;


        // I basically haven't gotten to test this because my IDE is tripping over itself constantly

        try {
            // Using while true just for testing purposes for now, no plans of actually using just true
            // in actual delivered product.
            while (true) {
                while (!playing) {
                    // From what I understand, using Thread.sleep is like not a super good idea so I will
                    // look into alternatives. Thread.sleep for now.
                    Thread.sleep(500);

                }


                Thread.sleep(500);
            }
        } catch (InterruptedException e) {}




    }

    private void DroneMonitor() {
        // Initialize all the stuff that we will be using
        DashBoard = new MonitorDashboard();
        // This is AWFUL and I am hardcoding it to have 3 drones that are instantiated in DroneMonitor's constructor
        // I am ashamed of myself.
        drones = new Drone[]{new Drone(), new Drone(), new Drone()};
        TeleGen = new TelemetryGenerator(); // According to trace in document, this will be instantiated with random
        AnomDetector = new AnomalyDetector();
        // Initialize random to be used for random change and value generation
        random = new Random();
    }

    /**
     * Updates all drones
     * @param theDrones
     */
    private Map<String, TelemetryData> updateAllDrones(Drone[] theDrones) {
        Map<String, TelemetryData> dronesData = new HashMap<>();
        for (Drone drone : theDrones) {
            String droneName = drone.getID;
            TelemetryData OldData = drone.getData;
            TelemetryData NewData = theTelGen.generateTelemetry(OldData);
            done.updateTelemetry(NewData);
            // Random chance for GPS spoofing
            if (random.nextInt(100) + 1 > 85) {
                // Here we would create a modified copy of NewData but with innacurate GPS data, and set NewData to refer to that
            }

            dronesData.put(droneName, NewData);
        }
    }

}
