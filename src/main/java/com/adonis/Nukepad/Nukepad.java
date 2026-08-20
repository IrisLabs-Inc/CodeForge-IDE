/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.adonis.Nukepad;

import com.adonis.Nukepad.plugins.PluginContext;
import com.adonis.Nukepad.plugins.PluginManager;
import com.adonis.Nukepad.plugins.PluginsDialog;
import com.adonis.Nukepad.toolchain.BuildRunner;
import com.adonis.Nukepad.toolchain.DeviceManagerPanel;
import com.adonis.Nukepad.toolchain.ProblemsManager;
import com.adonis.Nukepad.toolchain.ProjectTypeDetector;
import com.adonis.Nukepad.toolchain.ToolchainSettings;
import com.adonis.Nukepad.toolchain.ToolchainSettingsPanel;
import com.adonis.Nukepad.settings.IDESettings;
import com.adonis.Nukepad.settings.IDESettingsPanel;
import com.adonis.Nukepad.arduino.ArduinoCliRunner;
import com.adonis.Nukepad.arduino.SerialMonitorPanel;
import com.adonis.Nukepad.arduino.LibraryManagerPanel;
import com.adonis.Nukepad.arduino.BoardManagerPanel;
import java.util.ArrayList;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatDarculaLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
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
    private boolean terminalVisible = true; // Just tells the IDE / Computer that the terminal is visible initially
    private int lastDividerLocation = 500; // Last divider location for the vertical split pane (also initializes it)
    private GitRunner gitRunner; // Runs git
    private GitPanel gitPanel; // The git pannel
    private File activeDirectory; // Active directory, usefull for git
    private ActivityBar activityBar;
    private PluginManager pluginManager;

    private ToolchainSettings toolchainSettings;
    private IDESettings idesettings;
    private ProblemsManager problemsManager;
    private final List<BuildRunner> buildRunners = new ArrayList<>();
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
        frame = new JFrame("Editor");
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/nukepadlogo.png"));
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        RTextScrollPane scroll = new RTextScrollPane(text);
        scroll.setRowHeaderView(new LineNumberPanel(text));
        tabs.addTab("Untitled", scroll);
        
        PluginManager pluginManager = new PluginManager(
            System.getProperty("user.home") + "/.nukepad_plugins"
        );
        PluginContext pluginContext = new PluginContext(this, text);
        pluginManager.loadPlugins(pluginContext);
        
        text.addCaretListener(e -> pluginManager.notifyTextChange(text.getText()));

        toolchainSettings = new ToolchainSettings();
        deviceManagerPanel = new DeviceManagerPanel();
        ArduinoCliRunner arduinoRunner = new ArduinoCliRunner();
        registerBuildRunner(arduinoRunner);

        activityBar = new ActivityBar();

        JPopupMenu filePopup = new JPopupMenu();
        JMenu mNew = new JMenu("New");
        String[][] fileTypes2 = {
            {"Java Class","java","public class %s {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World!\");\n    }\n}\n"},
            {"C++ Source","cpp","#include <iostream>\n\nint main() {\n    std::cout << \"Hello World!\" << std::endl;\n    return 0;\n}\n"},
            {"C Source","c","#include <stdio.h>\n\nint main() {\n    printf(\"Hello World!\\n\");\n    return 0;\n}\n"},
            {"Python Script","py","print('Hello World!')\n"},
            {"JavaScript File","js","console.log('Hello World!');\n"},
            {"TypeScript File","ts","console.log('Hello World!');\n"},
            {"Arduino Sketch","ino","void setup() {\n    // Initialize here\n}\n\nvoid loop() {\n    // Main loop here\n}\n"},
        };
        for (String[] type : fileTypes2) {
            JMenuItem mi = new JMenuItem(type[0]);
            mi.addActionListener(ev -> createNewLanguageFile(type[1], type[2]));
            mNew.add(mi);
        }
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

        JMenuItem miDark  = new JMenuItem("Dark Theme");
        JMenuItem miLight = new JMenuItem("Light Theme");

        miDark.addActionListener(ev -> {
            try {
                ThemeManager.save("dark");
                idesettings.set("appearance.theme", "dark");
                idesettings.save();
                clearThemeOverrides();
                UIManager.setLookAndFeel(new FlatDarculaLaf());
                applyThemeToAllTabs(); applyTerminalTheme();
                interactiveTerminal.applyTheme(true);
                activityBar.applyTheme();
                SwingUtilities.updateComponentTreeUI(frame); frame.repaint();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        miLight.addActionListener(ev -> {
            try {
                ThemeManager.save("light");
                idesettings.set("appearance.theme", "light");
                idesettings.save();
                clearThemeOverrides();
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
                applyThemeToAllTabs(); applyTerminalTheme();
                interactiveTerminal.applyTheme(true);
                activityBar.applyTheme();
                SwingUtilities.updateComponentTreeUI(frame); frame.repaint();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        settingsPopup.add(miDark);
        settingsPopup.add(miLight);
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


        activityBar.addTop("compile", ActivityBar.PAINT_COMPILE, "Compile",
            e -> actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Compile")));

        activityBar.addTop("run", ActivityBar.PAINT_RUN, "Run",
            e -> actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Run")));

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
        verticalSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                tabs,
                buildBottomPanel());
        gitRunner = new GitRunner(terminalArea, bottomTabs);
        gitPanel = new GitPanel(gitRunner);
        verticalSplit.setResizeWeight(0.75);

        outerHSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                loadingLabel,
                verticalSplit);
        outerHSplit.setDividerLocation(280);
        frame.add(activityBar, BorderLayout.WEST);
        frame.add(outerHSplit, BorderLayout.CENTER);

        frame.setSize(1280, 720);
        frame.setVisible(true);
        new javax.swing.SwingWorker<JTabbedPane, Void>() {
            @Override
            protected JTabbedPane doInBackground() {
                File rootDir = new File(System.getProperty("user.home"));
                DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootDir);
                rootNode.add(new DefaultMutableTreeNode("Loading..."));
                DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
                fileTree = new JTree(treeModel);
                fileTree.setCellRenderer(new FileTreeCellRenderer());
                fileTree.setRootVisible(true);
                fileTree.setToggleClickCount(2);
                javax.swing.SwingUtilities.invokeLater(() -> fileTree.collapseRow(0));
                JScrollPane treeScroll = new JScrollPane(fileTree);

                fileTree.addTreeSelectionListener(e -> {
                    javax.swing.tree.TreePath path = e.getPath();
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        Object userObj = node.getUserObject();
                        if (userObj instanceof File) {
                            File selected = (File) userObj;
                            activeDirectory = selected.isDirectory() ? selected : selected.getParentFile();
                            if (gitPanel != null && activeDirectory != null) {
                                gitPanel.setRepoDir(activeDirectory);
                            }
                        }
                    }
                });
                fileTree.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        maybeShowPopup(e, fileTree);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        maybeShowPopup(e, fileTree);
                    }
                });

                fileTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
                    @Override
                    public void treeExpanded(TreeExpansionEvent event) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                        if (node.getChildCount() == 1 && node.getFirstChild().toString().equals("Loading...")) {
                            node.removeAllChildren();
                            File folder = (File) node.getUserObject();
                            File[] children = folder.listFiles();
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
                            treeModel.reload(node);
                        }
                    }

                    @Override
                    public void treeCollapsed(javax.swing.event.TreeExpansionEvent event) {

                    }

                });

                JPanel categoriesPanel = buildCategoriesPanel();
                JPanel categoriesWrapper = new JPanel(new BorderLayout());
                categoriesWrapper.add(categoriesPanel, BorderLayout.NORTH);
                JScrollPane categoriesScroll = new JScrollPane(categoriesWrapper);

                openedProjectsTree = new JTree(openedProjectsTreeModel);
                openedProjectsTree.setCellRenderer(new FileTreeCellRenderer());
                openedProjectsTree.setRootVisible(false);
                openedProjectsTree.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            int row = openedProjectsTree.getRowForLocation(e.getX(), e.getY());
                            if (row < 0)
                                return;
                            javax.swing.tree.TreePath treePath = openedProjectsTree.getPathForRow(row);
                            if (treePath == null)
                                return;
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                            Object userObj = node.getUserObject();
                            File clicked = null;
                            if (userObj instanceof File) {
                                clicked = (File) userObj;
                            } else if (userObj instanceof ProjectNode) {
                                clicked = ((ProjectNode) userObj).file;
                            }
                            if (clicked == null || clicked.isDirectory())
                                return;
                            try {
                                String content = new String(Files.readAllBytes(clicked.toPath()));
                                openFileInNewTab(clicked, content);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        maybeShowPopup(e, openedProjectsTree);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        maybeShowPopup(e, openedProjectsTree);
                    }
                });
                openedProjectsTree.addTreeSelectionListener(e -> {
                    javax.swing.tree.TreePath path = e.getPath();
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        Object userObj = node.getUserObject();
                        File selected = null;
                        if (userObj instanceof File) {
                            selected = (File) userObj;
                        } else if (userObj instanceof ProjectNode) {
                            selected = ((ProjectNode) userObj).file;
                        }
                        if (selected != null) {
                            activeDirectory = selected.isDirectory() ? selected : selected.getParentFile();
                            if (gitPanel != null && activeDirectory != null) {
                                gitPanel.setRepoDir(activeDirectory);
                            }
                        }
                    }
                });
                JScrollPane openedScroll = new JScrollPane(openedProjectsTree);
                openedProjectsTree.setRootVisible(false);
                openedProjectsTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
                    @Override
                    public void treeExpanded(javax.swing.event.TreeExpansionEvent event) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

                        if (node.getChildCount() == 1 && node.getFirstChild().toString().equals("Loading...")) {
                            node.removeAllChildren();
                            Object userObj = node.getUserObject();
                            File folder = null;
                            if (userObj instanceof File) {
                                folder = (File) userObj;
                            } else if (userObj instanceof ProjectNode) {
                                folder = ((ProjectNode) userObj).file;
                            }
                            if (folder == null) return;
                            File[] children = folder.listFiles();
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
                            openedProjectsTreeModel.reload(node);
                        }
                    }

                    @Override
                    public void treeCollapsed(TreeExpansionEvent event) {
                    }
                });

                JTabbedPane leftTabs = new JTabbedPane();
                  ImageIcon filesIcon = new ImageIcon(getClass().getResource("/icons/files.png"));
                  ImageIcon searchIcon = new ImageIcon(getClass().getResource("/icons/search.png"));
                  ImageIcon categoriesIcon = new ImageIcon(getClass().getResource("/icons/categories.png"));
                  ImageIcon projectsIcon = new ImageIcon(getClass().getResource("/icons/projects.png"));
                  ImageIcon gitIcon = new ImageIcon(getClass().getResource("/icons/git.png"));
                  ImageIcon openedProjectsIcon = new ImageIcon(getClass().getResource("/icons/openedprojects.png"));

                leftTabs.addTab(null, scaleIcon(filesIcon, 24), treeScroll, "Files");
                leftTabs.addTab(null, scaleIcon(searchIcon, 24), null, "Search");
                leftTabs.addTab(null, scaleIcon(categoriesIcon, 24), categoriesScroll, "Categories");
                leftTabs.addTab(null, scaleIcon(openedProjectsIcon, 24), openedScroll, "Opened Projects");
                leftTabs.addTab(null, scaleIcon(gitIcon, 24), gitPanel, "Git");
                return leftTabs;

            }

            @Override
            protected void done() {
                try {
                    JTabbedPane leftTabs = get();
                    leftTabsPanel = leftTabs;
                    hookTabFocusTracking(tabs);
                    outerHSplit.setLeftComponent(leftTabs);
                    outerHSplit.setDividerLocation(280);

                    JPanel searchPlaceholder = new JPanel(new BorderLayout());
                    searchPlaceholder.add(new JLabel("Click to load search", SwingConstants.CENTER));
                    leftTabs.setComponentAt(1, searchPlaceholder);

                    leftTabs.addChangeListener(e -> {
                        if (leftTabs.getSelectedIndex() == 1 &&
                                leftTabs.getComponentAt(1) == searchPlaceholder) {
                            SearchPanel sp = new SearchPanel(new File(System.getProperty("user.home")));
                            sp.setPreferredSize(new Dimension(280, 0));
                            sp.setMinimumSize(new Dimension(100, 100));
                            leftTabs.setComponentAt(1, sp);
                            leftTabs.revalidate();
                            leftTabs.repaint();
                        }

                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }.execute();

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
                JFileChooser jSave = new JFileChooser("f:");
                int rSave = jSave.showSaveDialog(null);

                if (rSave == JFileChooser.APPROVE_OPTION) {
                    File file = new File(jSave.getSelectedFile().getAbsolutePath());
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                        writer.write(text.getText());
                        
                         pluginManager.notifyFileSave(file);
                         
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
                            String content = new String(Files.readAllBytes(file.toPath()));
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
                frame.setVisible(false);
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
                        case "py":
                        case "js":
                        case "ts":
                        case "tsx":
                        case "jsx":
                            terminalArea.append("ℹ️" + ext.toUpperCase() + "is interpreted, use Run instead.\n");
                            bottomTabs.setSelectedIndex(0);
                            return;
                        default:
                            terminalArea.append("ERROR: Compilation not supported for '.'" + ext + "'files.\n");
                            bottomTabs.setSelectedIndex(0);
                            return;
                    }

                    pbC.redirectErrorStream(true);
                    Process procC = pbC.start();
                    BufferedReader readerC = new BufferedReader(new InputStreamReader(procC.getInputStream()));
                    Pattern errorPat = Pattern.compile(".+:(\\d+): (error|warning): (.+)");
                    String lineC;
                    int errors = 0, warnings = 0;
                    while ((lineC = readerC.readLine()) != null) {
                        terminalArea.append(lineC + "\n");
                        Matcher m = errorPat.matcher(lineC);
                        if (m.find()) {
                            String lineNum = m.group(1);
                            String type = m.group(2);
                            String msg = m.group(3);
                            String icon = type.equals("error") ? "❌" : "⚠️";
                            problemsModel.addRow(new Object[] { icon, msg, lineNum, currentFile.getName() });
                            if (type.equals("error"))
                                errors++;
                            else
                                warnings++;
                        }
                    }
                    procC.waitFor();
                    if (procC.exitValue() == 0) {
                        terminalArea.append("\n ✅ Build successful. \n");
                        bottomTabs.setSelectedIndex(0);
                    } else {
                        terminalArea.append("\n ❌ Build failed -" + errors + "error(s), " + warnings + "warning(s)\n");
                        bottomTabs.setSelectedIndex(1);
                    }

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
                    case "ts":
                    case "tsx":
                    case "jsx":
                    case "js":
                        pbR = new ProcessBuilder("node", currentFile.getPath());
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
                        }
                    }
                }.execute();
                break;

            default:
                System.out.println("Unknown command:" + s);
        }
    }

    public static void main(String[] args) {
        new IntroScreen();
    }

    public void openWebPage(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
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
            default:
                editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
                break;
        }
        
        ImportValidator importValidator = new ImportValidator(editor);
        importValidator.setExt(ext); 
        editor.addParser(importValidator);
        editor.setText(content);
        CombinedProvider tabProvider = new CombinedProvider(editor);
        tabProvider.setProjectWords(sharedProvider != null
                ? ((CombinedProvider) sharedProvider).getProjectWords() // see note below
                : Collections.emptySet());
        AutoCompletion ac = new AutoCompletion(tabProvider);
        ac.setAutoActivationEnabled(true);
        ac.setAutoActivationDelay(300);
        ac.install(editor);

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

        addToOpenedProjects(file.getParentFile().getAbsolutePath());

        setupDragAndDrop(scroll);
        setupDragAndDrop(editor);
        
        pluginManager.notifyFileOpen(file);
    }

    private void makeTabClosable(JTabbedPane tabs, Component tab, String title, String fullPath) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel(title);
        JButton close = new JButton("✕");
        close.setBorder(null);
        close.setFocusable(false);
        close.setContentAreaFilled(false);

        close.addActionListener(e -> {
            int index = tabs.indexOfComponent(tab);
            if (index != -1) {
                tabs.remove(index);
            }
        });

        panel.add(label, BorderLayout.WEST);
        panel.add(close, BorderLayout.EAST);

        tabs.setTabComponentAt(tabs.indexOfComponent(tab), panel);
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CATEGORIES_CONFIG_FILE))) {
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

    private void applyEditorTheme(RSyntaxTextArea editor) {
        try {
            String themePath = ThemeManager.load().equals("dark")
                    ? "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"
                    : "/org/fife/ui/rsyntaxtextarea/themes/idea.xml";
            org.fife.ui.rsyntaxtextarea.Theme theme = org.fife.ui.rsyntaxtextarea.Theme
                    .load(getClass().getResourceAsStream(themePath));
            theme.apply(editor);
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
                    String content = new String(Files.readAllBytes(f.toPath()));
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
        scanProjectIntoProvider(folder, sharedProvider);
        if (interactiveTerminal != null) {
            interactiveTerminal.cdTo(new File(path));
        }
    }

    public void registerBuildRunner(BuildRunner runner) {
        buildRunners.add(runner);
    }

    public ToolchainSettings getToolchainSettings() {
        return toolchainSettings;
    }

    public ProblemsManager getProblemsManager() {
        return problemsManager;
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
                            String content = new String(Files.readAllBytes(file.toPath()));
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
            verticalSplit.setDividerLocation(lastDividerLocation);
            terminalVisible = true;
        }
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
                    // Save current content to a temp file first
                    File tempFile = currentFile;
                    try (FileWriter fw = new FileWriter(tempFile)) {
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
                java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\\\b[a-zA-Z_][a-zA-Z0-9_]{2,}\\\\b");
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
                                java.nio.file.Files.readAllBytes(f.toPath()));
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
        frame.add(activityBar, BorderLayout.WEST);
        verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                tabs, buildBottomPanelWrapper());
        verticalSplit.setResizeWeight(0.75);

        switch (sidebarPosition) {
            case LEFT:
                outerHSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        leftTabsPanel, verticalSplit);
                outerHSplit.setDividerLocation(280);
                frame.add(outerHSplit, BorderLayout.CENTER);
                break;
            case RIGHT:
                outerHSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        verticalSplit, leftTabsPanel);
                outerHSplit.setDividerLocation(frame.getWidth() - 280);
                frame.add(outerHSplit, BorderLayout.CENTER);
                break;
            case CENTER: {
                JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        leftTabsPanel, rightTabs);
                rightSplit.setDividerLocation(280);
                rightSplit.setResizeWeight(0.0);

                JSplitPane fullH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        tabs, rightSplit);
                fullH.setDividerLocation(frame.getWidth() / 2 - 140);
                fullH.setResizeWeight(0.5);

                verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                        fullH, buildBottomPanelWrapper());
                verticalSplit.setResizeWeight(0.75);

                frame.add(verticalSplit, BorderLayout.CENTER);
                break;
            }
        }
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
                }
            }
        });
    }

    private void openFileInPane(File file, JTabbedPane targetPane) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
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
                    String content = new String(Files.readAllBytes(clicked.toPath()));
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


























