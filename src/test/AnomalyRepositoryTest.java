
package test;

import Model.AnomalyRecord;
import Model.AnomalyRepository;
import Model.TelemetryData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnomalyRepositoryTest {

    private AnomalyRepository myRepository;

    private final LocalDateTime myTimestamp = LocalDateTime.now();

    @BeforeEach
    void setUp() throws SQLException {
        myRepository = new AnomalyRepository("jdbc:sqlite::memory:");
        myRepository.initializeDatabase();
    }

    @AfterEach
    void tearDown() throws SQLException {
        myRepository.close();
    }

    private TelemetryData createTelemetry(
            double theSpeed,
            double theAltitude,
            double theLatitude,
            double theLongitude,
            double theOrientation,
            double theBattery) {

        return new TelemetryData(
                theSpeed,
                theAltitude,
                theLatitude,
                theLongitude,
                theOrientation,
                theBattery,
                myTimestamp
        );
    }

    private AnomalyRecord createAnomaly(
            String theDroneId,
            String theAnomalyType,
            String theDetails) {

        TelemetryData data = createTelemetry(
                30, 50, 47, 90, 80, 75
        );

        return new AnomalyRecord(
                theDroneId,
                data,
                myTimestamp,
                theAnomalyType,
                theDetails
        );
    }

    // ==========================================
    // DATABASE INITIALIZATION TESTS
    // ==========================================

    @Test
    void testInitializeDatabase() throws SQLException {
        assertNotNull(myRepository.getAllAnomalies());
    }

    @Test
    void testInitializeDatabaseTwice() throws SQLException {
        myRepository.close();

        myRepository = new AnomalyRepository("jdbc:sqlite::memory:");
        myRepository.initializeDatabase();
        myRepository.initializeDatabase();

        assertNotNull(myRepository.getAllAnomalies());
    }

    @Test
    void testInitializeDatabaseInvalidUrl() {
        AnomalyRepository invalidRepository =
                new AnomalyRepository("invalid_database_url");

        assertThrows(
                SQLException.class,
                invalidRepository::initializeDatabase
        );
    }

    // ==========================================
    // SAVE ANOMALY TESTS
    // ==========================================

    @Test
    void testSaveAnomaly() throws SQLException {
        AnomalyRecord anomaly = createAnomaly(
                "Drone-1",
                "LOW_BATTERY",
                "Battery below 15%"
        );

        assertTrue(myRepository.saveAnomaly(anomaly));
    }

    @Test
    void testSaveMultipleAnomalies() throws SQLException {
        AnomalyRecord first = createAnomaly(
                "Drone-1", "LOW_BATTERY", "Battery below threshold"
        );

        AnomalyRecord second = createAnomaly(
                "Drone-2", "GPS_CHANGE", "Unexpected GPS movement"
        );

        AnomalyRecord third = createAnomaly(
                "Drone-3", "SHARP_TURN", "Sharp orientation change"
        );

        assertTrue(myRepository.saveAnomaly(first));
        assertTrue(myRepository.saveAnomaly(second));
        assertTrue(myRepository.saveAnomaly(third));

        assertEquals(3, myRepository.getAllAnomalies().size());
    }

    @Test
    void testSaveAnomalyWithNullDetails() throws SQLException {
        AnomalyRecord anomaly = createAnomaly(
                "Drone-1",
                "LOW_BATTERY",
                null
        );

        assertTrue(myRepository.saveAnomaly(anomaly));

        List<AnomalyRecord> anomalies =
                myRepository.getAllAnomalies();

        assertEquals(1, anomalies.size());
        assertNull(anomalies.getFirst().myDetails());
    }

    @Test
    void testSaveAnomalyWithZeroTelemetryValues()
            throws SQLException {

        TelemetryData data = createTelemetry(
                0, 0, 0, 0, 0, 0
        );

        AnomalyRecord anomaly = new AnomalyRecord(
                "Drone-1",
                data,
                myTimestamp,
                "LOW_BATTERY",
                "All telemetry values are zero"
        );

        assertTrue(myRepository.saveAnomaly(anomaly));
    }

    @Test
    void testSaveAnomalyWithNegativeTelemetryValues()
            throws SQLException {

        TelemetryData data = createTelemetry(
                -10, -20, -30, -40, -50, -60
        );

        AnomalyRecord anomaly = new AnomalyRecord(
                "Drone-1",
                data,
                myTimestamp,
                "INVALID_TELEMETRY",
                "Negative telemetry values"
        );

        assertTrue(myRepository.saveAnomaly(anomaly));
    }

    // ==========================================
    // RETRIEVE ANOMALY TESTS
    // ==========================================

    @Test
    void testGetAllAnomaliesEmptyDatabase()
            throws SQLException {

        List<AnomalyRecord> anomalies =
                myRepository.getAllAnomalies();

        assertNotNull(anomalies);
        assertTrue(anomalies.isEmpty());
    }

    @Test
    void testGetAllAnomaliesSingleRecord()
            throws SQLException {

        AnomalyRecord anomaly = createAnomaly(
                "Drone-1",
                "GPS_CHANGE",
                "GPS movement exceeded 50 meters"
        );

        myRepository.saveAnomaly(anomaly);

        List<AnomalyRecord> anomalies =
                myRepository.getAllAnomalies();

        assertEquals(1, anomalies.size());
    }

    @Test
    void testGetAllAnomaliesMultipleRecords()
            throws SQLException {

        myRepository.saveAnomaly(
                createAnomaly("Drone-1", "LOW_BATTERY", "Battery low")
        );

        myRepository.saveAnomaly(
                createAnomaly("Drone-2", "GPS_CHANGE", "GPS jump")
        );

        myRepository.saveAnomaly(
                createAnomaly("Drone-3", "SHARP_TURN", "Sharp turn")
        );

        List<AnomalyRecord> anomalies =
                myRepository.getAllAnomalies();

        assertEquals(3, anomalies.size());
    }

    @Test
    void testRetrievedAnomalyDroneId()
            throws SQLException {

        myRepository.saveAnomaly(
                createAnomaly("Drone-5", "GPS_CHANGE", "GPS jump")
        );

        List<AnomalyRecord> anomalies =
                myRepository.getAllAnomalies();

        assertEquals("Drone-5", anomalies.getFirst().myDroneId());
    }

    @Test
    void testRetrievedAnomalyType()
            throws SQLException {

        myRepository.saveAnomaly(
                createAnomaly("Drone-1", "LOW_BATTERY", "Battery low")
        );

        List<AnomalyRecord> anomalies =
                myRepository.getAllAnomalies();

        assertEquals(
                "LOW_BATTERY",
                anomalies.getFirst().myAnomalyType()
        );
    }

    @Test
    void testRetrievedAnomalyDetails()
            throws SQLException {

        myRepository.saveAnomaly(
                createAnomaly("Drone-1", "GPS_CHANGE", "GPS exceeded threshold")
        );

        List<AnomalyRecord> anomalies =
                myRepository.getAllAnomalies();

        assertEquals(
                "GPS exceeded threshold",
                anomalies.getFirst().myDetails()
        );
    }

    @Test
    void testRetrievedAnomalyTimestamp()
            throws SQLException {

        myRepository.saveAnomaly(
                createAnomaly("Drone-1", "GPS_CHANGE", "GPS jump")
        );

        List<AnomalyRecord> anomalies =
                myRepository.getAllAnomalies();

        assertEquals(
                myTimestamp,
                anomalies.getFirst().myTimestamp()
        );
    }

    @Test
    void testRetrievedTelemetryData()
            throws SQLException {

        TelemetryData data = createTelemetry(
                30, 50, 47, 90, 80, 75
        );

        AnomalyRecord anomaly = new AnomalyRecord(
                "Drone-1",
                data,
                myTimestamp,
                "GPS_CHANGE",
                "GPS jump"
        );

        myRepository.saveAnomaly(anomaly);

        List<AnomalyRecord> anomalies =
                myRepository.getAllAnomalies();

        TelemetryData retrieved =
                anomalies.getFirst().myData();

        assertEquals(30, retrieved.getSpeed());
        assertEquals(50, retrieved.getAltitude());
        assertEquals(47, retrieved.getLatitude());
        assertEquals(90, retrieved.getLongitude());
        assertEquals(80, retrieved.getOrientation());
        assertEquals(75, retrieved.getBatteryLevel());
    }

    // ==========================================
    // DATABASE CONNECTION TESTS
    // ==========================================

    @Test
    void testCloseDatabase() throws SQLException {
        myRepository.close();

        assertThrows(
                SQLException.class,
                myRepository::getAllAnomalies
        );
    }
}
