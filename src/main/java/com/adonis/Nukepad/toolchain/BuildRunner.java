package com.adonis.Nukepad.toolchain;

import java.io.File;
import javax.swing.JTextArea;
import javax.swing.JTabbedPane;

public interface BuildRunner {
    String getName();
    boolean canHandle(File projectRoot, File currentFile);
    void compile(File projectRoot, File currentFile, JTextArea terminalArea, JTabbedPane bottomTabs, ProblemsManager problems);
    void run(File projectRoot, File currentFile, JTextArea terminalArea, JTabbedPane bottomTabs);
}
