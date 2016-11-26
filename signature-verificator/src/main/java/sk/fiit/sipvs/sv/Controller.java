package sk.fiit.sipvs.sv;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import sk.fiit.sipvs.sv.ui.UI;
import sk.fiit.sipvs.sv.ui.UIThread;
import sk.fiit.sipvs.sv.verify.MultipleDocumentsVerificator;

import javax.swing.*;

/**
 * Runs GUI thread
 * Handles GUI listeners
 */
public class Controller {

	private UI uiFrame;
	private UIThread uiThread;
	
	public Controller() {
		
		uiFrame = new UI();
		uiFrame.getBtnVerify().addActionListener(new VerifyListener());
		
		uiThread = new UIThread(uiFrame);
		new Thread(uiThread).start();
}
	
	/*
	 * VERIFY button listener
	 */
	class VerifyListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			String user_dir = System.getProperty("user.dir");
			File workingDirectory = new File(user_dir + "//documents");

			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(workingDirectory);
			chooser.setMultiSelectionEnabled(true);
			chooser.showOpenDialog(uiFrame);
			File[] files = chooser.getSelectedFiles();

			ArrayList<String> documents = new ArrayList<String>();
			for (File file : files) {
				documents.add(file.getAbsolutePath());
			}

			new MultipleDocumentsVerificator().verifyDocuments(documents, uiFrame);
		}
	}
}
