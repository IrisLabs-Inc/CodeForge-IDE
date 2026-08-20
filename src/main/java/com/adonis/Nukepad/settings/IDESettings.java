package com.adonis.Nukepad.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class IDESettings {

    private static final File SETTINGS_FILE = new File(
            System.getProperty("user.home") + "/.nukepad_settings.txt"
    );

    private final Map<String, String> values = new LinkedHashMap<>();
    private final Map<String, String> defaults = new LinkedHashMap<>();

    private static IDESettings instance;

    public static IDESettings getInstance() {
        if (instance == null) {
            instance = new IDESettings();
        }
        return instance;
    }

    private IDESettings() {
        registerDefaults();
        load();
    }

    private void registerDefaults() {
        // Editor
        defaults.put("editor.font.family", "Monospaced");
        defaults.put("editor.font.size", "14");
        defaults.put("editor.tab.size", "4");
        defaults.put("editor.line.wrap", "false");
        defaults.put("editor.bracket.matching", "true");
        defaults.put("editor.code.folding", "true");
        defaults.put("editor.auto.indent", "true");
        defaults.put("editor.highlight.current.line", "true");
        defaults.put("editor.strip.trailing.spaces", "false");
        defaults.put("editor.animate.brackets", "true");

        // Terminal
        defaults.put("terminal.font.size", "13");
        defaults.put("terminal.shell", "");

        // Appearance
        defaults.put("appearance.theme", "dark");
        defaults.put("appearance.window.width", "1280");
        defaults.put("appearance.window.height", "720");

        // General
        defaults.put("general.max.recents", "8");
        defaults.put("general.git.log.count", "20");
    }

    public String get(String key) {
        return values.getOrDefault(key, defaults.getOrDefault(key, ""));
    }

    public int getInt(String key) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException e) {
            String def = defaults.getOrDefault(key, "0");
            try {
                return Integer.parseInt(def);
            } catch (NumberFormatException e2) {
                return 0;
            }
        }
    }

    public boolean getBoolean(String key) {
        return "true".equalsIgnoreCase(get(key));
    }

    public void set(String key, String value) {
        values.put(key, value);
    }

    public void set(String key, int value) {
        values.put(key, String.valueOf(value));
    }

    public void set(String key, boolean value) {
        values.put(key, String.valueOf(value));
    }

    public String getDefault(String key) {
        return defaults.getOrDefault(key, "");
    }

    public void save() {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(SETTINGS_FILE))) {
            w.write("# Nukepad IDE Settings");
            w.newLine();
            for (Map.Entry<String, String> entry : values.entrySet()) {
                w.write(entry.getKey() + "=" + entry.getValue());
                w.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void load() {
        values.putAll(defaults);
        if (!SETTINGS_FILE.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(SETTINGS_FILE))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq > 0) {
                    String key = line.substring(0, eq).trim();
                    String val = line.substring(eq + 1).trim();
                    values.put(key, val);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getConfigPath() {
        return SETTINGS_FILE.getAbsolutePath();
    }
}
