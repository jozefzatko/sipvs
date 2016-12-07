package sk.fiit.sipvs.sv.verify.verifications;

import it.svario.xpathapi.jaxp.XPathAPI;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;

import org.w3c.dom.Node;
import sk.fiit.sipvs.sv.verify.DocumentVerificationException;

import javax.xml.xpath.XPathException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.*;
import java.util.HashMap;
import java.util.Map;

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
	public boolean verifyCertificateValidity(X509CRL crl, TimeStampToken ts_token) throws DocumentVerificationException {
		Map<String, String> nsMap = new HashMap<>();
		nsMap.put("ds", "http://www.w3.org/2000/09/xmldsig#");

		Node certificateNode = null;

		try {
			certificateNode = XPathAPI.selectSingleNode(document, "//ds:Signature/ds:KeyInfo/ds:X509Data/ds:X509Certificate", nsMap);
		} catch (XPathException e) {
			e.printStackTrace();
		}

		if (certificateNode == null){
			throw new DocumentVerificationException("Element ds:X509Certificate nenájdený.");
		}

		X509CertificateObject cert = null;
		ASN1InputStream asn1is = null;

		try {
			asn1is = new ASN1InputStream(new ByteArrayInputStream(Base64.decode(certificateNode.getTextContent())));
			ASN1Sequence sq = (ASN1Sequence) asn1is.readObject();
			cert = new X509CertificateObject(Certificate.getInstance(sq));
		} catch (IOException | CertificateParsingException e) {
			e.printStackTrace();
		} finally {
			if (asn1is != null) {
				try {
					asn1is.close();
				} catch (IOException e) {
					throw new DocumentVerificationException("Nie je možné prečítať certifikát dokumentu.");
				}
			}
		}

		try {
			cert.checkValidity(ts_token.getTimeStampInfo().getGenTime());
		} catch (CertificateExpiredException e) {
			throw new DocumentVerificationException("Certifikát dokumentu bol pri podpise expirovaný.");
		} catch (CertificateNotYetValidException e) {
			throw new DocumentVerificationException("Certifikát dokumentu ešte nebol platný v čase podpisovania.");
		}

		X509CRLEntry entry = crl.getRevokedCertificate(cert.getSerialNumber());
		if (entry != null && entry.getRevocationDate().before(ts_token.getTimeStampInfo().getGenTime())) {
			throw new DocumentVerificationException("Certifikát bol zrušený v čase podpisovania.");
		}

		return true;
	}

}