package sk.fiit.sipvs.ar.logic;

/**
 * Transform XML data file into HTML file using XSLT
 * 
 */

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;

public class XMLTransformer implements Runnable {

	private String xsltFile;
	private String xmlFile;
	
	public XMLTransformer(String xsltFile, String xmlFile) {
		
		this.xsltFile = xsltFile;
		this.xmlFile = xmlFile;
	}
	
	public void run() {
		File theDir = new File("html_folder");
		File report = null;
		try {
			String directoryName = new String();

			if (!theDir.exists()) {
				System.out.println("creating directory: " + directoryName);
				boolean result = false;

				try {
					theDir.mkdir();
					result = true;
				} catch (SecurityException se) {
					System.out.println("SecurityException");
				}
				if (result) {
					System.out.println("DIR created");
				}
			}

			TransformerFactory factory = TransformerFactory.newInstance();
			Source xslt = new StreamSource(new File(this.xsltFile));
			Transformer transformer = factory.newTransformer(xslt);

			Source text = new StreamSource(new File(this.xmlFile));
			
			report = new File("html_folder/result.html");
			if (report == null){
				throw new FileNotFoundException("Html subor nebol vytvoreny");
				}
			transformer.transform(text, new StreamResult(report));

		} catch (Exception ex) {
			System.out.println("Transforming error.");
		}

		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(report.toURI());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
