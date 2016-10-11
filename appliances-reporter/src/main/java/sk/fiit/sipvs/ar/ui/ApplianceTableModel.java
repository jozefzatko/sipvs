package sk.fiit.sipvs.ar.ui;

import sk.fiit.sipvs.ar.report.ApplianceReport;
import sk.fiit.sipvs.ar.report.ObjectFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;


public class ApplianceTableModel extends AbstractTableModel {
    private String[] columns = new String[]{"Typ", "Názov", "Sériové číslo", "Rok"};
    private ArrayList<ApplianceReport.Appliances.Appliance> appliances;

    public ApplianceTableModel(List<ApplianceReport.Appliances.Appliance> appliances) {

        this.appliances = new ArrayList<>(appliances);
    }

    public ArrayList<ApplianceReport.Appliances.Appliance> getAppliances() {
        return appliances;
    }

    @Override
    public int getRowCount() {
        return appliances.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    public String getColumnName(int c) {
        return columns[c];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case 0: return this.appliances.get(rowIndex).getType();
            case 1: return this.appliances.get(rowIndex).getName();
            case 2: return this.appliances.get(rowIndex).getSerialNumber();
            case 3: return this.appliances.get(rowIndex).getYear();
        }
        return null;
    }

    public void addRow(ApplianceReport.Appliances.Appliance appliance){
        this.appliances.add(appliance);
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
