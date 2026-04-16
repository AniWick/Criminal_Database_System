package view;

import app.AppContext;
import facade.CriminalManagement;
import model.*;
import service.*;
import strategy.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModernCriminalManagementGUI extends JFrame {
    private static final Color PAGE_BG = new Color(244, 247, 252);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color PRIMARY = new Color(20, 61, 89);
    private static final Color ACCENT = new Color(0, 122, 204);
    private static final Color NAV_BG = new Color(232, 236, 243);
    private static final Color NAV_ACTIVE = new Color(59, 69, 89);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 27);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);

    private CriminalManagement management;
    private JTabbedPane tabbedPane;
    private String loggedInUser;
    private String userRole;
    private JLabel statusLabel;
    private JLabel notificationLabel;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Map<String, Supplier<JPanel>> tabFactories = new LinkedHashMap<>();
    private final Map<String, JPanel> loadedTabs = new LinkedHashMap<>();
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private boolean seededSessionData;
    
    // Services for new features
    private PermissionService permissionService;
    private AlertService alertService;
    private EvidenceChainService chainService;
    private NotificationService notificationService;

    public ModernCriminalManagementGUI() {
        setTitle("Criminal Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 740);
        setLocationRelativeTo(null);
        setResizable(true);
        UIManager.put("Button.background", ACCENT);
        UIManager.put("Button.foreground", Color.WHITE);
        
        initializeServices();
        applyModernTheme();
        showLoginPanel();
    }

    private void initializeServices() {
        permissionService = new PermissionService();
        alertService = new AlertService();
        chainService = new EvidenceChainService();
        notificationService = new NotificationService();
        
        // Setup notification listener
        notificationService.addListener(notification -> {
            updateNotificationCount();
        });
    }

    private void applyModernTheme() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Ignore and keep default LAF.
            }
        }
    }

    private void showLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(PAGE_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Criminal Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 30, 10);
        loginPanel.add(titleLabel, gbc);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        loginPanel.add(userLabel, gbc);

        JTextField userField = new JTextField(20);
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        userField.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(passField, gbc);

        JButton loginBtn = createStyledButton("Login", 100, 35);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginPanel.add(loginBtn, gbc);

        JLabel demoLabel = new JLabel("<html><b>Demo Credentials:</b><br>Username: admin | Password: admin123<br>Username: detective | Password: detect123</html>");
        demoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        demoLabel.setForeground(new Color(100, 100, 100));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        loginPanel.add(demoLabel, gbc);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            management = AppContext.getInstance().getCriminalManagement();
            if (management.login(username, password)) {
                loggedInUser = username;
                seedOperationalData();
                showMainInterface();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passField.setText("");
            }
        });

        setContentPane(loginPanel);
        setVisible(true);
    }

    private void showMainInterface() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PAGE_BG);
        JPanel shellPanel = new JPanel(new BorderLayout());
        shellPanel.setBorder(new EmptyBorder(14, 14, 14, 14));
        shellPanel.setBackground(PAGE_BG);

        // Tabbed Pane content (header hidden; controlled from sidebar buttons)
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(CARD_BG);
        tabbedPane.setFont(FONT_BODY);
        tabbedPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        initializeLazyTabs();
        hideTabHeaders();
        tabbedPane.addChangeListener(e -> {
            loadSelectedTab();
            updateSidebarSelection();
        });
        loadSelectedTab();
        updateSidebarSelection();

        JPanel contentPanel = new JPanel(new BorderLayout(0, 12));
        contentPanel.setBackground(PAGE_BG);
        contentPanel.add(createCommandHeader(), BorderLayout.NORTH);
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        shellPanel.add(createSidebar(), BorderLayout.WEST);
        shellPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(shellPanel, BorderLayout.CENTER);

        // Status bar with notification count
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(255, 255, 255));
        statusPanel.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        JPanel statusLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLeftPanel.setOpaque(false);
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        notificationLabel = new JLabel("Notifications: 0");
        notificationLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        notificationLabel.setForeground(new Color(220, 20, 60));
        notificationLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusLeftPanel.add(statusLabel);
        statusLeftPanel.add(notificationLabel);
        statusPanel.add(statusLeftPanel, BorderLayout.WEST);
        shellPanel.add(statusPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        if (!isVisible()) setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(NAV_BG);
        sidebar.setBorder(new CompoundBorder(new LineBorder(new Color(215, 220, 229), 1), new EmptyBorder(16, 14, 16, 14)));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);
        JLabel brandIcon = new JLabel("◉");
        brandIcon.setForeground(PRIMARY);
        brandIcon.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20));
        JLabel brand = new JLabel("Criminal CMS");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(new Color(44, 49, 59));
        brandPanel.add(brandIcon);
        brandPanel.add(brand);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        JPanel navList = new JPanel();
        navList.setOpaque(false);
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));

        String[] labels = new String[] {
            "Dashboard", "Criminals", "Advanced Search", "Cases", "Evidence",
            "Chain of Custody", "Biometrics", "Wanted List", "Notifications", "Reports"
        };

        navButtons.clear();
        for (String label : labels) {
            JButton navBtn = new JButton(label);
            navBtn.setHorizontalAlignment(SwingConstants.LEFT);
            navBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            navBtn.setBackground(NAV_BG);
            navBtn.setForeground(new Color(66, 74, 88));
            navBtn.setBorder(new EmptyBorder(10, 12, 10, 12));
            navBtn.setFocusPainted(false);
            navBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            navBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            navBtn.addActionListener(e -> selectTab(label));
            navButtons.put(label, navBtn);
            navList.add(navBtn);
            navList.add(Box.createVerticalStrut(6));
        }

        sidebar.add(navList, BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel createCommandHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(new CompoundBorder(new LineBorder(new Color(219, 224, 232), 1), new EmptyBorder(14, 18, 14, 18)));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel title = new JLabel("Case Workspace");
        title.setFont(FONT_TITLE);
        title.setForeground(new Color(32, 39, 50));
        JLabel sub = new JLabel("Logged in as " + loggedInUser + " • " + LocalDateTime.now().format(dateFormatter));
        sub.setFont(FONT_BODY);
        sub.setForeground(new Color(109, 118, 131));
        left.add(title);
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JButton markResolvedBtn = createStyledButton("Close as Won", 120, 34);
        JButton markLostBtn = createStyledButton("Close as Lost", 120, 34);
        JButton logoutBtn = createStyledButton("Logout", 90, 34);

        markResolvedBtn.addActionListener(e -> handleCaseClosure("Won"));
        markLostBtn.addActionListener(e -> handleCaseClosure("Lost"));

        logoutBtn.addActionListener(e -> {
            getContentPane().removeAll();
            showLoginPanel();
            repaint();
        });
        right.add(markResolvedBtn);
        right.add(markLostBtn);
        right.add(logoutBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private void hideTabHeaders() {
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                return 0;
            }

            @Override
            protected int calculateTabAreaWidth(int tabPlacement, int vertRunCount, int maxTabWidth) {
                return 0;
            }
        });
    }

    private void selectTab(String tabName) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabName.equals(tabbedPane.getTitleAt(i))) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }
    }

    private void updateSidebarSelection() {
        if (tabbedPane.getSelectedIndex() < 0) {
            return;
        }
        String selectedTab = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean active = entry.getKey().equals(selectedTab);
            JButton button = entry.getValue();
            button.setBackground(active ? NAV_ACTIVE : NAV_BG);
            button.setForeground(active ? Color.WHITE : new Color(66, 74, 88));
        }
    }

    private void initializeLazyTabs() {
        tabFactories.clear();
        loadedTabs.clear();

        tabFactories.put("Dashboard", this::createDashboardPanel);
        tabFactories.put("Criminals", this::createCriminalsPanel);
        tabFactories.put("Advanced Search", this::createAdvancedSearchPanel);
        tabFactories.put("Cases", this::createCasesPanel);
        tabFactories.put("Evidence", this::createEvidencePanel);
        tabFactories.put("Chain of Custody", this::createChainOfCustodyPanel);
        tabFactories.put("Biometrics", this::createBiometricsPanel);
        tabFactories.put("Wanted List", this::createWantedListPanel);
        tabFactories.put("Notifications", this::createNotificationsPanel);
        tabFactories.put("Reports", this::createReportsPanel);

        for (String tabName : tabFactories.keySet()) {
            tabbedPane.addTab(tabName, createLoadingPanel());
            int index = tabbedPane.indexOfTab(tabName);
            if (index >= 0) {
                tabbedPane.setIconAt(index, getTabIcon(tabName));
            }
        }
    }

    private Icon getTabIcon(String tabName) {
        switch (tabName) {
            case "Dashboard":
                return UIManager.getIcon("FileView.computerIcon");
            case "Criminals":
                return UIManager.getIcon("FileView.directoryIcon");
            case "Advanced Search":
                return UIManager.getIcon("FileView.fileIcon");
            case "Cases":
                return UIManager.getIcon("Tree.closedIcon");
            case "Evidence":
                return UIManager.getIcon("OptionPane.informationIcon");
            case "Chain of Custody":
                return UIManager.getIcon("OptionPane.warningIcon");
            case "Biometrics":
                return UIManager.getIcon("OptionPane.questionIcon");
            case "Wanted List":
                return UIManager.getIcon("OptionPane.errorIcon");
            case "Notifications":
                return UIManager.getIcon("FileChooser.detailsViewIcon");
            case "Reports":
                return UIManager.getIcon("FileView.floppyDriveIcon");
            default:
                return UIManager.getIcon("FileView.fileIcon");
        }
    }

    private JPanel createLoadingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PAGE_BG);
        JLabel text = new JLabel("Loading section...");
        text.setForeground(new Color(90, 105, 120));
        panel.add(text);
        return panel;
    }

    private void loadSelectedTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }

        String tabName = tabbedPane.getTitleAt(selectedIndex);
        if (loadedTabs.containsKey(tabName)) {
            return;
        }

        Supplier<JPanel> factory = tabFactories.get(tabName);
        if (factory == null) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JPanel panel = factory.get();
        panel.setBackground(PAGE_BG);
        loadedTabs.put(tabName, panel);
        tabbedPane.setComponentAt(selectedIndex, panel);
        setCursor(Cursor.getDefaultCursor());
    }

    private void seedOperationalData() {
        if (seededSessionData || management == null) {
            return;
        }

        List<CaseRecord> seededCases = new ArrayList<>();
        List<Criminal> criminals = new ArrayList<>(management.getAllCriminals());
        if (criminals.size() < 10) {
            return;
        }

        for (CaseRecord caseRecord : management.getAllCases()) {
            seededCases.add(caseRecord);
        }

        if (alertService.getAllWantedNotices().isEmpty()) {
            for (int i = 0; i < Math.min(3, criminals.size()); i++) {
                Criminal c = criminals.get(i);
                alertService.addWantedNotice(c.getCriminalId(), "Flagged for priority tracking", i == 0 ? "HIGH" : "MEDIUM");
            }
        }

        if (chainService.getAllRecords().isEmpty()) {
            int seeded = 0;
            for (CaseRecord caseRecord : seededCases) {
                for (Evidence evidence : management.getEvidenceByCase(caseRecord.getCaseId())) {
                    chainService.addCustodyRecord(evidence.getEvidenceId(), "Officer Blake", "COLLECTED", "Scene Unit", "Initial recovery");
                    chainService.addCustodyRecord(evidence.getEvidenceId(), "Officer Chen", "TRANSFERRED", "Forensics Transit", "Sealed transfer");
                    chainService.addCustodyRecord(evidence.getEvidenceId(), "Dr. Avery", "TESTED", "Lab 3", "Primary analysis complete");
                    seeded += 3;
                    if (seeded >= 18) {
                        break;
                    }
                }
                if (seeded >= 18) {
                    break;
                }
            }
        }

        if (notificationService.getAllNotifications().isEmpty()) {
            notificationService.createNotification("Welcome", "Operational data loaded successfully.", "SYSTEM", loggedInUser);
            notificationService.createNotification("Seed Data", "10+ linked demo records are available for testing.", "INFO", loggedInUser);
        }

        seededSessionData = true;
    }

    private void updateNotificationCount() {
        int count = notificationService.getUnreadCount(loggedInUser);
        if (notificationLabel != null) {
            notificationLabel.setText("Notifications: " + count);
        }
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PAGE_BG);
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel heading = new JLabel("Operations Overview");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(PRIMARY);

        JLabel subtitle = new JLabel("Live snapshot of criminal, case, and system activity");
        subtitle.setFont(FONT_BODY);
        subtitle.setForeground(new Color(95, 108, 122));

        JPanel headingPanel = new JPanel(new GridLayout(2, 1));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        headingPanel.add(subtitle);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setOpaque(false);

        Collection<Criminal> criminals = management.getAllCriminals();
        Collection<CaseRecord> cases = management.getAllCases();
        int evidenceCount = collectAllEvidence().size();
        int custodyCount = chainService.getAllRecords().size();

        statsPanel.add(createStatCard("Total Criminals", String.valueOf(criminals.size()), new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Total Cases", String.valueOf(cases.size()), new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Total Evidence", String.valueOf(evidenceCount), new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Custody Records", String.valueOf(custodyCount), new Color(230, 126, 34)));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 18));
        contentPanel.setOpaque(false);
        contentPanel.add(headingPanel, BorderLayout.NORTH);
        contentPanel.add(statsPanel, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new LineBorder(color, 2));
        card.setPreferredSize(new Dimension(250, 100));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(80, 80, 80));
        titleLabel.setBorder(new EmptyBorder(10, 15, 5, 15));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setBorder(new EmptyBorder(5, 15, 10, 15));
        valueLabel.setHorizontalAlignment(JLabel.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCriminalsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(PAGE_BG);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton addBtn = createStyledButton("Add Criminal", 120, 35);
        JButton searchBtn = createStyledButton("Search", 100, 35);
        JButton updateBtn = createStyledButton("Update", 100, 35);
        JButton deleteBtn = createStyledButton("Delete", 100, 35);
        JButton refreshBtn = createStyledButton("Refresh", 100, 35);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Table
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Crime Type"}, 0);
        
        for (Criminal c : management.getAllCriminals()) {
            model.addRow(new Object[]{c.getCriminalId(), c.getName(), c.getAge(), c.getCrimeType()});
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add button actions
        addBtn.addActionListener(e -> showAddCriminalDialog());
        searchBtn.addActionListener(e -> showSearchCriminalDialog());
        updateBtn.addActionListener(e -> showUpdateCriminalDialog());
        deleteBtn.addActionListener(e -> showDeleteCriminalDialog());
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            for (Criminal c : management.getAllCriminals()) {
                model.addRow(new Object[]{c.getCriminalId(), c.getName(), c.getAge(), c.getCrimeType()});
            }
            updateStatus("Criminal list refreshed");
        });

        return panel;
    }

    private JPanel createCasesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(PAGE_BG);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton createCaseBtn = createStyledButton("Create Case", 120, 35);
        JButton assignCaseBtn = createStyledButton("Assign Case", 120, 35);
        JButton refreshCaseBtn = createStyledButton("Refresh", 100, 35);

        buttonPanel.add(createCaseBtn);
        buttonPanel.add(assignCaseBtn);
        buttonPanel.add(refreshCaseBtn);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Table
        DefaultTableModel model = new DefaultTableModel(new String[]{"Case ID", "Criminal ID", "Description", "Assigned Officer"}, 0);
        
        for (CaseRecord c : management.getAllCases()) {
            model.addRow(new Object[]{c.getCaseId(), c.getCriminalId(), c.getDescription(), c.getAssignedOfficer() != null ? c.getAssignedOfficer() : "Unassigned"});
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(46, 204, 113));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        createCaseBtn.addActionListener(e -> showCreateCaseDialog());
        assignCaseBtn.addActionListener(e -> showAssignCaseDialog());
        refreshCaseBtn.addActionListener(e -> {
            model.setRowCount(0);
            for (CaseRecord c : management.getAllCases()) {
                model.addRow(new Object[]{c.getCaseId(), c.getCriminalId(), c.getDescription(), c.getAssignedOfficer() != null ? c.getAssignedOfficer() : "Unassigned"});
            }
            updateStatus("Case list refreshed");
        });

        return panel;
    }

    private JPanel createEvidencePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(PAGE_BG);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton addEvidenceBtn = createStyledButton("Add Evidence", 120, 35);
        JButton searchEvidenceBtn = createStyledButton("Search by Case", 120, 35);
        JButton refreshEvidenceBtn = createStyledButton("Refresh", 100, 35);

        buttonPanel.add(addEvidenceBtn);
        buttonPanel.add(searchEvidenceBtn);
        buttonPanel.add(refreshEvidenceBtn);

        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setOpaque(false);
        topPanel.add(buttonPanel, BorderLayout.NORTH);
        JLabel helper = new JLabel("Tip: Refresh shows all evidence. Search by Case narrows the table.");
        helper.setFont(FONT_BODY);
        helper.setForeground(new Color(98, 112, 125));
        topPanel.add(helper, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Evidence ID", "Case ID", "Type", "Linked Criminal"}, 0);

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        applyTableStyle(table, new Color(84, 103, 144));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        populateEvidenceTable(model);

        addEvidenceBtn.addActionListener(e -> showAddEvidenceDialog());
        searchEvidenceBtn.addActionListener(e -> showEvidenceSearchDialog(model));
        refreshEvidenceBtn.addActionListener(e -> {
            populateEvidenceTable(model);
            updateStatus("Evidence list refreshed");
        });

        return panel;
    }

    private JPanel createBiometricsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(PAGE_BG);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton storeBioBtn = createStyledButton("Store Biometric", 140, 35);
        JButton viewBioBtn = createStyledButton("View Biometric", 140, 35);
        JButton refreshBioBtn = createStyledButton("Refresh", 100, 35);

        buttonPanel.add(storeBioBtn);
        buttonPanel.add(viewBioBtn);
        buttonPanel.add(refreshBioBtn);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Criminal ID", "Name", "Fingerprint", "DNA", "Status"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        applyTableStyle(table, new Color(24, 133, 103));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));

        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setOpaque(false);
        topPanel.add(buttonPanel, BorderLayout.NORTH);
        JLabel helper = new JLabel("All stored biometric records are listed below.");
        helper.setFont(FONT_BODY);
        helper.setForeground(new Color(98, 112, 125));
        topPanel.add(helper, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        populateBiometricTable(model);

        storeBioBtn.addActionListener(e -> showStoreBiometricDialog());
        viewBioBtn.addActionListener(e -> showViewBiometricDialog());
        refreshBioBtn.addActionListener(e -> {
            populateBiometricTable(model);
            updateStatus("Biometric list refreshed");
        });

        return panel;
    }

    // Feature 1: Advanced Search Panel
    private JPanel createAdvancedSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(PAGE_BG);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name contains:");
        JTextField nameField = new JTextField(15);
        JLabel ageLabel = new JLabel("Age range:");
        JTextField minAgeField = new JTextField(5);
        JTextField maxAgeField = new JTextField(5);
        JLabel crimeLabel = new JLabel("Crime type:");
        JTextField crimeField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        filterPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        filterPanel.add(ageLabel, gbc);
        gbc.gridx = 1;
        JPanel agePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        agePanel.setOpaque(false);
        agePanel.add(new JLabel("Min:")); agePanel.add(minAgeField);
        agePanel.add(new JLabel("Max:")); agePanel.add(maxAgeField);
        filterPanel.add(agePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        filterPanel.add(crimeLabel, gbc);
        gbc.gridx = 1;
        filterPanel.add(crimeField, gbc);

        JButton searchBtn = createStyledButton("Search", 100, 35);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        filterPanel.add(searchBtn, gbc);

        panel.add(filterPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Crime Type"}, 0);
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(100, 150, 200));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            model.setRowCount(0);
            Collection<Criminal> results = management.getAllCriminals();

            if (!nameField.getText().isEmpty()) {
                AdvancedSearchStrategy strategy = new NameSearchStrategy(nameField.getText());
                results = strategy.search(results);
            }

            if (!minAgeField.getText().isEmpty() && !maxAgeField.getText().isEmpty()) {
                try {
                    int min = Integer.parseInt(minAgeField.getText());
                    int max = Integer.parseInt(maxAgeField.getText());
                    AdvancedSearchStrategy strategy = new AgeRangeSearchStrategy(min, max);
                    results = strategy.search(results);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Enter valid age range", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }

            if (!crimeField.getText().isEmpty()) {
                AdvancedSearchStrategy strategy = new CrimeTypeSearchStrategy(crimeField.getText());
                results = strategy.search(results);
            }

            for (Criminal c : results) {
                model.addRow(new Object[]{c.getCriminalId(), c.getName(), c.getAge(), c.getCrimeType()});
            }
            updateStatus("Search completed: " + results.size() + " results");
        });

        return panel;
    }

    // Feature 5: Wanted List Panel
    private JPanel createWantedListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(PAGE_BG);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton addWantedBtn = createStyledButton("Add to Wanted", 120, 35);
        JButton removeWantedBtn = createStyledButton("Remove from Wanted", 150, 35);
        JButton refreshBtn = createStyledButton("Refresh", 100, 35);

        buttonPanel.add(addWantedBtn);
        buttonPanel.add(removeWantedBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Criminal ID", "Reason", "Severity", "Added Date"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(200, 0, 0));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            for (WantedNotice w : alertService.getAllWantedNotices()) {
                model.addRow(new Object[]{w.getCriminalId(), w.getReason(), w.getSeverity(), w.getDateAdded()});
            }
        });

        addWantedBtn.addActionListener(e -> {
            String crimId = JOptionPane.showInputDialog(panel, "Enter Criminal ID:");
            if (crimId != null && !crimId.isEmpty()) {
                String reason = JOptionPane.showInputDialog(panel, "Enter reason:");
                String[] severities = {"HIGH", "MEDIUM", "LOW"};
                String severity = (String) JOptionPane.showInputDialog(panel, "Select severity:", "Severity", 
                    JOptionPane.QUESTION_MESSAGE, null, severities, severities[0]);
                
                if (reason != null && severity != null) {
                    alertService.addWantedNotice(Integer.parseInt(crimId), reason, severity);
                    JOptionPane.showMessageDialog(panel, "Criminal added to wanted list", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshBtn.doClick();
                }
            }
        });

        removeWantedBtn.addActionListener(e -> {
            String crimId = JOptionPane.showInputDialog(panel, "Enter Criminal ID to remove:");
            if (crimId != null && !crimId.isEmpty()) {
                if (alertService.removeWantedNotice(Integer.parseInt(crimId))) {
                    JOptionPane.showMessageDialog(panel, "Removed from wanted list", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshBtn.doClick();
                }
            }
        });

        return panel;
    }

    // Feature 6: Chain of Custody Panel
    private JPanel createChainOfCustodyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(PAGE_BG);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setOpaque(false);

        JButton addRecordBtn = createStyledButton("Add Record", 120, 35);
        JButton autoFillBtn = createStyledButton("Auto Fill", 100, 35);
        JButton showAllBtn = createStyledButton("Show All", 100, 35);
        JTextField evidenceFilterField = new JTextField(10);
        evidenceFilterField.setFont(FONT_BODY);
        evidenceFilterField.setBorder(new CompoundBorder(new LineBorder(new Color(205, 211, 220), 1), new EmptyBorder(6, 8, 6, 8)));
        JButton filterBtn = createStyledButton("Filter", 90, 35);

        buttonPanel.add(addRecordBtn);
        buttonPanel.add(autoFillBtn);
        buttonPanel.add(showAllBtn);
        buttonPanel.add(new JLabel("Evidence ID:"));
        buttonPanel.add(evidenceFilterField);
        buttonPanel.add(filterBtn);
        panel.add(buttonPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Custody ID", "Evidence ID", "Criminal ID", "Action", "Handled By", "Location", "Timestamp", "Notes"},
            0
        );
        JTable table = new JTable(model);
        table.setRowHeight(25);
        applyTableStyle(table, new Color(112, 90, 171));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        populateChainTable(model, null);

        addRecordBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Add Custody Record", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);

            JPanel dlgPanel = new JPanel(new GridBagLayout());
            dlgPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JTextField evidIdField = new JTextField(15);
            JTextField handledByField = new JTextField(15);
            JComboBox<String> actionCombo = new JComboBox<>(new String[]{"COLLECTED", "TRANSFERRED", "STORED", "TESTED"});
            JTextField locationField = new JTextField(15);
            JTextArea notesArea = new JTextArea(3, 15);

            addLabelAndField(dlgPanel, gbc, "Evidence ID:", evidIdField, 0);
            addLabelAndField(dlgPanel, gbc, "Handled By:", handledByField, 1);
            
            JLabel actionLabel = new JLabel("Action:");
            gbc.gridx = 0; gbc.gridy = 2;
            dlgPanel.add(actionLabel, gbc);
            gbc.gridx = 1;
            dlgPanel.add(actionCombo, gbc);
            
            addLabelAndField(dlgPanel, gbc, "Location:", locationField, 3);
            
            JLabel notesLabel = new JLabel("Notes:");
            gbc.gridx = 0; gbc.gridy = 4;
            dlgPanel.add(notesLabel, gbc);
            gbc.gridx = 1;
            dlgPanel.add(new JScrollPane(notesArea), gbc);

            JButton saveBtn = createStyledButton("Save", 100, 35);
            gbc.gridx = 0; gbc.gridy = 5;
            gbc.gridwidth = 2;
            dlgPanel.add(saveBtn, gbc);

            saveBtn.addActionListener(evt -> {
                chainService.addCustodyRecord(Integer.parseInt(evidIdField.getText()), 
                    handledByField.getText(), (String) actionCombo.getSelectedItem(), 
                    locationField.getText(), notesArea.getText());
                JOptionPane.showMessageDialog(dialog, "Record added", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                populateChainTable(model, null);
            });

            dialog.add(dlgPanel);
            dialog.setVisible(true);
        });

        autoFillBtn.addActionListener(e -> {
            int added = autoFillChainFromEvidence();
            populateChainTable(model, null);
            updateStatus("Auto-filled chain records for " + added + " evidence item(s)");
        });

        showAllBtn.addActionListener(e -> {
            evidenceFilterField.setText("");
            populateChainTable(model, null);
            updateStatus("Showing complete chain of custody");
        });

        filterBtn.addActionListener(e -> {
            String raw = evidenceFilterField.getText().trim();
            if (raw.isEmpty()) {
                populateChainTable(model, null);
                return;
            }
            try {
                int evidenceId = Integer.parseInt(raw);
                populateChainTable(model, evidenceId);
                updateStatus("Showing chain for Evidence ID " + evidenceId);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Enter a valid Evidence ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    // Feature 10: Notifications Panel
    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(PAGE_BG);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton refreshBtn = createStyledButton("Refresh", 100, 35);
        JButton markReadBtn = createStyledButton("Mark All as Read", 120, 35);

        buttonPanel.add(refreshBtn);
        buttonPanel.add(markReadBtn);
        panel.add(buttonPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Title", "Message", "Type", "Timestamp", "Read"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(230, 126, 34));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            for (Notification n : notificationService.getNotificationsForUser(loggedInUser)) {
                model.addRow(new Object[]{n.getTitle(), n.getMessage(), n.getType(), n.getTimestamp(), n.isRead()});
            }
        });

        markReadBtn.addActionListener(e -> {
            for (Notification n : notificationService.getNotificationsForUser(loggedInUser)) {
                notificationService.markAsRead(n.getNotificationId());
            }
            refreshBtn.doClick();
        });

        return panel;
    }

    // Feature 7: Reports Panel
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(PAGE_BG);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton statsBtn = createStyledButton("Criminal Stats", 120, 35);
        JButton caseBtn = createStyledButton("Case Summary", 120, 35);
        JButton evidBtn = createStyledButton("Evidence Report", 130, 35);

        buttonPanel.add(statsBtn);
        buttonPanel.add(caseBtn);
        buttonPanel.add(evidBtn);
        panel.add(buttonPanel, BorderLayout.NORTH);

        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(reportArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        statsBtn.addActionListener(e -> {
            Report report = new ReportBuilder(1, loggedInUser)
                .withCriminalStats(management.getAllCriminals())
                .build();
            reportArea.setText(report.getContent());
            updateStatus("Criminal statistics report generated");
        });

        caseBtn.addActionListener(e -> {
            Report report = new ReportBuilder(2, loggedInUser)
                .withCaseSummary(management.getAllCases())
                .build();
            reportArea.setText(report.getContent());
            updateStatus("Case summary report generated");
        });

        evidBtn.addActionListener(e -> {
            Collection<Evidence> allEvidence = collectAllEvidence();
            Report report = new ReportBuilder(3, loggedInUser)
                .withEvidenceInventory(allEvidence)
                .build();
            reportArea.setText(report.getContent());
            updateStatus("Evidence report generated");
        });

        return panel;
    }

    // Dialog Methods
    private void showAddCriminalDialog() {
        JDialog dialog = new JDialog(this, "Add Criminal", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = (JTextField) addLabelAndField(panel, gbc, "Criminal ID:", new JTextField(), 0);
        JTextField nameField = (JTextField) addLabelAndField(panel, gbc, "Name:", new JTextField(), 1);
        JTextField ageField = (JTextField) addLabelAndField(panel, gbc, "Age:", new JTextField(), 2);
        JTextField crimeField = (JTextField) addLabelAndField(panel, gbc, "Crime Type:", new JTextField(), 3);

        JButton saveBtn = createStyledButton("Save", 100, 35);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String crime = crimeField.getText();

                if (name.isEmpty() || crime.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (management.registerCriminal(id, name, age, crime)) {
                    JOptionPane.showMessageDialog(dialog, "Criminal added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    updateStatus("Criminal ID " + id + " added");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Criminal ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for ID and Age.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JComponent addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int gridy) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);

        return field;
    }

    private void showSearchCriminalDialog() {
        JDialog dialog = new JDialog(this, "Search Criminal", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel("Enter Criminal ID:");
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        JTextField idField = new JTextField(15);
        idField.setFont(new Font("Arial", Font.PLAIN, 12));
        idField.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        gbc.gridx = 1;
        panel.add(idField, gbc);

        JButton searchBtn = createStyledButton("Search", 100, 35);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(searchBtn, gbc);

        JTextArea resultArea = new JTextArea(6, 30);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        searchBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                Criminal c = management.searchCriminal(id);
                if (c != null) {
                    resultArea.setText(String.format("Criminal Found:\nID: %d\nName: %s\nAge: %d\nCrime: %s", 
                        c.getCriminalId(), c.getName(), c.getAge(), c.getCrimeType()));
                    updateStatus("Criminal ID " + id + " found");
                } else {
                    resultArea.setText("No criminal found with ID " + id);
                    updateStatus("Criminal not found");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid ID number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showUpdateCriminalDialog() {
        JDialog dialog = new JDialog(this, "Update Criminal", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = (JTextField) addLabelAndField(panel, gbc, "Criminal ID:", new JTextField(), 0);
        JTextField crimeField = (JTextField) addLabelAndField(panel, gbc, "New Crime Type:", new JTextField(), 1);

        JButton updateBtn = createStyledButton("Update", 100, 35);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(updateBtn, gbc);

        updateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String crime = crimeField.getText();
                if (crime.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter new crime type.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (management.updateCrime(id, crime)) {
                    JOptionPane.showMessageDialog(dialog, "Criminal updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    updateStatus("Criminal ID " + id + " updated");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Criminal not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showDeleteCriminalDialog() {
        JDialog dialog = new JDialog(this, "Delete Criminal", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = (JTextField) addLabelAndField(panel, gbc, "Criminal ID:", new JTextField(), 0);

        JButton deleteBtn = createStyledButton("Delete", 100, 35);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(deleteBtn, gbc);

        deleteBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete criminal ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION && management.deleteCriminal(id)) {
                    JOptionPane.showMessageDialog(dialog, "Criminal deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    updateStatus("Criminal ID " + id + " deleted");
                } else if (confirm == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(dialog, "Criminal not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showCreateCaseDialog() {
        JDialog dialog = new JDialog(this, "Create Case", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField caseIdField = (JTextField) addLabelAndField(panel, gbc, "Case ID:", new JTextField(), 0);
        JTextField crimIdField = (JTextField) addLabelAndField(panel, gbc, "Criminal ID:", new JTextField(), 1);
        JTextField descField = (JTextField) addLabelAndField(panel, gbc, "Description:", new JTextField(), 2);

        JButton createBtn = createStyledButton("Create", 100, 35);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(createBtn, gbc);

        createBtn.addActionListener(e -> {
            try {
                int caseId = Integer.parseInt(caseIdField.getText());
                int crimId = Integer.parseInt(crimIdField.getText());
                String desc = descField.getText();
                if (desc.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (management.createCase(caseId, crimId, desc)) {
                    JOptionPane.showMessageDialog(dialog, "Case created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    updateStatus("Case ID " + caseId + " created");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Case creation failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAssignCaseDialog() {
        JDialog dialog = new JDialog(this, "Assign Case", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField caseIdField = (JTextField) addLabelAndField(panel, gbc, "Case ID:", new JTextField(), 0);
        JTextField officerField = (JTextField) addLabelAndField(panel, gbc, "Officer Name:", new JTextField(), 1);

        JButton assignBtn = createStyledButton("Assign", 100, 35);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(assignBtn, gbc);

        assignBtn.addActionListener(e -> {
            try {
                int caseId = Integer.parseInt(caseIdField.getText());
                String officer = officerField.getText();
                if (officer.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter officer name.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (management.assignCase(caseId, officer)) {
                    JOptionPane.showMessageDialog(dialog, "Case assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    updateStatus("Case ID " + caseId + " assigned to " + officer);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Case not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid case ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddEvidenceDialog() {
        JDialog dialog = new JDialog(this, "Add Evidence", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField evidIdField = (JTextField) addLabelAndField(panel, gbc, "Evidence ID:", new JTextField(), 0);
        JTextField caseIdField = (JTextField) addLabelAndField(panel, gbc, "Case ID:", new JTextField(), 1);
        JTextField typeField = (JTextField) addLabelAndField(panel, gbc, "Evidence Type:", new JTextField(), 2);

        JButton addBtn = createStyledButton("Add", 100, 35);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(addBtn, gbc);

        addBtn.addActionListener(e -> {
            try {
                int evidId = Integer.parseInt(evidIdField.getText());
                int caseId = Integer.parseInt(caseIdField.getText());
                String type = typeField.getText();
                if (type.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (management.addEvidence(evidId, caseId, type)) {
                    JOptionPane.showMessageDialog(dialog, "Evidence added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    updateStatus("Evidence ID " + evidId + " added to Case " + caseId);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Evidence addition failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEvidenceSearchDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Search Evidence by Case", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField caseIdField = (JTextField) addLabelAndField(panel, gbc, "Case ID:", new JTextField(), 0);

        JButton searchBtn = createStyledButton("Search", 100, 35);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(searchBtn, gbc);

        searchBtn.addActionListener(e -> {
            try {
                int caseId = Integer.parseInt(caseIdField.getText());
                Collection<Evidence> evidences = management.getEvidenceByCase(caseId);
                model.setRowCount(0);
                for (Evidence ev : evidences) {
                    model.addRow(new Object[]{ev.getEvidenceId(), ev.getCaseId(), ev.getEvidenceType(), findCriminalIdForCase(ev.getCaseId())});
                }
                dialog.dispose();
                updateStatus("Evidence for Case " + caseId + " displayed");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid case ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showStoreBiometricDialog() {
        JDialog dialog = new JDialog(this, "Store Biometric Data", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField crimIdField = (JTextField) addLabelAndField(panel, gbc, "Criminal ID:", new JTextField(), 0);
        JTextField fingerprintField = (JTextField) addLabelAndField(panel, gbc, "Fingerprint:", new JTextField(), 1);
        JTextField dnaField = (JTextField) addLabelAndField(panel, gbc, "DNA:", new JTextField(), 2);

        JButton storeBtn = createStyledButton("Store", 100, 35);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(storeBtn, gbc);

        storeBtn.addActionListener(e -> {
            try {
                int crimId = Integer.parseInt(crimIdField.getText());
                String fingerprint = fingerprintField.getText();
                String dna = dnaField.getText();
                if (fingerprint.isEmpty() || dna.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (management.storeBiometric(crimId, fingerprint, dna)) {
                    JOptionPane.showMessageDialog(dialog, "Biometric data stored successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    updateStatus("Biometric data stored for Criminal ID " + crimId);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to store biometric data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid criminal ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showViewBiometricDialog() {
        JDialog dialog = new JDialog(this, "View Biometric Data", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField crimIdField = (JTextField) addLabelAndField(panel, gbc, "Criminal ID:", new JTextField(), 0);

        JButton viewBtn = createStyledButton("View", 100, 35);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(viewBtn, gbc);

        JTextArea resultArea = new JTextArea(6, 30);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        viewBtn.addActionListener(e -> {
            try {
                int crimId = Integer.parseInt(crimIdField.getText());
                BiometricData bio = management.getBiometric(crimId);
                if (bio != null) {
                    resultArea.setText(String.format("Biometric Data:\nCriminal ID: %d\nFingerprint: %s\nDNA: %s", 
                        bio.getCriminalId(), bio.getFingerprint(), bio.getDna()));
                    updateStatus("Biometric data retrieved for Criminal ID " + crimId);
                } else {
                    resultArea.setText("No biometric data found for Criminal ID " + crimId);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid criminal ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text, int width, int height) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(width, height));
        btn.setFont(FONT_BUTTON);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setRolloverEnabled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private void applyTableStyle(JTable table, Color headerColor) {
        table.setFont(FONT_BODY);
        table.setGridColor(new Color(225, 230, 236));
        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    private Collection<Evidence> collectAllEvidence() {
        Map<Integer, Evidence> merged = new LinkedHashMap<>();
        for (CaseRecord caseRecord : management.getAllCases()) {
            for (Evidence evidence : management.getEvidenceByCase(caseRecord.getCaseId())) {
                merged.put(evidence.getEvidenceId(), evidence);
            }
        }
        return merged.values();
    }

    private void populateEvidenceTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Evidence evidence : collectAllEvidence()) {
            model.addRow(new Object[]{
                evidence.getEvidenceId(),
                evidence.getCaseId(),
                evidence.getEvidenceType(),
                findCriminalIdForCase(evidence.getCaseId())
            });
        }
    }

    private void populateBiometricTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Criminal criminal : management.getAllCriminals()) {
            BiometricData bio = management.getBiometric(criminal.getCriminalId());
            if (bio != null) {
                model.addRow(new Object[]{
                    criminal.getCriminalId(),
                    criminal.getName(),
                    bio.getFingerprint(),
                    bio.getDna(),
                    "Stored"
                });
            }
        }
    }

    private void populateChainTable(DefaultTableModel model, Integer evidenceIdFilter) {
        model.setRowCount(0);

        List<ChainOfCustody> rows = evidenceIdFilter == null
            ? chainService.getAllRecords()
            : chainService.getChainByEvidence(evidenceIdFilter);

        for (ChainOfCustody record : rows) {
            model.addRow(new Object[] {
                record.getCustodyId(),
                record.getEvidenceId(),
                findCriminalIdByEvidence(record.getEvidenceId()),
                record.getAction(),
                record.getHandledBy(),
                record.getLocation(),
                record.getTimestamp(),
                record.getNotes()
            });
        }
    }

    private int autoFillChainFromEvidence() {
        int seededEvidenceItems = 0;
        for (Evidence evidence : collectAllEvidence()) {
            if (!chainService.getChainByEvidence(evidence.getEvidenceId()).isEmpty()) {
                continue;
            }

            chainService.addCustodyRecord(evidence.getEvidenceId(), "Officer Miles", "COLLECTED", "Crime Scene Unit", "Evidence sealed and tagged");
            chainService.addCustodyRecord(evidence.getEvidenceId(), "Detective Rowan", "TRANSFERRED", "Secure Transit", "Chain verified at handoff");
            chainService.addCustodyRecord(evidence.getEvidenceId(), "Lab Analyst Vega", "TESTED", "Forensics Lab", "Initial forensic screening complete");
            seededEvidenceItems++;
        }
        return seededEvidenceItems;
    }

    private String findCriminalIdByEvidence(int evidenceId) {
        for (Evidence evidence : collectAllEvidence()) {
            if (evidence.getEvidenceId() == evidenceId) {
                return findCriminalIdForCase(evidence.getCaseId());
            }
        }
        return "Unknown";
    }

    private String findCriminalIdForCase(int caseId) {
        for (CaseRecord caseRecord : management.getAllCases()) {
            if (caseRecord.getCaseId() == caseId) {
                return String.valueOf(caseRecord.getCriminalId());
            }
        }
        return "Unknown";
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText("[" + LocalDateTime.now().format(dateFormatter) + "] " + message);
        }
    }

    private void handleCaseClosure(String outcome) {
        String input = JOptionPane.showInputDialog(this, "Enter Case ID to close as " + outcome + ":");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int caseId = Integer.parseInt(input.trim());
            CaseRecord targetCase = null;
            for (CaseRecord caseRecord : management.getAllCases()) {
                if (caseRecord.getCaseId() == caseId) {
                    targetCase = caseRecord;
                    break;
                }
            }

            if (targetCase == null) {
                JOptionPane.showMessageDialog(this, "Case ID " + caseId + " not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String updatedOfficer = appendOutcome(targetCase.getAssignedOfficer(), outcome);
            management.assignCase(caseId, updatedOfficer);
            notificationService.createNotification(
                "Case " + caseId + " closed as " + outcome,
                "Case " + caseId + " was marked as " + outcome + " by " + loggedInUser,
                "CASE_OUTCOME",
                loggedInUser
            );

            updateStatus("Case " + caseId + " closed as " + outcome);
            JOptionPane.showMessageDialog(this, "Case " + caseId + " marked as " + outcome + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Case ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String appendOutcome(String assignedOfficer, String outcome) {
        String value = assignedOfficer == null ? "" : assignedOfficer.trim();
        if (value.endsWith("| Outcome: Won") || value.endsWith("| Outcome: Lost")) {
            value = value.substring(0, value.lastIndexOf("| Outcome:")).trim();
        }

        if (value.isEmpty() || "Unassigned".equalsIgnoreCase(value)) {
            return "Outcome: " + outcome;
        }
        return value + " | Outcome: " + outcome;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModernCriminalManagementGUI());
    }
}
