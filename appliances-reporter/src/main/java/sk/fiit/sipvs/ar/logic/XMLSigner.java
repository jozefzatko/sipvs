package sk.fiit.sipvs.ar.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sk.fiit.sipvs.ar.logic.sign.DSignerBridge;
import sk.fiit.sipvs.ar.logic.sign.SignException;
import sk.fiit.sipvs.ar.logic.sign.TSAConnector;

/**
 * Sign XML document using DSig application
 * 
 * @author Jozef Zaťko
 */
public class XMLSigner implements Runnable {

	final static Logger logger = Logger.getLogger(XMLSigner.class);
	
	private static final String SIGNED_FILE_PATH = "src//main//resources//signed_document.xml";
	
	private static final String DEFAULT_XSD_REF = "http://www.w3.org/2001/XMLSchema";
	private static final String DEFAULT_XSLT_REF = "http://www.w3.org/1999/XSL/Transform";
	
	private String xmlFile;
	private String xsdFile;
	private String xsltFile;
	
	public XMLSigner(String xmlFile, String xsdFile, String xsltFile) {
		
		this.xmlFile = xmlFile;
		this.xsdFile = xsdFile;
		this.xsltFile = xsltFile;
	}
	
	public void run() {
		
		String xml, xsd, xslt;
		
		try {
			xml = readFile(xmlFile);
			xsd = readFile(xsdFile);
			xslt = readFile(xsltFile);
			
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			return;
		}
		
		
		DSignerBridge signerBridge;
		try {
			signerBridge = new DSignerBridge();
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			return;
		}
			
		
		try {
			signerBridge.addXMLToDSigner("id1", "Zoznam používaných elektrospotrebičov", xml, xsd, "", DEFAULT_XSD_REF, xslt, DEFAULT_XSLT_REF);
	
		} catch (SignException e) {
			logger.error(e);
			logger.error(e.getLocalizedMessage());
			return;
		}
		
		
		String signedXml;
		try {
			signedXml = signerBridge.signXML("sign", "sha1", "urn:oid:1.3.158.36061701.1.2.1");
			signedXml = signXMLwithTimestamp(signedXml);
		} catch (SignException e) {
			logger.error(e);
			logger.error(e.getLocalizedMessage());
			return;
		}

		try {
			saveToFile(signedXml, SIGNED_FILE_PATH);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			return;
		}
	}
	
	
	/**
	 * Send created XML to DSigner application to sign and get timestamp from TSA
	 *
	 * @return signed XML with timestamp
	 * @throws SignException
	 */
	public String signXMLwithTimestamp(String signedXML) throws SignException {

		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			InputSource source = new InputSource(new StringReader(signedXML));
			Document document = documentBuilder.parse(source);

			Node qualifyingProperties = document.getElementsByTagName("xades:QualifyingProperties").item(0);

			if (qualifyingProperties == null) {
				logger.error("nenajdeny xades:QualifyingProperties element");
				return null;
			}

			Element unsignedProperties = document.createElement("xades:UnsignedProperties");
			Element unsignedSignatureProperties = document.createElement("xades:UnsignedSignatureProperties");
			Element signatureTimestamp = document.createElement("xades:SignatureTimeStamp");
			Element encapsulatedTimeStamp = document.createElement("xades:EncapsulatedTimeStamp");

			unsignedProperties.appendChild(unsignedSignatureProperties);
			unsignedSignatureProperties.appendChild(signatureTimestamp);
			signatureTimestamp.appendChild(encapsulatedTimeStamp);

			Node signatureValue = document.getElementsByTagName("ds:SignatureValue").item(0);

			if (signatureValue == null) {
				logger.error("nenajdeny ds:SignatureValue element");
				return null;
			}

			TSAConnector tsaConnector = new TSAConnector();
			String timestamp = tsaConnector.getTimeStampToken(signatureValue.getTextContent());

			Text timestampNode = document.createTextNode(timestamp);
			encapsulatedTimeStamp.appendChild(timestampNode);
			qualifyingProperties.appendChild(unsignedProperties);

			//vytvorenie konecneho XML
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(new DOMSource(document), result);

			return result.getWriter().toString();

		} catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	/**
	 * Return file content in String
	 */
	private String readFile(String filePath) throws IOException {
		
		byte[] encoded = Files.readAllBytes(Paths.get(filePath));
		return new String(encoded, Charset.defaultCharset());
	}
	
	
	/**
	 * Save String into file
	 */
	private void saveToFile(String strToSave, String filePath) throws IOException {
		
		File file = new File(filePath);
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(strToSave);
		fileWriter.flush();
		fileWriter.close();
	}
	
	public static void main(String args[]) {
		
		String xml = "src//main//resources//file.xml";
		String xsd = "src//main//resources//appliances.xsd";
		String xslt = "src//main//resources//transformation.xslt";
		
		new XMLSigner(xml, xsd, xslt).run();
	}

}
