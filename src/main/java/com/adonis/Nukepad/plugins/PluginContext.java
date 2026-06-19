
package com.adonis.Nukepad.plugins;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import java.io.File;

public class PluginContext {
    private Object editor;  // Keep as Object to avoid circular import
    private RSyntaxTextArea currentEditor;
    
    public PluginContext(Object editor, RSyntaxTextArea currentEditor) {
        this.editor = editor;
        this.currentEditor = currentEditor;
    }
    
    public Object getEditor() { 
        return editor; 
    }
    
    public RSyntaxTextArea getCurrentEditor() { 
        return currentEditor; 
    }
    
    public void setCurrentEditor(RSyntaxTextArea editor) { 
        this.currentEditor = editor; 
    }
    
    public String getEditorText() { 
        return currentEditor != null ? currentEditor.getText() : ""; 
    }
    
    public void setEditorText(String text) { 
        if (currentEditor != null) {
            currentEditor.setText(text); 
        }
    }
    
    public File getCurrentFile() {
        // Will be implemented by the plugin user
        return null;
    }
}