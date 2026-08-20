package com.adonis.Nukepad.arduino;

import com.adonis.Nukepad.Nukepad;
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

public class LibraryManagerPanel extends JPanel {

    private JTextField searchField;
    private JButton searchBtn;
    private JButton refreshInstalledBtn;
    private JList<String> installedList;
    private DefaultListModel<String> installedModel;
    private JTextArea logArea;
    private ArduinoCliRunner runner;

    public LibraryManagerPanel() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        runner = new ArduinoCliRunner();

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchLibraries());
        searchPanel.add(searchField);

        searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchLibraries());
        searchPanel.add(searchBtn);

        JButton installBtn = new JButton("Install by Name...");
        installBtn.addActionListener(e -> installByName());
        searchPanel.add(installBtn);

        add(searchPanel, BorderLayout.NORTH);

        // Main content split: installed list + log
        JPanel centerPanel = new JPanel(new BorderLayout(0, 4));

        // Installed libraries section
        JPanel installedSection = new JPanel(new BorderLayout(0, 4));
        JLabel header = new JLabel("Installed Libraries");
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        installedSection.add(header, BorderLayout.NORTH);

        installedModel = new DefaultListModel<>();
        installedList = new JList<>(installedModel);
        installedList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        installedSection.add(new JScrollPane(installedList), BorderLayout.CENTER);

        refreshInstalledBtn = new JButton("Refresh Installed");
        refreshInstalledBtn.addActionListener(e -> refreshInstalled());
        JPanel refreshBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshBar.add(refreshInstalledBtn);
        installedSection.add(refreshBar, BorderLayout.SOUTH);

        installedSection.setPreferredSize(new Dimension(0, 200));
        centerPanel.add(installedSection, BorderLayout.NORTH);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        centerPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        refreshInstalled();
    }

    private void searchLibraries() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a search query.", "Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        logArea.setText("");
        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                logArea.append("[Arduino] Searching libraries for: " + query + "\n");
                List<String> cmd = List.of(runner.getCliPath(), "lib", "search", query);
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                List<String> results = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        results.add(line);
                    }
                }
                proc.waitFor();
                return results;
            }

            @Override
            protected void done() {
                try {
                    List<String> results = get();
                    for (String line : results) {
                        logArea.append(line + "\n");
                    }
                    if (results.isEmpty()) {
                        logArea.append("[Arduino] No results found.\n");
                    }
                } catch (Exception e) {
                    logArea.append("[Arduino] Search failed: " + e.getMessage() + "\n");
                }
            }
        };
        worker.execute();
    }

    private void installByName() {
        String name = JOptionPane.showInputDialog(this, "Enter library name (e.g., Servo, Wire):",
                "Install Library", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            logArea.setText("");
            runner.libInstall(name.trim(), logArea);
            refreshInstalled();
        }
    }

    private void refreshInstalled() {
        installedModel.clear();
        installedModel.addElement("Loading...");

        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                List<String> cmd = List.of(runner.getCliPath(), "lib", "list", "--format", "json");
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
                proc.waitFor();
                return lines;
            }

            @Override
            protected void done() {
                try {
                    List<String> lines = get();
                    installedModel.clear();
                    for (String line : lines) {
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        // Simple JSON line: {"library_name":"Servo","version":"1.2.3",...}
                        String name = extractJsonField(line, "library_name");
                        String version = extractJsonField(line, "version");
                        if (!name.isEmpty()) {
                            String entry = name + (!version.isEmpty() ? " " + version : "");
                            installedModel.addElement(entry);
                        }
                    }
                    if (installedModel.isEmpty()) {
                        installedModel.addElement("(none installed)");
                    }
                } catch (Exception e) {
                    installedModel.clear();
                    installedModel.addElement("Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private String extractJsonField(String json, String field) {
        String key = "\"" + field + "\":";
        int idx = json.indexOf(key);
        if (idx < 0) return "";
        idx += key.length();
        while (idx < json.length() && json.charAt(idx) == ' ') idx++;
        if (idx >= json.length()) return "";
        if (json.charAt(idx) == '"') {
            int end = json.indexOf('"', idx + 1);
            return end > idx ? json.substring(idx + 1, end) : "";
        }
        int end = json.indexOf(',', idx);
        if (end < 0) end = json.indexOf('}', idx);
        if (end < 0) end = json.length();
        return json.substring(idx, end).trim();
    }
}
