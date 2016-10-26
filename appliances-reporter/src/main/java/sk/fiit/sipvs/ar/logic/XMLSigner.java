package sk.fiit.sipvs.ar.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import com.jacob.com.ComThread;

import sk.fiit.sipvs.ar.logic.sign.DSignerBridge;
import sk.fiit.sipvs.ar.logic.sign.SignException;

/**
 * Sign XML document using DSig application
 * 
 * @author Jozef Zaťko
 */
public class XMLSigner implements Runnable {

	final static Logger logger = Logger.getLogger(XMLSigner.class);
	
	private static final String SIGNED_FILE_PATH = "src//main//resources//signed_document.xml";
	
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
			logger.error(e.getStackTrace());
			return;
		}
		
		ComThread.startMainSTA();
		ComThread.InitSTA();
		
		DSignerBridge signerBridge = new DSignerBridge();
		
		try {
			signerBridge.init();
		
		} catch (IOException e) {
			logger.error("Cannot initialize ActiveX component");
			logger.error(e.getLocalizedMessage());
			logger.error(e.getStackTrace());
			return;
		}
			
		try {
			signerBridge.addObject("id1", "Zoznam používaných elektrospotrebičov", xml, xsd, "", "http://www.w3.org/2001/XMLSchema", xslt, "http://www.w3.org/1999/XSL/Transform");
	
		} catch (SignException e) {
			logger.error(e);
			return;
		}
		
		String signedXml;
		try {
			signedXml = signerBridge.signXML("sign", "sha1", "urn:oid:1.3.158.36061701.1.2.1");
		} catch (SignException e) {
			logger.error(e);
			return;
		}
		
		ComThread.Release();
		ComThread.quitMainSTA();

		try {
			saveToFile(signedXml, SIGNED_FILE_PATH);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			logger.error(e.getStackTrace());
			return;
		}
	}
	
	/**
	 * Read file
	 * 
	 * @param filePath
	 * @return String of file content
	 * @throws IOException
	 */
	private String readFile(String filePath) throws IOException {
		
		byte[] encoded = Files.readAllBytes(Paths.get(filePath));
		return new String(encoded, Charset.defaultCharset());
	}
	
	/**
	 * Save String into file
	 * 
	 * @param strToSave
	 * @param filePath
	 * @throws IOException
	 */
	private void saveToFile(String strToSave, String filePath) throws IOException {
		
		File file = new File(filePath);
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(strToSave);
		fileWriter.flush();
		fileWriter.close();
	}

}
