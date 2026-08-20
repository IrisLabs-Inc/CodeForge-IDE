package com.adonis.Nukepad.arduino;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.SwingUtilities;

public class SerialMonitorPanel extends JPanel {

    private JComboBox<String> portCombo;
    private JComboBox<Integer> baudCombo;
    private JButton connectBtn;
    private JButton refreshBtn;
    private JButton clearBtn;
    private JTextArea outputArea;
    private JTextField inputField;
    private JButton sendBtn;

    private SerialPort activePort;
    private InputStream serialIn;
    private OutputStream serialOut;
    private Thread readerThread;
    private volatile boolean running = false;
    private final List<String> portHistory = new ArrayList<>();

    private static final int[] BAUD_RATES = {
        300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 74880, 115200, 230400, 250000, 500000, 1000000
    };

    public SerialMonitorPanel() {
        setLayout(new BorderLayout(0, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));

        toolbar.add(new JLabel("Port:"));
        portCombo = new JComboBox<>();
        portCombo.setPreferredSize(new Dimension(180, 24));
        toolbar.add(portCombo);

        refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshPorts());
        toolbar.add(refreshBtn);

        toolbar.add(new JLabel(" Baud:"));
        baudCombo = new JComboBox<>();
        for (int baud : BAUD_RATES) {
            baudCombo.addItem(baud);
        }
        baudCombo.setSelectedItem(115200);
        baudCombo.setPreferredSize(new Dimension(90, 24));
        toolbar.add(baudCombo);

        connectBtn = new JButton("Connect");
        connectBtn.addActionListener(e -> toggleConnection());
        toolbar.add(connectBtn);

        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> outputArea.setText(""));
        toolbar.add(clearBtn);

        add(toolbar, BorderLayout.NORTH);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setBackground(new Color(30, 30, 30));
        outputArea.setForeground(new Color(0, 255, 0));
        outputArea.setCaretColor(Color.GREEN);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout(4, 0));
        inputField = new JTextField();
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 13));
        inputField.addActionListener(e -> sendInput());
        inputPanel.add(inputField, BorderLayout.CENTER);

        sendBtn = new JButton("Send");
        sendBtn.addActionListener(e -> sendInput());
        inputPanel.add(sendBtn, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        refreshPorts();
    }

    public void refreshPorts() {
        portCombo.removeAllItems();
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            String desc = port.getSystemPortName();
            if (port.getDescriptivePortName() != null
                    && !port.getDescriptivePortName().equals(port.getSystemPortName())) {
                desc += " - " + port.getDescriptivePortName();
            }
            portCombo.addItem(desc);
        }
    }

    public String getSelectedPortSystemName() {
        String selected = (String) portCombo.getSelectedItem();
        if (selected == null) return null;
        int dashIdx = selected.indexOf(" - ");
        return dashIdx > 0 ? selected.substring(0, dashIdx).trim() : selected.trim();
    }

    private void toggleConnection() {
        if (running) {
            disconnect();
        } else {
            connect();
        }
    }

    private void connect() {
        String portName = getSelectedPortSystemName();
        if (portName == null) {
            outputArea.append("[Serial] No port selected.\n");
            return;
        }

        int baud = (Integer) baudCombo.getSelectedItem();

        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort p : ports) {
            if (p.getSystemPortName().equals(portName)) {
                activePort = p;
                break;
            }
        }

        if (activePort == null) {
            outputArea.append("[Serial] Port not found: " + portName + "\n");
            return;
        }

        activePort.setComPortParameters(baud, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        activePort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

        if (!activePort.openPort()) {
            outputArea.append("[Serial] Failed to open port: " + portName + "\n");
            activePort = null;
            return;
        }

        running = true;
        serialIn = activePort.getInputStream();
        serialOut = activePort.getOutputStream();
        connectBtn.setText("Disconnect");
        portCombo.setEnabled(false);
        baudCombo.setEnabled(false);
        outputArea.append("[Serial] Connected to " + portName + " at " + baud + " baud.\n");

        readerThread = new Thread(this::readLoop, "SerialMonitor-Reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void disconnect() {
        running = false;
        if (readerThread != null) {
            readerThread.interrupt();
            readerThread = null;
        }
        if (serialIn != null) { try { serialIn.close(); } catch (IOException ignored) {} }
        if (serialOut != null) { try { serialOut.close(); } catch (IOException ignored) {} }
        if (activePort != null) { activePort.closePort(); activePort = null; }

        connectBtn.setText("Connect");
        portCombo.setEnabled(true);
        baudCombo.setEnabled(true);
        outputArea.append("[Serial] Disconnected.\n");
    }

    private void readLoop() {
        byte[] buf = new byte[1024];
        while (running) {
            try {
                if (serialIn != null && serialIn.available() > 0) {
                    int len = serialIn.read(buf);
                    if (len > 0) {
                        String data = new String(buf, 0, len);
                        SwingUtilities.invokeLater(() -> outputArea.append(data));
                    }
                } else {
                    Thread.sleep(50);
                }
            } catch (IOException e) {
                if (running) {
                    SwingUtilities.invokeLater(() -> {
                        outputArea.append("[Serial] Read error: " + e.getMessage() + "\n");
                        disconnect();
                    });
                }
                break;
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void sendInput() {
        if (!running || serialOut == null) {
            outputArea.append("[Serial] Not connected.\n");
            return;
        }
        String text = inputField.getText();
        if (text.isEmpty()) return;

        try {
            String lineEnding = "\r\n";
            serialOut.write((text + lineEnding).getBytes());
            serialOut.flush();
            outputArea.append("> " + text + "\n");
            inputField.setText("");
        } catch (IOException e) {
            outputArea.append("[Serial] Write error: " + e.getMessage() + "\n");
        }
    }

    public void cleanup() {
        disconnect();
    }
}
