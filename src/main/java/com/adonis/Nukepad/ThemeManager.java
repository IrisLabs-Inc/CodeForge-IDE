package com.adonis.Nukepad;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ThemeManager {
    private static final File THEME_FILE = new File(
        System.getProperty("user.home") + "/.nukepad_theme.txt"
    );

    public static final Map<String, ThemeInfo> THEMES = new LinkedHashMap<>();

    static {
        THEMES.put("dark", new ThemeInfo("Flat Dark", false, null));
        THEMES.put("light", new ThemeInfo("Flat Light", false, null));
        THEMES.put("material-darker", new ThemeInfo("Material Darker", true, "/com/formdev/flatlaf/themes/darcula.theme.json"));
        THEMES.put("material-lighter", new ThemeInfo("Material Lighter", false, "/com/formdev/flatlaf/themes/arc-orange.theme.json"));
        THEMES.put("material-oceanic", new ThemeInfo("Material Oceanic", true, "/com/formdev/flatlaf/themes/one-dark.theme.json"));
        THEMES.put("material-palenight", new ThemeInfo("Material Palenight", true, "/com/formdev/flatlaf/themes/dark-purple.theme.json"));
        THEMES.put("one-dark", new ThemeInfo("One Dark", true, "/com/formdev/flatlaf/themes/one-dark.theme.json"));
        THEMES.put("carbon", new ThemeInfo("Carbon", true, "/com/formdev/flatlaf/themes/carbon.theme.json"));
        THEMES.put("arc-orange", new ThemeInfo("Arc Orange", true, "/com/formdev/flatlaf/themes/arc-orange.theme.json"));
        THEMES.put("solarized-light", new ThemeInfo("Solarized Light", false, "/com/formdev/flatlaf/themes/solarized-light.theme.json"));
        THEMES.put("solarized-dark", new ThemeInfo("Solarized Dark", true, "/com/formdev/flatlaf/themes/solarized-dark.theme.json"));
        THEMES.put("cyan-light", new ThemeInfo("Cyan Light", false, "/com/formdev/flatlaf/themes/cyan-light.theme.json"));
        THEMES.put("dark-purple", new ThemeInfo("Dark Purple", true, "/com/formdev/flatlaf/themes/dark-purple.theme.json"));
    }

    public static class ThemeInfo {
        public final String displayName;
        public final boolean isDark;
        public final String jsonResource;

        public ThemeInfo(String displayName, boolean isDark, String jsonResource) {
            this.displayName = displayName;
            this.isDark = isDark;
            this.jsonResource = jsonResource;
        }
    }

    private static final Color ACCENT = new Color(0, 122, 204);

    public static void apply() throws UnsupportedLookAndFeelException {
        String key = load();
        applyTheme(key);
    }

    public static void applyTheme(String themeKey) throws UnsupportedLookAndFeelException {
        try {
            ThemeInfo info = THEMES.get(themeKey);
            if (info == null) info = THEMES.get("dark");

            if (info.jsonResource == null) {
                if (info.isDark) {
                    applyFlatDark();
                } else {
                    applyFlatLight();
                }
            } else {
                InputStream is = ThemeManager.class.getResourceAsStream(info.jsonResource);
                if (is != null) {
                    IntelliJTheme theme = new IntelliJTheme(is);
                    FlatLaf laf = IntelliJTheme.createLaf(theme);
                    configureLaf(laf);
                    UIManager.setLookAndFeel(laf);
                    is.close();
                } else {
                    if (info.isDark) applyFlatDark();
                    else applyFlatLight();
                }
            }
            save(themeKey);
        } catch (Exception e) {
            e.printStackTrace();
            applyFlatDark();
        }
    }

    public static void applyFlatDark() throws UnsupportedLookAndFeelException {
        FlatDarkLaf laf = new FlatDarkLaf();
        configureLaf(laf);
        UIManager.setLookAndFeel(laf);
    }

    public static void applyFlatLight() throws UnsupportedLookAndFeelException {
        FlatLightLaf laf = new FlatLightLaf();
        configureLaf(laf);
        UIManager.setLookAndFeel(laf);
    }

    private static void configureLaf(FlatLaf laf) {
        System.setProperty("laf.useWindowDecorations", "true");
        System.setProperty("flatlaf.uiComponent.useNativeWindowDecorations", "true");
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Button.arc", 6);
        UIManager.put("Component.arc", 6);
        UIManager.put("TextComponent.arc", 4);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new java.awt.Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("TabbedPane.underlineColor", ACCENT);
        UIManager.put("TabbedPane.hoverColor", new Color(255, 255, 255, 20));
        UIManager.put("TabbedPane.focusColor", new Color(255, 255, 255, 30));
        UIManager.put("Tree.selectionBackground", ACCENT);
        UIManager.put("Tree.selectionForeground", Color.WHITE);
        UIManager.put("List.selectionBackground", ACCENT);
        UIManager.put("List.selectionForeground", Color.WHITE);
        UIManager.put("MenuItem.selectionBackground", ACCENT);
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);
        UIManager.put("MenuItem.acceleratorForeground", new Color(128, 128, 128));
        UIManager.put("MenuItem.acceleratorSelectionForeground", Color.WHITE);
        UIManager.put("Separator.foreground", new Color(128, 128, 128, 60));
        UIManager.put("Component.borderColor", new Color(128, 128, 128, 80));
        UIManager.put("ScrollBar.track", new Color(0, 0, 0, 0));
        UIManager.put("MenuBar.background", UIManager.getColor("Panel.background"));
        UIManager.put("ToolBar.isRollover", true);
    }

    public static String load() {
        if (!THEME_FILE.exists()) return "dark";
        try (BufferedReader r = new BufferedReader(new FileReader(THEME_FILE))) {
            String line = r.readLine();
            return (line != null && !line.isBlank()) ? line.trim() : "dark";
        } catch (IOException ex) {
            return "dark";
        }
    }

    public static void save(String theme) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(THEME_FILE))) {
            w.write(theme);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
