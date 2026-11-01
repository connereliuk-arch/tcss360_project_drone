package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class AnomalyRepository {
    private final String myDatabaseUrl;

    private Connection myDatabase;

    public AnomalyRepository(final String theDatabaseUrl) {
        myDatabaseUrl = theDatabaseUrl;
    }

    public void initializeDatabase() throws SQLException {
        myDatabase = DriverManager.getConnection(myDatabaseUrl);
    }

    public boolean saveAnomaly(final AnomalyRecord theAnomaly) {
        return false;
    }

    public List<AnomalyRecord> getAllAnomalies() {

    }

    public void close() {

    }

}
