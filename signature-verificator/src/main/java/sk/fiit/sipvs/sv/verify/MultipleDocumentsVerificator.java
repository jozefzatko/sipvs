package sk.fiit.sipvs.sv.verify;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sk.fiit.sipvs.sv.ui.UI;

/**
 * Open, parse and then validate ArrayList of documents
 * Verify if each document is XadesT Advanced Electronic Signature
 */
public class MultipleDocumentsVerificator {

	private static final Logger logger = Logger.getLogger(MultipleDocumentsVerificator.class);
	
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
	private static final String UTF8_BOM = "\uFEFF";

	
	public void verifyDocuments(ArrayList<String> documents, UI uiFrame) {

		for (String documentPath : documents) {
			
			String documentContent = null;
			try {
				documentContent = readFile(documentPath);
				documentContent = removeUTF8BOM(documentContent);
				documentContent = addXMLHeader(documentContent);
	
			} catch (IOException e) {
				logger.error("Cannot open or read " + documentPath, e);
				continue;
			}
			
			Document document = null;
			try {
				document = convertToDocument(documentContent);
				
			} catch (ParserConfigurationException | IOException | SAXException e) {
				logger.error("Cannot parse " + documentPath + " content into org.w3c.dom.Document", e);
				continue;
			}

			try {
				new DocumentVerificator(document).verify();
				
			} catch (DocumentVerificationException e) {
				logger.error("Document " + documentPath + " is not valid \"XADES-T\" Advanced Electronic Signature", e);
				uiFrame.appendToTextArea("Document " + documentPath + " is not valid \"XADES-T\" Advanced Electronic Signature\n");
				uiFrame.appendToTextArea(e.getMessage() + "\n\n");
				continue;
			}
			uiFrame.appendToTextArea("Document " + documentPath + " is valid \"XADES-T\" Advanced Electronic Signature\n");
			logger.info("Document " + documentPath + " is valid \"XADES-T\" Advanced Electronic Signature");
		}
	}
	
	private String readFile(String filePath) throws IOException {
		
		byte[] encoded = Files.readAllBytes(Paths.get(filePath));
		return new String(encoded, Charset.defaultCharset());
	}
	
	private String removeUTF8BOM(String s) {
	
		if (s.startsWith(UTF8_BOM)) {
	        s = s.substring(1);
	    }
	    return s;
	}
	
	private String addXMLHeader(String s) {
		
		if (s.startsWith("<?xml") == false) {	
			s = XML_HEADER + s;
		}
		return s;
	}
	
	private Document convertToDocument(String s) throws ParserConfigurationException, IOException, SAXException {
		
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		documentFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(s));
		
		return documentBuilder.parse(source);
	}

}
