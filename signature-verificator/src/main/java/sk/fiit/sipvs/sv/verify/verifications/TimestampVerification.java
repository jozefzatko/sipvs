package sk.fiit.sipvs.sv.verify.verifications;

import it.svario.xpathapi.jaxp.XPathAPI;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;

import org.w3c.dom.Node;
import sk.fiit.sipvs.sv.verify.DocumentVerificationException;

import javax.xml.xpath.XPathException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.X509CRL;
import java.util.*;

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
	public boolean verifyTimestampCerfificate(X509CRL crl, TimeStampToken ts_token) throws DocumentVerificationException {
		X509CertificateHolder signer = null;

		Store<X509CertificateHolder> certHolders = ts_token.getCertificates();
		ArrayList<X509CertificateHolder> certList = new ArrayList<>(certHolders.getMatches(null));

		BigInteger serialNumToken = ts_token.getSID().getSerialNumber();
		X500Name issuerToken = ts_token.getSID().getIssuer();

		for (X509CertificateHolder certHolder : certList) {
			if (certHolder.getSerialNumber().equals(serialNumToken) && certHolder.getIssuer().equals(issuerToken)){
				signer = certHolder;
				break;
			}
		}

		if (signer == null){
			throw new DocumentVerificationException("V dokumente sa nenachadza certifikat casovej peciatky.");
		}

		if (!signer.isValidOn(new Date())){
			throw new DocumentVerificationException("Podpisový certifikát časovej pečiatky nie je platný voči aktuálnemu času.");
		}

		if (crl.getRevokedCertificate(signer.getSerialNumber()) != null){
			throw new DocumentVerificationException("Podpisový certifikát časovej pečiatky nie je platný voči platnému poslednému CRL.");
		}

		return true;
	}
	
	/*
	 * Overenie MessageImprint z časovej pečiatky voči podpisu ds:SignatureValue
	 */
	public boolean verifyMessageImprint(TimeStampToken ts_token) throws DocumentVerificationException {
		byte[] messageImprint = ts_token.getTimeStampInfo().getMessageImprintDigest();
		String hashAlg = ts_token.getTimeStampInfo().getHashAlgorithm().getAlgorithm().getId();

		Map<String, String> nsMap = new HashMap<>();
		nsMap.put("ds", "http://www.w3.org/2000/09/xmldsig#");

		Node signatureValueNode = null;

		try {
			signatureValueNode = XPathAPI.selectSingleNode(document, "//ds:Signature/ds:SignatureValue", nsMap);
		} catch (XPathException e) {
			e.printStackTrace();
		}

		if (signatureValueNode == null){
			throw new DocumentVerificationException("Element ds:SignatureValue nenájdený.");
		}

		byte[] signatureValue = Base64.decode(signatureValueNode.getTextContent().getBytes());

		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance(hashAlg, "BC");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new DocumentVerificationException("Nepodporovaný algoritmus v message digest.");
		}

		if (!Arrays.equals(messageImprint, messageDigest.digest(signatureValue))){
			throw new DocumentVerificationException("MessageImprint z časovej pečiatky a podpis ds:SignatureValue sa nezhodujú.");
		}

		return true;
	}
}