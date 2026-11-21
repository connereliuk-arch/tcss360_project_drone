package Model;

public interface AnomalyCheck {
    AnomalyRecord detect(final TelemetryData thePrevious,
                        final TelemetryData theCurrent,
                        final String theDroneId);
}
