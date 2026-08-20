package com.adonis.Nukepad.toolchain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectTypeDetector {

    public enum ProjectType {
        ARDUINO("Arduino"),
        ESP_IDF("ESP-IDF"),
        ANDROID("Android"),
        UNKNOWN("Unknown");

        private final String label;

        ProjectType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public static ProjectType detect(File projectRoot) {
        if (projectRoot == null || !projectRoot.isDirectory()) {
            return ProjectType.UNKNOWN;
        }

        if (isArduinoProject(projectRoot)) {
            return ProjectType.ARDUINO;
        }
        if (isEspIdfProject(projectRoot)) {
            return ProjectType.ESP_IDF;
        }
        if (isAndroidProject(projectRoot)) {
            return ProjectType.ANDROID;
        }

        return ProjectType.UNKNOWN;
    }

    public static List<ProjectType> detectAll(File projectRoot) {
        List<ProjectType> types = new ArrayList<>();
        if (projectRoot == null || !projectRoot.isDirectory()) {
            return types;
        }
        if (isArduinoProject(projectRoot)) {
            types.add(ProjectType.ARDUINO);
        }
        if (isEspIdfProject(projectRoot)) {
            types.add(ProjectType.ESP_IDF);
        }
        if (isAndroidProject(projectRoot)) {
            types.add(ProjectType.ANDROID);
        }
        return types;
    }

    private static boolean isArduinoProject(File root) {
        return containsFileWithExtension(root, ".ino", 0);
    }

    private static boolean isEspIdfProject(File root) {
        File cmake = new File(root, "CMakeLists.txt");
        File idfComp = new File(root, "idf_component.yml");
        return cmake.exists() && idfComp.exists();
    }

    private static boolean isAndroidProject(File root) {
        boolean hasGradle = new File(root, "build.gradle").exists()
                || new File(root, "build.gradle.kts").exists();
        boolean hasManifest = findFile(root, "AndroidManifest.xml", 0);
        return hasGradle && hasManifest;
    }

    private static boolean containsFileWithExtension(File dir, String ext, int depth) {
        if (depth > 3) return false;
        File[] files = dir.listFiles();
        if (files == null) return false;
        for (File f : files) {
            if (f.isFile() && f.getName().endsWith(ext)) {
                return true;
            }
            if (f.isDirectory() && containsFileWithExtension(f, ext, depth + 1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean findFile(File dir, String name, int depth) {
        if (depth > 5) return false;
        File[] files = dir.listFiles();
        if (files == null) return false;
        for (File f : files) {
            if (f.isFile() && f.getName().equals(name)) {
                return true;
            }
            if (f.isDirectory() && findFile(f, name, depth + 1)) {
                return true;
            }
        }
        return false;
    }
}
