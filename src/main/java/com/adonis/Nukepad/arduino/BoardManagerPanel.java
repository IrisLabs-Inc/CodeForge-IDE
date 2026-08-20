package com.adonis.Nukepad.arduino;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class BoardManagerPanel extends JPanel {

    private JList<String> installedCoresList;
    private DefaultListModel<String> installedCoresModel;
    private JList<String> availableCoresList;
    private DefaultListModel<String> availableCoresModel;
    private JTextField installField;
    private JButton installBtn;
    private JButton refreshBtn;
    private JTextArea logArea;
    private ArduinoCliRunner runner;

    private static final String[] POPULAR_CORES = {
        "arduino:avr",
        "arduino:sam",
        "arduino:samd",
        "arduino:esp8266",
        "esp32:esp32",
        "esp32:esp32s2",
        "esp32:esp32s3",
        "esp32:esp32c3",
        "rp2040:rp2040",
        "adafruit:nrf52",
        "stm32:stm32",
        "megaavr:avr"
    };

    public BoardManagerPanel() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        runner = new ArduinoCliRunner();

        // Top: install core
        JPanel installPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        installPanel.add(new JLabel("Install Core:"));
        installField = new JTextField(20);
        installField.setToolTipText("e.g., esp32:esp32, arduino:avr");
        installPanel.add(installField);

        installBtn = new JButton("Install");
        installBtn.addActionListener(e -> installCore());
        installPanel.add(installBtn);

        add(installPanel, BorderLayout.NORTH);

        // Center: two lists side by side
        JPanel listsPanel = new JPanel();
        listsPanel.setLayout(new BoxLayout(listsPanel, BoxLayout.X_AXIS));

        // Installed cores
        JPanel installedSection = new JPanel(new BorderLayout(0, 4));
        installedSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        JLabel installedHeader = new JLabel("Installed Cores");
        installedHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        installedSection.add(installedHeader, BorderLayout.NORTH);

        installedCoresModel = new DefaultListModel<>();
        installedCoresList = new JList<>(installedCoresModel);
        installedCoresList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        installedSection.add(new JScrollPane(installedCoresList), BorderLayout.CENTER);
        installedSection.setPreferredSize(new Dimension(300, 0));

        listsPanel.add(installedSection);

        // Popular / available cores
        JPanel availableSection = new JPanel(new BorderLayout(0, 4));
        JLabel availableHeader = new JLabel("Popular Cores (double-click to install)");
        availableHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        availableSection.add(availableHeader, BorderLayout.NORTH);

        availableCoresModel = new DefaultListModel<>();
        for (String core : POPULAR_CORES) {
            availableCoresModel.addElement(core);
        }
        availableCoresList = new JList<>(availableCoresModel);
        availableCoresList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        availableCoresList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = availableCoresList.getSelectedValue();
                    if (selected != null) {
                        installField.setText(selected);
                        installCore();
                    }
                }
            }
        });
        availableSection.add(new JScrollPane(availableCoresList), BorderLayout.CENTER);

        listsPanel.add(availableSection);

        add(listsPanel, BorderLayout.CENTER);

        // Bottom: log area + refresh
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 4));

        refreshBtn = new JButton("Refresh Installed Cores");
        refreshBtn.addActionListener(e -> refreshInstalledCores());
        bottomPanel.add(refreshBtn, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setPreferredSize(new Dimension(0, 150));
        bottomPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        refreshInstalledCores();
    }

    private void installCore() {
        String core = installField.getText().trim();
        if (core.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a core name (e.g., esp32:esp32).",
                    "Install Core", JOptionPane.WARNING_MESSAGE);
            return;
        }

        installBtn.setEnabled(false);
        logArea.setText("");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                logArea.append("[Arduino] Installing core: " + core + "...\n");
                List<String> cmd = List.of(runner.getCliPath(), "core", "install", core);
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logArea.append(line + "\n");
                    }
                }

                int exitCode = proc.waitFor();
                if (exitCode == 0) {
                    logArea.append("[Arduino] Core " + core + " installed successfully.\n");
                } else {
                    logArea.append("[Arduino] Core install failed (exit code " + exitCode + ").\n");
                }
                return null;
            }

            @Override
            protected void done() {
                installBtn.setEnabled(true);
                refreshInstalledCores();
            }
        };
        worker.execute();
    }

    private void refreshInstalledCores() {
        installedCoresModel.clear();
        installedCoresModel.addElement("Loading...");

        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                List<String> cmd = List.of(runner.getCliPath(), "core", "list");
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    boolean headerSkipped = false;
                    while ((line = reader.readLine()) != null) {
                        if (!headerSkipped) {
                            headerSkipped = true;
                            continue;
                        }
                        line = line.trim();
                        if (!line.isEmpty()) {
                            lines.add(line);
                        }
                    }
                }
                proc.waitFor();
                return lines;
            }

            @Override
            protected void done() {
                try {
                    List<String> lines = get();
                    installedCoresModel.clear();
                    for (String line : lines) {
                        installedCoresModel.addElement(line);
                    }
                    if (installedCoresModel.isEmpty()) {
                        installedCoresModel.addElement("(none installed)");
                    }
                } catch (Exception e) {
                    installedCoresModel.clear();
                    installedCoresModel.addElement("Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}
