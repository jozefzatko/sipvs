package sk.fiit.sipvs.sv.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ElementFinder {

	private Document document;
	
	public ElementFinder(Document document) {
		
		this.document = document;
	}
	
	public Element findByAttributeValue(String elementType, String attributeName, String attributeValue) {
		
		NodeList elements = this.document.getElementsByTagName(elementType);
		
		for (int i=0; i<elements.getLength(); i++) {
			
			Element element = (Element) elements.item(i);
			
			if (element.hasAttribute(attributeName) && element.getAttribute(attributeName).equals(attributeValue)) {
				
				return element;
			}
		}
		
		return null;
	}
}
