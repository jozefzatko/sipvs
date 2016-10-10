package sk.fiit.sipvs.ar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import sk.fiit.sipvs.ar.logic.XMLSaver;
import sk.fiit.sipvs.ar.logic.XMLTransformer;
import sk.fiit.sipvs.ar.logic.XMLValidator;
import sk.fiit.sipvs.ar.report.ApplianceReport;
import sk.fiit.sipvs.ar.report.ObjectFactory;
import sk.fiit.sipvs.ar.ui.UI;
import sk.fiit.sipvs.ar.ui.UIThread;

/**
 * Runs GUI thread
 * Handles GUI listeners
 * 
 * @author Jozef Zaťko
 *
 */
public class Controller {

	private UI uiFrame;
	private UIThread uiThread;
	
	public Controller() {
		
		uiFrame = new UI();

		uiFrame.getBtnAddAppliance().addActionListener(new AddApplianceListener());
		uiFrame.getBtnSave().addActionListener(new SaveListener());
		uiFrame.getBtnValidate().addActionListener(new ValidateListener());
		uiFrame.getBtnTransform().addActionListener(new TransformListener());
		
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
				report.setAccommodatedInfo(fillAccomodatedInfo());
				report.setAppliances(fillAppliances());
				report.setRoomInfo(fillRoomInfo());
				report.setPlace(uiFrame.getTfFilledIn());

				XMLSaver saver = new XMLSaver(report);
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
			
			XMLValidator validator = new XMLValidator();
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

			XMLTransformer transformer = new XMLTransformer();
			new Thread(transformer).start();
		}
	}

	class AddApplianceListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			uiFrame.addAppliance();
		}
	}
}
