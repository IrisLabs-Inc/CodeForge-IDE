package com.adonis.Nukepad.toolchain;

import javax.swing.table.DefaultTableModel;

public class ProblemsManager {

    private final DefaultTableModel model;

    public ProblemsManager(DefaultTableModel model) {
        this.model = model;
    }

    public void clear() {
        model.setRowCount(0);
    }

    public void addError(String description, int line, String file) {
        model.addRow(new Object[]{"\u274C", description, line, file});
    }

    public void addWarning(String description, int line, String file) {
        model.addRow(new Object[]{"\u26A0\uFE0F", description, line, file});
    }

    public void addProblem(String icon, String description, String line, String file) {
        model.addRow(new Object[]{icon, description, line, file});
    }

    public DefaultTableModel getModel() {
        return model;
    }
}
