package Controller;
import java.util.*;

import Model.*;
import View.*;

/**
 * Controller class that will be used in {@link DroneMonitorApp}.
 *
 * @author Imran Ahmed
 * @version Autumn 2026
 * */
public class DroneMonitorController {

    // TODO This class currently is only used for a single method.
    //  Discuss Scrapping it and moving that method up to DroneMonitorApp.

    final private List<Drone> myDrones;
    final private MonitorDashboard myDashBoard;
    final private TelemetryGenerator myTeleGen;
    final private AnomalyDetector myAnomDetector;
    final private Random myRandom;


    public DroneMonitorController(
            final MonitorDashboard theDash,
            final List<Drone> theDrones,
            final TelemetryGenerator theTeleGen,
            final AnomalyDetector theAnomDetector,
            final Random theRand
            ) {
        myDashBoard = theDash;
        this.myDrones = theDrones;
        myTeleGen = theTeleGen;
        myAnomDetector = theAnomDetector;
        myRandom = theRand;

    }

    /**
     * Updates the given {@link Drone}.
     * @return {@link TelemetryData} that has the new data and also possible GPS spoofing.
     */
    public TelemetryData updateDrone(final Drone theDrone) {
        String ID = theDrone.getDroneId();
        TelemetryData oldData = theDrone.getData();
        TelemetryData newData = myTeleGen.generateTelemetry(oldData);
        theDrone.updateTelemetry(newData);
        // Random chance for GPS spoofing
        final int gpsChance = myRandom.nextInt(99) + 1;
        newData = myTeleGen.gpsSpoofer(newData, gpsChance);
        return newData;

    }

}
