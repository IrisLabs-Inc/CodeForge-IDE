package com.adonis.Nukepad.toolchain;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ToolchainSettingsPanel extends JDialog {

    private final ToolchainSettings settings;
    private final JTextField arduinoCliField;
    private final JTextField idfPathField;
    private final JTextField androidSdkField;
    private final JTextField jdkHomeField;

    public ToolchainSettingsPanel(JFrame owner, ToolchainSettings settings) {
        super(owner, "Toolchain Settings", true);
        this.settings = settings;

        setSize(620, 340);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("SansSerif", Font.BOLD, 13);
        Font fieldFont = new Font("Monospaced", Font.PLAIN, 13);

        arduinoCliField = createField(fieldFont);
        idfPathField = createField(fieldFont);
        androidSdkField = createField(fieldFont);
        jdkHomeField = createField(fieldFont);

        arduinoCliField.setText(settings.get("arduino-cli"));
        idfPathField.setText(settings.get("idf-path"));
        androidSdkField.setText(settings.get("android-sdk"));
        jdkHomeField.setText(settings.get("jdk-home"));

        String[] labels = {"arduino-cli path:", "ESP-IDF (IDF_PATH):", "Android SDK root:", "JDK home:"};
        JTextField[] fields = {arduinoCliField, idfPathField, androidSdkField, jdkHomeField};
        String[] browseKeys = {"arduino-cli", "idf-path", "android-sdk", "jdk-home"};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(labelFont);
            form.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            form.add(fields[i], gbc);

            gbc.gridx = 2;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            final int idx = i;
            final String key = browseKeys[i];
            JButton browseBtn = new JButton("...");
            browseBtn.setPreferredSize(new Dimension(40, 28));
            browseBtn.addActionListener(e -> browseForField(fields[idx], key));
            form.add(browseBtn, gbc);
        }

        root.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> saveAndClose());
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JTextField createField(Font font) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setPreferredSize(new Dimension(300, 28));
        return field;
    }

    private void browseForField(JTextField field, String key) {
        JFileChooser chooser = new JFileChooser(field.getText());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            field.setText(selected.getAbsolutePath());
        }
    }

    private void saveAndClose() {
        settings.set("arduino-cli", arduinoCliField.getText().trim());
        settings.set("idf-path", idfPathField.getText().trim());
        settings.set("android-sdk", androidSdkField.getText().trim());
        settings.set("jdk-home", jdkHomeField.getText().trim());
        settings.save();
        dispose();
    }
}
