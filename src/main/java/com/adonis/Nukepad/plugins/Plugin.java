/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.adonis.Nukepad.plugins;

import java.io.File;
import javax.swing.JPanel;

/**
 *
 * @author croco
 */
public interface Plugin {
    String getName();
    String getVersion();
    String getDescription();
    void initialize(PluginContext context);
    void onEditorTextChange(String text);
    void onFileOpen(java.io.File file);
    JPanel getPanel();
    void shutdown();

    void onFileSave(File file);
}
