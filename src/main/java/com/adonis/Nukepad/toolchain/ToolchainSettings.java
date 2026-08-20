package com.adonis.Nukepad.toolchain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ToolchainSettings {

    private static final File SETTINGS_FILE = new File(
            System.getProperty("user.home") + "/.nukepad_toolchains.txt"
    );

    private static final Map<String, String> defaults = new LinkedHashMap<>();
    static {
        defaults.put("arduino-cli", "");
        defaults.put("idf-path", "");
        defaults.put("android-sdk", "");
        defaults.put("jdk-home", System.getProperty("java.home"));
    }

    private final Map<String, String> values = new LinkedHashMap<>(defaults);

    public ToolchainSettings() {
        load();
    }

    public String get(String key) {
        return values.getOrDefault(key, defaults.getOrDefault(key, ""));
    }

    public void set(String key, String value) {
        values.put(key, value);
    }

    public void save() {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(SETTINGS_FILE))) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                w.write(entry.getKey() + "=" + entry.getValue());
                w.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void load() {
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

    public boolean isToolInstalled(String key) {
        String path = get(key);
        if (path.isEmpty()) return false;
        return new File(path).exists();
    }

    public static String getConfigPath() {
        return SETTINGS_FILE.getAbsolutePath();
    }
}
