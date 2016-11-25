package sk.fiit.sipvs.sv.verify.verifications;

import org.w3c.dom.Document;

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
		
		return true;
	}
}
