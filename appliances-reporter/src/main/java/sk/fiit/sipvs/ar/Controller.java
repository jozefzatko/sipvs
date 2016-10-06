package sk.fiit.sipvs.ar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sk.fiit.sipvs.ar.logic.XMLSaver;
import sk.fiit.sipvs.ar.logic.XMLTransformer;
import sk.fiit.sipvs.ar.logic.XMLValidator;
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
			
			XMLSaver saver = new XMLSaver();
			new Thread(saver).start();
		}
	}
	
	/*
	 * VALIDUJ button listener
	 */
	class ValidateListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			
			XMLValidator validator = new XMLValidator();
			new Thread(validator).start();
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
}
