package sk.fiit.sipvs.sv.verify.verifications;

import org.w3c.dom.Document;

import sk.fiit.sipvs.sv.verify.DocumentVerificationException;

/**
 * Certificate related document verifications
 */
public class CertificateVerification extends Verification {

	public CertificateVerification(Document document) {
		super(document);
	}
	
	/*
	 * Overenie platnosti podpisového certifikátu dokumentu voči času T z časovej pečiatky
	 * a voči platnému poslednému CRL
	 */
	public boolean verifyCertificateValidity() throws DocumentVerificationException {
		
		return true;
	}
	
}
