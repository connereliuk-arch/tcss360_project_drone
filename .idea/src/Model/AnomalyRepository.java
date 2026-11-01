package Model;

import java.util.List;

public class AnomalyRepository {
    private final String myDatabaseUrl;

    public AnomalyRepository(final String theDatabaseUrl) {
        myDatabaseUrl = theDatabaseUrl;
    }

    public static void initializeDatabase() {

    }

    public boolean saveAnomaly(final AnomalyRecord theAnomaly) {
        return false;
    }

    public List<AnomalyRecord> getAllAnomalies() {

    }

    public void close() {

    }

}
