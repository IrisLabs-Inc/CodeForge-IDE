/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.adonis.Nukepad;

import com.adonis.Nukepad.plugins.PluginContext;
import com.adonis.Nukepad.plugins.PluginManager;
import com.adonis.Nukepad.plugins.PluginsDialog;
import com.adonis.Nukepad.lsp.*;
import com.adonis.Nukepad.toolchain.BuildRunner;
import com.adonis.Nukepad.toolchain.DeviceManagerPanel;
import com.adonis.Nukepad.toolchain.BuildOrchestrator;
import com.adonis.Nukepad.toolchain.ProblemsManager;
import com.adonis.Nukepad.toolchain.ProjectTypeDetector;
import com.adonis.Nukepad.toolchain.ToolchainSettings;
import com.adonis.Nukepad.toolchain.ToolchainSettingsPanel;
import com.adonis.Nukepad.settings.IDESettings;
import com.adonis.Nukepad.settings.IDESettingsPanel;
import com.adonis.Nukepad.ui.FileTreeManager;
import com.adonis.Nukepad.arduino.ArduinoCliRunner;
import com.adonis.Nukepad.arduino.SerialMonitorPanel;
import com.adonis.Nukepad.arduino.LibraryManagerPanel;
import com.adonis.Nukepad.arduino.BoardManagerPanel;
import com.adonis.Nukepad.android.AndroidBuildRunner;
import com.adonis.Nukepad.android.LogcatPanel;
import com.adonis.Nukepad.android.DeviceFileExplorerPanel;
import com.adonis.Nukepad.android.GradleSyncPanel;
import com.adonis.Nukepad.android.ApkAnalyzerPanel;
import com.adonis.Nukepad.android.SigningConfigPanel;
import com.adonis.Nukepad.android.BuildVariantsPanel;
import com.adonis.Nukepad.android.SdkManagerPanel;
import com.adonis.Nukepad.espidf.EspIdfBuildRunner;
import com.adonis.Nukepad.espidf.EspIdfProjectTemplate;
import com.adonis.Nukepad.robotics.FtcBuildRunner;
import com.adonis.Nukepad.robotics.FrcBuildRunner;
import com.adonis.Nukepad.robotics.FtcOpModeTemplates;
import com.adonis.Nukepad.robotics.FrcProjectTemplates;
import com.adonis.Nukepad.robotics.SdkReferencePanel;
import com.adonis.Nukepad.robotics.FtcLogcatPanel;
import com.adonis.Nukepad.robotics.FtcHardwareConfigEditor;
import com.adonis.Nukepad.robotics.GamepadVisualizerPanel;
import com.adonis.Nukepad.robotics.MotorServoHelperPanel;
import com.adonis.Nukepad.robotics.CodeSnippetsPanel;
import com.adonis.Nukepad.robotics.FtcDashboardPanel;
import com.adonis.Nukepad.robotics.FrcSmartDashboardPanel;
import com.adonis.Nukepad.robotics.CompetitionModePanel;
import com.adonis.Nukepad.robotics.FtcProjectCreator;
import com.adonis.Nukepad.robotics.FrcProjectCreator;
import com.adonis.Nukepad.robotics.VexProjectCreator;
import com.adonis.Nukepad.robotics.RoboticsSdkVersionCache;
import com.adonis.Nukepad.editor.EditorUndoManager;
import com.adonis.Nukepad.editor.FindReplaceDialog;
import com.adonis.Nukepad.workspace.WorkspaceManager;
import com.adonis.Nukepad.arduino.ArduinoProjectCreator;
import com.adonis.Nukepad.ui.JetBrainsMenuBuilder;
import com.adonis.Nukepad.ui.StatusBar;
import com.adonis.Nukepad.ui.BottomToolWindowBar;
import com.adonis.Nukepad.ui.ProjectToolWindow;
import com.adonis.Nukepad.ui.IconManager;
import com.adonis.Nukepad.ui.animation.AnimationEngine;
import com.adonis.Nukepad.ui.animation.CursorTrailEffect;
import com.adonis.Nukepad.ui.animation.SmoothTabTransition;
import com.adonis.Nukepad.ui.animation.SmoothPanelAnimator;
import com.adonis.Nukepad.ui.animation.FocusDimmer;
import java.util.ArrayList;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.ButtonGroup;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author croco
 */
public class Nukepad extends JFrame implements ActionListener {
    private InteractiveTerminal interactiveTerminal; // Declares the terminal (with the output of certain command
                                                     // executions)
    private CombinedProvider sharedProvider;// Declares the combined provider (forgit what it is)
    private JSplitPane verticalSplit;// Declares a vertical split pane (top + bottom, usefull for showing both the
                                     // terminal and the editor itself)
    private JSplitPane outerHSplit;// Declares the outer horizontal split pane(left and right, only while the left
                                   // tabs section is moved to the center)
    private boolean terminalVisible = false;
    private int lastDividerLocation = 500;
    private int lastSidebarWidth = 280;
    private GitRunner gitRunner; // Runs git
    private GitPanel gitPanel; // The git pannel
    private File activeDirectory; // Active directory, usefull for git
    private ActivityBar activityBar;
    private PluginManager pluginManager;
    private volatile Process runningProcess;

    private LspClientManager lspManager;
    private LspDocumentAdapter lspDocAdapter;
    private LspDiagnosticsParser lspDiagnosticsParser;
    private LspCompletionProvider lspCompletionProvider;
    private LspHoverListener lspHoverListener;
    private LspGoToDefinitionHandler lspGoToHandler;

    private ToolchainSettings toolchainSettings;
    private IDESettings idesettings;
    private ProblemsManager problemsManager;
    private final List<BuildRunner> buildRunners = new ArrayList<>();
    private final BuildOrchestrator buildOrchestrator = new BuildOrchestrator();
    private FileTreeManager fileTreeManager;
    private DeviceManagerPanel deviceManagerPanel;

    private final File CATEGORIES_CONFIG_FILE = new File(System.getProperty("user.home"), ".nukepad_categories.cfg");
    private Map<String, List<File>> categoriesData = new LinkedHashMap<>();

    private DefaultTreeModel openedProjectsTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode("Projects"));
    private JTree fileTree;
    private JTree openedProjectsTree;

    enum SidebarPosition {
        LEFT,
        CENTER,
        RIGHT
    }

    private SidebarPosition sidebarPosition = SidebarPosition.LEFT;
    private JTabbedPane rightTabs;
    private JTabbedPane leftTabsPanel;
    private JPanel bottomPanelCache;
    private StatusBar statusBar;
    private BottomToolWindowBar bottomToolWindowBar;
    private ProjectToolWindow projectToolWindow;
    private CursorTrailEffect cursorTrail;
    private SmoothTabTransition tabTransition;
    private SmoothPanelAnimator sidebarAnimator;
    private FocusDimmer focusDimmer;
    private JPanel topSection;
    private final WorkspaceManager workspaceManager = new WorkspaceManager();
    private FindReplaceDialog findReplaceDialog;

    public static Nukepad getInstance() {
        return instance;
    }

    private JTabbedPane tabs;
    RSyntaxTextArea text;
    JFrame frame;
    private File currentFile;
    private JTabbedPane bottomTabs;
    private JTextArea terminalArea;
    private javax.swing.table.DefaultTableModel problemsModel;

    Nukepad(File projectRoot) {
        instance = this;
        try {
            ThemeManager.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame = new JFrame("CodeForge IDE");
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/nukepadlogo.png"));
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                workspaceManager.saveWorkspace(tabs);
                workspaceManager.stopAutoSave();
                lspManager.shutdownAll();
            }
        });
        frame.setLayout(new BorderLayout());

        tabs = new JTabbedPane();
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        text = new RSyntaxTextArea();
        text.setCodeFoldingEnabled(true);
        text.setAntiAliasingEnabled(true);
        sharedProvider = new CombinedProvider(text);
        AutoCompletion ac = new AutoCompletion(sharedProvider);
        ac.setAutoActivationEnabled(true);
        ac.setAutoActivationDelay(300);
        ac.install(text);
        idesettings = IDESettings.getInstance();
        applyEditorTheme(text);
        applySettingsToEditor(text);
        installLiveErrorParser(text);

        lspManager = new LspClientManager();
        lspDocAdapter = new LspDocumentAdapter(text, lspManager);
        lspCompletionProvider = new LspCompletionProvider(text, lspManager);
        lspHoverListener = new LspHoverListener(text, lspManager);
        lspGoToHandler = new LspGoToDefinitionHandler(text, lspManager, null);
        RTextScrollPane scroll = new RTextScrollPane(text);
        scroll.setRowHeaderView(new LineNumberPanel(text));
        tabs.addTab("Untitled", scroll);
        
        pluginManager = new PluginManager(
            System.getProperty("user.home") + "/.nukepad_plugins"
        );
        PluginContext pluginContext = new PluginContext(this, text);
        pluginManager.loadPlugins(pluginContext);
        
        text.addCaretListener(e -> {
            if (statusBar != null) {
                try {
                    int pos = Math.min(e.getDot(), text.getDocument().getLength());
                    int line = text.getLineOfOffset(pos) + 1;
                    int col = pos - text.getLineStartOffset(text.getLineOfOffset(pos)) + 1;
                    statusBar.setFileInfo(
                        currentFile != null ? currentFile.getAbsolutePath() : "",
                        line, col
                    );
                } catch (Exception ignored) {}
            }
        });

        toolchainSettings = new ToolchainSettings();
        deviceManagerPanel = new DeviceManagerPanel();
        ArduinoCliRunner arduinoRunner = new ArduinoCliRunner();
        registerBuildRunner(arduinoRunner);
        AndroidBuildRunner androidRunner = new AndroidBuildRunner();
        registerBuildRunner(androidRunner);
        EspIdfBuildRunner espIdfRunner = new EspIdfBuildRunner();
        registerBuildRunner(espIdfRunner);
        FtcBuildRunner ftcRunner = new FtcBuildRunner();
        registerBuildRunner(ftcRunner);
        FrcBuildRunner frcRunner = new FrcBuildRunner();
        registerBuildRunner(frcRunner);

        RoboticsSdkVersionCache.getInstance().startBackgroundRefresh();

        activityBar = new ActivityBar();

        JPopupMenu filePopup = new JPopupMenu();
        JMenu mNew = new JMenu("New");
        String[][] fileTypes2 = {
            {"Java Class","java","public class %s {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World!\");\n    }\n}\n"},
            {"C++ Source","cpp","#include <iostream>\n\nint main() {\n    std::cout << \"Hello World!\" << std::endl;\n    return 0;\n}\n"},
            {"C Source","c","#include <stdio.h>\n\nint main() {\n    printf(\"Hello World!\\n\");\n    return 0;\n}\n"},
            {"Python Script","py","print('Hello World!')\n"},
            {"Arduino Sketch","ino","void setup() {\n    // Initialize here\n}\n\nvoid loop() {\n    // Main loop here\n}\n"},
        };
        for (String[] type : fileTypes2) {
            JMenuItem mi = new JMenuItem(type[0]);
            mi.addActionListener(ev -> createNewLanguageFile(type[1], type[2]));
            mNew.add(mi);
        }
        JMenuItem miNewEspProject = new JMenuItem("ESP-IDF Project...");
        miNewEspProject.addActionListener(ev -> {
            String name = JOptionPane.showInputDialog(frame, "Enter project name:");
            if (name != null && !name.trim().isEmpty()) {
                File dir = getSelectedDirectory();
                if (dir == null) dir = new File(System.getProperty("user.home"));
                try {
                    File projectDir = EspIdfProjectTemplate.createProject(dir, name.trim());
                    terminalArea.append("[ESP-IDF] Created project: " + projectDir.getAbsolutePath() + "\n");
                    addToOpenedProjects(projectDir.getAbsolutePath());
                    if (fileTree != null) refreshTreeDirectory(fileTree, dir);
                } catch (Exception ex) {
                    terminalArea.append("[ESP-IDF] Failed to create project: " + ex.getMessage() + "\n");
                }
            }
        });
        mNew.addSeparator();
        mNew.add(miNewEspProject);
        JMenuItem miNewFtcProject = new JMenuItem("FTC Project...");
        miNewFtcProject.addActionListener(ev -> {
            String name = JOptionPane.showInputDialog(frame, "Enter project name:");
            if (name != null && !name.trim().isEmpty()) {
                String teamNumber = JOptionPane.showInputDialog(frame, "Enter team number (e.g. 12345):", "12345");
                File dir = getSelectedDirectory();
                if (dir == null) dir = new File(System.getProperty("user.home"));
                try {
                    File projectDir = FtcProjectCreator.createProject(dir, name.trim(), teamNumber);
                    terminalArea.append("[FTC] Created project: " + projectDir.getAbsolutePath() + "\n");
                    addToOpenedProjects(projectDir.getAbsolutePath());
                    if (fileTree != null) refreshTreeDirectory(fileTree, dir);
                } catch (Exception ex) {
                    terminalArea.append("[FTC] Failed to create project: " + ex.getMessage() + "\n");
                }
            }
        });
        mNew.add(miNewFtcProject);
        JMenuItem miNewFrcProject = new JMenuItem("FRC Project...");
        miNewFrcProject.addActionListener(ev -> {
            String name = JOptionPane.showInputDialog(frame, "Enter project name:");
            if (name != null && !name.trim().isEmpty()) {
                String teamNumber = JOptionPane.showInputDialog(frame, "Enter team number (e.g. 1234):", "1234");
                File dir = getSelectedDirectory();
                if (dir == null) dir = new File(System.getProperty("user.home"));
                try {
                    File projectDir = FrcProjectCreator.createProject(dir, name.trim(), teamNumber);
                    terminalArea.append("[FRC] Created project: " + projectDir.getAbsolutePath() + "\n");
                    addToOpenedProjects(projectDir.getAbsolutePath());
                    if (fileTree != null) refreshTreeDirectory(fileTree, dir);
                } catch (Exception ex) {
                    terminalArea.append("[FRC] Failed to create project: " + ex.getMessage() + "\n");
                }
            }
        });
        mNew.add(miNewFrcProject);
        JMenuItem miNewVexProject = new JMenuItem("VEX PROS Project...");
        miNewVexProject.addActionListener(ev -> {
            String name = JOptionPane.showInputDialog(frame, "Enter project name:");
            if (name != null && !name.trim().isEmpty()) {
                File dir = getSelectedDirectory();
                if (dir == null) dir = new File(System.getProperty("user.home"));
                try {
                    File projectDir = VexProjectCreator.createProject(dir, name.trim());
                    terminalArea.append("[VEX] Created project: " + projectDir.getAbsolutePath() + "\n");
                    addToOpenedProjects(projectDir.getAbsolutePath());
                    if (fileTree != null) refreshTreeDirectory(fileTree, dir);
                } catch (Exception ex) {
                    terminalArea.append("[VEX] Failed to create project: " + ex.getMessage() + "\n");
                }
            }
        });
        mNew.add(miNewVexProject);
        JMenuItem miNewArduinoSketch = new JMenuItem("Arduino Sketch...");
        miNewArduinoSketch.addActionListener(ev -> {
            String name = JOptionPane.showInputDialog(frame, "Enter sketch name:");
            if (name != null && !name.trim().isEmpty()) {
                String fqbn = JOptionPane.showInputDialog(frame, "Board FQBN:", "arduino:avr:uno");
                File dir = getSelectedDirectory();
                if (dir == null) dir = new File(System.getProperty("user.home"));
                try {
                    File projectDir = ArduinoProjectCreator.createProject(dir, name.trim(), fqbn);
                    terminalArea.append("[Arduino] Created sketch: " + projectDir.getAbsolutePath() + "\n");
                    addToOpenedProjects(projectDir.getAbsolutePath());
                    if (fileTree != null) refreshTreeDirectory(fileTree, dir);
                } catch (Exception ex) {
                    terminalArea.append("[Arduino] Failed to create sketch: " + ex.getMessage() + "\n");
                }
            }
        });
        mNew.add(miNewArduinoSketch);
        JMenu mRobotics = new JMenu("Robotics Templates");
        String[][] roboticsTemplates = {
            {"FTC LinearOpMode", "java", FtcOpModeTemplates.getLinearOpModeTemplate()},
            {"FTC Iterative OpMode", "java", FtcOpModeTemplates.getIterativeOpModeTemplate()},
            {"FTC Sensor-Only OpMode", "java", FtcOpModeTemplates.getSensorOnlyTemplate()},
            {"FTC Autonomous", "java", FtcOpModeTemplates.getAutonomousTemplate()},
            {"FTC TeleOp", "java", FtcOpModeTemplates.getTeleOpTemplate()},
            {"FTC Python OpMode", "py", FtcOpModeTemplates.getPythonTemplate()},
        };
        for (String[] tpl : roboticsTemplates) {
            JMenuItem mi = new JMenuItem(tpl[0]);
            mi.addActionListener(ev -> createNewLanguageFile(tpl[1], tpl[2]));
            mRobotics.add(mi);
        }
        mRobotics.addSeparator();
        String[][] frcTemplates = {
            {"FRC TimedRobot", "java", FrcProjectTemplates.getTimedRobotTemplate()},
            {"FRC Commands-Based", "java", FrcProjectTemplates.getCommandsBasedTemplate()},
            {"FRC Constants", "java", FrcProjectTemplates.getConstantsTemplate()},
        };
        for (String[] tpl : frcTemplates) {
            JMenuItem mi = new JMenuItem(tpl[0]);
            mi.addActionListener(ev -> createNewLanguageFile(tpl[1], tpl[2]));
            mRobotics.add(mi);
        }
        mNew.add(mRobotics);
        JMenuItem miOpen  = new JMenuItem("Open");  miOpen .addActionListener(this);
        JMenuItem miSave  = new JMenuItem("Save");  miSave .addActionListener(this);
        JMenuItem miPrint = new JMenuItem("Print"); miPrint.addActionListener(this);
        JMenuItem miQuit  = new JMenuItem("Quit");  miQuit .addActionListener(this);
        filePopup.add(mNew); filePopup.add(miOpen); filePopup.add(miSave);
        filePopup.add(miPrint); filePopup.addSeparator(); filePopup.add(miQuit);

        activityBar.addLogo(e -> {
            filePopup.show(activityBar, ActivityBar.getBarWidth(), 0);
        });

// Edit popup
        JPopupMenu editPopup = new JPopupMenu();
        JMenuItem miCut   = new JMenuItem("Cut");   miCut  .addActionListener(this);
        JMenuItem miCopy  = new JMenuItem("Copy");  miCopy .addActionListener(this);
        JMenuItem miPaste = new JMenuItem("Paste"); miPaste.addActionListener(this);
        editPopup.add(miCut); editPopup.add(miCopy); editPopup.add(miPaste);

        ActivityBar.IconBtn btnEdit = activityBar.addTop("edit",
            ActivityBar.PAINT_EDIT, "Edit", e -> {
            editPopup.show(activityBar, ActivityBar.getBarWidth(), activityBar.getComponent(0).getHeight());
        });


// Git popup
        JPopupMenu gitPopup = new JPopupMenu();
        String[][] gitActions2 = {
            {"Init","init"},{"Status","status"},{"Pull","pull"},
            {"Push","push","--set-upstream","origin","HEAD"},
            {"Log","log","--oneline","-20"},{"Diff","diff"},
        };
        for (String[] action : gitActions2) {
            JMenuItem item = new JMenuItem(action[0]);
            String[] args = Arrays.copyOfRange(action, 1, action.length);
            item.addActionListener(ev -> {
                File dir = getSelectedDirectory();
                gitRunner.run(dir, () -> { if (gitPanel!=null) gitPanel.setRepoDir(dir); }, args);
            });
            gitPopup.add(item);
        }
// Branch submenu
        JMenu branchSub = new JMenu("Branch");
        JMenuItem newBranchItem = new JMenuItem("New branch...");
        newBranchItem.addActionListener(ev -> {
            String bname = JOptionPane.showInputDialog(frame, "Branch name:");
            if (bname != null && !bname.isBlank()) {
            File dir = getSelectedDirectory();
                if (dir == null) { JOptionPane.showMessageDialog(frame, "No directory selected."); return; }
                gitRunner.run(dir, () -> { if (gitPanel!=null) gitPanel.setRepoDir(dir); }, "checkout","-b",bname);
            }
        });
        branchSub.add(newBranchItem);
        gitPopup.add(branchSub);
// Remote submenu
        JMenu remoteSub = new JMenu("Remote");
        JMenuItem addOriginItem = new JMenuItem("Add Remote Origin...");
        addOriginItem.addActionListener(ev -> {
            File dir = getSelectedDirectory();
            if (dir == null) { JOptionPane.showMessageDialog(frame,"No directory selected."); return; }
            String url = JOptionPane.showInputDialog(frame, "Enter remote origin URL:");
            if (url != null && !url.isBlank())
                gitRunner.run(dir, () -> { if (gitPanel!=null) gitPanel.setRepoDir(dir); }, "remote","add","origin",url.trim());
        });
        JMenuItem setOriginItem = new JMenuItem("Set Remote Origin URL...");
        setOriginItem.addActionListener(ev -> {
            File dir = getSelectedDirectory();
            if (dir == null) { JOptionPane.showMessageDialog(frame,"No directory selected."); return; }
            String url = JOptionPane.showInputDialog(frame, "Enter new remote origin URL:");
            if (url != null && !url.isBlank())
                gitRunner.run(dir, () -> { if (gitPanel!=null) gitPanel.setRepoDir(dir); }, "remote","set-url","origin",url.trim());
        });
        JMenuItem pushOriginItem = new JMenuItem("Push to Origin");
        pushOriginItem.addActionListener(ev -> {
            File dir = getSelectedDirectory();
            if (dir == null) { JOptionPane.showMessageDialog(frame,"No directory selected."); return; }
            gitRunner.run(dir, () -> { if (gitPanel!=null) gitPanel.setRepoDir(dir); }, "push","-u","origin","HEAD");
        });
        remoteSub.add(addOriginItem); remoteSub.add(setOriginItem); remoteSub.add(pushOriginItem);
        gitPopup.add(remoteSub);
        
        JPopupMenu pluginMenu = new JPopupMenu();
        JMenuItem managePlugins = new JMenuItem("Manage plugins");
        JMenuItem pluginsDir = new JMenuItem("Open plugins folder");
        
        managePlugins.addActionListener(e -> {
            new PluginsDialog(frame, pluginManager, pluginManager.getPluginDir()).setVisible(true);
        });
        
        pluginsDir.addActionListener(e -> {
            try {
            File pluginFolder = pluginManager.getPluginDir();
                if (!pluginFolder.exists()) {
                    pluginFolder.mkdirs();
                }
                Desktop.getDesktop().open(pluginFolder);
            } catch (Exception ex) { 
                ex.printStackTrace(); 
            }
        });
        pluginMenu.add(managePlugins);
        pluginMenu.add(pluginsDir);
        
        JPopupMenu settingsPopup = new JPopupMenu();

        JMenuItem miSettingsPage = new JMenuItem("Settings...");
        miSettingsPage.addActionListener(ev -> openSettingsPage());
        settingsPopup.add(miSettingsPage);
        settingsPopup.addSeparator();

        JMenu themeMenu = new JMenu("Theme");
        ButtonGroup themeGroup = new ButtonGroup();
        String currentTheme = ThemeManager.load();
        for (Map.Entry<String, ThemeManager.ThemeInfo> entry : ThemeManager.THEMES.entrySet()) {
            String key = entry.getKey();
            ThemeManager.ThemeInfo info = entry.getValue();
            JCheckBoxMenuItem mi = new JCheckBoxMenuItem(info.displayName, key.equals(currentTheme));
            themeGroup.add(mi);
            mi.addActionListener(ev -> {
                try {
                    clearThemeOverrides();
                    ThemeManager.applyTheme(key);
                    idesettings.set("appearance.theme", key);
                    idesettings.save();
                    cachedEditorThemeKey = null;
                    applyThemeToAllTabs();
                    applyTerminalTheme();
                    interactiveTerminal.applyTheme(true);
                    activityBar.applyTheme();
                    statusBar.applyTheme();
                    bottomToolWindowBar.applyTheme();
                    bottomToolWindowBar.refreshTabs();
                    SwingUtilities.updateComponentTreeUI(frame);
                } catch (Exception ex) { ex.printStackTrace(); }
            });
            themeMenu.add(mi);
        }
        settingsPopup.add(themeMenu);
        settingsPopup.addSeparator();
        JMenuItem miToolchain = new JMenuItem("Toolchain Settings...");
        miToolchain.addActionListener(ev -> {
            ToolchainSettingsPanel panel = new ToolchainSettingsPanel(frame, toolchainSettings);
            panel.setVisible(true);
        });
        settingsPopup.add(miToolchain);
        JMenuItem miDevices = new JMenuItem("Device Manager...");
        miDevices.addActionListener(ev -> {
            JFrame deviceFrame = new JFrame("Device Manager");
            deviceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            deviceFrame.setContentPane(deviceManagerPanel);
            deviceFrame.setSize(400, 450);
            deviceFrame.setLocationRelativeTo(frame);
            deviceFrame.setVisible(true);
            deviceManagerPanel.refresh();
        });
        settingsPopup.add(miDevices);

activityBar.addBottom("settings", ActivityBar.PAINT_SETTINGS, "Settings (Theme)", e ->
    settingsPopup.show(activityBar, ActivityBar.getBarWidth(), 0));

        activityBar.addDividerTop();
        activityBar.addTop("plugins", ActivityBar.PAINT_PLUGINS, "Plugins", e ->
        pluginMenu.show(activityBar, ActivityBar.getBarWidth(), 0));

        activityBar.addTop("git", ActivityBar.PAINT_GIT, "Git", e ->
            gitPopup.show(activityBar, ActivityBar.getBarWidth(), 0));

        activityBar.addDividerTop();



// Arduino popup
        JPopupMenu arduinoPopup = new JPopupMenu();
        JMenuItem miBoardManager = new JMenuItem("Board Manager...");
        miBoardManager.addActionListener(ev -> {
            BoardManagerPanel panel = new BoardManagerPanel();
            JFrame boardFrame = new JFrame("Board Manager");
            boardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            boardFrame.setContentPane(panel);
            boardFrame.setSize(700, 500);
            boardFrame.setLocationRelativeTo(frame);
            boardFrame.setVisible(true);
        });
        arduinoPopup.add(miBoardManager);

        JMenuItem miLibManager = new JMenuItem("Library Manager...");
        miLibManager.addActionListener(ev -> {
            LibraryManagerPanel panel = new LibraryManagerPanel();
            JFrame libFrame = new JFrame("Library Manager");
            libFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            libFrame.setContentPane(panel);
            libFrame.setSize(700, 500);
            libFrame.setLocationRelativeTo(frame);
            libFrame.setVisible(true);
        });
        arduinoPopup.add(miLibManager);

        arduinoPopup.addSeparator();

        JMenuItem miCompileArduino = new JMenuItem("Compile Sketch");
        miCompileArduino.addActionListener(ev -> actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Compile")));
        arduinoPopup.add(miCompileArduino);

        JMenuItem miUploadArduino = new JMenuItem("Upload Sketch");
        miUploadArduino.addActionListener(ev -> {
            if (currentFile == null) {
                terminalArea.append("[Arduino] No file open. Open a sketch first.\n");
                bottomTabs.setSelectedIndex(0);
                return;
            }
            terminalArea.setText("");
            File compileProjectRoot = getProjectRoot();
            for (BuildRunner runner : buildRunners) {
                if (runner.canHandle(compileProjectRoot, currentFile)) {
                    runner.run(compileProjectRoot, currentFile, terminalArea, bottomTabs);
                    break;
                }
            }
        });
        arduinoPopup.add(miUploadArduino);

        arduinoPopup.addSeparator();

        JMenuItem miSerialMonitor = new JMenuItem("Open Serial Monitor");
        miSerialMonitor.addActionListener(ev -> {
            for (int i = 0; i < bottomTabs.getTabCount(); i++) {
                if ("Serial Monitor".equals(bottomTabs.getTitleAt(i))) {
                    bottomTabs.setSelectedIndex(i);
                    toggleTerminal();
                    return;
                }
            }
        });
        arduinoPopup.add(miSerialMonitor);

        activityBar.addTop("arduino", ActivityBar.PAINT_COMPILE, "Arduino", e ->
            arduinoPopup.show(activityBar, ActivityBar.getBarWidth(), 0));

// Android popup
        JPopupMenu androidPopup = new JPopupMenu();
        JMenuItem miAndroidBuild = new JMenuItem("Build (assembleDebug)");
        miAndroidBuild.addActionListener(ev -> actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Compile")));
        androidPopup.add(miAndroidBuild);

        JMenuItem miAndroidRun = new JMenuItem("Build & Deploy");
        miAndroidRun.addActionListener(ev -> {
            if (currentFile == null) {
                terminalArea.append("[Android] No project open.\n");
                bottomTabs.setSelectedIndex(0);
                return;
            }
            terminalArea.setText("");
            File androidRoot = getProjectRoot();
            for (BuildRunner runner : buildRunners) {
                if (runner.canHandle(androidRoot, currentFile)) {
                    runner.run(androidRoot, currentFile, terminalArea, bottomTabs);
                    break;
                }
            }
        });
        androidPopup.add(miAndroidRun);

        androidPopup.addSeparator();

        JMenuItem miLogcat = new JMenuItem("Open Logcat");
        miLogcat.addActionListener(ev -> {
            for (int i = 0; i < bottomTabs.getTabCount(); i++) {
                if ("Logcat".equals(bottomTabs.getTitleAt(i))) {
                    bottomTabs.setSelectedIndex(i);
                    if (!terminalVisible) toggleTerminal();
                    return;
                }
            }
        });
        androidPopup.add(miLogcat);

        JMenuItem miDeviceMgr = new JMenuItem("Device Manager...");
        miDeviceMgr.addActionListener(ev -> {
            JFrame deviceFrame = new JFrame("Device Manager");
            deviceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            deviceFrame.setContentPane(deviceManagerPanel);
            deviceFrame.setSize(400, 450);
            deviceFrame.setLocationRelativeTo(frame);
            deviceFrame.setVisible(true);
            deviceManagerPanel.refresh();
        });
        androidPopup.add(miDeviceMgr);

        androidPopup.addSeparator();

        JMenuItem miDeviceExplorer = new JMenuItem("Device File Explorer...");
        miDeviceExplorer.addActionListener(ev -> {
            DeviceFileExplorerPanel panel = new DeviceFileExplorerPanel();
            JFrame explorerFrame = new JFrame("Device File Explorer");
            explorerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            explorerFrame.setContentPane(panel);
            explorerFrame.setSize(700, 500);
            explorerFrame.setLocationRelativeTo(frame);
            explorerFrame.setVisible(true);
        });
        androidPopup.add(miDeviceExplorer);

        JMenuItem miGradleSync = new JMenuItem("Gradle Sync...");
        miGradleSync.addActionListener(ev -> {
            GradleSyncPanel panel = new GradleSyncPanel();
            panel.setProjectRoot(getProjectRoot());
            JFrame gradleFrame = new JFrame("Gradle Sync");
            gradleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            gradleFrame.setContentPane(panel);
            gradleFrame.setSize(700, 500);
            gradleFrame.setLocationRelativeTo(frame);
            gradleFrame.setVisible(true);
        });
        androidPopup.add(miGradleSync);

        JMenuItem miApkAnalyzer = new JMenuItem("APK Analyzer...");
        miApkAnalyzer.addActionListener(ev -> {
            ApkAnalyzerPanel panel = new ApkAnalyzerPanel();
            JFrame apkFrame = new JFrame("APK Analyzer");
            apkFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            apkFrame.setContentPane(panel);
            apkFrame.setSize(700, 500);
            apkFrame.setLocationRelativeTo(frame);
            apkFrame.setVisible(true);
        });
        androidPopup.add(miApkAnalyzer);

        JMenuItem miSigningConfig = new JMenuItem("Signing Config...");
        miSigningConfig.addActionListener(ev -> {
            SigningConfigPanel panel = new SigningConfigPanel();
            JFrame signFrame = new JFrame("Signing Config");
            signFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            signFrame.setContentPane(panel);
            signFrame.setSize(700, 400);
            signFrame.setLocationRelativeTo(frame);
            signFrame.setVisible(true);
        });
        androidPopup.add(miSigningConfig);

        JMenuItem miBuildVariants = new JMenuItem("Build Variants...");
        miBuildVariants.addActionListener(ev -> {
            BuildVariantsPanel panel = new BuildVariantsPanel();
            panel.setProjectRoot(getProjectRoot());
            JFrame variantFrame = new JFrame("Build Variants");
            variantFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            variantFrame.setContentPane(panel);
            variantFrame.setSize(600, 400);
            variantFrame.setLocationRelativeTo(frame);
            variantFrame.setVisible(true);
        });
        androidPopup.add(miBuildVariants);

        JMenuItem miSdkManager = new JMenuItem("SDK Manager...");
        miSdkManager.addActionListener(ev -> {
            SdkManagerPanel panel = new SdkManagerPanel();
            JFrame sdkFrame = new JFrame("SDK Manager");
            sdkFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            sdkFrame.setContentPane(panel);
            sdkFrame.setSize(800, 500);
            sdkFrame.setLocationRelativeTo(frame);
            sdkFrame.setVisible(true);
        });
        androidPopup.add(miSdkManager);

        activityBar.addTop("android", ActivityBar.PAINT_ANDROID, "Android", e ->
            androidPopup.show(activityBar, ActivityBar.getBarWidth(), 0));

// ESP-IDF popup
        JPopupMenu espIdfPopup = new JPopupMenu();
        JMenuItem miEspBuild = new JMenuItem("Build");
        miEspBuild.addActionListener(ev -> actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Compile")));
        espIdfPopup.add(miEspBuild);

        JMenuItem miEspFlash = new JMenuItem("Build & Flash");
        miEspFlash.addActionListener(ev -> {
            if (currentFile == null) {
                terminalArea.append("[ESP-IDF] No project open.\n");
                bottomTabs.setSelectedIndex(0);
                return;
            }
            terminalArea.setText("");
            File espRoot = getProjectRoot();
            for (BuildRunner runner : buildRunners) {
                if (runner.canHandle(espRoot, currentFile)) {
                    runner.run(espRoot, currentFile, terminalArea, bottomTabs);
                    break;
                }
            }
        });
        espIdfPopup.add(miEspFlash);

        JMenuItem miEspMonitor = new JMenuItem("Serial Monitor");
        miEspMonitor.addActionListener(ev -> {
            if (currentFile == null) return;
            terminalArea.setText("");
            File espRoot = getProjectRoot();
            espIdfRunner.monitor(espRoot, terminalArea);
            bottomTabs.setSelectedIndex(0);
        });
        espIdfPopup.add(miEspMonitor);

        espIdfPopup.addSeparator();

        JMenuItem miEspMenuconfig = new JMenuItem("Menuconfig");
        miEspMenuconfig.addActionListener(ev -> {
            if (currentFile == null) return;
            terminalArea.setText("");
            File espRoot = getProjectRoot();
            espIdfRunner.menuconfig(espRoot, terminalArea);
            bottomTabs.setSelectedIndex(0);
        });
        espIdfPopup.add(miEspMenuconfig);

        JMenuItem miEspClean = new JMenuItem("Full Clean");
        miEspClean.addActionListener(ev -> {
            if (currentFile == null) return;
            terminalArea.setText("");
            File espRoot = getProjectRoot();
            espIdfRunner.clean(espRoot, terminalArea);
            bottomTabs.setSelectedIndex(0);
        });
        espIdfPopup.add(miEspClean);

        espIdfPopup.addSeparator();

        JMenuItem miNewEspIdf = new JMenuItem("New ESP-IDF Project...");
        miNewEspIdf.addActionListener(ev -> {
            String name = JOptionPane.showInputDialog(frame, "Enter project name:");
            if (name != null && !name.trim().isEmpty()) {
                File dir = getSelectedDirectory();
                if (dir == null) dir = new File(System.getProperty("user.home"));
                try {
                    File projectDir = EspIdfProjectTemplate.createProject(dir, name.trim());
                    terminalArea.append("[ESP-IDF] Created project: " + projectDir.getAbsolutePath() + "\n");
                    addToOpenedProjects(projectDir.getAbsolutePath());
                    if (fileTree != null) refreshTreeDirectory(fileTree, dir);
                } catch (Exception ex) {
                    terminalArea.append("[ESP-IDF] Failed to create project: " + ex.getMessage() + "\n");
                }
            }
        });
        espIdfPopup.add(miNewEspIdf);

        activityBar.addTop("espidf", ActivityBar.PAINT_CONSOLE, "ESP-IDF", e ->
            espIdfPopup.show(activityBar, ActivityBar.getBarWidth(), 0));

// Robotics popup
        JPopupMenu roboticsPopup = new JPopupMenu();

        JMenu ftcSub = new JMenu("FTC");
        JMenuItem miFtcBuild = new JMenuItem("Build (assembleDebug)");
        miFtcBuild.addActionListener(ev -> actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Compile")));
        ftcSub.add(miFtcBuild);
        JMenuItem miFtcDeploy = new JMenuItem("Build & Deploy to Hub");
        miFtcDeploy.addActionListener(ev -> {
            if (currentFile == null) {
                terminalArea.append("[FTC] No file open.\n");
                return;
            }
            terminalArea.setText("");
            File ftcRoot = getProjectRoot();
            for (BuildRunner runner : buildRunners) {
                if (runner.canHandle(ftcRoot, currentFile)) {
                    runner.run(ftcRoot, currentFile, terminalArea, bottomTabs);
                    break;
                }
            }
        });
        ftcSub.add(miFtcDeploy);
        ftcSub.addSeparator();
        JMenuItem miFtcLogcat = new JMenuItem("FTC Logcat");
        miFtcLogcat.addActionListener(ev -> {
            for (int i = 0; i < bottomTabs.getTabCount(); i++) {
                if ("FTC Logcat".equals(bottomTabs.getTitleAt(i))) {
                    bottomTabs.setSelectedIndex(i);
                    if (!terminalVisible) toggleTerminal();
                    return;
                }
            }
        });
        ftcSub.add(miFtcLogcat);
        JMenuItem miFtcHwConfig = new JMenuItem("Hardware Config Editor...");
        miFtcHwConfig.addActionListener(ev -> {
            JFrame hwFrame = new JFrame("FTC Hardware Config Editor");
            hwFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            hwFrame.setContentPane(new FtcHardwareConfigEditor());
            hwFrame.setSize(700, 500);
            hwFrame.setLocationRelativeTo(frame);
            hwFrame.setVisible(true);
        });
        ftcSub.add(miFtcHwConfig);
        JMenuItem miFtcDashboard = new JMenuItem("FTC Dashboard...");
        miFtcDashboard.addActionListener(ev -> {
            JFrame dashFrame = new JFrame("FTC Dashboard");
            dashFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dashFrame.setContentPane(new FtcDashboardPanel());
            dashFrame.setSize(600, 450);
            dashFrame.setLocationRelativeTo(frame);
            dashFrame.setVisible(true);
        });
        ftcSub.add(miFtcDashboard);
        roboticsPopup.add(ftcSub);

        JMenu frcSub = new JMenu("FRC");
        JMenuItem miFrcBuild = new JMenuItem("Build (gradlew build)");
        miFrcBuild.addActionListener(ev -> actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Compile")));
        frcSub.add(miFrcBuild);
        JMenuItem miFrcDeploy = new JMenuItem("Deploy to RoboRIO");
        miFrcDeploy.addActionListener(ev -> {
            if (currentFile == null) {
                terminalArea.append("[FRC] No file open.\n");
                return;
            }
            terminalArea.setText("");
            File frcRoot = getProjectRoot();
            for (BuildRunner runner : buildRunners) {
                if (runner.canHandle(frcRoot, currentFile)) {
                    runner.run(frcRoot, currentFile, terminalArea, bottomTabs);
                    break;
                }
            }
        });
        frcSub.add(miFrcDeploy);
        frcSub.addSeparator();
        JMenuItem miFrcDash = new JMenuItem("SmartDashboard...");
        miFrcDash.addActionListener(ev -> {
            JFrame dashFrame = new JFrame("FRC SmartDashboard");
            dashFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dashFrame.setContentPane(new FrcSmartDashboardPanel());
            dashFrame.setSize(700, 500);
            dashFrame.setLocationRelativeTo(frame);
            dashFrame.setVisible(true);
        });
        frcSub.add(miFrcDash);
        roboticsPopup.add(frcSub);

        roboticsPopup.addSeparator();

        JMenuItem miSdkRef = new JMenuItem("SDK API Reference...");
        miSdkRef.addActionListener(ev -> {
            JFrame sdkFrame = new JFrame("Robot SDK API Reference");
            sdkFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            sdkFrame.setContentPane(new SdkReferencePanel());
            sdkFrame.setSize(800, 600);
            sdkFrame.setLocationRelativeTo(frame);
            sdkFrame.setVisible(true);
        });
        roboticsPopup.add(miSdkRef);

        JMenuItem miGamepad = new JMenuItem("Gamepad Visualizer...");
        miGamepad.addActionListener(ev -> {
            JFrame gpFrame = new JFrame("Gamepad Visualizer");
            gpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            gpFrame.setContentPane(new GamepadVisualizerPanel());
            gpFrame.setSize(500, 450);
            gpFrame.setLocationRelativeTo(frame);
            gpFrame.setVisible(true);
        });
        roboticsPopup.add(miGamepad);

        JMenuItem miMotorServo = new JMenuItem("Motor/Servo Helper...");
        miMotorServo.addActionListener(ev -> {
            JFrame msFrame = new JFrame("Motor/Servo Helper");
            msFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            msFrame.setContentPane(new MotorServoHelperPanel());
            msFrame.setSize(600, 450);
            msFrame.setLocationRelativeTo(frame);
            msFrame.setVisible(true);
        });
        roboticsPopup.add(miMotorServo);

        JMenuItem miSnippets = new JMenuItem("Code Snippets...");
        miSnippets.addActionListener(ev -> {
            JFrame snFrame = new JFrame("Robotics Code Snippets");
            snFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            snFrame.setContentPane(new CodeSnippetsPanel());
            snFrame.setSize(500, 600);
            snFrame.setLocationRelativeTo(frame);
            snFrame.setVisible(true);
        });
        roboticsPopup.add(miSnippets);

        roboticsPopup.addSeparator();

        JMenuItem miCompetition = new JMenuItem("Competition Mode...");
        miCompetition.addActionListener(ev -> {
            JFrame compFrame = new JFrame("Competition Mode");
            compFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            compFrame.setContentPane(new CompetitionModePanel());
            compFrame.setSize(500, 400);
            compFrame.setLocationRelativeTo(frame);
            compFrame.setVisible(true);
        });
        roboticsPopup.add(miCompetition);

        activityBar.addTop("robotics", ActivityBar.PAINT_HAMMER, "Robotics", e ->
            roboticsPopup.show(activityBar, ActivityBar.getBarWidth(), 0));


        activityBar.addTop("terminal", ActivityBar.PAINT_TERM, "Toggle Terminal",
            e -> toggleTerminal());
        activityBar.addDividerTop();


        final String[] sidebarLabels = {"☰ Left","☰ Center","☰ Right"};
        final int[] sidebarIdx = {0};
        activityBar.addTop("sidebar", ActivityBar.PAINT_SIDEBAR, "Cycle Sidebar Position", e -> {
            cycleSidebarPosition();
            sidebarIdx[0] = (sidebarIdx[0]+1) % 3;
        });

        activityBar.addBottom("author", ActivityBar.PAINT_AUTHOR, "Author's GitHub", e -> {
            try { java.awt.Desktop.getDesktop().browse(java.net.URI.create("https://github.com/clatitapitita")); }
            catch (java.io.IOException ex) { ex.printStackTrace(); }
        });
        
        RTextScrollPane scroll2 = new RTextScrollPane(text);
        scroll2.setRowHeaderView(new LineNumberPanel(text));
        tabs = new JTabbedPane();
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.addTab("Untitled", scroll2);

        tabs.addChangeListener(e -> {
            Component selected = tabs.getSelectedComponent();
            if (selected instanceof javax.swing.JComponent) {
                File file = (File) ((javax.swing.JComponent) selected).getClientProperty("file");
                if (file != null) {
                    currentFile = file;
                    activeDirectory = file.getParentFile();
                    if (gitPanel != null) {
                        gitPanel.setRepoDir(activeDirectory);
                    }
                }
            }
        });

        setupDragAndDrop(tabs);
        setupDragAndDrop(scroll2);
        setupDragAndDrop(text);

        JLabel loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);

        buildBottomPanel();
        lspDiagnosticsParser = new LspDiagnosticsParser(text, lspManager, problemsModel);

        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomToolWindowBar = new BottomToolWindowBar(bottomTabs, this::toggleTerminal);
        bottomSection.add(bottomToolWindowBar, BorderLayout.NORTH);
        bottomSection.add(bottomTabs, BorderLayout.CENTER);

        verticalSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                tabs,
                bottomSection);
        gitRunner = new GitRunner(terminalArea, bottomTabs);
        gitPanel = new GitPanel(gitRunner);
        verticalSplit.setResizeWeight(0.75);
        verticalSplit.setDividerLocation(1.0);
        verticalSplit.setDividerSize(5);
        verticalSplit.setBorder(null);
        verticalSplit.setOneTouchExpandable(false);
        bottomSection.setVisible(false);

        DefaultMutableTreeNode initRoot = new DefaultMutableTreeNode("Projects");
        DefaultTreeModel initModel = new DefaultTreeModel(initRoot);
        projectToolWindow = new ProjectToolWindow(initRoot, initModel);
        projectToolWindow.setPreferredSize(new Dimension(280, 0));

        outerHSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                projectToolWindow,
                verticalSplit);
        outerHSplit.setDividerLocation(280);
        outerHSplit.setDividerSize(5);
        outerHSplit.setBorder(null);
        outerHSplit.setOneTouchExpandable(false);

        statusBar = new StatusBar();
        statusBar.setPreferredSize(new Dimension(0, 24));

        JMenuBar menuBar = JetBrainsMenuBuilder.build(this);
        frame.setJMenuBar(menuBar);

        JPanel toolbar = buildToolbar();
        topSection = new JPanel(new BorderLayout());
        topSection.add(toolbar, BorderLayout.CENTER);

        frame.getContentPane().add(topSection, BorderLayout.NORTH);
        frame.getContentPane().add(outerHSplit, BorderLayout.CENTER);
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);

        frame.setSize(1280, 720);
        frame.setVisible(true);
        hookTabFocusTracking(tabs);

        // Global keyboard shortcuts
        frame.getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F,
                    java.awt.event.InputEvent.CTRL_DOWN_MASK), "openFindReplace");
        frame.getRootPane().getActionMap().put("openFindReplace", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (findReplaceDialog == null) {
                    findReplaceDialog = new FindReplaceDialog(frame, text);
                }
                findReplaceDialog.setEditor(text);
                findReplaceDialog.setSearchRoot(getSelectedDirectory());
                findReplaceDialog.activate();
            }
        });

        // Start workspace auto-save (saves all open files)
        workspaceManager.initAutoSave();
        workspaceManager.onAutoSave(() -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                Component comp = tabs.getComponentAt(i);
                if (comp instanceof RTextScrollPane sp) {
                    File f = (File) sp.getClientProperty("file");
                    if (f != null && f.exists() && sp.getTextArea() instanceof RSyntaxTextArea ed) {
                        try {
                            java.nio.file.Files.write(f.toPath(),
                                    ed.getText().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                        } catch (Exception ignored) {}
                    }
                }
            }
        });
        workspaceManager.onSave(() -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                Component comp = tabs.getComponentAt(i);
                if (comp instanceof RTextScrollPane sp) {
                    File f = (File) sp.getClientProperty("file");
                    if (f != null && f.exists() && sp.getTextArea() instanceof RSyntaxTextArea ed) {
                        try {
                            java.nio.file.Files.write(f.toPath(),
                                    ed.getText().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                        } catch (Exception ignored) {}
                    }
                }
            }
        });

        cursorTrail = new CursorTrailEffect(frame);
        frame.setGlassPane(cursorTrail);
        cursorTrail.setOpaque(false);

        tabTransition = new SmoothTabTransition(tabs);
        tabTransition.setDuration(180);
        tabTransition.setEase(AnimationEngine.Ease.EASE_OUT_CUBIC);

        sidebarAnimator = new SmoothPanelAnimator(outerHSplit);

        focusDimmer = new FocusDimmer(frame);
        focusDimmer.setDimmedComponent(outerHSplit);
        focusDimmer.setDimOpacity(0.2f);
        focusDimmer.setDuration(300);

        // Restore workspace (tabs + caret positions) from last session
        restoreWorkspace();

    }

    private void restoreWorkspace() {
        java.util.List<WorkspaceManager.SavedTab> savedTabs = workspaceManager.loadWorkspace();
        if (savedTabs.isEmpty()) return;
        for (WorkspaceManager.SavedTab st : savedTabs) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(st.file.toPath()),
                        java.nio.charset.StandardCharsets.UTF_8);
                openFileInNewTab(st.file, content);
                RSyntaxTextArea ed = getEditorText();
                if (ed != null) {
                    if (st.caretPosition > 0 && st.caretPosition < ed.getDocument().getLength()) {
                        ed.setCaretPosition(st.caretPosition);
                    }
                    Component comp = tabs.getSelectedComponent();
                    if (comp instanceof RTextScrollPane sp) {
                        javax.swing.SwingUtilities.invokeLater(() ->
                                sp.getVerticalScrollBar().setValue(st.scrollPosition));
                    }
                }
            } catch (Exception ignored) {
            }
        }
        int activeIdx = workspaceManager.getActiveTabIndex();
        if (activeIdx >= 0 && activeIdx < tabs.getTabCount()) {
            tabs.setSelectedIndex(activeIdx);
        }
    }

    private static Nukepad instance;

    @Override
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        switch (s) {
            case "Cut":
                text.cut();
                break;
            case "Copy":
                text.copy();
                break;
            case "Paste":
                text.paste();
                break;
            case "Save":
                JFileChooser jSave = new JFileChooser(currentFile != null ? currentFile.getParent() : System.getProperty("user.home"));
                int rSave = jSave.showSaveDialog(null);

                if (rSave == JFileChooser.APPROVE_OPTION) {
                    File file = new File(jSave.getSelectedFile().getAbsolutePath());
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                        writer.write(text.getText());
                        pluginManager.notifyFileSave(file);
                        String sid = lspManager.findServerIdForFile(file);
                        if (sid != null) {
                            lspManager.ensureServerRunning(sid, file.getParentFile());
                            lspManager.saveDocument(sid, file);
                        }
                    } catch (Exception evt) {
                        JOptionPane.showMessageDialog(frame, evt.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "The user has cancelled the operation!");
                }
                break;
            case "Print":
                try {
                    text.print();
                } catch (Exception evt) {
                    JOptionPane.showMessageDialog(frame, evt.getMessage());
                }
                break;
            case "Open":
                JFileChooser jOpen = new JFileChooser(System.getProperty("user.home"));
                jOpen.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int rOpen = jOpen.showOpenDialog(null);
                if (rOpen == JFileChooser.APPROVE_OPTION) {
                    File file = jOpen.getSelectedFile();
                    if (file.isDirectory()) {
                        addToOpenedProjects(file.getAbsolutePath());
                    } else {
                        try {
                            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                            openFileInNewTab(file, content);
                        } catch (Exception evt) {
                            JOptionPane.showMessageDialog(frame, evt.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(frame, "The user has cancelled the operation!");
                }
                break;
            case "Quit":
                System.exit(0);
                break;
            case "SaveAs":
                JFileChooser jSaveAs = new JFileChooser(currentFile != null ? currentFile.getParent() : System.getProperty("user.home"));
                int rSaveAs = jSaveAs.showSaveDialog(frame);
                if (rSaveAs == JFileChooser.APPROVE_OPTION) {
                    File saveFile = new File(jSaveAs.getSelectedFile().getAbsolutePath());
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile, StandardCharsets.UTF_8))) {
                        writer.write(text.getText());
                        pluginManager.notifyFileSave(saveFile);
                        String sid = lspManager.findServerIdForFile(saveFile);
                        if (sid != null) {
                            lspManager.ensureServerRunning(sid, saveFile.getParentFile());
                            lspManager.saveDocument(sid, saveFile);
                        }
                    } catch (Exception evt) {
                        JOptionPane.showMessageDialog(frame, evt.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            case "Settings":
                openSettingsPage();
                break;
            case "SelectAll":
                text.selectAll();
                break;
            case "Undo":
                terminalArea.append("[Edit] Undo (Ctrl+Z via RSyntaxTextArea)\n");
                break;
            case "Redo":
                terminalArea.append("[Edit] Redo (Ctrl+Y via RSyntaxTextArea)\n");
                break;
            case "ToggleTerminal":
                toggleTerminal();
                break;
            case "ToggleSidebar":
                if (outerHSplit.getDividerLocation() > 10) {
                    lastSidebarWidth = outerHSplit.getDividerLocation();
                    outerHSplit.setDividerLocation(0);
                } else {
                    outerHSplit.setDividerLocation(lastSidebarWidth > 0 ? lastSidebarWidth : 280);
                }
                outerHSplit.revalidate();
                break;
            case "ShowProject":
                outerHSplit.setDividerLocation(lastSidebarWidth > 0 ? lastSidebarWidth : 280);
                outerHSplit.revalidate();
                break;
            case "ShowProblems":
                showBottomTab(1);
                break;
            case "ShowLogcat":
                showBottomTab(4);
                break;
            case "ShowFtcLogcat":
                showBottomTab(5);
                break;
            case "ShowSdkReference":
                showBottomTab(8);
                break;
            case "FullScreen":
                if ((frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                    frame.setExtendedState(JFrame.NORMAL);
                } else {
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
                break;
            case "Debug":
            case "RunNoDebug":
            case "RunCurrentFile":
            case "DebugCurrentFile":
                actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Run"));
                break;
            case "ToolchainSettings":
                ToolchainSettingsPanel tcPanel = new ToolchainSettingsPanel(frame, toolchainSettings);
                tcPanel.setVisible(true);
                break;
            case "ArduinoBoardManager":
                showBottomTab(3);
                terminalArea.append("[Arduino] Opening Board Manager...\n");
                break;
            case "ArduinoLibraryManager":
                showBottomTab(3);
                terminalArea.append("[Arduino] Opening Library Manager...\n");
                break;
            case "ArduinoSerialMonitor":
                showBottomTab(3);
                break;
            case "GamepadVisualizer":
                showBottomTab(6);
                break;
            case "MotorServoHelper":
                showBottomTab(7);
                break;
            case "CodeSnippets":
                showBottomTab(8);
                break;
            case "DeviceManager":
                terminalArea.append("[Device Manager] Scanning for connected devices...\n");
                showBottomTab(0);
                break;
            case "FtcHardwareConfig":
                terminalArea.append("[FTC] Opening Hardware Config Editor...\n");
                showBottomTab(0);
                break;
            case "FtcDashboard":
                terminalArea.append("[FTC] Starting FTC Dashboard...\n");
                showBottomTab(0);
                break;
            case "FrcSmartDashboard":
                terminalArea.append("[FRC] Starting SmartDashboard...\n");
                showBottomTab(0);
                break;
            case "GitInit":
            case "GitStatus":
            case "GitPull":
            case "GitPush":
            case "GitLog":
            case "GitDiff":
                String gitCmd = s.replace("Git", "").toLowerCase();
                gitRunner.run(getSelectedDirectory(), () -> { if (gitPanel != null) gitPanel.setRepoDir(getSelectedDirectory()); }, gitCmd);
                break;
            case "GitNewBranch":
                String bname = JOptionPane.showInputDialog(frame, "Branch name:");
                if (bname != null && !bname.isBlank()) {
                    gitRunner.run(getSelectedDirectory(), () -> { if (gitPanel != null) gitPanel.setRepoDir(getSelectedDirectory()); }, "checkout", "-b", bname);
                }
                break;
            case "GitAddRemoteOrigin":
                String url = JOptionPane.showInputDialog(frame, "Enter remote origin URL:");
                if (url != null && !url.isBlank()) {
                    gitRunner.run(getSelectedDirectory(), () -> { if (gitPanel != null) gitPanel.setRepoDir(getSelectedDirectory()); }, "remote", "add", "origin", url.trim());
                }
                break;
            case "GitPushToOrigin":
                gitRunner.run(getSelectedDirectory(), () -> { if (gitPanel != null) gitPanel.setRepoDir(getSelectedDirectory()); }, "push", "-u", "origin", "HEAD");
                break;
            case "About":
                JOptionPane.showMessageDialog(frame,
                    "CodeForge IDE\nVersion 1.0\n\nA modern IDE for Arduino, ESP-IDF, Android,\nFTC, FRC, and general development.",
                    "About CodeForge IDE",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
            case "GitHubRepository":
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/croco/CodeForge-IDE"));
                } catch (Exception ex) {
                    terminalArea.append("[Error] Could not open browser: " + ex.getMessage() + "\n");
                }
                break;
            case "Compile":
                try {
                    problemsManager.clear();
                    terminalArea.setText("");

                    if (currentFile == null) {
                        terminalArea.append("ERROR: Save your file first before compiling. \n");
                        bottomTabs.setSelectedIndex(0);
                        return;
                    }

                    File compileProjectRoot = getProjectRoot();
                    boolean handledByPlugin = false;
                    for (BuildRunner runner : buildRunners) {
                        if (runner.canHandle(compileProjectRoot, currentFile)) {
                            runner.compile(compileProjectRoot, currentFile, terminalArea, bottomTabs, problemsManager);
                            handledByPlugin = true;
                            break;
                        }
                    }
                    if (handledByPlugin) break;

                    String ext = currentFile.getName().contains(".")
                            ? currentFile.getName().substring(currentFile.getName().lastIndexOf('.') + 1).toLowerCase()
                            : "";
                    ProcessBuilder pbC;
                    switch (ext) {
                        case "java":
                            pbC = new ProcessBuilder("javac", currentFile.getPath());
                            break;
                        case "cpp":
                            pbC = new ProcessBuilder("g++", currentFile.getPath(), "-o",
                                    currentFile.getPath().replace(".cpp", ""));
                            break;
                        case "c":
                            pbC = new ProcessBuilder("gcc", currentFile.getPath(), "-o",
                                    currentFile.getPath().replace(".c", ""));
                            break;
                        default:
                            terminalArea.append("ERROR: Compilation not supported for '.'" + ext + "'files.\n");
                            bottomTabs.setSelectedIndex(0);
                            return;
                    }

                    pbC.redirectErrorStream(true);
                    final ProcessBuilder finalPbC = pbC;
                    terminalArea.append("Compiling...\n");

                    new javax.swing.SwingWorker<Integer, Void>() {
                        @Override
                        protected Integer doInBackground() throws Exception {
                            Process procC = finalPbC.start();
                            runningProcess = procC;
                            BufferedReader readerC = new BufferedReader(new InputStreamReader(procC.getInputStream()));
                            Pattern errorPat = Pattern.compile(".+:(\\d+): (error|warning): (.+)");
                            String lineC;
                            while ((lineC = readerC.readLine()) != null) {
                                final String ln = lineC;
                                final Matcher m = errorPat.matcher(lineC);
                                SwingUtilities.invokeLater(() -> {
                                    terminalArea.append(ln + "\n");
                                    if (m.find()) {
                                        String lineNum = m.group(1);
                                        String type = m.group(2);
                                        String msg = m.group(3);
                                        String icon = type.equals("error") ? "❌" : "⚠️";
                                        problemsModel.addRow(new Object[] { icon, msg, lineNum, currentFile.getName() });
                                    }
                                });
                            }
                            return procC.waitFor();
                        }

                        @Override
                        protected void done() {
                            try {
                                int exitCode = get();
                                if (exitCode == 0) {
                                    terminalArea.append("\n ✅ Build successful. \n");
                                    bottomTabs.setSelectedIndex(0);
                                } else {
                                    terminalArea.append("\n ❌ Build failed.\n");
                                    bottomTabs.setSelectedIndex(1);
                                }
                            } catch (Exception ex) {
                                terminalArea.append("Compile error: " + ex.getMessage() + "\n");
                                bottomTabs.setSelectedIndex(0);
                            } finally {
                                runningProcess = null;
                            }
                        }
                    }.execute();

                } catch (Exception evt) {
                    terminalArea.append("Exception: " + evt.getMessage() + "\n");
                    bottomTabs.setSelectedIndex(0);
                }
                break;

            case "Run":
                terminalArea.setText("");
                bottomTabs.setSelectedIndex(0);

                if (currentFile == null) {
                    terminalArea.append("ERROR: No file is currently open.\n");
                    break;
                }

                File runProjectRoot = getProjectRoot();
                boolean runHandledByPlugin = false;
                for (BuildRunner runner : buildRunners) {
                    if (runner.canHandle(runProjectRoot, currentFile)) {
                        runner.run(runProjectRoot, currentFile, terminalArea, bottomTabs);
                        runHandledByPlugin = true;
                        break;
                    }
                }
                if (runHandledByPlugin) break;

                String ext2 = currentFile.getName().contains(".")
                        ? currentFile.getName().substring(currentFile.getName().lastIndexOf('.') + 1).toLowerCase()
                        : "";
                String classDir = currentFile.getParent();
                String baseName = currentFile.getName().replace("." + ext2, "");

                ProcessBuilder pbR = null;
                switch (ext2) {
                    case "java":
                        pbR = new ProcessBuilder("java", "-cp", classDir, baseName);
                        break;
                    case "py":
                        pbR = new ProcessBuilder("python3", currentFile.getPath());
                        break;
                    case "cpp":
                    case "c":
                        pbR = new ProcessBuilder(currentFile.getPath().replace("." + ext2, ""));
                        break;
                    
                    default:
                        terminalArea.append("ERROR: Run not supported for '." + ext2 + "' files.\n");
                        return;
                }
                final ProcessBuilder finalPb = pbR;
                finalPb.redirectErrorStream(true);
                finalPb.directory(new File(classDir));

                new javax.swing.SwingWorker<Integer, String>() {
                    @Override
                    protected Integer doInBackground() throws Exception {
                        Process proc = finalPb.start();
                        runningProcess = proc;
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(proc.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            publish(line);
                        }
                        return proc.waitFor();
                    }

                    @Override
                    protected void process(java.util.List<String> chunks) {

                        for (String line : chunks) {
                            terminalArea.append(line + "\n");
                            terminalArea.setCaretPosition(terminalArea.getDocument().getLength());
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            int exitCode = get();
                            terminalArea.append("\n--- Process exited with code " + exitCode + " ---\n");
                        } catch (Exception ex) {
                            terminalArea.append("Exception: " + ex.getMessage() + "\n");
                        } finally {
                            runningProcess = null;
                        }
                    }
                }.execute();
                break;

            default:
                System.out.println("Unknown command:" + s);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new Nukepad(null));
    }

    public void openWebPage(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public RSyntaxTextArea getEditorText() {
        return text;
    }

    public void setEditorText(String textis) {
        text.setText(textis);
    }

    public File getCurrentFile() {
        return currentFile;
    }

    void setCurrentFile(File file) {
        this.currentFile = file;
    }

    public void openFileInNewTab(File file, String content) {
        RSyntaxTextArea editor = new RSyntaxTextArea();
        editor.setCodeFoldingEnabled(true);
        editor.setAntiAliasingEnabled(true);
        applyEditorTheme(editor);
        applySettingsToEditor(editor);
        installLiveErrorParser(editor);
        EditorUndoManager undoMgr = new EditorUndoManager(editor);
        editor.putClientProperty("undoManager", undoMgr);
        
        
        String name = file.getName().toLowerCase();
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : "";
        switch (name.substring(name.lastIndexOf('.') + 1)) {
            case "java":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                break;
            case "xml":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
                break;
            case "html":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
                break;
            case "js":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
                break;
            case "py":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
                break;
            case "cpp":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
                break;
            case "cs":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSHARP);
                break;
            case "c":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
                break;
            case "ino":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
                break;
            case "tsx":
            case "ts":
            case "jsx":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT);
                break;
            case "json":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
                break;
            case "sql":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
                break;
            case "go":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GO);
                break;
            case "f90":
            case "f":
            case "for":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_FORTRAN);
                break;
            case "php":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);
                break;
            case "rs":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUST);
                break;
            case "kt":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_KOTLIN);
                break;
            case "groovy":
            case "gradle":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
                break;
            case "yaml":
            case "yml":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_YAML);
                break;
            default:
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
                break;
        }
        
        ImportValidator importValidator = new ImportValidator(editor);
        importValidator.setExt(ext);
        editor.addParser(importValidator);
        editor.setText(content);

        String lspServerId = lspManager.findServerIdForFile(file);
        if (lspServerId != null) {
            LspDiagnosticsParser diagParser = new LspDiagnosticsParser(editor, lspManager, problemsModel);
            diagParser.setCurrentFile(file);
            editor.addParser(diagParser);
            lspManager.addDiagnosticListener(lspServerId, diagParser::onDiagnostics);

            LspCompletionProvider lspProv = new LspCompletionProvider(editor, lspManager);
            lspProv.bind(lspServerId, file);
            AutoCompletion lac = new AutoCompletion(lspProv);
            lac.setAutoActivationEnabled(true);
            lac.setAutoActivationDelay(300);
            lac.install(editor);

            LspHoverListener hov = new LspHoverListener(editor, lspManager);
            hov.bind(lspServerId, file);
            editor.addMouseListener(hov);

            LspGoToDefinitionHandler goTo = new LspGoToDefinitionHandler(editor, lspManager, tabs);
            goTo.bind(lspServerId, file);

            LspDocumentAdapter docAdapter = new LspDocumentAdapter(editor, lspManager);
            docAdapter.bind(lspServerId, file);
            editor.putClientProperty("lspDocAdapter", docAdapter);
            editor.putClientProperty("lspHoverListener", hov);
            editor.putClientProperty("lspServerId", lspServerId);
        } else {
            CombinedProvider tabProvider = new CombinedProvider(editor);
            tabProvider.setProjectWords(sharedProvider != null
                    ? ((CombinedProvider) sharedProvider).getProjectWords()
                    : Collections.emptySet());
            AutoCompletion ac = new AutoCompletion(tabProvider);
            ac.setAutoActivationEnabled(true);
            ac.setAutoActivationDelay(300);
            ac.install(editor);
        }

        RTextScrollPane scroll = new RTextScrollPane(editor);
        scroll.setRowHeaderView(new LineNumberPanel(editor));
        scroll.putClientProperty("file", file);

        tabs.addTab(file.getName(), scroll);
        tabs.setSelectedComponent(scroll);

        this.text = editor;
        this.currentFile = file;

        if (gitPanel != null) {
            gitPanel.setRepoDir(file.getParentFile());
        }

        makeTabClosable(tabs, scroll, file.getName(), file.getAbsolutePath());
        updateStatusBar();

        addToOpenedProjects(file.getParentFile().getAbsolutePath());

        setupDragAndDrop(scroll);
        setupDragAndDrop(editor);
        
        pluginManager.notifyFileOpen(file);
    }

    private void makeTabClosable(JTabbedPane tabs, Component tab, String title, String fullPath) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        JLabel label = new JLabel(title);
        label.setFont(UIManager.getFont("TabbedPane.font") != null ? UIManager.getFont("TabbedPane.font") : new Font("SansSerif", Font.PLAIN, 12));

        JButton close = new JButton("\u2715");
        close.setFont(new Font("SansSerif", Font.PLAIN, 10));
        close.setBorder(null);
        close.setFocusable(false);
        close.setContentAreaFilled(false);
        close.setOpaque(false);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.setMargin(new Insets(0, 2, 0, 2));
        close.setForeground(UIManager.getColor("Label.disabledForeground"));
        close.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                close.setOpaque(true);
                close.setBackground(new Color(255, 255, 255, 30));
                close.setForeground(UIManager.getColor("Label.foreground"));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                close.setOpaque(false);
                close.setForeground(UIManager.getColor("Label.disabledForeground"));
            }
        });

        close.addActionListener(e -> {
            int index = tabs.indexOfComponent(tab);
            if (index != -1) {
                cleanupTabResources(tab);
                tabs.remove(index);
            }
        });

        panel.add(label, BorderLayout.WEST);
        panel.add(close, BorderLayout.EAST);

        tabs.setTabComponentAt(tabs.indexOfComponent(tab), panel);
    }

    private void cleanupTabResources(Component tab) {
        if (!(tab instanceof RTextScrollPane sp)) return;
        RSyntaxTextArea editor = (RSyntaxTextArea) sp.getTextArea();
        if (editor == null) return;

        // Clean up LSP document adapter (shuts down its scheduler executor)
        Object docAdapterObj = editor.getClientProperty("lspDocAdapter");
        if (docAdapterObj instanceof LspDocumentAdapter docAdapter) {
            docAdapter.dispose();
        }

        // Clean up LSP hover listener
        Object hoverObj = editor.getClientProperty("lspHoverListener");
        if (hoverObj instanceof LspHoverListener hoverListener) {
            hoverListener.unbind();
            editor.removeMouseListener(hoverListener);
        }

        // Clean up undo manager
        Object undoObj = editor.getClientProperty("undoManager");
        if (undoObj instanceof EditorUndoManager undoManager) {
            undoManager.discardAllEdits();
        }

        // Remove all mouse listeners and action/input map bindings
        for (var listener : editor.getMouseListeners()) {
            editor.removeMouseListener(listener);
        }
        for (var listener : editor.getMouseMotionListeners()) {
            editor.removeMouseMotionListener(listener);
        }

        // Clear document listeners by setting a fresh empty document
        editor.setDocument(new javax.swing.text.PlainDocument());

        // Null out references
        editor.setText(null);
    }

    public File getSelectedDirectory() {
        if (activeDirectory != null) {
            return activeDirectory;
        }
        if (currentFile != null) {
            return currentFile.getParentFile();
        }
        return new File(System.getProperty("user.home"));
    }

    private void createNewLanguageFile(String ext, String boilerplate) {
        String filename = JOptionPane.showInputDialog(frame, "Enter filename (without extension):");
        if (filename != null && !filename.trim().isEmpty()) {
            filename = filename.trim();

            File dir = getSelectedDirectory();

            File newFile = new File(dir, filename + "." + ext);
            if (newFile.exists()) {
                JOptionPane.showMessageDialog(frame, "File already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String contentToReplace = boilerplate;
                if (ext.equals("java")) {
                    contentToReplace = String.format(boilerplate, filename);
                }
                Files.write(newFile.toPath(), contentToReplace.getBytes());
                openFileInNewTab(newFile, contentToReplace);

                // Refresh the trees
                if (fileTree != null && dir != null) {
                    refreshTreeDirectory(fileTree, dir);
                }
                if (openedProjectsTree != null && dir != null) {
                    refreshTreeDirectory(openedProjectsTree, dir);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error creating file: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshTreeDirectory(JTree tree, File dir) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        java.util.Enumeration<?> e = root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            Object userObj = node.getUserObject();
            if (userObj instanceof File && ((File) userObj).getAbsolutePath().equals(dir.getAbsolutePath())) {
                node.removeAllChildren();
                File[] children = dir.listFiles();
                if (children != null) {
                    java.util.Arrays.sort(children, (a, b) -> {
                        if (a.isDirectory() && !b.isDirectory())
                            return -1;
                        if (!a.isDirectory() && b.isDirectory())
                            return 1;
                        return a.getName().compareToIgnoreCase(b.getName());
                    });
                    for (File child : children) {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                        if (child.isDirectory()) {
                            childNode.add(new DefaultMutableTreeNode("Loading..."));
                        }
                        node.add(childNode);
                    }
                }
                model.reload(node);
                tree.expandPath(new javax.swing.tree.TreePath(node.getPath()));
                break;
            }
        }
    }

    private JPanel buildCategoriesPanel() {
        loadCategoriesConfig();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        JButton addCat = new JButton("Add Category (+)");
        JButton removeCat = new JButton("Remove Category (-)");
        toolbar.add(addCat);
        toolbar.add(removeCat);
        panel.add(toolbar);

        // Populate sections from persisted data
        for (Map.Entry<String, List<File>> entry : categoriesData.entrySet()) {
            panel.add(buildCategorySection(entry.getKey(), entry.getValue(), panel, toolbar));
        }

        addCat.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(panel, "Category Name:");
            if (name == null || name.isBlank())
                return;
            List<File> files = new ArrayList<>();
            categoriesData.put(name, files);
            panel.add(buildCategorySection(name, files, panel, toolbar));
            panel.revalidate();
            saveCategoriesConfig();
        });

        removeCat.addActionListener(e -> {
            String[] names = categoriesData.keySet().toArray(new String[0]);
            if (names.length == 0)
                return;
            String choice = (String) JOptionPane.showInputDialog(
                    panel, "Which category to remove?", "Remove",
                    JOptionPane.PLAIN_MESSAGE, null, names, names[0]);
            if (choice == null)
                return;
            categoriesData.remove(choice);
            saveCategoriesConfig();
            rebuildCategoriesPanel(panel, toolbar);
        });

        return panel;
    }

    private void rebuildCategoriesPanel(JPanel panel, JPanel toolbar) {
        panel.removeAll();
        panel.add(toolbar);
        for (Map.Entry<String, List<File>> entry : categoriesData.entrySet()) {
            panel.add(buildCategorySection(entry.getKey(), entry.getValue(), panel, toolbar));
        }
        panel.revalidate();
        panel.repaint();
    }

    private void saveCategoriesConfig() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CATEGORIES_CONFIG_FILE, StandardCharsets.UTF_8))) {
            for (Map.Entry<String, List<File>> entry : categoriesData.entrySet()) {
                writer.write("[" + entry.getKey() + "]");
                writer.newLine();
                for (File f : entry.getValue()) {
                    writer.write(f.getAbsolutePath());
                    writer.newLine();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadCategoriesConfig() {
        categoriesData.clear();
        if (!CATEGORIES_CONFIG_FILE.exists())
            return;
        try {
            List<String> lines = Files.readAllLines(CATEGORIES_CONFIG_FILE.toPath());
            String currentCat = null;
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                if (line.startsWith("[") && line.endsWith("]")) {
                    currentCat = line.substring(1, line.length() - 1);
                    categoriesData.put(currentCat, new ArrayList<>());
                } else if (currentCat != null) {
                    File f = new File(line);
                    if (f.exists()) {
                        categoriesData.get(currentCat).add(f);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void applyThemeToAllTabs() {
        applyThemeToPane(tabs);
        applyThemeToPane(rightTabs);
    }

    private void applyThemeToPane(JTabbedPane pane) {
        if (pane == null)
            return;
        for (int i = 0; i < pane.getTabCount(); i++) {
            Component c = pane.getComponentAt(i);
            if (c instanceof RTextScrollPane) {
                RSyntaxTextArea editor = (RSyntaxTextArea) ((RTextScrollPane) c).getTextArea();
                applyEditorTheme(editor);
            }
        }
    }

    private void clearThemeOverrides() {
        String[] keys = {
                "Panel.background", "Panel.foreground", "Label.foreground",
                "Button.background", "Button.foreground", "MenuBar.background",
                "MenuBar.foreground", "Menu.background", "Menu.foreground",
                "MenuItem.background", "MenuItem.foreground", "TabbedPane.background",
                "TabbedPane.foreground", "ScrollPane.background", "ScrollBar.background",
                "Tree.background", "Tree.foreground", "List.background", "List.foreground",
                "SplitPane.background", "TextField.background", "TextField.foreground",
                "TextArea.background", "TextArea.foreground"
        };
        for (String key : keys)
            UIManager.put(key, null);
    }

    private org.fife.ui.rsyntaxtextarea.Theme cachedEditorTheme;
    private String cachedEditorThemeKey;

    private void applyEditorTheme(RSyntaxTextArea editor) {
        try {
            String themeKey = ThemeManager.load();
            if (!themeKey.equals(cachedEditorThemeKey) || cachedEditorTheme == null) {
                String themePath = ThemeManager.THEMES.get(themeKey) != null && ThemeManager.THEMES.get(themeKey).isDark
                        ? "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"
                        : "/org/fife/ui/rsyntaxtextarea/themes/idea.xml";
                cachedEditorTheme = org.fife.ui.rsyntaxtextarea.Theme
                        .load(getClass().getResourceAsStream(themePath));
                cachedEditorThemeKey = themeKey;
            }
            cachedEditorTheme.apply(editor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel buildCategorySection(
            String name,
            List<File> files,
            JPanel parent,
            JPanel toolbar) {

        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createTitledBorder(name));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        section.setPreferredSize(new Dimension(0, 180));

        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        for (File f : files) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(f);
            if (f.isDirectory())
                node.add(new DefaultMutableTreeNode("Loading..."));
            root.add(node);
        }
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        JTree tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new FileTreeCellRenderer());

        
        tree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            @Override
            public void treeExpanded(javax.swing.event.TreeExpansionEvent event) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                if (node.getChildCount() == 1 && node.getFirstChild().toString().equals("Loading...")) {
                    node.removeAllChildren();
                    File folder = (File) node.getUserObject();
                    File[] children = folder.listFiles();
                    if (children != null) {
                        Arrays.sort(children, (a, b) -> {
                            if (a.isDirectory() && !b.isDirectory())
                                return -1;
                            if (!a.isDirectory() && b.isDirectory())
                                return 1;
                            return a.getName().compareToIgnoreCase(b.getName());
                        });
                        for (File child : children) {
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                            if (child.isDirectory())
                                childNode.add(new DefaultMutableTreeNode("Loading..."));
                            node.add(childNode);
                        }
                    }
                    treeModel.reload(node);
                }
            }

            @Override
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent event) {
            }
        });

        
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2)
                    return;
                javax.swing.tree.TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
                if (tp == null)
                    return;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
                if (!(node.getUserObject() instanceof File))
                    return;
                File f = (File) node.getUserObject();
                if (!f.isFile())
                    return;
                try {
                    String content = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
                    openFileInNewTab(f, content);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        
        JPopupMenu popup = new JPopupMenu();
        JMenuItem addFile = new JMenuItem("Add file...");
        JMenuItem addFolder = new JMenuItem("Add folder...");
        JMenuItem removeItem = new JMenuItem("Remove selected");

        addFile.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setMultiSelectionEnabled(true);
            if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                for (File f : fc.getSelectedFiles()) {
                    if (!files.contains(f)) {
                        files.add(f);
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(f);
                        root.add(node);
                    }
                }
                treeModel.reload(root);
                saveCategoriesConfig();
            }
        });

        addFolder.addActionListener(e -> {
            JFileChooser fc2 = new JFileChooser();
            fc2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc2.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                File folder = fc2.getSelectedFile();
                if (!files.contains(folder)) {
                    files.add(folder);
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
                    node.add(new DefaultMutableTreeNode("Loading..."));
                    root.add(node);
                    treeModel.reload(root);
                    saveCategoriesConfig();
                }
            }
        });

        removeItem.addActionListener(e -> {
            javax.swing.tree.TreePath[] selectedPaths = tree.getSelectionPaths();
            if (selectedPaths == null)
                return;
            for (javax.swing.tree.TreePath selPath : selectedPaths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                if (node.getParent() == root && node.getUserObject() instanceof File) {
                    files.remove((File) node.getUserObject());
                    root.remove(node);
                }
            }
            treeModel.reload(root);
            saveCategoriesConfig();
        });

        popup.add(addFile);
        popup.add(addFolder);
        popup.addSeparator();
        popup.add(removeItem);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShow(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShow(e);
            }

            private void maybeShow(MouseEvent e) {
                if (e.isPopupTrigger())
                    popup.show(tree, e.getX(), e.getY());
            }
        });

        section.add(new JScrollPane(tree), BorderLayout.CENTER);
        return section;
    }

    public void addToOpenedProjects(String path) {
        File folder = new File(path);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) openedProjectsTreeModel.getRoot();
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            if (child.getUserObject().equals(folder))
                return;
        }

        ProjectTypeDetector.ProjectType type = ProjectTypeDetector.detect(folder);
        String displayName = (type != ProjectTypeDetector.ProjectType.UNKNOWN)
                ? folder.getName() + "  [" + type.getLabel() + "]"
                : folder.getName();

        DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(new ProjectNode(folder, displayName));
        folderNode.add(new DefaultMutableTreeNode("Loading..."));
        root.add(folderNode);
        openedProjectsTreeModel.reload(root);

        if (projectToolWindow != null) {
            projectToolWindow.setProjectRoot(folder);
        }

        scanProjectIntoProvider(folder, sharedProvider);
        if (interactiveTerminal != null) {
            interactiveTerminal.cdTo(new File(path));
        }
    }

    public void registerBuildRunner(BuildRunner runner) {
        buildRunners.add(runner);
        buildOrchestrator.registerBuildRunner(runner);
    }

    public ToolchainSettings getToolchainSettings() {
        return toolchainSettings;
    }

    public ProblemsManager getProblemsManager() {
        return problemsManager;
    }

    public BuildOrchestrator getBuildOrchestrator() {
        return buildOrchestrator;
    }

    public FileTreeManager getFileTreeManager() {
        return fileTreeManager;
    }

    public DeviceManagerPanel getDeviceManagerPanel() {
        return deviceManagerPanel;
    }

    public JTabbedPane getBottomTabs() {
        return bottomTabs;
    }

    public JTextArea getTerminalArea() {
        return terminalArea;
    }

    private File getProjectRoot() {
        if (activeDirectory != null) {
            return activeDirectory;
        }
        if (currentFile != null) {
            return currentFile.getParentFile();
        }
        return null;
    }

    private void openSettingsPage() {
        IDESettingsPanel panel = new IDESettingsPanel(frame, idesettings);
        panel.onApply(() -> applySettings());
        panel.setVisible(true);
    }

    public void applySettings() {
        applySettingsToEditor(text);
        applyTerminalSettings();
        applyWindowSettings();
        ThemeManager.save(idesettings.get("appearance.theme"));
    }

    private void applySettingsToEditor(RSyntaxTextArea editor) {
        String fontFamily = idesettings.get("editor.font.family");
        int fontSize = idesettings.getInt("editor.font.size");
        editor.setFont(new Font(fontFamily, Font.PLAIN, fontSize));

        editor.setTabSize(idesettings.getInt("editor.tab.size"));
        editor.setLineWrap(idesettings.getBoolean("editor.line.wrap"));
        editor.setBracketMatchingEnabled(idesettings.getBoolean("editor.bracket.matching"));
        editor.setCodeFoldingEnabled(idesettings.getBoolean("editor.code.folding"));
        editor.setAutoIndentEnabled(idesettings.getBoolean("editor.auto.indent"));
        editor.setHighlightCurrentLine(idesettings.getBoolean("editor.highlight.current.line"));
    }

    private void applyTerminalSettings() {
        int fontSize = idesettings.getInt("terminal.font.size");
        Font termFont = new Font("Monospaced", Font.PLAIN, fontSize);
        terminalArea.setFont(termFont);

        if (interactiveTerminal != null) {
            interactiveTerminal.setFontSize(fontSize);
        }
    }

    private void applyWindowSettings() {
        int w = idesettings.getInt("appearance.window.width");
        int h = idesettings.getInt("appearance.window.height");
        if (w > 0 && h > 0) {
            frame.setSize(w, h);
        }
    }

    static class ProjectNode {
        final File file;
        final String displayName;

        ProjectNode(File file, String displayName) {
            this.file = file;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ProjectNode)) return false;
            return file.equals(((ProjectNode) o).file);
        }

        @Override
        public int hashCode() {
            return file.hashCode();
        }
    }

    public void setupDragAndDrop(Component target) {
        new java.awt.dnd.DropTarget(target, new java.awt.dnd.DropTargetAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void drop(java.awt.dnd.DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
                    java.awt.datatransfer.Transferable transferable = evt.getTransferable();
                    java.util.List<File> files = (java.util.List<File>) transferable
                            .getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                    for (File file : files) {
                        if (file.isDirectory()) {
                            addToOpenedProjects(file.getAbsolutePath());
                        } else {
                            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                            openFileInNewTab(file, content);
                        }
                    }
                    evt.dropComplete(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    evt.dropComplete(false);
                }
            }
        });
    }

    private Component buildBottomPanel() {
        bottomTabs = new JTabbedPane();

        terminalArea = new JTextArea();
        terminalArea.setEditable(false);
        terminalArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        applyTerminalTheme();
        JScrollPane termScroll = new JScrollPane(terminalArea);

        String[] cols = {
                "", "Description", "Line", "File"
        };
        problemsModel = new javax.swing.table.DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        problemsManager = new ProblemsManager(problemsModel);
        javax.swing.JTable problemsTable = new javax.swing.JTable(problemsModel);
        problemsTable.getColumnModel().getColumn(0).setMaxWidth(30);
        problemsTable.getColumnModel().getColumn(2).setMaxWidth(60);

        problemsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = problemsTable.getSelectedRow();
                if (row < 0)
                    return;
                Object lineVal = problemsModel.getValueAt(row, 2);
                if (lineVal == null)
                    return;
                try {
                    int line = Integer.parseInt(lineVal.toString());
                    jumpToLine(text, line);
                } catch (NumberFormatException igonored) {
                }
            }

        });
        bottomTabs.addTab("Terminal", termScroll);
        bottomTabs.addTab("Problems", new JScrollPane(problemsTable));

        interactiveTerminal = new InteractiveTerminal();
        bottomTabs.addTab("Shell", interactiveTerminal);

        SerialMonitorPanel serialMonitor = new SerialMonitorPanel();
        bottomTabs.addTab("Serial Monitor", serialMonitor);

        LogcatPanel logcatPanel = new LogcatPanel();
        bottomTabs.addTab("Logcat", logcatPanel);

        FtcLogcatPanel ftcLogcatPanel = new FtcLogcatPanel();
        bottomTabs.addTab("FTC Logcat", ftcLogcatPanel);

        GamepadVisualizerPanel gamepadPanel = new GamepadVisualizerPanel();
        bottomTabs.addTab("Gamepad", gamepadPanel);

        MotorServoHelperPanel motorServoPanel = new MotorServoHelperPanel();
        bottomTabs.addTab("Motor/Servo", motorServoPanel);

        CodeSnippetsPanel snippetsPanel = new CodeSnippetsPanel();
        bottomTabs.addTab("Snippets", snippetsPanel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setPreferredSize(new Dimension(0, 200));
        wrapper.add(bottomTabs);
        return wrapper;
    }

    private void applyTerminalTheme() {
        boolean isDark = ThemeManager.load().equals("dark");
        terminalArea.setBackground(isDark ? new Color(30, 30, 30) : new Color(255, 255, 255));
        terminalArea.setForeground(isDark ? new Color(200, 200, 200) : new Color(30, 30, 30));
        terminalArea.setCaretColor(isDark ? Color.WHITE : Color.BLACK);
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolbar.setBackground(UIManager.getColor("Panel.background"));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.foreground")));

        Font btnFont = UIManager.getFont("Button.font");
        if (btnFont == null) btnFont = new Font("SansSerif", Font.PLAIN, 12);

        JButton btnBuild = makeToolbarButton("Build", "hammer", "Build Project (Ctrl+F9)",
                e -> actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Compile")));
        toolbar.add(btnBuild);

        JButton btnRun = makeToolbarButton("Run", "play", "Run Current File (Shift+F10)",
                e -> actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Run")));
        toolbar.add(btnRun);

        JButton btnDebug = makeToolbarButton("Debug", "bug", "Debug Current File (Shift+F9)", e -> {
            terminalArea.append("[Debug] Starting debug session...\n");
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Run"));
        });
        toolbar.add(btnDebug);

        toolbar.add(Box.createHorizontalStrut(8));

        JButton btnStop = makeToolbarButton("Stop", "stop", "Stop Running Process", e -> {
            Process p = runningProcess;
            if (p != null && p.isAlive()) {
                p.destroyForcibly();
                terminalArea.append("[Stop] Process terminated.\n");
            }
        });
        toolbar.add(btnStop);

        toolbar.add(Box.createHorizontalStrut(8));

        JButton btnTerminal = makeToolbarButton("Terminal", "console", "Toggle Terminal (Alt+F12)",
                e -> toggleTerminal());
        toolbar.add(btnTerminal);

        toolbar.add(Box.createHorizontalGlue());

        JLabel projectLabel = new JLabel("CodeForge IDE  ");
        projectLabel.setFont(btnFont.deriveFont(Font.BOLD));
        projectLabel.setForeground(UIManager.getColor("Separator.foreground"));
        toolbar.add(projectLabel);

        return toolbar;
    }

    private JButton makeToolbarButton(String text, String iconName, String tooltip, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setIcon(IconManager.getToolbarIcon(iconName));
        btn.setFont(UIManager.getFont("Button.font") != null ? UIManager.getFont("Button.font") : new Font("SansSerif", Font.PLAIN, 12));
        btn.setToolTipText(tooltip);
        btn.addActionListener(action);
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(4, 8, 4, 8));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setOpaque(true);
                btn.setBackground(UIManager.getColor("Button.hoverBackground") != null
                    ? UIManager.getColor("Button.hoverBackground") : new Color(255, 255, 255, 25));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setOpaque(false);
            }
        });
        return btn;
    }

    public void updateStatusBar() {
        if (statusBar == null) return;
        if (currentFile != null) {
            try {
                int pos = text.getCaret().getDot();
                int line = text.getLineOfOffset(Math.min(pos, text.getDocument().getLength())) + 1;
                int col = pos - text.getLineStartOffset(text.getLineOfOffset(Math.min(pos, text.getDocument().getLength()))) + 1;
                statusBar.setFileInfo(currentFile.getAbsolutePath(), line, col);
            } catch (Exception ignored) {}
            String ext = currentFile.getName().contains(".")
                    ? currentFile.getName().substring(currentFile.getName().lastIndexOf('.') + 1).toLowerCase() : "";
            String lang = switch (ext) {
                case "java" -> "Java";
                case "py" -> "Python";
                case "cpp", "c", "ino" -> "C++";
                case "kt" -> "Kotlin";
                case "xml" -> "XML";
                case "gradle", "groovy" -> "Groovy";
                case "yaml", "yml" -> "YAML";
                case "json" -> "JSON";
                default -> ext.isEmpty() ? "Plain" : ext.toUpperCase();
            };
            statusBar.setLanguage(lang);
        }
        ProjectTypeDetector.ProjectType type = ProjectTypeDetector.ProjectType.UNKNOWN;
        if (activeDirectory != null) {
            type = ProjectTypeDetector.detect(activeDirectory);
        }
        if (type != ProjectTypeDetector.ProjectType.UNKNOWN) {
            Color badgeColor = switch (type) {
                case ARDUINO -> new Color(0, 150, 136);
                case ESP_IDF -> new Color(156, 39, 176);
                case ANDROID -> new Color(76, 175, 80);
                case FTC -> new Color(255, 152, 0);
                case FRC -> new Color(33, 150, 243);
                case VEX -> new Color(244, 67, 54);
                case ROS2 -> new Color(63, 81, 181);
                default -> Color.GRAY;
            };
            statusBar.setProjectType(type.getLabel(), badgeColor);
        }
    }

    private void jumpToLine(RSyntaxTextArea editor, int line) {
        try {
            int offset = editor.getLineStartOffset(line - 1);
            editor.setCaretPosition(offset);
            editor.requestFocusInWindow();

        } catch (javax.swing.text.BadLocationException ignored) {
        }
    }

    private void toggleTerminal() {
        if (terminalVisible) {
            lastDividerLocation = verticalSplit.getDividerLocation();
            verticalSplit.getBottomComponent().setVisible(false);
            verticalSplit.setDividerLocation(1.0);
            terminalVisible = false;
        } else {
            verticalSplit.getBottomComponent().setVisible(true);
            int loc = lastDividerLocation > 0 ? lastDividerLocation : verticalSplit.getHeight() / 3;
            verticalSplit.setDividerLocation(Math.min(loc, verticalSplit.getHeight() - 50));
            terminalVisible = true;
        }
        verticalSplit.revalidate();
        verticalSplit.repaint();
    }

    private void showBottomTab(int index) {
        if (!terminalVisible) {
            terminalVisible = true;
            verticalSplit.getBottomComponent().setVisible(true);
            int loc = lastDividerLocation > 0 ? lastDividerLocation : verticalSplit.getHeight() / 3;
            verticalSplit.setDividerLocation(Math.min(loc, verticalSplit.getHeight() - 50));
        }
        if (index >= 0 && index < bottomTabs.getTabCount()) {
            bottomTabs.setSelectedIndex(index);
        }
        bottomToolWindowBar.refreshTabs();
        verticalSplit.revalidate();
        verticalSplit.repaint();
    }

    private void installLiveErrorParser(RSyntaxTextArea editor) {
        editor.addParser(new AbstractParser() {
            @Override
            public ParseResult parse(org.fife.ui.rsyntaxtextarea.RSyntaxDocument doc, String style) {
                DefaultParseResult result = new DefaultParseResult(this);
                if (currentFile == null)
                    return result;

                String ext = currentFile.getName().contains(".")
                        ? currentFile.getName().substring(currentFile.getName().lastIndexOf('.') + 1).toLowerCase()
                        : "";

                // Only parse supported compiled languages
                if (!ext.equals("java") && !ext.equals("c") && !ext.equals("cpp"))
                    return result;

                try {
                    File tempFile = File.createTempFile("nukepad_live_", "." + ext);
                    tempFile.deleteOnExit();
                    try (FileWriter fw = new FileWriter(tempFile, java.nio.charset.StandardCharsets.UTF_8)) {
                        fw.write(editor.getText());
                    }

                    ProcessBuilder pb;
                    switch (ext) {
                        case "java":
                            File tempOut = new File(System.getProperty("java.io.tmpdir"), "nukepad_compile_out");
                            tempOut.mkdirs();
                            pb = new ProcessBuilder("javac", "-d", tempOut.getAbsolutePath(), tempFile.getPath());
                            break;
                        case "cpp":
                            pb = new ProcessBuilder("g++", "-fsyntax-only", tempFile.getPath());
                            break;
                        case "c":
                            pb = new ProcessBuilder("gcc", "-fsyntax-only", tempFile.getPath());
                            break;
                        default:
                            return result;
                    }
                    pb.redirectErrorStream(true);
                    Process proc = pb.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    Pattern pat = Pattern.compile(".+:(\\d+): (error|warning): (.+)");
                    String line;
                    SwingUtilities.invokeLater(() -> problemsModel.setRowCount(0));

                    while ((line = reader.readLine()) != null) {
                        Matcher m = pat.matcher(line);
                        if (m.find()) {
                            int lineNum = Integer.parseInt(m.group(1));
                            String type = m.group(2);
                            String msg = m.group(3);
                            DefaultParserNotice notice = new DefaultParserNotice(
                                    this, msg, lineNum - 1);
                            notice.setLevel(type.equals("error")
                                    ? ParserNotice.Level.ERROR
                                    : ParserNotice.Level.WARNING);
                            result.addNotice(notice);
                            String icon = type.equals("error") ? "❌" : "⚠️";
                            final String fMsg = msg;
                            final int fLine = lineNum;
                            SwingUtilities.invokeLater(() -> problemsModel
                                    .addRow(new Object[] { icon, fMsg, fLine, currentFile.getName() }));
                        }
                    }
                    proc.waitFor();

                } catch (Exception ex) {

                }
                File tempOut2 = new File(System.getProperty("java.io.tmpdir"), "nukepad_compile_out");
                File[] classFiles = tempOut2.listFiles((dir, name) -> name.endsWith(".class"));
                if (classFiles != null) {
                    for (File cf : classFiles)
                        cf.delete();
                }
                return result;
            }
        });
    }

    private void scanProjectIntoProvider(File projectDir, CombinedProvider provider) {
        new SwingWorker<Set<String>, Void>() {
            @Override
            protected Set<String> doInBackground() throws Exception {
                Set<String> words = new HashSet<>();
                java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]{2,}\\b");
                scanDir(projectDir, p, words, 0);
                return words;
            }

            private void scanDir(File dir, java.util.regex.Pattern p,
                    Set<String> words, int depth) throws Exception {
                if (depth > 5)
                    return;
                File[] files = dir.listFiles();
                if (files == null)
                    return;
                for (File f : files) {
                    if (f.isDirectory()) {
                        scanDir(f, p, words, depth + 1);
                    } else if (isSource(f)) {
                        String content = new String(
                                java.nio.file.Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
                        java.util.regex.Matcher mat = p.matcher(content);
                        while (mat.find())
                            words.add(mat.group());
                    }
                }
            }

            private boolean isSource(File f) {
                String n = f.getName();
                return n.endsWith(".java") || n.endsWith(".py")
                        || n.endsWith(".js") || n.endsWith(".ts")
                        || n.endsWith(".cpp") || n.endsWith(".c")
                        || n.endsWith(".ino");

            }

            @Override
            protected void done() {
                try {
                    provider.setProjectWords(get());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }.execute();
    }

    private void cycleSidebarPosition() {
        switch (sidebarPosition) {
            case LEFT:
                moveSidebarTo(SidebarPosition.CENTER);
                break;
            case CENTER:
                moveSidebarTo(SidebarPosition.RIGHT);
                break;
            case RIGHT:
                moveSidebarTo(SidebarPosition.LEFT);
                break;
        }
    }

    private void moveSidebarTo(SidebarPosition target) {
        if (target == sidebarPosition)
            return;

        boolean wasCenter = (sidebarPosition == SidebarPosition.CENTER);
        boolean goCenter = (target == SidebarPosition.CENTER);

        sidebarPosition = target;

        if (wasCenter && !goCenter) {
            mergeRightTabsIntoLeft();
            rightTabs = null;
        }

        if (goCenter) {
            rightTabs = new JTabbedPane();
            rightTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            hookTabFocusTracking(rightTabs);

            if (rightTabs.getTabCount() == 0) {
                RSyntaxTextArea rightEditor = createFreshEditor();
                applyEditorTheme(rightEditor);
                RTextScrollPane rs = new RTextScrollPane(rightEditor);
                rs.setRowHeaderView(new LineNumberPanel(rightEditor));
                rightTabs.addTab("Untitled", rs);

            }

        }
        rebuildLayout();
    }

    private void rebuildLayout() {
        frame.getContentPane().removeAll();

        buildBottomPanel();
        bottomToolWindowBar = new BottomToolWindowBar(bottomTabs, this::toggleTerminal);

        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.add(bottomToolWindowBar, BorderLayout.NORTH);
        bottomSection.add(bottomTabs, BorderLayout.CENTER);

        verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                tabs, bottomSection);
        verticalSplit.setResizeWeight(0.75);

        switch (sidebarPosition) {
            case LEFT:
                outerHSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        leftTabsPanel != null ? leftTabsPanel : projectToolWindow, verticalSplit);
                outerHSplit.setDividerLocation(280);
                break;
            case RIGHT:
                outerHSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        verticalSplit, leftTabsPanel != null ? leftTabsPanel : projectToolWindow);
                outerHSplit.setDividerLocation(frame.getWidth() - 280);
                break;
            case CENTER: {
                JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        leftTabsPanel != null ? leftTabsPanel : projectToolWindow, rightTabs);
                rightSplit.setDividerLocation(280);
                rightSplit.setResizeWeight(0.0);

                JSplitPane fullH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        tabs, rightSplit);
                fullH.setDividerLocation(frame.getWidth() / 2 - 140);
                fullH.setResizeWeight(0.5);

                verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                        fullH, bottomSection);
                verticalSplit.setResizeWeight(0.75);

                outerHSplit = verticalSplit;
                break;
            }
        }

        if (statusBar == null) {
            statusBar = new StatusBar();
            statusBar.setPreferredSize(new Dimension(0, 24));
        }
        frame.getContentPane().add(topSection, BorderLayout.NORTH);
        frame.getContentPane().add(outerHSplit, BorderLayout.CENTER);
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
        frame.revalidate();
        frame.repaint();
    }

    private void hookTabFocusTracking(JTabbedPane pane) {
        pane.addChangeListener(e -> {
            Component sel = pane.getSelectedComponent();
            if (sel instanceof RTextScrollPane) {
                RTextScrollPane sp = (RTextScrollPane) sel;
                RSyntaxTextArea editor = (RSyntaxTextArea) sp.getTextArea();
                text = editor;
                File f = (File) sp.getClientProperty("file");
                if (f != null) {
                    currentFile = f;
                    activeDirectory = f.getParentFile();
                    if (gitPanel != null)
                        gitPanel.setRepoDir(activeDirectory);
                    LspDocumentAdapter adapter = (LspDocumentAdapter) editor.getClientProperty("lspDocAdapter");
                    if (adapter != null && adapter.getCurrentFile() != null) {
                        lspDocAdapter = adapter;
                    }
                }
                if (findReplaceDialog != null) {
                    findReplaceDialog.setEditor(editor);
                }
            }
        });
    }

    private void openFileInPane(File file, JTabbedPane targetPane) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            openFileInTab(file, content, targetPane);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openFileInTab(File file, String content, JTabbedPane targetPane) {
        if (targetPane == null)
            targetPane = tabs;

        RSyntaxTextArea editor = new RSyntaxTextArea();
        editor.setCodeFoldingEnabled(true);
        editor.setAntiAliasingEnabled(true);
        applyEditorTheme(editor);
        applySettingsToEditor(editor);
        installLiveErrorParser(editor);

        String name = file.getName().toLowerCase();
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : "";
        
        ImportValidator importValidator = new ImportValidator(editor);
        importValidator.setExt(ext); 
        editor.addParser(importValidator);
        
        switch (ext) {
            case "java":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                break;
            case "xml":
            case "html":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
                break;
            case "js":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
                break;
            case "py":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
                break;
            case "cpp":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
                break;
            case "cs":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSHARP);
                break;
            case "c":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
                break;
            case "ino":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
                break;
            case "tsx":
            case "ts":
            case "jsx":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT);
                break;
            case "json":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
                break;
            case "sql":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
                break;
            case "go":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GO);
                break;
            case "f90":
            case "f":
            case "for":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_FORTRAN);
                break;
            case "php":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);
                break;
            case "rs":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUST);
                break;
            case "kt":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_KOTLIN);
                break;
            case "groovy":
            case "gradle":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
                break;
            case "yaml":
            case "yml":
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_YAML);
                break;
            default:
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
                break;
        }
        editor.setText(content);
        CombinedProvider tabProvider = new CombinedProvider(editor);
        tabProvider.setProjectWords(sharedProvider != null
                ? sharedProvider.getProjectWords()
                : Collections.emptySet());
        AutoCompletion ac = new AutoCompletion(tabProvider);
        ac.setAutoActivationEnabled(true);
        ac.setAutoActivationDelay(300);
        ac.install(editor);

        RTextScrollPane scroll = new RTextScrollPane(editor);
        scroll.setRowHeaderView(new LineNumberPanel(editor));
        scroll.putClientProperty("file", file);

        targetPane.addTab(file.getName(), scroll);
        targetPane.setSelectedComponent(scroll);

        this.text = editor;
        this.currentFile = file;

        if (gitPanel != null)
            gitPanel.setRepoDir(file.getParentFile());
        makeTabClosable(targetPane, scroll, file.getName(), file.getAbsolutePath());
        addToOpenedProjects(file.getParentFile().getAbsolutePath());
        setupDragAndDrop(scroll);
        setupDragAndDrop(editor);
    }

    public void maybeShowPopup(MouseEvent e, JTree tree) {
        if (!e.isPopupTrigger())
            return;

        int row = tree.getRowForLocation(e.getX(), e.getY());
        if (row < 0)
            return;
        tree.setSelectionRow(row);

        javax.swing.tree.TreePath tp = tree.getPathForRow(row);
        if (tp == null)
            return;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
        Object userObj = node.getUserObject();
        File clickedFile = null;
        if (userObj instanceof File) {
            clickedFile = (File) userObj;
        } else if (userObj instanceof ProjectNode) {
            clickedFile = ((ProjectNode) userObj).file;
        }
        if (clickedFile == null || clickedFile.isDirectory())
            return;
        final File clicked = clickedFile;

        JPopupMenu popup = new JPopupMenu();

        if (sidebarPosition == SidebarPosition.CENTER) {
            JMenuItem openLeft = new JMenuItem("Open on the left side");
            openLeft.addActionListener(ae -> openFileInPane(clicked, tabs));
            popup.add(openLeft);

            JMenuItem openRight = new JMenuItem("Open on the right side");
            openRight.addActionListener(ae -> {
                if (rightTabs != null)
                    openFileInPane(clicked, rightTabs);
            });
            popup.add(openRight);
        } else {
            JMenuItem open = new JMenuItem("Open");
            open.addActionListener(ae -> {
                try {
                    String content = new String(Files.readAllBytes(clicked.toPath()), StandardCharsets.UTF_8);
                    openFileInNewTab(clicked, content);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            popup.add(open);
        }
        popup.show(tree, e.getX(), e.getY());
    }

    private RSyntaxTextArea createFreshEditor() {
        RSyntaxTextArea eiumatlum = new RSyntaxTextArea();
        eiumatlum.setCodeFoldingEnabled(true);
        eiumatlum.setAntiAliasingEnabled(true);
        applyEditorTheme(eiumatlum);
        applySettingsToEditor(eiumatlum);
        installLiveErrorParser(eiumatlum);
        return eiumatlum;
    }

    private JPanel buildBottomPanelWrapper() {
        if (bottomPanelCache != null)
            return bottomPanelCache;
        bottomPanelCache = new JPanel(new BorderLayout());
        bottomPanelCache.setPreferredSize(new Dimension(0, 200));
        bottomPanelCache.add(buildBottomPanel());
        return bottomPanelCache;
    }
    
    private ImageIcon loadAndScaleIcon(String iconPath, int size) {
    try {
        ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
        if (icon.getImage() != null) {
            return new ImageIcon(icon.getImage().getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH));
        }
    } catch (Exception e) {
        System.err.println("Failed to load icon: " + iconPath);
    }
    return null;
}
    
    private ImageIcon scaleIcon(ImageIcon icon, int size) {
    return new ImageIcon(icon.getImage().getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH));
}

    private void mergeRightTabsIntoLeft() {
        if (rightTabs == null)
            return;
        while (rightTabs.getTabCount() > 0) {
            String title = rightTabs.getTitleAt(0);
            Component comp = rightTabs.getComponentAt(0);
            rightTabs.removeTabAt(0);
            if (comp instanceof RTextScrollPane) {
                File f = (File) ((RTextScrollPane) comp).getClientProperty("file");
                if (f == null && title.equals("Untitled"))
                    continue;
                tabs.addTab(title, comp);
                if (f != null) {
                    makeTabClosable(tabs, comp, title, f.getAbsolutePath());
                } else {
                    tabs.addTab(title, comp);
                } // john

            } // james

        } // jared

    }// jasmine

}// jerry


























