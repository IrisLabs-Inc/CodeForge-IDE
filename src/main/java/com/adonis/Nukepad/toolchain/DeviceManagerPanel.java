package com.adonis.Nukepad.toolchain;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class DeviceManagerPanel extends JPanel {

    private final DefaultListModel<String> serialModel = new DefaultListModel<>();
    private final DefaultListModel<String> adbModel = new DefaultListModel<>();
    private final JList<String> serialList;
    private final JList<String> adbList;
    private final JLabel serialLabel;
    private final JLabel adbLabel;

    public DeviceManagerPanel() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        Font headerFont = new Font("SansSerif", Font.BOLD, 12);

        // Serial ports section
        JPanel serialSection = new JPanel(new BorderLayout(0, 4));
        serialLabel = new JLabel("Serial Ports");
        serialLabel.setFont(headerFont);
        serialSection.add(serialLabel, BorderLayout.NORTH);

        serialList = new JList<>(serialModel);
        serialList.setCellRenderer(new DeviceRenderer());
        serialList.setPreferredSize(new Dimension(0, 120));
        serialSection.add(new JScrollPane(serialList), BorderLayout.CENTER);

        // ADB devices section
        JPanel adbSection = new JPanel(new BorderLayout(0, 4));
        adbLabel = new JLabel("ADB Devices");
        adbLabel.setFont(headerFont);
        adbSection.add(adbLabel, BorderLayout.NORTH);

        adbList = new JList<>(adbModel);
        adbList.setCellRenderer(new DeviceRenderer());
        adbList.setPreferredSize(new Dimension(0, 120));
        adbSection.add(new JScrollPane(adbList), BorderLayout.CENTER);

        // Refresh button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        topBar.add(refreshBtn);

        add(topBar, BorderLayout.NORTH);

        JPanel listsPanel = new JPanel(new BorderLayout(0, 8));
        listsPanel.add(serialSection, BorderLayout.NORTH);
        listsPanel.add(adbSection, BorderLayout.CENTER);
        add(listsPanel, BorderLayout.CENTER);
    }

    public void refresh() {
        serialModel.clear();
        adbModel.clear();
        serialLabel.setText("Serial Ports (loading...)");
        adbLabel.setText("ADB Devices (loading...)");

        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() {
                List<String[]> result = new ArrayList<>();
                result.add(0, getSerialPorts());
                result.add(1, getAdbDevices());
                return result;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    List<String[]> result = get();
                    String[] serialPorts = result.get(0);
                    String[] adbDevices = result.get(1);

                    serialModel.clear();
                    for (String port : serialPorts) {
                        serialModel.addElement(port);
                    }
                    serialLabel.setText("Serial Ports (" + serialPorts.length + ")");

                    adbModel.clear();
                    for (String device : adbDevices) {
                        adbModel.addElement(device);
                    }
                    adbLabel.setText("ADB Devices (" + adbDevices.length + ")");
                } catch (Exception ex) {
                    serialLabel.setText("Serial Ports (error)");
                    adbLabel.setText("ADB Devices (error)");
                }
            }
        }.execute();
    }

    private String[] getSerialPorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        List<String> list = new ArrayList<>();
        for (SerialPort port : ports) {
            String desc = port.getSystemPortName();
            if (port.getDescriptivePortName() != null
                    && !port.getDescriptivePortName().equals(port.getSystemPortName())) {
                desc += " - " + port.getDescriptivePortName();
            }
            list.add(desc);
        }
        return list.toArray(new String[0]);
    }

    private String[] getAdbDevices() {
        List<String> list = new ArrayList<>();
        try {
            ProcessBuilder pb = new ProcessBuilder("adb", "devices", "-l");
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    list.add(line);
                }
            }
            proc.waitFor();
        } catch (Exception ex) {
            list.add("adb not found or not in PATH");
        }
        return list.toArray(new String[0]);
    }

    public String getSelectedSerialPort() {
        return serialList.getSelectedValue();
    }

    public String getSelectedAdbDevice() {
        return adbList.getSelectedValue();
    }

    private static class DeviceRenderer extends JPanel implements ListCellRenderer<String> {
        private final JLabel label = new JLabel();

        DeviceRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 4, 2));
            add(label);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value,
                int index, boolean isSelected, boolean cellHasFocus) {
            label.setText(value);
            label.setFont(new Font("Monospaced", Font.PLAIN, 12));

            if (isSelected) {
                setBackground(UIManager.getColor("List.selectionBackground"));
                label.setForeground(UIManager.getColor("List.selectionForeground"));
            } else {
                setBackground(UIManager.getColor("List.background"));
                label.setForeground(UIManager.getColor("List.foreground"));
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }
}
