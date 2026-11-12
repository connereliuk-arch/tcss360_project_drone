package View;

import Model.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class MonitorDashboard extends JFrame {

    // Dashboard components
    private JPanel myMainPanel;
    private JPanel myMapPanel;
    private JPanel myDroneInfoPanel;
    private JPanel myDroneCardsPanel;
    private JPanel myAnomalyNotificationPanel;
    private JPanel myAnomalyNotificationsList;
    private JPanel myMapViewPanel;
    private JPanel myAnomalyLogPanel;

    private final Map<String, DroneLabels> myDroneLabelMap = new LinkedHashMap<>();

    private JTabbedPane myTabbedPane;
    private JTable myAnomalyTable;
    private DefaultTableModel myAnomalyTableModel;
    private TableRowSorter<DefaultTableModel> myAnomalyTableSorter;
    private JTextField myAnomalySearchField;
    private final DateTimeFormatter myAnomalyTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

        // Scrollable grid containing one telemetry card for every drone.
        myDroneInfoPanel = new JPanel(new BorderLayout());
        myDroneInfoPanel.setBorder(
                BorderFactory.createTitledBorder("Drone Information"));

        myDroneCardsPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        myDroneCardsPanel.add(new JLabel(
                "Waiting for drone telemetry...", SwingConstants.CENTER));

        JScrollPane myDroneScrollPane = new JScrollPane(myDroneCardsPanel);
        myDroneScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        myDroneInfoPanel.add(myDroneScrollPane, BorderLayout.CENTER);

        myConstraints.gridx = 1;
        myConstraints.weightx = 0.35;
        myContentPanel.add(myDroneInfoPanel, myConstraints);

        myMainPanel.add(myContentPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a scrollable area that can show simultaneous anomaly notifications.
     */
    private void createAnomalyNotification() {
        myAnomalyNotificationPanel = new JPanel(new BorderLayout());
        myAnomalyNotificationPanel.setBorder(
                BorderFactory.createTitledBorder("Recent Anomaly Notifications"));

        myAnomalyNotificationsList = new JPanel();
        myAnomalyNotificationsList.setLayout(
                new javax.swing.BoxLayout(myAnomalyNotificationsList,
                        javax.swing.BoxLayout.Y_AXIS));
        JLabel myEmptyLabel = new JLabel("No recent anomalies");
        myEmptyLabel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        myAnomalyNotificationsList.add(myEmptyLabel);

        JScrollPane myNotificationScrollPane =
                new JScrollPane(myAnomalyNotificationsList);
        myNotificationScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        myAnomalyNotificationPanel.add(
                myNotificationScrollPane, BorderLayout.CENTER);
        myAnomalyNotificationPanel.setPreferredSize(new Dimension(0, 115));
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
        myAnomalyTableSorter = new TableRowSorter<>(myAnomalyTableModel);
        myAnomalyTable.setRowSorter(myAnomalyTableSorter);

        JPanel mySearchPanel = new JPanel(new BorderLayout(8, 4));
        mySearchPanel.add(new JLabel("Search anomalies:"), BorderLayout.WEST);
        myAnomalySearchField = new JTextField();
        myAnomalySearchField.setToolTipText(
                "Filter by drone ID, timestamp, anomaly type, or details");
        myAnomalySearchField.getDocument().addDocumentListener(
                new DocumentListener() {
                    private void applySearch() {
                        String myQuery = myAnomalySearchField.getText().trim();
                        if (myQuery.isEmpty()) {
                            myAnomalyTableSorter.setRowFilter(null);
                        } else {
                            myAnomalyTableSorter.setRowFilter(
                                    RowFilter.regexFilter(
                                            "(?i)" + Pattern.quote(myQuery)));
                        }
                    }

                    @Override
                    public void insertUpdate(final DocumentEvent theEvent) {
                        applySearch();
                    }

                    @Override
                    public void removeUpdate(final DocumentEvent theEvent) {
                        applySearch();
                    }

                    @Override
                    public void changedUpdate(final DocumentEvent theEvent) {
                        applySearch();
                    }
                });
        mySearchPanel.add(myAnomalySearchField, BorderLayout.CENTER);
        mySearchPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        myAnomalyLogPanel.add(mySearchPanel, BorderLayout.NORTH);
        myAnomalyLogPanel.add(
                new JScrollPane(myAnomalyTable), BorderLayout.CENTER);

        myTabbedPane.addTab("Map View", myMapViewPanel);
        myTabbedPane.addTab("Anomaly Log", myAnomalyLogPanel);

        // Place the tabs and menu button together.
        JPanel myBottomPanel = new JPanel(new BorderLayout(5, 5));
        JPanel myBottomContentPanel = new JPanel(new BorderLayout(5, 5));
        myBottomContentPanel.add(myAnomalyNotificationPanel, BorderLayout.NORTH);
        myBottomContentPanel.add(myTabbedPane, BorderLayout.CENTER);
        myBottomPanel.add(myBottomContentPanel, BorderLayout.CENTER);

        myMenuButton = new JButton("Menu \u2630");
        myMenuButton.setFocusable(false);
        myBottomPanel.add(myMenuButton, BorderLayout.EAST);

        myMainPanel.add(myBottomPanel, BorderLayout.SOUTH);
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
            if (myDroneLabelMap.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No drone telemetry has been received yet.",
                        "Select Drone", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Object mySelectedDrone = JOptionPane.showInputDialog(
                    this, "Select a drone to locate in the telemetry list:",
                    "Select Drone", JOptionPane.PLAIN_MESSAGE, null,
                    myDroneLabelMap.keySet().toArray(),
                    myDroneLabelMap.keySet().iterator().next());
            if (mySelectedDrone != null) {
                DroneLabels myLabels = myDroneLabelMap.get(mySelectedDrone.toString());
                if (myLabels != null) {
                    myDroneCardsPanel.scrollRectToVisible(
                            myLabels.getPanel().getBounds());
                }
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
     * Updates the displayed telemetry for every drone in the supplied map.
     * The map key is the drone ID and the value is that drone's telemetry.
     * Existing drone cards are updated; newly seen drones receive a new card.
     *
     * @param theDrones map of drone IDs to telemetry data
     */
    public void updateDroneTelemetry(
            final Map<String, TelemetryData> theDrones) {
        if (theDrones == null) {
            return;
        }

        // Snapshot the map so the Swing update is not affected by caller changes.
        final Map<String, TelemetryData> myTelemetrySnapshot =
                new LinkedHashMap<>(theDrones);
        Runnable myUpdate = () -> {
            if (myDroneLabelMap.isEmpty()
                    && myDroneCardsPanel.getComponentCount() == 1
                    && myDroneCardsPanel.getComponent(0) instanceof JLabel
                    && "Waiting for drone telemetry...".equals(
                    ((JLabel) myDroneCardsPanel.getComponent(0)).getText())
                    && !myTelemetrySnapshot.isEmpty()) {
                myDroneCardsPanel.removeAll();
            }

            // Remove cards for drones that are no longer present in this snapshot.
            java.util.Iterator<Map.Entry<String, DroneLabels>> myIterator =
                    myDroneLabelMap.entrySet().iterator();
            while (myIterator.hasNext()) {
                Map.Entry<String, DroneLabels> myExisting = myIterator.next();
                if (!myTelemetrySnapshot.containsKey(myExisting.getKey())) {
                    myDroneCardsPanel.remove(myExisting.getValue().getPanel());
                    myIterator.remove();
                }
            }

            for (Map.Entry<String, TelemetryData> myEntry
                    : myTelemetrySnapshot.entrySet()) {
                String myDroneId = myEntry.getKey();
                TelemetryData myData = myEntry.getValue();
                if (myDroneId == null || myData == null) {
                    continue;
                }

                DroneLabels myLabels = myDroneLabelMap.get(myDroneId);
                if (myLabels == null) {
                    myLabels = new DroneLabels();
                    myDroneLabelMap.put(myDroneId, myLabels);
                    myDroneCardsPanel.add(myLabels.getPanel());
                }
                myLabels.setMyDroneIdLabel(myDroneId);
                myLabels.setMySpeedLabel(myData.getSpeed());
                myLabels.setMyAltitudeLabel(myData.getAltitude());
                myLabels.setMyLatitudeLabel(myData.getLatitude());
                myLabels.setMyLongitudeLabel(myData.getLongitude());
                myLabels.setMyBatteryLabel(myData.getBatteryLevel());
            }
            if (myDroneLabelMap.isEmpty()
                    && myDroneCardsPanel.getComponentCount() == 0) {
                myDroneCardsPanel.add(new JLabel(
                        "Waiting for drone telemetry...", SwingConstants.CENTER));
            }
            myDroneCardsPanel.revalidate();
            myDroneCardsPanel.repaint();
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
     * Adds an anomaly notification without replacing notifications already shown.
     *
     * @param theRecord anomaly to display
     */
    public void displayAnomaly(final AnomalyRecord theRecord) {
        if (theRecord == null) {
            return;
        }
        Runnable myUpdate = () -> {
            if (myAnomalyNotificationsList.getComponentCount() == 1
                    && myAnomalyNotificationsList.getComponent(0) instanceof JLabel
                    && "No recent anomalies".equals(
                    ((JLabel) myAnomalyNotificationsList.getComponent(0)).getText())) {
                myAnomalyNotificationsList.removeAll();
            }
            addAnomalyNotification(theRecord);
            addAnomalyToTable(theRecord);
            myAnomalyNotificationsList.revalidate();
            myAnomalyNotificationsList.repaint();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            myUpdate.run();
        } else {
            SwingUtilities.invokeLater(myUpdate);
        }
    }

    /** Adds one anomaly row using the record's actual record accessors. */
    private void addAnomalyToTable(final AnomalyRecord theRecord) {
        myAnomalyTableModel.addRow(new Object[] {
                theRecord.myDroneId(),
                theRecord.myTimeStamp() == null ? "" :
                        theRecord.myTimeStamp().format(myAnomalyTimeFormatter),
                theRecord.myAnomalyType(),
                theRecord.myDetails()
        });
    }

    /** Adds a visible notification for one anomaly. */
    private void addAnomalyNotification(final AnomalyRecord theRecord) {
        String myTimestamp = theRecord.myTimeStamp() == null ? "Unknown time"
                : theRecord.myTimeStamp().format(myAnomalyTimeFormatter);
        String myMessage = "<html><b>" + escapeHtml(theRecord.myAnomalyType())
                + "</b> — " + escapeHtml(theRecord.myDetails())
                + " (Drone: " + escapeHtml(theRecord.myDroneId())
                + ", " + myTimestamp + ")</html>";
        JLabel myNotification = new JLabel(myMessage);
        myNotification.setFont(new Font("Arial", Font.PLAIN, 13));
        myNotification.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        myNotification.setAlignmentX(LEFT_ALIGNMENT);
        myAnomalyNotificationsList.add(myNotification);
    }

    /** Escapes record text before placing it in a Swing HTML label. */
    private String escapeHtml(final String theText) {
        if (theText == null) {
            return "";
        }
        return theText.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Replaces the anomaly log and notification list with the supplied records.
     *
     * @param theRecords anomaly records to display
     */
    public void refreshAnomalyLog(final List<AnomalyRecord> theRecords) {
        final List<AnomalyRecord> myRecordSnapshot = theRecords == null
                ? new ArrayList<>() : new ArrayList<>(theRecords);
        Runnable myUpdate = () -> {
            myAnomalyTableModel.setRowCount(0);
            myAnomalyNotificationsList.removeAll();

            for (AnomalyRecord myRecord : myRecordSnapshot) {
                if (myRecord != null) {
                    addAnomalyToTable(myRecord);
                    addAnomalyNotification(myRecord);
                }
            }
            if (myAnomalyNotificationsList.getComponentCount() == 0) {
                JLabel myEmptyLabel = new JLabel("No recent anomalies");
                myEmptyLabel.setBorder(
                        BorderFactory.createEmptyBorder(4, 6, 4, 6));
                myAnomalyNotificationsList.add(myEmptyLabel);
            }
            myAnomalyNotificationsList.revalidate();
            myAnomalyNotificationsList.repaint();
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
