/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.adonis.Nukepad;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;

/**
 *
 * @author croco
 */
public class CombinedProvider extends DefaultCompletionProvider {
    private final RSyntaxTextArea editor;
    private final Set<String> projectWords = ConcurrentHashMap.newKeySet();
    private volatile long cachedModCount = -1;
    private volatile List<Completion> cachedCompletions = List.of();

    public CombinedProvider(RSyntaxTextArea editor) {
        this.editor = editor;
        setAutoActivationRules(true, null);

        editor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { invalidateCache(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { invalidateCache(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { invalidateCache(); }
        });
    }

    private void invalidateCache() {
        cachedModCount = -1;
    }

    public void setProjectWords(Set<String> words) {
        projectWords.clear();
        projectWords.addAll(words);
        invalidateCache();
    }

    @Override
    public List<Completion> getCompletions(JTextComponent comp) {
        long currentModCount = editor.getDocument().getLength()
                + (long) editor.getLineCount() * 31
                + projectWords.size();

        if (cachedModCount == currentModCount && !cachedCompletions.isEmpty()) {
            return cachedCompletions;
        }

        clear();
        Set<String> seen = new HashSet<>();

        RSyntaxDocument doc = (RSyntaxDocument) editor.getDocument();
        int lineCount = editor.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            Token tk = doc.getTokenListForLine(i);
            while (tk != null && tk.isPaintable()) {
                int type = tk.getType();
                if (type == Token.RESERVED_WORD
                        || type == Token.RESERVED_WORD_2
                        || type == Token.FUNCTION
                        || type == Token.IDENTIFIER) {
                    String word = tk.getLexeme();
                    if (word.length() > 1 && seen.add(word))
                        addCompletion(new BasicCompletion(this, word));
                }
                tk = tk.getNextToken();
            }
        }
        for (String word : projectWords) {
            if (seen.add(word))
                addCompletion(new BasicCompletion(this, word));
        }

        cachedModCount = currentModCount;
        cachedCompletions = super.getCompletions(comp);
        return cachedCompletions;
    }

    public Set<String> getProjectWords() {
        return Collections.unmodifiableSet(projectWords);
    }
    
    private void addImportCompletions(String ext, String typed, List<Completion> completions) {
        String prefix = typed.replaceAll("^(import|from|use|using|#include[<\"]?)\\s*", "").trim();
        if (prefix.length() < 2) return;
        
        for(String pkg : PackageRegistry.getKnown(ext)) {
            if (pkg.startsWith(prefix)) {
                completions.add(new org.fife.ui.autocomplete.BasicCompletion(this, pkg));
            }
        }
    }
    
}
