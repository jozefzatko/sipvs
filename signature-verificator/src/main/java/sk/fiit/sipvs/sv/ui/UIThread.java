package sk.fiit.sipvs.sv.ui;

/**
* Wraps UI into Thread
*/
public class UIThread implements Runnable {

	private UI uiFrame;
	
	public UIThread(UI uiFrame) {
		
		this.uiFrame = uiFrame;
	}
	
	public void run() {
		
		try {
			
			uiFrame.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}