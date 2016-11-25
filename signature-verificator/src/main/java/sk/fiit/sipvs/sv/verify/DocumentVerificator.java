package sk.fiit.sipvs.sv.verify;

import org.w3c.dom.Document;

import sk.fiit.sipvs.sv.verify.verifications.CertificateVerification;
import sk.fiit.sipvs.sv.verify.verifications.EnvelopeVerification;
import sk.fiit.sipvs.sv.verify.verifications.SignatureVerification;
import sk.fiit.sipvs.sv.verify.verifications.TimestampVerification;

/**
 * Verify if document is XadesT Advanced Electronic Signature
 */
public class DocumentVerificator {
	
	private EnvelopeVerification envelopeVerificator;
	private SignatureVerification xmlSignatureVerificator;
	private TimestampVerification timestampVerificator;
	private CertificateVerification certificateVerificator;
	
	public DocumentVerificator(Document document) {
		
		envelopeVerificator = new EnvelopeVerification(document);
		xmlSignatureVerificator = new SignatureVerification(document);
		timestampVerificator = new TimestampVerification(document);
		certificateVerificator = new CertificateVerification(document);
	}
	
	public void verify() throws DocumentVerificationException {
		
		envelopeVerificator.verifyRootElement();
		
		xmlSignatureVerificator.verifySignatureMethodAndCanonicalizationMethod();
		xmlSignatureVerificator.verifyTransformsAndDigestMethod();
		xmlSignatureVerificator.verifyCoreReferencesAndDigestValue();
		xmlSignatureVerificator.verifyCoreSignatureValue();
		xmlSignatureVerificator.verifySignature();
		xmlSignatureVerificator.verifySignatureValueId();
		xmlSignatureVerificator.verifySignedInfoReferencesAndAttributeValues();
		xmlSignatureVerificator.verifyKeyInfoContent();
		xmlSignatureVerificator.verifySignaturePropertiesContent();
		xmlSignatureVerificator.verifyManifestElements();
		xmlSignatureVerificator.verifyManifestElementsReferences();
		
		timestampVerificator.verifyTimestampCerfificate();
		timestampVerificator.verifyMessageImprint();
		
		certificateVerificator.verifyCertificateValidity();
	}

}
