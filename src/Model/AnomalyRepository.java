package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnomalyRepository {
    private final String myDatabaseUrl;

    private Connection myDatabase;

    public AnomalyRepository(final String theDatabaseUrl) {
        myDatabaseUrl = theDatabaseUrl;
    }

    public void initializeDatabase() throws SQLException {
        final StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS anomalies ("
                + "anomaly_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "drone_id TEXT NOT NULL,"
                + "anomaly_type TEXT NOT NULL,"
                + "details TEXT,"
                + "time_stamp TEXT NOT NULL,"
                + "speed REAL NOT NULL,"
                + "altitude REAL NOT NULL,"
                + "gps_latitude REAL NOT NULL,"
                + "gps_longitude REAL NOT NULL,"
                + "orientation REAL NOT NULL,"
                + "battery_level REAL NOT NULL)");
        myDatabase = DriverManager.getConnection(myDatabaseUrl);

        PreparedStatement statement = myDatabase.prepareStatement(sql.toString());
        statement.execute();
        statement.close();
    }

    public boolean saveAnomaly(final AnomalyRecord theAnomaly) throws SQLException {
        final StringBuilder sql = new StringBuilder(
            "INSERT INTO anomalies (drone_id, anomaly_type, details, time_stamp, "
                                    + "speed, altitude, gps_latitude, "
                                    + "gps_longitude, orientation, battery_level) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );
        PreparedStatement statement = myDatabase.prepareStatement(sql.toString());
        statement.setString(1, theAnomaly.getDroneId());
        statement.setString(2, theAnomaly.getAnomalyType());
        statement.setString(3, theAnomaly.getDetails());
        statement.setString(4, theAnomaly.getTimestamp().toString());
        statement.setDouble(5, theAnomaly.getData().getSpeed());
        statement.setDouble(6, theAnomaly.getData().getAltitude());
        statement.setDouble(7, theAnomaly.getData().getLatitude());
        statement.setDouble(8, theAnomaly.getData().getLongitude());
        statement.setDouble(9, theAnomaly.getData().getOrientation());
        statement.setDouble(10, theAnomaly.getData().getBatteryLevel());

        final int rowsAffected = statement.executeUpdate();
        statement.close();

        return rowsAffected == 1;
    }

    public List<AnomalyRecord> getAllAnomalies() throws SQLException {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        final StringBuilder sql = new StringBuilder("SELECT * FROM anomalies");
        PreparedStatement statement = myDatabase.prepareStatement(sql.toString());
        ResultSet items = statement.executeQuery();


        while (items.next()) {
            TelemetryData data = new TelemetryData(
                items.getDouble("speed"),
                items.getDouble("altitude"),
                items.getDouble("gps_latitude"),
                items.getDouble("gps_longitude"),
                items.getDouble("orientation"),
                items.getDouble("battery_level"),
                LocalDateTime.now()
            );

            anomalies.add(new AnomalyRecord(
                items.getString("drone_id"),
                data,
                LocalDateTime.parse(items.getString("time_stamp")),
                items.getString("anomaly_type"),
                items.getString("details"))
            );

        }

        statement.close();
        items.close();
        return anomalies;
    }

    public void close() throws SQLException {
        myDatabase.close();
    }

}
