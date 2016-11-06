package sk.fiit.sipvs.ar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

import sk.fiit.sipvs.ar.logic.XMLSaver;
import sk.fiit.sipvs.ar.logic.XMLSigner;
import sk.fiit.sipvs.ar.logic.XMLTransformer;
import sk.fiit.sipvs.ar.logic.XMLValidator;
import sk.fiit.sipvs.ar.report.ApplianceReport;
import sk.fiit.sipvs.ar.report.ObjectFactory;
import sk.fiit.sipvs.ar.ui.UI;
import sk.fiit.sipvs.ar.ui.UIThread;

import javax.swing.*;

/**
 * Runs GUI thread
 * Handles GUI listeners
 * 
 * @author Jozef Zaťko
 *
 */
public class Controller {

	private static final String XML_FILE = "src//main//resources//file.xml";
	private static final String XSD_SCHEMA = "src//main//resources//appliances.xsd";
	private static final String XSLT_FILE = "src//main//resources//transformation.xslt";
	
	private UI uiFrame;
	private UIThread uiThread;
	
	public Controller() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		uiFrame = new UI();
		uiFrame.pack();
		uiFrame.setBounds(100,100,450,600);

		uiFrame.getBtnAddAppliance().addActionListener(new AddApplianceListener());
		uiFrame.getBtnSave().addActionListener(new SaveListener());
		uiFrame.getBtnValidate().addActionListener(new ValidateListener());
		uiFrame.getBtnTransform().addActionListener(new TransformListener());
		uiFrame.getBtnSign().addActionListener(new SignListener());
		
		uiThread = new UIThread(uiFrame);
		new Thread(uiThread).start();
	}
	
	/*
	 * ULOZ button listener
	 */
	class SaveListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			if (uiFrame.areAllFieldsFilled()) {
				ApplianceReport report = new ObjectFactory().createApplianceReport();
				report.setReportId(UUID.randomUUID().toString());
				report.setReportLang("SK");
				report.setAccommodatedInfo(fillAccomodatedInfo());
				report.setAppliances(fillAppliances());
				report.setRoomInfo(fillRoomInfo());
				report.setPlace(uiFrame.getTfFilledIn());

				XMLSaver saver = new XMLSaver(report, XML_FILE);
				new Thread(saver).start();
			}
		}

		private ApplianceReport.AccommodatedInfo fillAccomodatedInfo() {
			ApplianceReport.AccommodatedInfo acInfo = new ObjectFactory().createApplianceReportAccommodatedInfo();
			acInfo.setFirstName(uiFrame.getTfName());
			acInfo.setFamilyName(uiFrame.getTfSurname());
			acInfo.setBirthDate(uiFrame.getBirthday());
			acInfo.setFaculty(uiFrame.getCbFaculty());

			return acInfo;
		}

		private ApplianceReport.Appliances fillAppliances() {

			ApplianceReport.Appliances appliances = new ApplianceReport.Appliances();
			appliances.getAppliance().addAll(uiFrame.getListOfAppliances());

			return appliances;
		}

		private ApplianceReport.RoomInfo fillRoomInfo() {
			ApplianceReport.RoomInfo roomInfo = new ObjectFactory().createApplianceReportRoomInfo();
			roomInfo.setBlock(uiFrame.getCbBloc());
			roomInfo.setRoomNumber(uiFrame.getTfRoom());

			return roomInfo;
		}

	}
	
	/*
	 * VALIDUJ button listener
	 */
	class ValidateListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			
			XMLValidator validator = new XMLValidator(XSD_SCHEMA, XML_FILE);
			Thread validating = new Thread(validator);
			validating.start();

			try {
				validating.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			uiFrame.showValidationErrors(validator.getValidationErrors());
		}
	}
	
	/*
	 * TRANSFORMUJ button listener
	 */
	class TransformListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			XMLTransformer transformer = new XMLTransformer(XSLT_FILE, XML_FILE);
			new Thread(transformer).start();
		}
	}
	
	/*
	 * PODPIS button listener
	 */
	class SignListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			XMLSigner signer = new XMLSigner(XML_FILE, XSD_SCHEMA, XSLT_FILE);
			new Thread(signer).start();
		}
	}

	class AddApplianceListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			uiFrame.addAppliance();
		}
	}
}
