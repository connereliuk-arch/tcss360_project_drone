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
    final private List<Drone> myOldDrones;
    final private MonitorDashboard myDashBoard;
    final private TelemetryGenerator myTeleGen;
    final private AnomalyDetector myAnomDetector;
    final private Random myRandom;


    public DroneMonitorController(
            final MonitorDashboard theDash,
            final List<Drone> theDrones,
            final List<Drone> theOldDrones,
            final TelemetryGenerator theTeleGen,
            final AnomalyDetector theAnomDetector,
            final Random theRand
            ) {
        myDashBoard = theDash;
        this.myDrones = theDrones;
        myOldDrones = theOldDrones;
        myTeleGen = theTeleGen;
        myAnomDetector = theAnomDetector;
        myRandom = theRand;

    }




}
