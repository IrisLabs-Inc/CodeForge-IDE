/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.adonis.Nukepad;


import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.*;
import javax.swing.SwingWorker;
import java.util.*;

class ImportValidator extends AbstractParser {
      private final RSyntaxTextArea editor;
    private String ext = "";

    public ImportValidator(RSyntaxTextArea editor) {
        this.editor = editor;
    }

    public void setExt(String ext) {
        this.ext = ext == null ? "" : ext.toLowerCase();
    }

    @Override
    public ParseResult parse(org.fife.ui.rsyntaxtextarea.RSyntaxDocument doc, String style) {
        DefaultParseResult result = new DefaultParseResult(this);
        if (ext.isBlank()) return result;

        String source = editor.getText();
        List<ImportParser.ImportEntry> imports = ext.equals("python")
                ? ImportParser.parsePython(source)
                : ImportParser.parse(ext, source);

        for (ImportParser.ImportEntry entry : imports) {
            boolean known = PackageRegistry.isKnown(ext, entry.token());

            if (!known) {
        
                DefaultParserNotice notice = new DefaultParserNotice(
                        this,
                        "Unresolved import: '" + entry.token() + "' — not found in stdlib or known packages",
                        entry.line() - 1);
                notice.setLevel(ParserNotice.Level.WARNING);
                result.addNotice(notice);

          
                final String token = entry.token();
                final int    lineN = entry.line();
                new SwingWorker<List<String>, Void>() {
                    @Override
                    protected List<String> doInBackground() {
                        return PackageRegistry.queryRegistry(ext, token);
                    }
                    @Override
                    protected void done() {
                        try {
                            List<String> hits = get();
                            if (!hits.isEmpty()) {
                               
                                PackageRegistry.getKnown(ext).add(token);
                                editor.forceReparsing(ImportValidator.this);
                            }
                        } catch (Exception ignored) {}
                    }
                }.execute();
            }
        }
        return result;
    }
}
