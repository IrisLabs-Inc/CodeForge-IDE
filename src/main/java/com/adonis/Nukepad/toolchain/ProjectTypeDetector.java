package com.adonis.Nukepad.toolchain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectTypeDetector {

    public enum ProjectType {
        ARDUINO("Arduino"),
        ESP_IDF("ESP-IDF"),
        ANDROID("Android"),
        FTC("FTC"),
        FRC("FRC"),
        VEX("VEX"),
        ROS2("ROS2"),
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
        if (isFtcProject(projectRoot)) {
            return ProjectType.FTC;
        }
        if (isFrcProject(projectRoot)) {
            return ProjectType.FRC;
        }
        if (isVexProject(projectRoot)) {
            return ProjectType.VEX;
        }
        if (isRos2Project(projectRoot)) {
            return ProjectType.ROS2;
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
        if (isFtcProject(projectRoot)) {
            types.add(ProjectType.FTC);
        }
        if (isFrcProject(projectRoot)) {
            types.add(ProjectType.FRC);
        }
        if (isVexProject(projectRoot)) {
            types.add(ProjectType.VEX);
        }
        if (isRos2Project(projectRoot)) {
            types.add(ProjectType.ROS2);
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

    private static boolean isFtcProject(File root) {
        File ftcDir = new File(root, "ftc_robot_controller");
        if (ftcDir.exists()) return true;

        File gradle = new File(root, "build.gradle");
        if (gradle.exists()) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(gradle.toPath()));
                if (content.contains("com.qualcomm.ftcrobotcontroller")) return true;
            } catch (Exception ignored) {}
        }

        File teamDir = new File(root, "TeamCode");
        if (teamDir.exists()) {
            File teamGradle = new File(teamDir, "build.gradle");
            if (teamGradle.exists()) {
                try {
                    String content = new String(java.nio.file.Files.readAllBytes(teamGradle.toPath()));
                    if (content.contains("com.qualcomm.ftcrobotcontroller")
                            || content.contains("ftc")) return true;
                } catch (Exception ignored) {}
            }
        }

        return containsFileWithExtension(root, ".java", 0) && hasOpModeAnnotation(root);
    }

    private static boolean isFrcProject(File root) {
        File gradle = new File(root, "build.gradle");
        if (gradle.exists()) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(gradle.toPath()));
                if (content.contains("edu.wpi.first")) return true;
                if (content.contains("GradleRIO")) return true;
            } catch (Exception ignored) {}
        }

        File gradleKts = new File(root, "build.gradle.kts");
        if (gradleKts.exists()) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(gradleKts.toPath()));
                if (content.contains("edu.wpi.first")) return true;
            } catch (Exception ignored) {}
        }

        File robotContainer = findFileRecursive(root, "RobotContainer.java", 0);
        if (robotContainer != null) return true;

        File vendorDeps = new File(root, "vendordeps");
        return vendorDeps.exists() && vendorDeps.isDirectory();
    }

    private static boolean isVexProject(File root) {
        File vexDir = new File(root, "vex");
        if (vexDir.exists()) return true;

        File prosCfg = new File(root, "pros.cfg");
        if (prosCfg.exists()) return true;

        File projJson = new File(root, "project.pros");
        if (projJson.exists()) return true;

        return false;
    }

    private static boolean isRos2Project(File root) {
        File packageXml = new File(root, "package.xml");
        if (packageXml.exists()) return true;

        File cmake = new File(root, "CMakeLists.txt");
        if (cmake.exists()) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(cmake.toPath()));
                if (content.contains("ament_cmake")) return true;
            } catch (Exception ignored) {}
        }

        File setupPy = new File(root, "setup.py");
        if (setupPy.exists()) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(setupPy.toPath()));
                if (content.contains("ament_python")) return true;
            } catch (Exception ignored) {}
        }

        File colconMeta = new File(root, "colcon.meta");
        return colconMeta.exists();
    }

    private static boolean hasOpModeAnnotation(File root) {
        return containsStringInJavaFiles(root, "@OpMode", 0)
                || containsStringInJavaFiles(root, "@TeleOp", 0)
                || containsStringInJavaFiles(root, "@Autonomous", 0);
    }

    private static boolean containsStringInJavaFiles(File dir, String search, int depth) {
        if (depth > 5) return false;
        File[] files = dir.listFiles();
        if (files == null) return false;
        for (File f : files) {
            if (f.isFile() && f.getName().endsWith(".java")) {
                try {
                    String content = new String(java.nio.file.Files.readAllBytes(f.toPath()));
                    if (content.contains(search)) return true;
                } catch (Exception ignored) {}
            }
            if (f.isDirectory() && containsStringInJavaFiles(f, search, depth + 1)) {
                return true;
            }
        }
        return false;
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

    private static File findFileRecursive(File dir, String name, int depth) {
        if (depth > 5) return null;
        File[] files = dir.listFiles();
        if (files == null) return null;
        for (File f : files) {
            if (f.isFile() && f.getName().equals(name)) {
                return f;
            }
            if (f.isDirectory()) {
                File found = findFileRecursive(f, name, depth + 1);
                if (found != null) return found;
            }
        }
        return null;
    }
}
