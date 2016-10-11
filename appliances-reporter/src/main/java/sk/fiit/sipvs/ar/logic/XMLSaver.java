package sk.fiit.sipvs.ar.logic;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import sk.fiit.sipvs.ar.report.*;

/**
 * Saves data from Swing form into XML file
 *
 */
public class XMLSaver implements Runnable {

	private ApplianceReport report;
	private String file;

	public XMLSaver(ApplianceReport report, String file) {
		this.report = report;
		this.file = file;
	}

	public void run() {
		System.out.println("Saving to XML...");
		saveActualDate(this.report);

		try {
			saveToXMLFile(this.report, file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		System.out.println(file + " created.");
	}
	
	/**
	 * Save ApplianceReport object into well-formed XML file
	 * 
	 * @param report
	 * @param filename
	 * @throws JAXBException
	 */
	private void saveToXMLFile(ApplianceReport report, String filename) throws JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance("sk.fiit.sipvs.ar.report");
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		File file = new File(filename);
		
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(report, file);
	}
	
	/**
	 * Add actual date into ApplianceReport object
	 * 
	 * @param report
	 */
	private void saveActualDate(ApplianceReport report) {
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		
		XMLGregorianCalendar date = null;
		try {
			date = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		report.setDate(date);
	}
}
