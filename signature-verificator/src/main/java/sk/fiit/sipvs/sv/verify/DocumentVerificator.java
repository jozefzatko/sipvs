package sk.fiit.sipvs.sv.verify;

import it.svario.xpathapi.jaxp.XPathAPI;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sk.fiit.sipvs.sv.verify.verifications.CertificateVerification;
import sk.fiit.sipvs.sv.verify.verifications.EnvelopeVerification;
import sk.fiit.sipvs.sv.verify.verifications.SignatureVerification;
import sk.fiit.sipvs.sv.verify.verifications.TimestampVerification;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Verify if document is XadesT Advanced Electronic Signature
 */
public class DocumentVerificator {
	
	private EnvelopeVerification envelopeVerificator;
	private SignatureVerification xmlSignatureVerificator;
	private TimestampVerification timestampVerificator;
	private CertificateVerification certificateVerificator;
	private Document document;

	public DocumentVerificator(Document document) {
		this.document = document;
		envelopeVerificator = new EnvelopeVerification(document);
		xmlSignatureVerificator = new SignatureVerification(document);
		timestampVerificator = new TimestampVerification(document);
		certificateVerificator = new CertificateVerification(document);
	}

	public void verify() throws DocumentVerificationException {
		Security.addProvider(new BouncyCastleProvider());

		X509CRL crl = getCRL();
		TimeStampToken token = getTimestampToken();

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
		
		timestampVerificator.verifyTimestampCerfificate(crl, token);
		timestampVerificator.verifyMessageImprint(token);
		
		certificateVerificator.verifyCertificateValidity(crl, token);
	}

	private TimeStampToken getTimestampToken() throws DocumentVerificationException {
		TimeStampToken ts_token = null;

		Node timestamp = null;
		Map<String, String> nsMap = new HashMap<>();
		nsMap.put("xades", "http://uri.etsi.org/01903/v1.3.2#");

		try {
			timestamp = XPathAPI.selectSingleNode(document, "//xades:EncapsulatedTimeStamp", nsMap);
		} catch (XPathException e) {
			e.printStackTrace();
		}

		if (timestamp == null){
			throw new DocumentVerificationException("Dokument neobsahuje casovu peciatku.");
		}

		try {
			ts_token = new TimeStampToken(new CMSSignedData(Base64.decode(timestamp.getTextContent())));
		} catch (TSPException | IOException | CMSException e) {
			e.printStackTrace();
		}

		return ts_token;
	}

	private X509CRL getCRL() throws DocumentVerificationException {
		ByteArrayInputStream crlData = getDataFromUrl("http://test.monex.sk/DTCCACrl/DTCCACrl.crl");

		if (crlData == null){
			throw new DocumentVerificationException("Nepodarilo sa stiahnut CRL zo stranky.");
		}

		CertificateFactory certFactory;
		try {
			certFactory = CertificateFactory.getInstance("X.509", "BC");
		} catch (CertificateException | NoSuchProviderException e) {
			throw new DocumentVerificationException("Nepodarilo sa vytvorit zadanu instanciu CertificateFactory.");
		}


		X509CRL crl;

		try {
			crl = (X509CRL) certFactory.generateCRL(crlData);
		} catch (CRLException e) {
			throw new DocumentVerificationException("Nepodarilo sa ziskat CRL z obdrzanych dat.");
		}


		return crl;
	}

	private ByteArrayInputStream getDataFromUrl(String url) {
		URL urlHandler = null;
		try {
			urlHandler = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = urlHandler.openStream();
			byte[] byteChunk = new byte[4096];
			int n;

			while ( (n = is.read(byteChunk)) > 0 ) {
				baos.write(byteChunk, 0, n);
			}
		}
		catch (IOException e) {
			System.err.printf ("Failed while reading bytes from %s: %s", urlHandler.toExternalForm(), e.getMessage());
			return null;
		}
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return new ByteArrayInputStream(baos.toByteArray());
	}


}
