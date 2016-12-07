package sk.fiit.sipvs.sv.utils;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

public class Converter {

	public static String fromElementToString(Element element) throws TransformerException {

		StreamResult result = new StreamResult(new StringWriter());
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(element), result);
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		
		return result.getWriter().toString();

	}
}
