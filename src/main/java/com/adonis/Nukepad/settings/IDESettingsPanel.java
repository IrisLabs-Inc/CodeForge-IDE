package com.adonis.Nukepad.settings;

import com.adonis.Nukepad.ThemeManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class IDESettingsPanel extends JDialog {

    private final IDESettings settings;
    private final List<Runnable> applyCallbacks = new ArrayList<>();

    // Editor fields
    private JComboBox<String> editorFontFamily;
    private JSpinner editorFontSize;
    private JSpinner editorTabSize;
    private JCheckBox editorLineWrap;
    private JCheckBox editorBracketMatching;
    private JCheckBox editorCodeFolding;
    private JCheckBox editorAutoIndent;
    private JCheckBox editorHighlightLine;

    // Terminal fields
    private JSpinner terminalFontSize;
    private JTextField terminalShell;

    // Appearance fields
    private JComboBox<String> themeCombo;
    private JSpinner windowWidth;
    private JSpinner windowHeight;

    // General fields
    private JSpinner maxRecents;
    private JSpinner gitLogCount;

    public IDESettingsPanel(JFrame owner, IDESettings settings) {
        super(owner, "Settings", true);
        this.settings = settings;

        setSize(650, 520);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Editor", buildEditorTab());
        tabs.addTab("Terminal", buildTerminalTab());
        tabs.addTab("Appearance", buildAppearanceTab());
        tabs.addTab("General", buildGeneralTab());
        root.add(tabs, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton saveBtn = new JButton("Save & Close");
        saveBtn.addActionListener(e -> saveAndClose());
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    public void onApply(Runnable callback) {
        applyCallbacks.add(callback);
    }

    private JPanel buildEditorTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 12);
        int row = 0;

        // Font family
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Font family:", labelFont), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        editorFontFamily = new JComboBox<>(getAvailableFontFamilies());
        editorFontFamily.setSelectedItem(settings.get("editor.font.family"));
        panel.add(editorFontFamily, gbc);

        // Font size
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Font size:", labelFont), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        editorFontSize = new JSpinner(new SpinnerNumberModel(
                settings.getInt("editor.font.size"), 8, 72, 1));
        panel.add(editorFontSize, gbc);

        // Tab size
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Tab size:", labelFont), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        editorTabSize = new JSpinner(new SpinnerNumberModel(
                settings.getInt("editor.tab.size"), 1, 16, 1));
        panel.add(editorTabSize, gbc);

        // Checkboxes
        editorLineWrap = new JCheckBox("Enable line wrapping", settings.getBoolean("editor.line.wrap"));
        editorBracketMatching = new JCheckBox("Bracket matching", settings.getBoolean("editor.bracket.matching"));
        editorCodeFolding = new JCheckBox("Code folding", settings.getBoolean("editor.code.folding"));
        editorAutoIndent = new JCheckBox("Auto indent", settings.getBoolean("editor.auto.indent"));
        editorHighlightLine = new JCheckBox("Highlight current line", settings.getBoolean("editor.highlight.current.line"));

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 1.0; gbc.gridwidth = 2;
        panel.add(editorLineWrap, gbc);
        row++;
        gbc.gridy = row;
        panel.add(editorBracketMatching, gbc);
        row++;
        gbc.gridy = row;
        panel.add(editorCodeFolding, gbc);
        row++;
        gbc.gridy = row;
        panel.add(editorAutoIndent, gbc);
        row++;
        gbc.gridy = row;
        panel.add(editorHighlightLine, gbc);

        // Spacer
        row++;
        gbc.gridy = row; gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return wrapInScroll(panel);
    }

    private JPanel buildTerminalTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 12);
        int row = 0;

        // Font size
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Font size:", labelFont), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        terminalFontSize = new JSpinner(new SpinnerNumberModel(
                settings.getInt("terminal.font.size"), 8, 48, 1));
        panel.add(terminalFontSize, gbc);

        // Shell command
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Shell command:", labelFont), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        terminalShell = new JTextField(settings.get("terminal.shell"), 20);
        terminalShell.setToolTipText("Leave empty to auto-detect (bash/zsh/cmd.exe)");
        panel.add(terminalShell, gbc);

        // Spacer
        row++;
        gbc.gridy = row; gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return wrapInScroll(panel);
    }

    private JPanel buildAppearanceTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 12);
        int row = 0;

        // Theme
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Theme:", labelFont), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        themeCombo = new JComboBox<>(ThemeManager.THEMES.keySet().toArray(new String[0]));
        themeCombo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                if (value instanceof String key) {
                    ThemeManager.ThemeInfo info = ThemeManager.THEMES.get(key);
                    setText(info != null ? info.displayName : key);
                }
                return this;
            }
        });
        themeCombo.setSelectedItem(settings.get("appearance.theme"));
        panel.add(themeCombo, gbc);

        // Window width
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Window width:", labelFont), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        windowWidth = new JSpinner(new SpinnerNumberModel(
                settings.getInt("appearance.window.width"), 800, 3840, 10));
        panel.add(windowWidth, gbc);

        // Window height
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Window height:", labelFont), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        windowHeight = new JSpinner(new SpinnerNumberModel(
                settings.getInt("appearance.window.height"), 600, 2160, 10));
        panel.add(windowHeight, gbc);

        // Spacer
        row++;
        gbc.gridy = row; gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return wrapInScroll(panel);
    }

    private JPanel buildGeneralTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 12);
        int row = 0;

        // Max recents
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Max recent files:", labelFont), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        maxRecents = new JSpinner(new SpinnerNumberModel(
                settings.getInt("general.max.recents"), 1, 50, 1));
        panel.add(maxRecents, gbc);

        // Git log count
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Git log entries:", labelFont), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gitLogCount = new JSpinner(new SpinnerNumberModel(
                settings.getInt("general.git.log.count"), 5, 200, 5));
        panel.add(gitLogCount, gbc);

        // Spacer
        row++;
        gbc.gridy = row; gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return wrapInScroll(panel);
    }

    private void saveAndClose() {
        settings.set("editor.font.family", editorFontFamily.getSelectedItem().toString());
        settings.set("editor.font.size", (Integer) editorFontSize.getValue());
        settings.set("editor.tab.size", (Integer) editorTabSize.getValue());
        settings.set("editor.line.wrap", editorLineWrap.isSelected());
        settings.set("editor.bracket.matching", editorBracketMatching.isSelected());
        settings.set("editor.code.folding", editorCodeFolding.isSelected());
        settings.set("editor.auto.indent", editorAutoIndent.isSelected());
        settings.set("editor.highlight.current.line", editorHighlightLine.isSelected());

        settings.set("terminal.font.size", (Integer) terminalFontSize.getValue());
        settings.set("terminal.shell", terminalShell.getText().trim());

        settings.set("appearance.theme", themeCombo.getSelectedItem().toString());
        settings.set("appearance.window.width", (Integer) windowWidth.getValue());
        settings.set("appearance.window.height", (Integer) windowHeight.getValue());

        settings.set("general.max.recents", (Integer) maxRecents.getValue());
        settings.set("general.git.log.count", (Integer) gitLogCount.getValue());

        settings.save();

        for (Runnable cb : applyCallbacks) {
            cb.run();
        }

        dispose();
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        return lbl;
    }

    private JPanel wrapInScroll(JPanel content) {
        JPanel wrapper = new JPanel(new BorderLayout());
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private String[] getAvailableFontFamilies() {
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] families = ge.getAvailableFontFamilyNames();
        List<String> result = new ArrayList<>();
        String preferred = settings.get("editor.font.family");
        for (String f : families) {
            result.add(f);
        }
        return result.toArray(new String[0]);
    }
}
