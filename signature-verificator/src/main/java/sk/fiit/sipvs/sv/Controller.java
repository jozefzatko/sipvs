package sk.fiit.sipvs.sv;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import sk.fiit.sipvs.sv.ui.UI;
import sk.fiit.sipvs.sv.ui.UIThread;
import sk.fiit.sipvs.sv.verify.MultipleDocumentsVerificator;

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
			
			ArrayList<String> documents = new ArrayList<String>();
			
			documents.add("documents//01XadesT.xml");
			documents.add("documents//02XadesT.xml");
			documents.add("documents//03XadesT.xml");
			documents.add("documents//04XadesT.xml");
			documents.add("documents//05XadesT.xml");
			documents.add("documents//06XadesT.xml");
			documents.add("documents//07XadesT.xml");
			documents.add("documents//08XadesT.xml");
			documents.add("documents//09XadesT.xml");
			documents.add("documents//10XadesT.xml");
			documents.add("documents//11XadesT.xml");
			documents.add("documents//12XadesT.xml");
			documents.add("documents//XadesT.xml");
			
			new MultipleDocumentsVerificator().verifyDocuments(documents);
		}
	}
}
