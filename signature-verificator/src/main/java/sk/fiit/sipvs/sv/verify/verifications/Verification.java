package sk.fiit.sipvs.sv.verify.verifications;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sk.fiit.sipvs.sv.utils.CertificateFinder;
import sk.fiit.sipvs.sv.utils.ElementFinder;

/**
 * Abstract Verification class
 * 
 * Includes common verification functions
 */
public abstract class Verification {

	protected Document document;
	
	protected ElementFinder elementFinder;
	protected CertificateFinder certFinder;
	
	public Verification(Document document) {
		
		this.document = document;
		
		this.elementFinder = new ElementFinder(document);
		this.certFinder = new CertificateFinder(document);
		
		org.apache.xml.security.Init.init();
	}
	
	protected boolean assertElementAttributeValue(Element element, String attribute, String expectedValue) {
		
		String actualValue = element.getAttribute(attribute);
		
		if (actualValue != null && actualValue.equals(expectedValue)) {
			
			return true;
			
		}
		return false;
	}
	
	protected boolean assertElementAttributeValue(Element element, String attribute, List<String> expectedValues) {
		
		for (String expectedValue : expectedValues) {
			
			if (assertElementAttributeValue(element, attribute, expectedValue)) {
				
				return true;
			}
		}
		return false;
	}
	
	protected boolean assertElementAttributeValue(Element element, String attribute) {
		
		String actualValue = element.getAttribute(attribute);
		
		if (!actualValue.isEmpty()) {
			
			return true;
			
		}
		return false;
	}
	
}
