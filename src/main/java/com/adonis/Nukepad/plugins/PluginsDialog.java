// src/com/adonis/Nukepad/plugins/PluginsDialog.java
package com.adonis.Nukepad.plugins;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class PluginsDialog extends JDialog {
    private JTable pluginTable;
    private DefaultTableModel tableModel;
    private PluginManager pluginManager;
    private File pluginDir;
    
    public PluginsDialog(JFrame parent, PluginManager manager, File pluginDirectory) {
        super(parent, "Manage Plugins", true);
        this.pluginManager = manager;
        this.pluginDir = pluginDirectory;
        
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        initializeUI();
    }
    
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ── Header ──────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Installed Plugins");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // ── Table ───────────────────────────────────────────────────────
        String[] columnNames = {"Enabled", "Name", "Version", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only enabled column is editable
            }
        };
        
        pluginTable = new JTable(tableModel);
        pluginTable.setRowHeight(25);
        pluginTable.getColumnModel().getColumn(0).setMaxWidth(70);
        pluginTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        pluginTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        pluginTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        
        JScrollPane scrollPane = new JScrollPane(pluginTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // ── Button Panel ────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> refreshPluginList());
        
        JButton openFolderBtn = new JButton("📁 Open Plugins Folder");
        openFolderBtn.addActionListener(e -> openPluginsFolder());
        
        JButton uninstallBtn = new JButton("❌ Uninstall");
        uninstallBtn.addActionListener(e -> uninstallSelectedPlugin());
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(openFolderBtn);
        buttonPanel.add(uninstallBtn);
        buttonPanel.add(closeBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // ── Info Panel ──────────────────────────────────────────────────
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        JTextArea infoArea = new JTextArea();
        infoArea.setText("Place .jar files in the plugins folder to install plugins.\n" +
                         "Plugins will be loaded on the next editor restart.\n" +
                         "Enable/disable plugins using the checkbox.");
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        infoPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        infoPanel.setPreferredSize(new Dimension(0, 60));
        
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Load plugins on dialog open
        refreshPluginList();
    }
    
    private void refreshPluginList() {
        tableModel.setRowCount(0);
        
        // Add loaded plugins
        List<Plugin> plugins = pluginManager.getLoadedPlugins();
        for (Plugin plugin : plugins) {
            if (plugin != null) {
                Object[] row = {
                    true, // Enabled
                    plugin.getName(),
                    plugin.getVersion(),
                    plugin.getDescription()
                };
                tableModel.addRow(row);
            }
        }
        
        // Add jar files from plugins folder that aren't loaded yet
        if (pluginDir.exists()) {
            File[] jarFiles = pluginDir.listFiles((d, n) -> n.endsWith(".jar"));
            if (jarFiles != null) {
                for (File jar : jarFiles) {
                    // Check if already in table
                    boolean found = false;
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        // Could improve this by storing file references
                        found = true;
                        break;
                    }
                    
                    if (!found) {
                        Object[] row = {
                            true,
                            jar.getName(),
                            "Unknown",
                            "Plugin JAR file"
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        }
        
        pluginTable.repaint();
    }
    
    private void openPluginsFolder() {
        try {
            if (!pluginDir.exists()) {
                pluginDir.mkdirs();
            }
            Desktop.getDesktop().open(pluginDir);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Failed to open plugins folder: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void uninstallSelectedPlugin() {
        int selectedRow = pluginTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a plugin to uninstall.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String pluginName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to uninstall '" + pluginName + "'?",
            "Confirm Uninstall", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Find and delete the jar file
            if (pluginDir.exists()) {
                File[] jarFiles = pluginDir.listFiles((d, n) -> n.endsWith(".jar"));
                if (jarFiles != null) {
                    for (File jar : jarFiles) {
                        if (jar.getName().contains(pluginName.toLowerCase().replace(" ", ""))) {
                            if (jar.delete()) {
                                JOptionPane.showMessageDialog(this,
                                    "Plugin uninstalled successfully.\nRestart the editor to apply changes.",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                                refreshPluginList();
                            } else {
                                JOptionPane.showMessageDialog(this,
                                    "Failed to delete plugin file.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            return;
                        }
                    }
                }
            }
            
            JOptionPane.showMessageDialog(this,
                "Could not find plugin file to uninstall.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}