package sk.fiit.sipvs.ar.logic;

/**
 * Validate XML data file against XSD schema
 *
 */
public class XMLValidator implements Runnable {

	//ak nenastane chyba pri validacii, vrat null
	private String validationErrors;

	public void run() {
		
		// TODO: Validate data in XML
		System.out.println("Validating XML...");
	}

	public String getValidationErrors() {
		return validationErrors;
	}
}
