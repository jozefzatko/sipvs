package sk.fiit.sipvs.ar.ui;

import sk.fiit.sipvs.ar.report.ApplianceReport;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;


public class ApplianceTableModel extends AbstractTableModel {
    private ArrayList<ApplianceReport.Appliances.Appliance> appliances;
    private Object[] columns;

    public ApplianceTableModel(List<ApplianceReport.Appliances.Appliance> users, Object[] columns) {

        this.appliances = new ArrayList<>(users);
        this.columns = columns;
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
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }

    public void addRow(ApplianceReport.Appliances.Appliance appliance){
        appliances.add(appliance);
    }
}
