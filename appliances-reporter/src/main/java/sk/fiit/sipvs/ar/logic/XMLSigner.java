package sk.fiit.sipvs.ar.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import sk.fiit.sipvs.ar.logic.sign.DSignerBridge;

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
		
		
		String xml = "";
		String xsd = "";
		String xslt = "";
		
		try {
			xml = readFile(xmlFile);
			xsd = readFile(xsdFile);
			xslt = readFile(xsltFile);
			
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		DSignerBridge signerBridge = new DSignerBridge();
		
		signerBridge.init();
		//signerBridge.addObject("", "Používané elektrospotrebiče", xml, xsd, xslt);
		//String signedXml = signerBridge.signXML();
	}
	
	private String readFile(String filePath) throws IOException {
		
		byte[] encoded = Files.readAllBytes(Paths.get(filePath));
		return new String(encoded, Charset.defaultCharset());
	}

}
