package sk.fiit.sipvs.ar.logic;

/**
 * Transform XML data file into HTML file using XSLT
 * 
 */

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;

public class XMLTransformer implements Runnable {

	final static Logger logger = Logger.getLogger(XMLTransformer.class);
	
	private String xsltFile;
	private String xmlFile;
	
	public XMLTransformer(String xsltFile, String xmlFile) {
		
		this.xsltFile = xsltFile;
		this.xmlFile = xmlFile;
	}
	
	public void run() {
		File theDir = new File("html_folder");
		File report = null;
		String directoryName = new String();

		if (!theDir.exists()) {
			logger.info("creating directory: " + directoryName);
			boolean result = false;
			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				logger.error("SecurityException");
				logger.error(se.getLocalizedMessage());
				se.printStackTrace();
			}
			if (result) {
				logger.info("DIR created");
			}
		}
			
		try {
		TransformerFactory factory = TransformerFactory.newInstance();
		Source xslt = new StreamSource(new File(this.xsltFile));
		Transformer transformer = factory.newTransformer(xslt);

		Source text = new StreamSource(new File(this.xmlFile));
			
		report = new File("html_folder/result.html");
		if (report == null)
		{
			throw new FileNotFoundException("Html subor nebol vytvoreny");
		}
		transformer.transform(text, new StreamResult(report));

		} catch (Exception ex) {
			logger.error("Transforming error.");
			logger.error(ex.getLocalizedMessage());
			ex.printStackTrace();
		}

		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(report.toURI());
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}
}
