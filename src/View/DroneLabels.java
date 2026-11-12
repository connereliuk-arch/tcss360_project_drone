package View;

import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Holds the telemetry labels for one drone and its display panel.
 */
public class DroneLabels {
    private final JPanel myPanel;
    private final JLabel myDroneIdLabel;
    private final JLabel mySpeedLabel;
    private final JLabel myAltitudeLabel;
    private final JLabel myLatitudeLabel;
    private final JLabel myLongitudeLabel;
    private final JLabel myBatteryLabel;

    /** Creates a complete set of initialized labels for one drone. */
    public DroneLabels() {
        myPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        myPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        myDroneIdLabel = createLabel("Drone ID: --", Font.BOLD);
        mySpeedLabel = createLabel("Speed: --", Font.PLAIN);
        myAltitudeLabel = createLabel("Altitude: --", Font.PLAIN);
        myLatitudeLabel = createLabel("Latitude: --", Font.PLAIN);
        myLongitudeLabel = createLabel("Longitude: --", Font.PLAIN);
        myBatteryLabel = createLabel("Battery: --", Font.PLAIN);

        myPanel.add(myDroneIdLabel);
        myPanel.add(mySpeedLabel);
        myPanel.add(myAltitudeLabel);
        myPanel.add(myLatitudeLabel);
        myPanel.add(myLongitudeLabel);
        myPanel.add(myBatteryLabel);
    }

    private JLabel createLabel(final String theText, final int theStyle) {
        JLabel myLabel = new JLabel(theText);
        myLabel.setFont(new Font("Arial", theStyle, 14));
        return myLabel;
    }

    /** @return the panel containing this drone's telemetry labels */
    public JPanel getPanel() {
        return myPanel;
    }

    // Setters for drone labels.
    public void setMyDroneIdLabel(final String theDroneId) {
        myDroneIdLabel.setText("Drone ID: " + theDroneId);
    }

    public void setMySpeedLabel(final double theSpeed) {
        mySpeedLabel.setText(String.format("Speed: %.2f", theSpeed));
    }

    public void setMyAltitudeLabel(final double theAltitude) {
        myAltitudeLabel.setText(String.format("Altitude: %.2f", theAltitude));
    }

    public void setMyLatitudeLabel(final double theLatitude) {
        myLatitudeLabel.setText(String.format("Latitude: %.5f", theLatitude));
    }

    public void setMyLongitudeLabel(final double theLongitude) {
        myLongitudeLabel.setText(String.format("Longitude: %.5f", theLongitude));
    }

    public void setMyBatteryLabel(final double theBattery) {
        myBatteryLabel.setText(String.format("Battery: %.0f%%", theBattery));
    }

    // Getters for drone labels.
    public JLabel getMyDroneIdLabel() { return myDroneIdLabel; }
    public JLabel getMySpeedLabel() { return mySpeedLabel; }
    public JLabel getMyAltitudeLabel() { return myAltitudeLabel; }
    public JLabel getMyLatitudeLabel() { return myLatitudeLabel; }
    public JLabel getMyLongitudeLabel() { return myLongitudeLabel; }
    public JLabel getMyBatteryLabel() { return myBatteryLabel; }
}
