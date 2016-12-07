package sk.fiit.sipvs.sv.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sk.fiit.sipvs.sv.verify.DocumentVerificationException;

public class CertificateFinder {
	
	private Document document;
	
	public CertificateFinder(Document document) {
		
		this.document = document;
	}

	public X509CertificateObject getCertificate() throws XPathExpressionException, DocumentVerificationException {
		
		Element keyInfo = (Element) document.getElementsByTagName("ds:KeyInfo").item(0);
		Element x509Data = (Element) keyInfo.getElementsByTagName("ds:X509Data").item(0);
		Element x509Certificate = (Element) x509Data.getElementsByTagName("ds:X509Certificate").item(0);

		X509CertificateObject certObject = null;
		ASN1InputStream inputStream = null;
		
		try {
			inputStream = new ASN1InputStream(new ByteArrayInputStream(Base64.decode(x509Certificate.getTextContent())));
			ASN1Sequence equence = (ASN1Sequence) inputStream.readObject();
			certObject = new X509CertificateObject(Certificate.getInstance(equence));
			
		} catch (IOException | java.security.cert.CertificateParsingException e) {
			
			throw new DocumentVerificationException("Certifikát nebolo možné načítať");
			
		} finally {
			
			closeQuietly(inputStream);
		}

		return certObject;
	}


	private void closeQuietly(ASN1InputStream inputStream) {
		
		if (inputStream == null) {
			return;
		}
		
		try {
			inputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
