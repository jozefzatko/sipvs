package sk.fiit.sipvs.ar.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import sk.fiit.sipvs.ar.report.ApplianceReport;
import sk.fiit.sipvs.ar.report.BlockType;
import sk.fiit.sipvs.ar.report.FacultyType;
import sk.fiit.sipvs.ar.report.ObjectFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.awt.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class UI extends JFrame {

	private static final long serialVersionUID = -8337220584677354655L;

	private JTextField tfName;
	private JTextField tfSurname;
	private JDatePickerImpl datePicker;
	private JComboBox cbFaculty;
	private JComboBox cbBloc;
	private JTextField tfRoom;
	private JTextField tfTypeOfAppliance;
	private JTextField tfNameOfAppliance;
	private JTextField tfSerialNo;
	private JTextField tfYearOfProduction;
	private JButton btnAddAppliance;
	private JTable listOfAppliances;
	private ApplianceTableModel tableModel;
	private JTextField tfFilledIn;
	private JButton btnSave;
	private JButton btnValidate;
	private JButton btnTransform;

	/**
	 * Create the frame.
	 */
	public UI() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 600);

		final JPanel panel1 = new JPanel();
		panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panel1);
		panel1.setLayout(new GridLayoutManager(19, 3, new Insets(0, 0, 0, 0), -1, -1));

		final JLabel label = new JLabel();
		label.setText("Zoznam používaných elektrospotrebičov");
		Font font = new Font("Tahoma", Font.BOLD, 13);
		label.setFont(font);
		panel1.add(label, new GridConstraints(0, 0, 1, 3, GridConstraints.ALIGN_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

		final JLabel label1 = new JLabel();
		label1.setText("Údaje ubytovaného");
		panel1.add(label1, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Meno");
		panel1.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		tfName = new JTextField();
		tfName.setText("");
		panel1.add(tfName, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText("Priezvisko");
		panel1.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		tfSurname = new JTextField();
		panel1.add(tfSurname, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("Dátum narodenia");
		panel1.add(label4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

		UtilDateModel model = new UtilDateModel();
		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		datePicker = new JDatePickerImpl(datePanel);

		panel1.add(datePicker, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

		final JLabel label5 = new JLabel();
		label5.setText("Fakulta");
		panel1.add(label5, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		cbFaculty = new JComboBox(FacultyType.values());
		panel1.add(cbFaculty, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label6 = new JLabel();
		label6.setText("Blok");
		panel1.add(label6, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel3, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		cbBloc = new JComboBox(BlockType.values());
		panel3.add(cbBloc, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label7 = new JLabel();
		label7.setText("Číslo izby");
		panel3.add(label7, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		tfRoom = new JTextField();
		panel3.add(tfRoom, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		panel3.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		panel1.add(spacer2, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final Spacer spacer3 = new Spacer();
		panel1.add(spacer3, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JLabel label8 = new JLabel();
		label8.setText("Údaje o spotrebiči");
		panel1.add(label8, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label9 = new JLabel();
		label9.setText("Typ");
		panel1.add(label9, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		this.tfTypeOfAppliance = new JTextField();
		panel1.add(this.tfTypeOfAppliance, new GridConstraints(9, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label10 = new JLabel();
		label10.setText("Názov");
		panel1.add(label10, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		this.tfNameOfAppliance = new JTextField();
		panel1.add(this.tfNameOfAppliance, new GridConstraints(10, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label11 = new JLabel();
		label11.setText("Sériové číslo");
		panel1.add(label11, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		this.tfSerialNo = new JTextField();
		panel1.add(this.tfSerialNo, new GridConstraints(11, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label12 = new JLabel();
		label12.setText("Rok výroby");
		panel1.add(label12, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		this.tfYearOfProduction = new JTextField();
		panel1.add(this.tfYearOfProduction, new GridConstraints(12, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final Spacer spacer4 = new Spacer();
		panel1.add(spacer4, new GridConstraints(13, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		btnAddAppliance = new JButton();
		btnAddAppliance.setText("Pridaj");
		panel1.add(btnAddAppliance, new GridConstraints(13, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel4, new GridConstraints(14, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

		ArrayList<ApplianceReport.Appliances.Appliance> tableData = new ArrayList<>();
		tableModel = new ApplianceTableModel(tableData);
		listOfAppliances = new JTable(tableModel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(listOfAppliances);

		panel4.add(scrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 80), null, 0, false));
		final Spacer spacer5 = new Spacer();
		panel1.add(spacer5, new GridConstraints(15, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JLabel label13 = new JLabel();
		label13.setText("Vyplnené v");
		panel1.add(label13, new GridConstraints(16, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		tfFilledIn = new JTextField();
		panel1.add(tfFilledIn, new GridConstraints(16, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final Spacer spacer6 = new Spacer();
		panel1.add(spacer6, new GridConstraints(17, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel5, new GridConstraints(18, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		btnSave = new JButton();
		btnSave.setText("Ulož");
		panel5.add(btnSave, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnValidate = new JButton();
		btnValidate.setText("Validuj");
		panel5.add(btnValidate, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnTransform = new JButton();
		btnTransform.setText("Transformuj");
		panel5.add(btnTransform, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}


	public String getTfName() {
		return this.tfName.getText();
	}

	public String getTfSurname() {
		return this.tfSurname.getText();
	}

	public XMLGregorianCalendar getBirthday() {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime((java.util.Date) this.datePicker.getModel().getValue());
		XMLGregorianCalendar dateXML = null;
		try {
			dateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}

		return dateXML;
	}

	public FacultyType getCbFaculty() {
		if (this.cbFaculty.getSelectedItem() != null) {
			return (FacultyType) this.cbFaculty.getSelectedItem();
		} else return null;
	}

	public BlockType getCbBloc() {
		if (this.cbBloc.getSelectedItem() != null) {
			return (BlockType) this.cbBloc.getSelectedItem();
		} else return null;
	}

	public int getTfRoom() {
		if (!this.tfRoom.getText().isEmpty()){
			try {
				return Integer.parseInt(this.tfRoom.getText());
			} catch (NumberFormatException e){
				return -1;
			}
		} else return -2;
	}

	public String getTfTypeOfAppliance() {
		return this.tfTypeOfAppliance.getText();
	}

	public String getTfNameOfAppliance() {
		return this.tfNameOfAppliance.getText();
	}

	public String getTfSerialNo() {
		return this.tfSerialNo.getText();
	}

	public int getTfYearOfProduction() {
		if (!this.tfYearOfProduction.getText().isEmpty()){
			try {
				return Integer.parseInt(this.tfYearOfProduction.getText());
			} catch (NumberFormatException e){
				return -1;
			}
		}
		else return -2;
	}

	public JButton getBtnAddAppliance() {
		return this.btnAddAppliance;
	}

	public ArrayList<ApplianceReport.Appliances.Appliance> getListOfAppliances() {
		return this.tableModel.getAppliances();
	}

	public String getTfFilledIn() {
		return this.tfFilledIn.getText();
	}

	public JButton getBtnSave() {

		return this.btnSave;
	}

	public JButton getBtnValidate() {

		return this.btnValidate;
	}

	public JButton getBtnTransform() {

		return this.btnTransform;
	}

	public void addAppliance(){

		if (!getTfTypeOfAppliance().equals("") &
			!getTfNameOfAppliance().equals("") &
			!getTfSerialNo().equals("") &
				(this.tfYearOfProduction.getText().equals("") ||
					(getTfYearOfProduction() > 1900 &&
					getTfYearOfProduction() <= 2099))) {

			ApplianceReport.Appliances.Appliance appliance = new ObjectFactory().createApplianceReportAppliancesAppliance();
			appliance.setType(getTfTypeOfAppliance());
			appliance.setName(getTfNameOfAppliance());
			appliance.setSerialNumber(getTfSerialNo());
			appliance.setYear(getTfYearOfProduction());

			this.tableModel.addRow(appliance);
		}
		else {
			JOptionPane.showMessageDialog(null, "Vyplňte všetky hodnoty alebo zadajte správny rok");
		}
	}

	public boolean areAllFieldsFilled() {

		if (getTfName().equals("") || getTfSurname().equals("") || getCbFaculty().value() == null ||
				getCbBloc().value() == null || getTfRoom() < -1 || getTfFilledIn().equals("") ||
				getBirthday() == null || getListOfAppliances().size() == 0) {
			JOptionPane.showMessageDialog(null, "Vyplňte všetky hodnoty");
			return false;
		} else if (getTfRoom() < 11 || getTfRoom() > 99) {
			JOptionPane.showMessageDialog(null, "Zadajte platné číslo izby");
			return false;
		}

		return true;
	}

	public void showValidationErrors(String validationErrors) {
		if (validationErrors != null) {
			JTextArea ta = new JTextArea(30, 50);
			ta.setText(validationErrors);
			JOptionPane.showMessageDialog(null, new JScrollPane(ta), "Chyby vo validácii", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "XML súbor validný", "Validácia", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
