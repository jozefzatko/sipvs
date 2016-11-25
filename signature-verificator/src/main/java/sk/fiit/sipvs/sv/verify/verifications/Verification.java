package sk.fiit.sipvs.sv.verify.verifications;

import org.w3c.dom.Document;

/**
 * Abstract Verification class
 * 
 * Includes common verification functions
 */
public abstract class Verification {

	protected Document document;
	
	public Verification(Document document) {
		
		this.document = document;
	}
}
