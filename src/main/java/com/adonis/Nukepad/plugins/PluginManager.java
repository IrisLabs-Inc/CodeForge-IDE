/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.adonis.Nukepad.plugins;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 *
 * @author croco
 */
public class PluginManager {
    private List<Plugin> loadedPlugins = new ArrayList<>();
    private File pluginDir;
    
    public PluginManager(String pluginDirPath) {
        this.pluginDir = new File(pluginDirPath);
        if (!pluginDir.exists()) pluginDir.mkdirs();
    }
    public File getPluginDir() {
        return pluginDir;
    }
    
    public void loadPlugins(PluginContext context) {
        File[] jarFiles = pluginDir.listFiles((d, n) -> n.endsWith(".jar"));
        if(jarFiles == null) return;
        
        for(File jar : jarFiles) {
            try {
                loadPlugin(jar, context);
            } catch (Exception e) {
                System.err.println("Failed to load plugin: " + jar.getName());
                e.printStackTrace();
            }
        }
    }
    
    private void loadPlugin(File jar, PluginContext context) throws Exception {
        URLClassLoader classLoader = new URLClassLoader (
               new URL[]{jar.toURI().toURL()},
                getClass().getClassLoader() 
        );
        // Read plugin manifest or use reflection to find Plugin implementation
        // For simplicity, assume the main class implements Plugin
        Enumeration<URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");
        
        // Alternative: scan jar for Plugin implementations
        // You can use a library like Reflections or manually scan
        loadedPlugins.add(null); //placeholder2
    }
    
    public List<Plugin> getLoadedPlugins() {
        return loadedPlugins;
    }
    
    public void notifyTextChange(String text) {
        for (Plugin p : loadedPlugins) {
            if (p != null) p.onEditorTextChange(text);
        }
    }
    
    public void notifyFileOpen(File file) {
        for (Plugin p : loadedPlugins) {
            if (p != null) p.onFileOpen(file);
        }
    }
    
    public void notifyFileSave(File file) {
        for (Plugin p : loadedPlugins) {
            if (p != null) p.onFileSave(file);
        }
    }
}
