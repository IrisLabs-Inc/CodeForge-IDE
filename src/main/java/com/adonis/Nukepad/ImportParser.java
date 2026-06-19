/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.adonis.Nukepad;

import java.util.*;
import java.util.regex.*;

class ImportParser {
    record ImportEntry(String token, int line) {}
    
    public static List<ImportEntry> parse(String ext, String source) {
         List<ImportEntry> entries = new ArrayList<>();
         String[] lines = source.split("\n", -1);
         
         Pattern pat = patternFor(ext);
         if(pat == null) return entries;
         
         for(int i = 0; i < lines.length; i++) {
             Matcher m = pat.matcher(lines[i]);
             if(m.find()) {
                 entries.add(new ImportEntry(m.group(1).trim(), i + 1));
             }
         }
         return entries;
    }
    private static Pattern patternFor(String ext) {
                return switch (ext) {
            case "java" -> Pattern.compile("^\\s*import\\s+([\\w.]+)");
            case "python" -> Pattern.compile(
                    "^\\s*(?:import\\s+([\\w.]+)|from\\s+([\\w.]+)\\s+import)");
            case "js","ts","tsx","jsx" -> Pattern.compile(
                    "^\\s*(?:import\\s.*?from\\s+['\"]([\\w@/.\\-]+)['\"]" +
                    "|require\\s*\\(\\s*['\"]([\\w@/.\\-]+)['\"]\\s*\\))");
            case "cpp","c" -> Pattern.compile(
                    "^\\s*#include\\s+[<\"]([\\w./]+)[>\"]");
            case "go"  -> Pattern.compile("^\\s*\"([\\w./]+)\"");
            case "rs"  -> Pattern.compile("^\\s*use\\s+([\\w:]+)");
            case "cs"  -> Pattern.compile("^\\s*using\\s+([\\w.]+)");
            case "php" -> Pattern.compile(
                    "^\\s*(?:use|require|include)\\s+['\"]?([\\w\\\\/.]+)['\"]?");
            default    -> null;
        };
    }
      
    public static List<ImportEntry> parsePython(String source) {
        List<ImportEntry> entries = new ArrayList<>();
        String[] lines = source.split("\n", -1);
        Pattern p1 = Pattern.compile("^\\s*import\\s+([\\w.]+)");
        Pattern p2 = Pattern.compile("^\\s*from\\s+([\\w.]+)\\s+import");
        for (int i = 0; i < lines.length; i++) {
            Matcher m1 = p1.matcher(lines[i]);
            Matcher m2 = p2.matcher(lines[i]);
            if (m1.find()) entries.add(new ImportEntry(m1.group(1).trim(), i + 1));
            else if (m2.find()) entries.add(new ImportEntry(m2.group(1).trim(), i + 1));
        }
        return entries;
    }
    
}
