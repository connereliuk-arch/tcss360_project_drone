package test;

import Model.AnomalyDetector;
import Model.AnomalyRecord;
import Model.TelemetryData;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

class AnomalyDetectorTest {
    private final AnomalyDetector detector = new AnomalyDetector();

    /*
     * Helper method to create telemetry data.
     * Parameter order:
     * speed, altitude, latitude, longitude, orientation, batteryLevel, timestamp
     */
    private TelemetryData createTelemetry(
            double speed,
            double altitude,
            double latitude,
            double orientation,
            double batteryLevel) {

        return new TelemetryData(
                speed,
                altitude,
                latitude,
                -122,
                orientation,
                batteryLevel,
                LocalDateTime.now()
        );
    }

    private List<String> getAnomalyTypes(
            TelemetryData previous, TelemetryData current) {

        return detector.detectAnomalies(previous, current, "Drone-1")
                .stream()
                .map(AnomalyRecord::myAnomalyType)
                .collect(Collectors.toList());
    }

    // ------------------------------------------
    // Constructor
    // ------------------------------------------

    @Test
    void testConstructor() {
        assertNotNull(new AnomalyDetector());
    }

    // ------------------------------------------
    // detectAnomalies()
    // ------------------------------------------

    @Test
    void testDetectAnomaliesNoAnomalies() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(32, 55, 47, 100, 80);

        List<AnomalyRecord> records =
                detector.detectAnomalies(previous, current, "Drone-1");

        assertTrue(records.isEmpty());
    }

    @Test
    void testDetectAnomaliesMultipleAnomalies() {
        TelemetryData previous = createTelemetry(20, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(65, 65, 47, 150, 10);

        List<String> anomalies = getAnomalyTypes(previous, current);

        assertTrue(anomalies.contains("LOW_BATTERY"));
        assertTrue(anomalies.contains("ALTITUDE_CHANGE"));
        assertTrue(anomalies.contains("SPEED_CHANGE"));
        assertTrue(anomalies.contains("HIGH_SPEED"));
        assertTrue(anomalies.contains("TURN_ANGLE"));
    }

    @Test
    void testDetectAnomaliesDroneId() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47, 90, 10);

        List<AnomalyRecord> records =
                detector.detectAnomalies(previous, current, "Drone-5");
        assertEquals("Drone-5", records.getFirst().myDroneId());
    }

    // ------------------------------------------
    // checkLowBattery()
    // ------------------------------------------

    @Test
    void testLowBattery() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47, 90, 14);

        assertTrue(getAnomalyTypes(previous, current).contains("LOW_BATTERY"));
    }

    @Test
    void testLowBatteryAtThreshold() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47, 90, 15);

        assertTrue(getAnomalyTypes(previous, current).contains("LOW_BATTERY"));
    }

    @Test
    void testBatteryAboveThreshold() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47, 90, 16);

        assertFalse(getAnomalyTypes(previous, current).contains("LOW_BATTERY"));
    }

    // ------------------------------------------
    // checkAltitudeChange()
    // ------------------------------------------

    @Test
    void testAltitudeChange() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 65, 47, 90, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("ALTITUDE_CHANGE"));
    }

    @Test
    void testAltitudeChangeBelowThreshold() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 59, 47, 90, 80);

        assertFalse(getAnomalyTypes(previous, current).contains("ALTITUDE_CHANGE"));
    }

    @Test
    void testAltitudeChangeAtThreshold() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 60, 47, 90, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("ALTITUDE_CHANGE"));
    }

    @Test
    void testNegativeAltitudeChange() {
        TelemetryData previous = createTelemetry(30, 60, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47, 90, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("ALTITUDE_CHANGE"));
    }

    // ------------------------------------------
    // checkAltitudeHeight()
    // ------------------------------------------

    @Test
    void testAltitudeHeightExceeded() {
        TelemetryData previous = createTelemetry(30, 100, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 121, 47, 90, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("DRONE_ALTITUDE"));
    }

    @Test
    void testAltitudeHeightAtMaximum() {
        TelemetryData previous = createTelemetry(30, 120, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 120, 47, 90, 80);

        assertFalse(getAnomalyTypes(previous, current).contains("DRONE_ALTITUDE"));
    }

    // ------------------------------------------
    // checkSpeedChange()
    // ------------------------------------------

    @Test
    void testSpeedChange() {
        TelemetryData previous = createTelemetry(20, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(45, 50, 47, 90, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("SPEED_CHANGE"));
    }

    @Test
    void testSpeedChangeAtThreshold() {
        TelemetryData previous = createTelemetry(20, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(40, 50, 47, 90, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("SPEED_CHANGE"));
    }

    @Test
    void testSpeedChangeBelowThreshold() {
        TelemetryData previous = createTelemetry(20, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(39, 50, 47, 90, 80);

        assertFalse(getAnomalyTypes(previous, current).contains("SPEED_CHANGE"));
    }

    @Test
    void testNegativeSpeedChange() {
        TelemetryData previous = createTelemetry(45, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(20, 50, 47, 90, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("SPEED_CHANGE"));
    }

    // ------------------------------------------
    // checkSpeed()
    // ------------------------------------------

    @Test
    void testHighSpeed() {
        TelemetryData previous = createTelemetry(50, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(61, 50, 47, 90, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("HIGH_SPEED"));
    }

    @Test
    void testSpeedAtMaximum() {
        TelemetryData previous = createTelemetry(60, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(60, 50, 47, 90, 80);

        assertFalse(getAnomalyTypes(previous, current).contains("HIGH_SPEED"));
    }

    // ------------------------------------------
    // calculateDistance() / checkGpsChange()
    // ------------------------------------------

    @Test
    void testGpsChange() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47.001, 90, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("GPS_CHANGE"));
    }

    @Test
    void testGpsNoChange() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47, 90, 80);

        assertFalse(getAnomalyTypes(previous, current).contains("GPS_CHANGE"));
    }

    @Test
    void testGpsChangeUnderThreshold() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47.000225, 90, 80);

        assertFalse(getAnomalyTypes(previous, current).contains("GPS_CHANGE"));
    }

    @Test
    void testGpsChangeAtThreshold() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47.0004496608, 90, 80);

        assertFalse(getAnomalyTypes(previous, current).contains("GPS_CHANGE"));
    }

    @Test
    void testGpsChangeAboveThreshold() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47.000460, 90, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("GPS_CHANGE"));
    }

    @Test
    void testGpsLargeJump() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 48, 90, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("GPS_CHANGE"));
    }

    // ------------------------------------------
    // checkTurnAngle()
    // ------------------------------------------

    @Test
    void testTurnAngleExceeded() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47, 136, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("TURN_ANGLE"));
    }

    @Test
    void testTurnAngleAtThreshold() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47, 135, 80);

        assertFalse(getAnomalyTypes(previous, current).contains("TURN_ANGLE"));
    }

    @Test
    void testNegativeTurnAngle() {
        TelemetryData previous = createTelemetry(30, 50, 47, 90, 80);
        TelemetryData current = createTelemetry(30, 50, 47, 30, 80);

        assertTrue(getAnomalyTypes(previous, current).contains("TURN_ANGLE"));
    }

}
