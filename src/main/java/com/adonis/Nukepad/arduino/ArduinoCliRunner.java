package com.adonis.Nukepad.arduino;

import com.adonis.Nukepad.Nukepad;
import com.adonis.Nukepad.toolchain.BuildRunner;
import com.adonis.Nukepad.toolchain.ProblemsManager;
import com.adonis.Nukepad.toolchain.ToolchainSettings;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class ArduinoCliRunner implements BuildRunner {

    public String getCliPath() {
        Nukepad nukepad = Nukepad.getInstance();
        if (nukepad == null) return "arduino-cli";
        ToolchainSettings settings = nukepad.getToolchainSettings();
        if (settings == null) return "arduino-cli";
        String path = settings.get("arduino-cli");
        return (path != null && !path.trim().isEmpty()) ? path.trim() : "arduino-cli";
    }

    @Override
    public String getName() {
        return "Arduino";
    }

    @Override
    public boolean canHandle(File projectRoot, File currentFile) {
        if (currentFile != null && currentFile.getName().endsWith(".ino")) return true;
        if (projectRoot == null) return false;
        File sketch = findSketch(projectRoot);
        return sketch != null;
    }

    private File findSketch(File dir) {
        if (dir == null || !dir.isDirectory()) return null;
        File[] inoFiles = dir.listFiles((d, name) -> name.endsWith(".ino"));
        if (inoFiles != null && inoFiles.length > 0) return inoFiles[0];
        return null;
    }

    private File getSketchDir(File projectRoot, File currentFile) {
        if (currentFile != null && currentFile.getName().endsWith(".ino")) {
            return currentFile.getParentFile() != null ? currentFile.getParentFile() : projectRoot;
        }
        File sketch = findSketch(projectRoot);
        return sketch != null ? sketch.getParentFile() : projectRoot;
    }

    private File getFqbnFile(File sketchDir) {
        return new File(sketchDir, ".arduino-cli.fqbn");
    }

    private String getDefaultFqbn() {
        Nukepad nukepad = Nukepad.getInstance();
        if (nukepad != null) {
            String fqbn = nukepad.getToolchainSettings().get("arduino.default.fqbn");
            if (fqbn != null && !fqbn.trim().isEmpty()) return fqbn.trim();
        }
        return "arduino:avr:uno";
    }

    private String getDefaultPort() {
        Nukepad nukepad = Nukepad.getInstance();
        if (nukepad != null) {
            String port = nukepad.getToolchainSettings().get("arduino.default.port");
            if (port != null && !port.trim().isEmpty()) return port.trim();
        }
        return null;
    }

    @Override
    public void compile(File projectRoot, File currentFile, JTextArea terminalArea, JTabbedPane bottomTabs, ProblemsManager problems) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                File sketchDir = getSketchDir(projectRoot, currentFile);
                File fqbnFile = getFqbnFile(sketchDir);
                String fqbn = getDefaultFqbn();
                if (fqbnFile.exists()) {
                    fqbn = new String(java.nio.file.Files.readAllBytes(fqbnFile.toPath())).trim();
                }

                terminalArea.append("[Arduino] Compiling sketch...\n");
                terminalArea.append("[Arduino] FQBN: " + fqbn + "\n");
                bottomTabs.setSelectedIndex(0);

                String sketchPath = sketchDir.getAbsolutePath();
                List<String> cmd = new ArrayList<>();
                cmd.add(getCliPath());
                cmd.add("compile");
                cmd.add("--fqbn");
                cmd.add(fqbn);
                cmd.add("--warnings");
                cmd.add("all");
                cmd.add(sketchPath);

                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                pb.directory(sketchDir);
                Process proc = pb.start();

                Pattern errorPattern = Pattern.compile("(.+\\.ino):(\\d+)(:\\d+)?:\\s*(error:.+)");
                Pattern warningPattern = Pattern.compile("(.+\\.ino):(\\d+)(:\\d+)?:\\s*(warning:.+)");
                Pattern generalError = Pattern.compile("^(Error|error).+", Pattern.CASE_INSENSITIVE);

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        terminalArea.append(line + "\n");

                        Matcher errMatch = errorPattern.matcher(line);
                        if (errMatch.find()) {
                            String file = new File(errMatch.group(1)).getName();
                            int lineNum = Integer.parseInt(errMatch.group(2));
                            problems.addError(errMatch.group(4).trim(), lineNum, file);
                            continue;
                        }

                        Matcher warnMatch = warningPattern.matcher(line);
                        if (warnMatch.find()) {
                            String file = new File(warnMatch.group(1)).getName();
                            int lineNum = Integer.parseInt(warnMatch.group(2));
                            problems.addWarning(warnMatch.group(4).trim(), lineNum, file);
                            continue;
                        }

                        if (generalError.matcher(line).find()) {
                            problems.addError(line.trim(), 0, currentFile != null ? currentFile.getName() : "sketch");
                        }
                    }
                }

                int exitCode = proc.waitFor();
                if (exitCode == 0) {
                    terminalArea.append("[Arduino] Compilation successful.\n");
                } else {
                    terminalArea.append("[Arduino] Compilation failed (exit code " + exitCode + ").\n");
                }
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public void run(File projectRoot, File currentFile, JTextArea terminalArea, JTabbedPane bottomTabs) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                File sketchDir = getSketchDir(projectRoot, currentFile);
                File fqbnFile = getFqbnFile(sketchDir);
                String fqbn = getDefaultFqbn();
                if (fqbnFile.exists()) {
                    fqbn = new String(java.nio.file.Files.readAllBytes(fqbnFile.toPath())).trim();
                }

                String port = getDefaultPort();
                terminalArea.append("[Arduino] Uploading sketch...\n");
                terminalArea.append("[Arduino] FQBN: " + fqbn + "\n");
                if (port != null) {
                    terminalArea.append("[Arduino] Port: " + port + "\n");
                } else {
                    terminalArea.append("[Arduino] Warning: No port configured. Set it in Toolchain Settings.\n");
                }
                bottomTabs.setSelectedIndex(0);

                String sketchPath = sketchDir.getAbsolutePath();
                List<String> cmd = new ArrayList<>();
                cmd.add(getCliPath());
                cmd.add("upload");
                cmd.add("--fqbn");
                cmd.add(fqbn);
                if (port != null && !port.isEmpty()) {
                    cmd.add("--port");
                    cmd.add(port);
                }
                cmd.add(sketchPath);

                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                pb.directory(sketchDir);
                Process proc = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        terminalArea.append(line + "\n");
                    }
                }

                int exitCode = proc.waitFor();
                if (exitCode == 0) {
                    terminalArea.append("[Arduino] Upload successful.\n");
                } else {
                    terminalArea.append("[Arduino] Upload failed (exit code " + exitCode + ").\n");
                }
                return null;
            }
        };
        worker.execute();
    }

    public void boardList(JTextArea terminalArea) {
        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                List<String> cmd = List.of(getCliPath(), "board", "list");
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                List<String> boards = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        boards.add(line);
                        if (terminalArea != null) terminalArea.append(line + "\n");
                    }
                }
                proc.waitFor();
                return boards;
            }
        };
        worker.execute();
    }

    public void coreInstall(String coreName, JTextArea terminalArea) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (terminalArea != null) terminalArea.append("[Arduino] Installing core: " + coreName + "...\n");
                List<String> cmd = List.of(getCliPath(), "core", "install", coreName);
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (terminalArea != null) terminalArea.append(line + "\n");
                    }
                }

                int exitCode = proc.waitFor();
                if (exitCode == 0) {
                    if (terminalArea != null) terminalArea.append("[Arduino] Core " + coreName + " installed.\n");
                } else {
                    if (terminalArea != null) terminalArea.append("[Arduino] Core install failed (exit code " + exitCode + ").\n");
                }
                return null;
            }
        };
        worker.execute();
    }

    public void coreList(JTextArea terminalArea) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<String> cmd = List.of(getCliPath(), "core", "list");
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (terminalArea != null) terminalArea.append(line + "\n");
                    }
                }
                proc.waitFor();
                return null;
            }
        };
        worker.execute();
    }

    public void libInstall(String libName, JTextArea terminalArea) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (terminalArea != null) terminalArea.append("[Arduino] Installing library: " + libName + "...\n");
                List<String> cmd = List.of(getCliPath(), "lib", "install", libName);
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (terminalArea != null) terminalArea.append(line + "\n");
                    }
                }

                int exitCode = proc.waitFor();
                if (exitCode == 0) {
                    if (terminalArea != null) terminalArea.append("[Arduino] Library " + libName + " installed.\n");
                } else {
                    if (terminalArea != null) terminalArea.append("[Arduino] Library install failed (exit code " + exitCode + ").\n");
                }
                return null;
            }
        };
        worker.execute();
    }

    public void libSearch(String query, JTextArea terminalArea) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (terminalArea != null) terminalArea.append("[Arduino] Searching libraries for: " + query + "...\n");
                List<String> cmd = List.of(getCliPath(), "lib", "search", query);
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process proc = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (terminalArea != null) terminalArea.append(line + "\n");
                    }
                }
                proc.waitFor();
                return null;
            }
        };
        worker.execute();
    }
}
