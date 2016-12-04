package sk.fiit.sipvs.sv.verify.verifications;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sk.fiit.sipvs.sv.verify.DocumentVerificationException;

/**
 * Data envelope related document verifications
 */
public class EnvelopeVerification extends Verification {
	
	public EnvelopeVerification(Document document) {
		super(document);
	}

	/*
	 * Koreňový element musí obsahovať atribúty xmlns:xzep a xmlns:ds podľa profilu XADES_ZEP
	 */
	public boolean verifyRootElement() throws DocumentVerificationException {
		
		Element envelope = document.getDocumentElement();
		
		if (assertElementAttributeValue(envelope, "xmlns:xzep", "http://www.ditec.sk/ep/signature_formats/xades_zep/v1.0") == false) {
			
			throw new DocumentVerificationException(
					"Atribút xmlns:xzep koreňového elementu neobsahuje hodnotu http://www.ditec.sk/ep/signature_formats/xades_zep/v1.0");
		}
		
		if (assertElementAttributeValue(envelope, "xmlns:ds", "http://www.w3.org/2000/09/xmldsig#") == false) {
			
			throw new DocumentVerificationException(
					"Atribút xmlns:ds koreňového elementu neobsahuje hodnotu http://www.w3.org/2000/09/xmldsig#");
		}
		
		return true;
	}
}
