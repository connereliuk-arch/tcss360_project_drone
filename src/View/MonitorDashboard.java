package View;

import Model.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class MonitorDashboard extends JFrame {

    // Dashboard components
    private JPanel myMainPanel;
    private JPanel myMapPanel;
    private JPanel myDroneInfoPanel;
    private JPanel myAnomalyNotificationPanel;
    private JPanel myMapViewPanel;
    private JPanel myAnomalyLogPanel;

    private JLabel myDroneIdLabel;
    private JLabel mySpeedLabel;
    private JLabel myAltitudeLabel;
    private JLabel myLatitudeLabel;
    private JLabel myLongitudeLabel;
    private JLabel myBatteryLabel;
    private JLabel myAnomalyNotificationLabel;

    private JTabbedPane myTabbedPane;
    private JTable myAnomalyTable;
    private DefaultTableModel myAnomalyTableModel;

    private JButton myMenuButton;
    private JPopupMenu myMenu;

    // Display-only state; monitoring logic will be implemented elsewhere.
    private boolean myMonitoringPaused;

    /**
     * Creates the drone monitoring dashboard.
     */
    public MonitorDashboard() {
        myMonitoringPaused = false;
        initializeDashboard();
    }

    /**
     * Initializes the dashboard window and its components.
     */
    private void initializeDashboard() {
        setTitle("Drone Monitoring Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        myMainPanel = new JPanel(new BorderLayout(10, 10));
        myMainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(myMainPanel, BorderLayout.CENTER);

        createHeader();
        createMainDisplay();
        createAnomalyNotification();
        createBottomTabs();
        createMenu();
    }

    /**
     * Creates the dashboard title at the top of the window.
     */
    private void createHeader() {
        JLabel myTitleLabel = new JLabel(
                "DRONE MONITORING DASHBOARD",
                SwingConstants.CENTER);

        myTitleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        myTitleLabel.setOpaque(true);
        myTitleLabel.setBackground(new Color(40, 55, 75));
        myTitleLabel.setForeground(Color.WHITE);
        myTitleLabel.setBorder(
                BorderFactory.createEmptyBorder(15, 10, 15, 10));

        myMainPanel.add(myTitleLabel, BorderLayout.NORTH);
    }

    /**
     * Creates the map and drone information sections.
     */
    private void createMainDisplay() {
        JPanel myContentPanel = new JPanel(new GridBagLayout());

        GridBagConstraints myConstraints = new GridBagConstraints();
        myConstraints.fill = GridBagConstraints.BOTH;
        myConstraints.insets = new Insets(5, 5, 5, 5);
        myConstraints.weighty = 1.0;

        // Map placeholder.
        myMapPanel = new JPanel(new BorderLayout());
        myMapPanel.setBackground(new Color(235, 239, 242));
        myMapPanel.setBorder(BorderFactory.createTitledBorder("Map"));

        JLabel myMapPlaceholderLabel = new JLabel(
                "Map will be displayed here",
                SwingConstants.CENTER);

        myMapPlaceholderLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        myMapPlaceholderLabel.setForeground(Color.DARK_GRAY);
        myMapPanel.add(myMapPlaceholderLabel, BorderLayout.CENTER);

        myConstraints.gridx = 0;
        myConstraints.gridy = 0;
        myConstraints.weightx = 0.65;
        myContentPanel.add(myMapPanel, myConstraints);

        // Drone information panel.
        myDroneInfoPanel = new JPanel(new GridBagLayout());
        myDroneInfoPanel.setBorder(
                BorderFactory.createTitledBorder("Drone Information"));

        GridBagConstraints myInfoConstraints = new GridBagConstraints();
        myInfoConstraints.gridx = 0;
        myInfoConstraints.anchor = GridBagConstraints.WEST;
        myInfoConstraints.fill = GridBagConstraints.HORIZONTAL;
        myInfoConstraints.insets = new Insets(10, 12, 10, 12);

        myDroneIdLabel = createInfoLabel("Drone ID: Drone-001");
        mySpeedLabel = createInfoLabel("Speed: --");
        myAltitudeLabel = createInfoLabel("Altitude: --");
        myLatitudeLabel = createInfoLabel("Latitude: --");
        myLongitudeLabel = createInfoLabel("Longitude: --");
        myBatteryLabel = createInfoLabel("Battery: --");

        JLabel[] myInfoLabels = {
                myDroneIdLabel,
                mySpeedLabel,
                myAltitudeLabel,
                myLatitudeLabel,
                myLongitudeLabel,
                myBatteryLabel
        };

        for (int myIndex = 0; myIndex < myInfoLabels.length; myIndex++) {
            myInfoConstraints.gridy = myIndex;
            myDroneInfoPanel.add(myInfoLabels[myIndex], myInfoConstraints);
        }

        myConstraints.gridx = 1;
        myConstraints.weightx = 0.35;
        myContentPanel.add(myDroneInfoPanel, myConstraints);

        myMainPanel.add(myContentPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a consistently styled drone information label.
     *
     * @param theText text displayed by the label
     * @return the configured label
     */
    private JLabel createInfoLabel(final String theText) {
        JLabel myLabel = new JLabel(theText);
        myLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        return myLabel;
    }

    /**
     * Creates the recent anomaly notification section.
     */
    private void createAnomalyNotification() {
        myAnomalyNotificationPanel = new JPanel(new BorderLayout());
        myAnomalyNotificationPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Recent Anomaly Notification"));

        myAnomalyNotificationLabel = new JLabel(
                "No recent anomalies",
                SwingConstants.LEFT);

        myAnomalyNotificationLabel.setFont(
                new Font("Arial", Font.PLAIN, 14));

        myAnomalyNotificationPanel.add(
                myAnomalyNotificationLabel,
                BorderLayout.CENTER);

        myAnomalyNotificationPanel.setPreferredSize(
                new Dimension(0, 75));

        myMainPanel.add(
                myAnomalyNotificationPanel,
                BorderLayout.SOUTH);
    }

    /**
     * Creates the bottom tabs and their associated panels.
     */
    private void createBottomTabs() {
        myTabbedPane = new JTabbedPane(
                SwingConstants.TOP,
                JTabbedPane.WRAP_TAB_LAYOUT);

        // Map View tab.
        myMapViewPanel = new JPanel(new BorderLayout());
        myMapViewPanel.add(
                new JLabel(
                        "Map View",
                        SwingConstants.CENTER),
                BorderLayout.CENTER);

        // Anomaly Log tab.
        myAnomalyLogPanel = new JPanel(new BorderLayout());

        String[] myColumnNames = {
                "Drone ID",
                "Timestamp",
                "Anomaly Type",
                "Details"
        };

        myAnomalyTableModel = new DefaultTableModel(
                myColumnNames, 0) {
            @Override
            public boolean isCellEditable(
                    final int theRow,
                    final int theColumn) {
                return false;
            }
        };

        myAnomalyTable = new JTable(myAnomalyTableModel);
        myAnomalyTable.setFillsViewportHeight(true);

        myAnomalyLogPanel.add(
                new JScrollPane(myAnomalyTable),
                BorderLayout.CENTER);

        myTabbedPane.addTab("Map View", myMapViewPanel);
        myTabbedPane.addTab("Anomaly Log", myAnomalyLogPanel);

        // Place the tabs and menu button together.
        JPanel myBottomPanel = new JPanel(new BorderLayout(5, 5));
        myBottomPanel.add(myTabbedPane, BorderLayout.CENTER);

        myMenuButton = new JButton("Menu \u2630");
        myMenuButton.setFocusable(false);
        myBottomPanel.add(myMenuButton, BorderLayout.EAST);

        myMainPanel.add(myBottomPanel, BorderLayout.PAGE_END);
    }

    /**
     * Creates the dashboard menu and its options.
     */
    private void createMenu() {
        myMenu = new JPopupMenu();

        JMenuItem myPauseItem = new JMenuItem("Pause Monitoring");
        JMenuItem mySelectDroneItem = new JMenuItem("Select Drone");
        JMenuItem mySettingsItem = new JMenuItem("Dashboard Settings");
        JMenuItem myExitItem = new JMenuItem("Exit");

        myPauseItem.addActionListener(theEvent -> {
            myMonitoringPaused = !myMonitoringPaused;

            if (myMonitoringPaused) {
                myPauseItem.setText("Resume Monitoring");
            } else {
                myPauseItem.setText("Pause Monitoring");
            }

            // This changes the display menu state only.
            // Actual monitoring control will be implemented later.
        });

        mySelectDroneItem.addActionListener(theEvent -> {
            String mySelectedDrone = (String) JOptionPane.showInputDialog(
                    this,
                    "Select a drone:",
                    "Select Drone",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[] {
                            "Drone-001",
                            "Drone-002",
                            "Drone-003"
                    },
                    "Drone-001");

            if (mySelectedDrone != null) {
                myDroneIdLabel.setText("Drone ID: " + mySelectedDrone);
            }
        });

        mySettingsItem.addActionListener(theEvent -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Dashboard settings will be implemented later.",
                    "Dashboard Settings",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        myExitItem.addActionListener(theEvent -> {
            int myChoice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit?",
                    "Exit Dashboard",
                    JOptionPane.YES_NO_OPTION);

            if (myChoice == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        myMenu.add(myPauseItem);
        myMenu.add(mySelectDroneItem);
        myMenu.add(mySettingsItem);
        myMenu.addSeparator();
        myMenu.add(myExitItem);

        myMenuButton.addActionListener(theEvent ->
                myMenu.show(
                        myMenuButton,
                        0,
                        myMenuButton.getHeight()));
    }

    /**
     * Updates the displayed telemetry for a drone.
     *
     * @param theData telemetry data to display
     * @param theDroneID identifier of the drone
     */
    public void updateDroneTelemetry(
            final TelemetryData theData,
            final String theDroneID) {

        if (theData == null) {
            return;
        }

        Runnable myUpdate = () -> {
            myDroneIdLabel.setText("Drone ID: " + theDroneID);
            mySpeedLabel.setText(
                    String.format("Speed: %.2f", theData.getSpeed()));
            myAltitudeLabel.setText(
                    String.format("Altitude: %.2f", theData.getAltitude()));
            myLatitudeLabel.setText(
                    String.format("Latitude: %.5f", theData.getLatitude()));
            myLongitudeLabel.setText(
                    String.format("Longitude: %.5f", theData.getLongitude()));
            myBatteryLabel.setText(
                    String.format("Battery: %.0f%%",
                            theData.getBatteryLevel()));
        };

        if (SwingUtilities.isEventDispatchThread()) {
            myUpdate.run();
        } else {
            SwingUtilities.invokeLater(myUpdate);
        }
    }

    /**
     * Placeholder for updating a drone's location on the map.
     *
     * @param theData telemetry data containing the drone's location
     * @param theDroneID identifier of the drone
     */
    public void updateMap(
            final TelemetryData theData,
            final String theDroneID) {
        // Map implementation will be added later.
    }

    /**
     * Displays a recent anomaly notification.
     *
     * @param theRecord anomaly to display
     */
    public void displayAnomaly(final AnomalyRecord theRecord) {
        if (theRecord == null) {
            return;
        }

        Runnable myUpdate = () -> {
            myAnomalyNotificationLabel.setText(
                    "<html><b>" + theRecord.getAnomalyType()
                            + ":</b> " + theRecord.getDetails()
                            + " (Drone: " + theRecord.getDroneId()
                            + ")</html>");

            myAnomalyTableModel.addRow(new Object[] {
                    theRecord.getDroneId(),
                    theRecord.getTimestamp().format(
                            DateTimeFormatter.ofPattern(
                                    "yyyy-MM-dd HH:mm:ss")),
                    theRecord.getAnomalyType(),
                    theRecord.getDetails()
            });
        };

        if (SwingUtilities.isEventDispatchThread()) {
            myUpdate.run();
        } else {
            SwingUtilities.invokeLater(myUpdate);
        }
    }

    /**
     * Refreshes the anomaly log with the supplied records.
     *
     * @param theRecords anomaly records to display
     */
    public void refreshAnomalyLog(
            final List<AnomalyRecord> theRecords) {

        Runnable myUpdate = () -> {
            myAnomalyTableModel.setRowCount(0);

            if (theRecords != null) {
                for (AnomalyRecord myRecord : theRecords) {
                    myAnomalyTableModel.addRow(new Object[] {
                            myRecord.getDroneId(),
                            myRecord.getTimestamp().format(
                                    DateTimeFormatter.ofPattern(
                                            "yyyy-MM-dd HH:mm:ss")),
                            myRecord.getAnomalyType(),
                            myRecord.getDetails()
                    });
                }
            }

            if (theRecords == null || theRecords.isEmpty()) {
                myAnomalyNotificationLabel.setText(
                        "No recent anomalies");
            } else {
                AnomalyRecord myLatestRecord =
                        theRecords.get(theRecords.size() - 1);

                myAnomalyNotificationLabel.setText(
                        "<html><b>"
                                + myLatestRecord.getAnomalyType()
                                + ":</b> "
                                + myLatestRecord.getDetails()
                                + " (Drone: "
                                + myLatestRecord.getDroneId()
                                + ")</html>");
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            myUpdate.run();
        } else {
            SwingUtilities.invokeLater(myUpdate);
        }
    }

    /**
     * Displays the dashboard.
     */
    public void display() {
        if (SwingUtilities.isEventDispatchThread()) {
            setVisible(true);
        } else {
            SwingUtilities.invokeLater(() -> setVisible(true));
        }
    }
}
