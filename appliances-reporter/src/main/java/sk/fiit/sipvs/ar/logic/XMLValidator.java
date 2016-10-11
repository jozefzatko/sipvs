package sk.fiit.sipvs.ar.logic;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Validate XML data file against XSD schema
 * 
 * @author Jozef Zaťko
 */
public class XMLValidator implements Runnable {

	final static Logger logger = Logger.getLogger(XMLValidator.class);
	
	private String xsdFile;
	private String xmlFile;
	
	private String validationErrors;

	public XMLValidator(String xsdFile, String xmlFile) {
		
		this.xsdFile = xsdFile;
		this.xmlFile = xmlFile;
	}
	
	public void run() {
		
		logger.info("Validating XML...");
		
		this.validationErrors = null;
		
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	    Schema schema = null;
		try {
			schema = factory.newSchema(new StreamSource(this.xsdFile));
		} catch (SAXException e) {
			logger.error(e.getLocalizedMessage());
			this.validationErrors = e.getLocalizedMessage();
			e.printStackTrace();
			return;
		}
		
	    Validator validator = schema.newValidator();
	    try {
			validator.validate(new StreamSource(this.xmlFile));
	    } catch (IOException e) {
	    	logger.error(e.getLocalizedMessage());
	    	this.validationErrors = e.getLocalizedMessage();
	    	e.printStackTrace();
		} catch (SAXException e) {
			logger.error("XML file does not match schema");
			logger.error(e.getLocalizedMessage());
			this.validationErrors = e.getLocalizedMessage();
			e.printStackTrace();
		}
	    
	    logger.info("XML is valid and match schema.");
	}

	public String getValidationErrors() {
		return validationErrors;
	}
}
