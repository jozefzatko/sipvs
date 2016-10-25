package sk.fiit.sipvs.ar.logic;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

/**
 * Sign XML document using DSig application
 * 
 * @author Jozef Zaťko
 */
public class XMLSigner implements Runnable {

	final static Logger logger = Logger.getLogger(XMLSigner.class);
	
	private String xmlFile;
	private String xsdFile;
	private String xsltFile;
	
	public XMLSigner(String xmlFile, String xsdFile, String xsltFile) {
		
		this.xmlFile = xmlFile;
		this.xsdFile = xsdFile;
		this.xsltFile = xsltFile;
	}
	
	public void run() {
		
		
		String xmlFileAbsolutePath;
		String xsdFileAbsolutePath;
		String xsltFileAbsolutePath;
		
		try {
			xmlFileAbsolutePath = getAbsolutePath(xmlFile);
			xsdFileAbsolutePath = getAbsolutePath(xsdFile);
			xsltFileAbsolutePath = getAbsolutePath(xsltFile);
			
		} catch (FileNotFoundException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		// TODO: sign document
	}
	
	public String getAbsolutePath(String relFilePath) throws FileNotFoundException {
		
		File file = new File(relFilePath);
		if(!file.exists()) {
			
			throw new FileNotFoundException("File " + relFilePath + " does not exist.");
		}
		
		return file.getAbsolutePath();
	}

}
