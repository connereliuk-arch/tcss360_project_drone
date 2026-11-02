package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
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
                + "time_stamp TEXT NOT NULL,"
                + "details TEXT)");
        myDatabase = DriverManager.getConnection(myDatabaseUrl);

        PreparedStatement statement = myDatabase.prepareStatement(sql.toString());
        statement.execute();
        statement.close();
    }

    public boolean saveAnomaly(final AnomalyRecord theAnomaly) throws SQLException {
        final StringBuilder sql = new StringBuilder(
                "INSERT INTO anomalies (drone_id, anomaly_type, details, time_stamp) "
                + "VALUES (?, ?, ?, ?)");
        PreparedStatement statement = myDatabase.prepareStatement(sql.toString());
        statement.setString(1, theAnomaly.getDroneId());
        statement.setString(2, theAnomaly.getAnomalyType());
        statement.setString(3, theAnomaly.getDetails());
        statement.setString(4, theAnomaly.getTimestamp().toString());

        final int rowsAffected = statement.executeUpdate();
        statement.close();

        return rowsAffected == 1;
    }

    public List<AnomalyRecord> getAllAnomalies() {

    }

    public void close() throws SQLException {
        myDatabase.close();
    }

}
