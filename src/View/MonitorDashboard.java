package View;

import Model.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Polygon;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import javax.swing.Timer;
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
    private OsmMapPanel myOsmMapPanel;

    private final Map<String, DroneLabels> myDroneLabelMap = new LinkedHashMap<>();

    private JTabbedPane myTabbedPane;
    private JTable myAnomalyTable;
    private DefaultTableModel myAnomalyTableModel;
    private TableRowSorter<DefaultTableModel> myAnomalyTableSorter;
    private JTextField myAnomalySearchField;
    private final DateTimeFormatter myAnomalyTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Retain every anomaly received by the dashboard for its lifetime.
    private final java.util.LinkedHashSet<AnomalyRecord> myAnomalyRecords =
            new java.util.LinkedHashSet<>();

    private JButton myMenuButton;
    private JPopupMenu myMenu;

    // The dashboard exposes this state so the monitoring loop can pause/resume.
    private volatile boolean myMonitoringPaused;

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

        // OpenStreetMap is rendered directly inside Swing; no browser is required.
        myMapPanel = new JPanel(new BorderLayout());
        myMapPanel.setBorder(BorderFactory.createTitledBorder("Live Drone Map"));
        myOsmMapPanel = new OsmMapPanel();
        myMapPanel.add(myOsmMapPanel, BorderLayout.CENTER);

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

        // The map and drone information panels are placed together inside
        // the Map View tab in createBottomTabs().
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

        // Map View tab: show the live map and scrollable drone cards side by side.
        myMapViewPanel = new JPanel(new GridBagLayout());
        GridBagConstraints myMapConstraints = new GridBagConstraints();
        myMapConstraints.fill = GridBagConstraints.BOTH;
        myMapConstraints.insets = new Insets(5, 5, 5, 5);
        myMapConstraints.gridy = 0;
        myMapConstraints.weighty = 1.0;

        myMapConstraints.gridx = 0;
        myMapConstraints.weightx = 0.68;
        myMapViewPanel.add(myMapPanel, myMapConstraints);

        myMapConstraints.gridx = 1;
        myMapConstraints.weightx = 0.32;
        myMapViewPanel.add(myDroneInfoPanel, myMapConstraints);

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

        myMainPanel.add(myBottomPanel, BorderLayout.CENTER);
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

            // The application loop reads isMonitoringPaused() and suspends work
            // until this flag becomes false.
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
            // Keep the map markers synchronized with the same telemetry snapshot
            // used to update the drone information cards.
            myOsmMapPanel.setDroneTelemetry(myTelemetrySnapshot);

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
                myLabels.setMySpeedLabel(myData.speed());
                myLabels.setMyAltitudeLabel(myData.altitude());
                myLabels.setMyLatitudeLabel(myData.latitude());
                myLabels.setMyLongitudeLabel(myData.longitude());
                myLabels.setMyBatteryLabel(myData.batteryLevel());
                myLabels.setMyOrientationLabel(myData.orientation());
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
        if (theData == null || theDroneID == null) {
            return;
        }
        Runnable myUpdate = () -> myOsmMapPanel.updateDrone(theDroneID, theData);
        if (SwingUtilities.isEventDispatchThread()) {
            myUpdate.run();
        } else {
            SwingUtilities.invokeLater(myUpdate);
        }
    }

    /**
     * Returns whether the user has paused monitoring in the dashboard.
     * The application monitoring loop must check this value before generating
     * telemetry or running anomaly detection.
     *
     * @return true while monitoring is paused
     */
    public boolean isMonitoringPaused() {
        return myMonitoringPaused;
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
            if (myAnomalyRecords.add(theRecord)) {
                rebuildAnomalyDisplay();
            }
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
                theRecord.myTimestamp() == null ? "" :
                        theRecord.myTimestamp().format(myAnomalyTimeFormatter),
                theRecord.myAnomalyType(),
                theRecord.myDetails()
        });
    }

    /** Adds a visible notification for one anomaly. */
    private void addAnomalyNotification(final AnomalyRecord theRecord) {
        String myTimestamp = theRecord.myTimestamp() == null ? "Unknown time"
                : theRecord.myTimestamp().format(myAnomalyTimeFormatter);
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
            // Merge incoming records with the existing history instead of
            // clearing the display when the caller supplies only recent data.
            for (AnomalyRecord myRecord : myRecordSnapshot) {
                if (myRecord != null) {
                    myAnomalyRecords.add(myRecord);
                }
            }
            rebuildAnomalyDisplay();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            myUpdate.run();
        } else {
            SwingUtilities.invokeLater(myUpdate);
        }
    }

    /** Rebuilds both anomaly views from the retained AnomalyRecord history. */
    private void rebuildAnomalyDisplay() {
        myAnomalyTableModel.setRowCount(0);
        myAnomalyNotificationsList.removeAll();

        for (AnomalyRecord myRecord : myAnomalyRecords) {
            addAnomalyToTable(myRecord);
            addAnomalyNotification(myRecord);
        }

        if (myAnomalyNotificationsList.getComponentCount() == 0) {
            JLabel myEmptyLabel = new JLabel("No recent anomalies");
            myEmptyLabel.setBorder(
                    BorderFactory.createEmptyBorder(4, 6, 4, 6));
            myAnomalyNotificationsList.add(myEmptyLabel);
        }
        myAnomalyNotificationsList.revalidate();
        myAnomalyNotificationsList.repaint();
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

    /**
     * Lightweight Swing OpenStreetMap viewer. Tiles are fetched directly from
     * the public OSM tile service and cached for the lifetime of this panel.
     * The panel supports mouse dragging to pan and the mouse wheel to zoom.
     */
    private static final class OsmMapPanel extends JPanel {
        private static final int TILE_SIZE = 256;
        private static final int MIN_ZOOM = 3;
        private static final int MAX_ZOOM = 19;
        private static final String TILE_URL =
                "https://tile.openstreetmap.org/%d/%d/%d.png";

        private final Map<String, TelemetryData> myDrones =
                new LinkedHashMap<>();
        private final Map<String, BufferedImage> myTileCache =
                new ConcurrentHashMap<>();
        private final Set<String> myTilesLoading =
                ConcurrentHashMap.newKeySet();
        private final Set<String> myFailedTiles =
                ConcurrentHashMap.newKeySet();
        private final ExecutorService myTileLoader =
                Executors.newFixedThreadPool(4, theRunnable -> {
                    Thread myThread = new Thread(theRunnable, "osm-tile-loader");
                    myThread.setDaemon(true);
                    return myThread;
                });

        private double myCenterLatitude = 47.2454;
        private double myCenterLongitude = -122.4385;
        private int myZoom = 13;
        private double myDragStartX;
        private double myDragStartY;
        private boolean myDragging;
        private volatile String myTileStatus =
                "Loading OpenStreetMap tiles...";
        private volatile boolean myHasLoadedTile;

        OsmMapPanel() {
            setBackground(new Color(235, 239, 242));
            setPreferredSize(new Dimension(500, 400));
            setOpaque(true);

            MouseAdapter myMouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(final MouseEvent theEvent) {
                    myDragStartX = theEvent.getX();
                    myDragStartY = theEvent.getY();
                    myDragging = true;
                }

                @Override
                public void mouseReleased(final MouseEvent theEvent) {
                    myDragging = false;
                }

                @Override
                public void mouseDragged(final MouseEvent theEvent) {
                    if (!myDragging) {
                        return;
                    }
                    double myDx = theEvent.getX() - myDragStartX;
                    double myDy = theEvent.getY() - myDragStartY;
                    Point2DWorld myCenter = project(
                            myCenterLatitude, myCenterLongitude, myZoom);
                    myCenter.x -= myDx;
                    myCenter.y -= myDy;
                    updateCenterFromWorld(myCenter);
                    myDragStartX = theEvent.getX();
                    myDragStartY = theEvent.getY();
                    repaint();
                }

                @Override
                public void mouseWheelMoved(final MouseWheelEvent theEvent) {
                    int myNewZoom = Math.max(MIN_ZOOM,
                            Math.min(MAX_ZOOM, myZoom - theEvent.getWheelRotation()));
                    if (myNewZoom != myZoom) {
                        myZoom = myNewZoom;
                        repaint();
                    }
                }
            };
            addMouseListener(myMouseHandler);
            addMouseMotionListener(myMouseHandler);
            addMouseWheelListener(myMouseHandler);

            // Repaint periodically so asynchronously loaded tiles appear promptly.
            Timer myRepaintTimer = new Timer(350, theEvent -> repaint());
            myRepaintTimer.start();
        }

        void setDroneTelemetry(final Map<String, TelemetryData> theTelemetry) {
            synchronized (myDrones) {
                myDrones.clear();
                if (theTelemetry != null) {
                    myDrones.putAll(theTelemetry);
                }
            }
            repaint();
        }

        void updateDrone(final String theDroneId, final TelemetryData theData) {
            synchronized (myDrones) {
                myDrones.put(theDroneId, theData);
            }
            repaint();
        }

        @Override
        protected void paintComponent(final Graphics theGraphics) {
            super.paintComponent(theGraphics);
            Graphics2D myGraphics = (Graphics2D) theGraphics.create();
            myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Point2DWorld myCenter = project(
                    myCenterLatitude, myCenterLongitude, myZoom);
            double myLeft = myCenter.x - getWidth() / 2.0;
            double myTop = myCenter.y - getHeight() / 2.0;
            int myFirstTileX = (int) Math.floor(myLeft / TILE_SIZE);
            int myLastTileX = (int) Math.floor(
                    (myLeft + getWidth()) / TILE_SIZE);
            int myFirstTileY = (int) Math.floor(myTop / TILE_SIZE);
            int myLastTileY = (int) Math.floor(
                    (myTop + getHeight()) / TILE_SIZE);
            int myTileCount = 1 << myZoom;

            for (int myTileX = myFirstTileX; myTileX <= myLastTileX; myTileX++) {
                for (int myTileY = myFirstTileY; myTileY <= myLastTileY; myTileY++) {
                    if (myTileY < 0 || myTileY >= myTileCount) {
                        continue;
                    }
                    int myWrappedX = Math.floorMod(myTileX, myTileCount);
                    String myKey = myZoom + "/" + myWrappedX + "/" + myTileY;
                    BufferedImage myTile = myTileCache.get(myKey);
                    int myScreenX = (int) Math.round(
                            myTileX * TILE_SIZE - myLeft);
                    int myScreenY = (int) Math.round(
                            myTileY * TILE_SIZE - myTop);
                    if (myTile != null) {
                        myGraphics.drawImage(myTile, myScreenX, myScreenY, null);
                    } else {
                        myGraphics.setColor(new Color(225, 231, 234));
                        myGraphics.fillRect(myScreenX, myScreenY,
                                TILE_SIZE, TILE_SIZE);
                        loadTile(myKey, myZoom, myWrappedX, myTileY);
                    }
                }
            }

            drawDroneMarkers(myGraphics, myLeft, myTop);

            // Display a useful status instead of silently showing a blank map
            // when the tile service cannot be reached.
            if (!myHasLoadedTile && myTileStatus != null) {
                myGraphics.setColor(new Color(255, 255, 255, 225));
                myGraphics.fillRoundRect(12, 12,
                        Math.min(getWidth() - 24, 390), 42, 10, 10);
                myGraphics.setColor(new Color(35, 45, 55));
                myGraphics.setFont(new Font("Arial", Font.BOLD, 12));
                myGraphics.drawString(myTileStatus, 22, 37);
            }

            myGraphics.setColor(new Color(255, 255, 255, 220));
            myGraphics.fillRoundRect(8, getHeight() - 28, 230, 20, 8, 8);
            myGraphics.setColor(Color.DARK_GRAY);
            myGraphics.setFont(new Font("Arial", Font.PLAIN, 11));
            myGraphics.drawString("© OpenStreetMap contributors", 14,
                    getHeight() - 14);
            myGraphics.dispose();
        }

        private void drawDroneMarkers(final Graphics2D theGraphics,
                                      final double theLeft,
                                      final double theTop) {
            Map<String, TelemetryData> mySnapshot;
            synchronized (myDrones) {
                mySnapshot = new LinkedHashMap<>(myDrones);
            }
            for (Map.Entry<String, TelemetryData> myEntry : mySnapshot.entrySet()) {
                TelemetryData myData = myEntry.getValue();
                if (myData == null) {
                    continue;
                }
                Point2DWorld myWorld = project(
                        myData.latitude(), myData.longitude(), myZoom);
                int myX = (int) Math.round(myWorld.x - theLeft);
                int myY = (int) Math.round(myWorld.y - theTop);
                if (myX < -25 || myY < -25
                        || myX > getWidth() + 25 || myY > getHeight() + 25) {
                    continue;
                }

                Graphics2D myArrowGraphics = (Graphics2D) theGraphics.create();
                myArrowGraphics.translate(myX, myY);
                // Telemetry orientation is clockwise from North: 0=N, 90=E.
                myArrowGraphics.rotate(Math.toRadians(myData.orientation()));
                Polygon myArrow = new Polygon(
                        new int[] {0, 10, 0, -10},
                        new int[] {-16, 11, 6, 11}, 4);
                myArrowGraphics.setColor(new Color(20, 95, 180));
                myArrowGraphics.fill(myArrow);
                myArrowGraphics.setColor(Color.WHITE);
                myArrowGraphics.setStroke(new java.awt.BasicStroke(2f));
                myArrowGraphics.draw(myArrow);
                myArrowGraphics.dispose();

                theGraphics.setFont(new Font("Arial", Font.BOLD, 11));
                String myLabel = myEntry.getKey();
                int myLabelWidth = theGraphics.getFontMetrics().stringWidth(myLabel);
                theGraphics.setColor(new Color(255, 255, 255, 220));
                theGraphics.fillRoundRect(myX - myLabelWidth / 2 - 4,
                        myY + 14, myLabelWidth + 8, 17, 7, 7);
                theGraphics.setColor(new Color(25, 45, 65));
                theGraphics.drawString(myLabel,
                        myX - myLabelWidth / 2, myY + 26);
            }
        }

        private void loadTile(final String theKey, final int theZoom,
                              final int theX, final int theY) {
            if (myFailedTiles.contains(theKey)
                    || !myTilesLoading.add(theKey)) {
                return;
            }
            myTileLoader.submit(() -> {
                HttpURLConnection myConnection = null;
                try {
                    URL myUrl = new URL(String.format(TILE_URL,
                            theZoom, theX, theY));
                    myConnection = (HttpURLConnection) myUrl.openConnection();
                    myConnection.setRequestProperty("User-Agent",
                            "TCSS360DroneMonitor/1.0 (Java Swing desktop map)");
                    myConnection.setConnectTimeout(10000);
                    myConnection.setReadTimeout(10000);
                    int myResponseCode = myConnection.getResponseCode();
                    if (myResponseCode != HttpURLConnection.HTTP_OK) {
                        myFailedTiles.add(theKey);
                        myTileStatus = "OpenStreetMap tile server returned HTTP "
                                + myResponseCode;
                    } else {
                        try (var myInput = myConnection.getInputStream()) {
                            BufferedImage myImage =
                                    javax.imageio.ImageIO.read(myInput);
                            if (myImage != null) {
                                myTileCache.put(theKey, myImage);
                                myHasLoadedTile = true;
                                myTileStatus = null;
                            } else {
                                myTileStatus = "Map tile response was not a PNG image.";
                            }
                        }
                    }
                } catch (IOException myException) {
                    myFailedTiles.add(theKey);
                    myTileStatus = "Map tiles unavailable: "
                            + myException.getClass().getSimpleName()
                            + (myException.getMessage() == null ? ""
                            : " - " + myException.getMessage());
                } finally {
                    if (myConnection != null) {
                        myConnection.disconnect();
                    }
                    myTilesLoading.remove(theKey);
                    SwingUtilities.invokeLater(this::repaint);
                }
            });
        }

        private void updateCenterFromWorld(final Point2DWorld theWorld) {
            double myWorldSize = TILE_SIZE * (double) (1 << myZoom);
            double myX = ((theWorld.x % myWorldSize) + myWorldSize) % myWorldSize;
            double myY = Math.max(0, Math.min(myWorldSize, theWorld.y));
            double myLongitude = myX / myWorldSize * 360.0 - 180.0;
            double myN = Math.PI - 2.0 * Math.PI * myY / myWorldSize;
            double myLatitude = Math.toDegrees(Math.atan(Math.sinh(myN)));
            myCenterLatitude = myLatitude;
            myCenterLongitude = myLongitude;
        }

        private static Point2DWorld project(final double theLatitude,
                                            final double theLongitude,
                                            final int theZoom) {
            double myLat = Math.max(-85.05112878,
                    Math.min(85.05112878, theLatitude));
            double myWorldSize = TILE_SIZE * (double) (1 << theZoom);
            double myX = (theLongitude + 180.0) / 360.0 * myWorldSize;
            double mySinLatitude = Math.sin(Math.toRadians(myLat));
            double myY = (0.5 - Math.log(
                    (1 + mySinLatitude) / (1 - mySinLatitude))
                    / (4 * Math.PI)) * myWorldSize;
            return new Point2DWorld(myX, myY);
        }

        private static final class Point2DWorld {
            private double x;
            private double y;

            Point2DWorld(final double theX, final double theY) {
                x = theX;
                y = theY;
            }
        }
    }

}
