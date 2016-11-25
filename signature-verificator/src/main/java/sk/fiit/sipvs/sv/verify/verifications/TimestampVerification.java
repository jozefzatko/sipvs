package sk.fiit.sipvs.sv.verify.verifications;

import org.w3c.dom.Document;

import sk.fiit.sipvs.sv.verify.DocumentVerificationException;

/**
 * Timestamp related document verifications
 */
public class TimestampVerification extends Verification {

	public TimestampVerification(Document document) {
		super(document);
	}

	/*
	 * Overenie platnosti podpisového certifikátu časovej pečiatky voči času UtcNow
	 * a voči platnému poslednému CRL
	 */
	public boolean verifyTimestampCerfificate() throws DocumentVerificationException {
		
		return true;
	}
	
	/*
	 * Overenie MessageImprint z časovej pečiatky voči podpisu ds:SignatureValue
	 */
	public boolean verifyMessageImprint() throws DocumentVerificationException {
		
		return true;
	}
}
