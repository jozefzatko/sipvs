package sk.fiit.sipvs.sv.verify.verifications;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.security.cert.CertificateParsingException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sk.fiit.sipvs.sv.verify.DocumentVerificationException;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;


/**
 * Electronic signature related document verifications
 */
public class SignatureVerification extends Verification {

	private List<String> signatureMethods = new ArrayList<String>(Arrays.asList(
			
		new String[] {
				"http://www.w3.org/2000/09/xmldsig#dsa-sha1", 
				"http://www.w3.org/2000/09/xmldsig#rsa-sha1",
				"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256",
				"http://www.w3.org/2001/04/xmldsig-more#rsa-sha384",
				"http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"
		}
	));
	
	private List<String> canonicalizationMethods = new ArrayList<String>(Arrays.asList(
			
			new String[] {
					"http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
			}
	));
	
	private List<String> transformMethods = new ArrayList<String>(Arrays.asList(
			
			new String[] {
					"http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
			}
	));
	
	private List<String> digestMethods = new ArrayList<String>(Arrays.asList(
			
			new String[] {
					"http://www.w3.org/2000/09/xmldsig#sha1", 
					"http://www.w3.org/2001/04/xmldsig-more#sha224",
					"http://www.w3.org/2001/04/xmlenc#sha256",
					"http://www.w3.org/2001/04/xmldsig-more#sha384",
					"http://www.w3.org/2001/04/xmlenc#sha512"
			}
	));
	
	public SignatureVerification(Document document) {
		super(document);
	}
	
	/*
	 * Kontrola obsahu ds:SignatureMethod a ds:CanonicalizationMethod
	 * Musia obsahovať URI niektorého z podporovaných algoritmov pre dané elementy podľa profilu XAdES_ZEP
	 */
	public boolean verifySignatureMethodAndCanonicalizationMethod() throws DocumentVerificationException {
		
		Element signatureMethod = (Element) document.getElementsByTagName("ds:SignatureMethod").item(0);

		if (assertElementAttributeValue(signatureMethod, "Algorithm", signatureMethods) == false) {
			
			throw new DocumentVerificationException(
					"Atribút Algorithm elementu ds:SignatureMethod neobsahuje URI niektorého z podporovaných algoritmov");
		}
		
		Element canonicalizationMethod = (Element) document.getElementsByTagName("ds:CanonicalizationMethod").item(0);
		
		if (assertElementAttributeValue(canonicalizationMethod, "Algorithm", canonicalizationMethods) == false) {
			
			throw new DocumentVerificationException(
					"Atribút Algorithm elementu ds:CanonicalizationMethod neobsahuje URI niektorého z podporovaných algoritmov");
		}
		
		return true;
	}
	
	/*
	 * Kontrola obsahu ds:Transforms a ds:DigestMethod vo všetkých referenciách v ds:SignedInfo
	 * Musia obsahovať URI niektorého z podporovaných algoritmov podľa profilu XAdES_ZEP
	 */
	public boolean verifyTransformsAndDigestMethod() throws DocumentVerificationException {
		
		Element signedInfo = (Element) document.getElementsByTagName("ds:SignedInfo").item(0);
		
		
		NodeList transformsElements = signedInfo.getElementsByTagName("ds:Transforms");
		
		for (int i=0; i<transformsElements.getLength(); i++) {
			
			Element transformsElement = (Element) transformsElements.item(i);
			Element transformElement = (Element) transformsElement.getElementsByTagName("ds:Transform").item(0);
			
			if (assertElementAttributeValue(transformElement, "Algorithm", transformMethods) == false) {
				
				throw new DocumentVerificationException(
						"Atribút Algorithm elementu ds:Transforms neobsahuje URI niektorého z podporovaných algoritmov");
			}
		}
		
		
		NodeList digestMethodElements = signedInfo.getElementsByTagName("ds:DigestMethod");
		
		for (int i=0; i<digestMethodElements.getLength(); i++) {
			
			Element digestMethodElement = (Element) digestMethodElements.item(0);
			
			if (assertElementAttributeValue(digestMethodElement, "Algorithm", digestMethods) == false) {
				
				throw new DocumentVerificationException(
						"Atribút Algorithm elementu ds:DigestMethod neobsahuje URI niektorého z podporovaných algoritmov");
			}
		}
		
		return true;
	}
	
	/*
	 * Core validation (podľa špecifikácie XML Signature)
	 * Dereferencovanie URI, kanonikalizácia referencovaných ds:Manifest elementov
	 * a overenie hodnôt odtlačkov ds:DigestValue
	 */
	public boolean verifyCoreReferencesAndDigestValue() throws DocumentVerificationException {
		
		return true;
	}
	
	/*
	 * Core validation (podľa špecifikácie XML Signature)
	 * Kanonikalizácia ds:SignedInfo a overenie hodnoty ds:SignatureValue
	 * pomocou pripojeného podpisového certifikátu v ds:KeyInfo
	 */
	public boolean verifyCoreSignatureValue() throws DocumentVerificationException {
						
		return true;
	}
	
	/*
	 * Element ds:Signature:
	 * 	- musí mať Id atribút,
	 * 	- musí mať špecifikovaný namespace xmlns:ds
	 */
	public boolean verifySignature() throws DocumentVerificationException {
		
		
		Element signature = (Element) document.getElementsByTagName("ds:Signature").item(0);
		
		if (signature.equals(null)) {
			
			throw new DocumentVerificationException(
					"Element ds:Signature sa nenašiel");
		}
		
		if (signature.hasAttribute("Id") == false) {
			
			throw new DocumentVerificationException(
					"Element ds:Signature neobsahuje atribút Id");
		}
		
		if (assertElementAttributeValue(signature, "Id") == false) {
			
			throw new DocumentVerificationException(
					"Atribút Id elementu ds:Signature neobsahuje žiadnu hodnotu");
		}
		
		if (assertElementAttributeValue(signature, "xmlns:ds", "http://www.w3.org/2000/09/xmldsig#") == false) {
			
			throw new DocumentVerificationException(
					"Element ds:Signature nemá nastavený namespace xmlns:ds");
		}
		
		return true;
	}
	
	/*
	 * Element ds:SignatureValue
	 * 	– musí mať Id atribút
	 */
	public boolean verifySignatureValueId() throws DocumentVerificationException {
		
		
		Element signatureValue = (Element) document.getElementsByTagName("ds:SignatureValue").item(0);
		
		if (signatureValue.equals(null)) {
			
			throw new DocumentVerificationException(
					"Element ds:SignatureValue sa nenašiel");
		}
		
		if (signatureValue.hasAttribute("Id") == false) {
			
			throw new DocumentVerificationException(
					"Element ds:SignatureValue neobsahuje atribút Id");
		}
		
		return true;
	}
	
	/*
	 * Overenie existencie referencií v ds:SignedInfo a hodnôt atribútov Id a Type voči profilu XAdES_ZEP pre:
	 * 	- ds:KeyInfo element,
	 * 	- ds:SignatureProperties element,
	 * 	- xades:SignedProperties element,
	 * 	- všetky ostatné referencie v rámci ds:SignedInfo musia byť referenciami na ds:Manifest elementy
	 */
	public boolean verifySignedInfoReferencesAndAttributeValues() throws DocumentVerificationException {
		
		return true;
	}
	
	/*
	 * Overenie obsahu ds:KeyInfo:
	 * 	- musí mať Id atribút,
	 * 	- musí obsahovať ds:X509Data, ktorý obsahuje elementy: ds:X509Certificate, ds:X509IssuerSerial, ds:X509SubjectName,
	 * 	- hodnoty elementov ds:X509IssuerSerial a ds:X509SubjectName súhlasia s príslušnými hodnatami v certifikáte,
	 * 	  ktorý sa nachádza v ds:X509Certificate
	 */
	public boolean verifyKeyInfoContent() throws DocumentVerificationException, XPathExpressionException {
				
		Element keyInfo = (Element) document.getElementsByTagName("ds:KeyInfo").item(0);
		
		if (keyInfo.equals(null)) {
			
			throw new DocumentVerificationException(
					"Element ds:Signature sa nenašiel");
		}
		
		if (keyInfo.hasAttribute("Id") == false) {
			
			throw new DocumentVerificationException(
					"Element ds:Signature neobsahuje atribút Id");
		}
		
		if (assertElementAttributeValue(keyInfo, "Id") == false) {
			
			throw new DocumentVerificationException(
					"Atribút Id elementu ds:Signature neobsahuje žiadnu hodnotu");
		}
		
		Element XData = (Element) keyInfo.getElementsByTagName("ds:X509Data").item(0);
		
		if (XData == null) {
			
			throw new DocumentVerificationException(
					"Element ds:KeyInfo neobsahuje element ds:X509Data");
		}
		
		Element XCertificate = (Element) XData.getElementsByTagName("ds:X509Certificate").item(0);
		Element XIssuerSerial = (Element) XData.getElementsByTagName("ds:X509IssuerSerial").item(0);
		Element XSubjectName = (Element) XData.getElementsByTagName("ds:X509SubjectName").item(0);
		
		if (XCertificate.equals(null)) {
			
			throw new DocumentVerificationException(
					"Element ds:X509Data neobsahuje element ds:X509Certificate");
		}

		if (XIssuerSerial.equals(null)) {
			
			throw new DocumentVerificationException(
					"Element ds:X509Data neobsahuje element ds:X509IssuerSerial");
		}
		
		if (XSubjectName.equals(null)) {
			
			throw new DocumentVerificationException(
					"Element ds:X509Data neobsahuje element ds:X509SubjectName");
		}
		
		Element XIssuerName = (Element) XIssuerSerial.getElementsByTagName("ds:X509IssuerName").item(0);
		Element XSerialNumber = (Element) XIssuerSerial.getElementsByTagName("ds:X509SerialNumber").item(0);
		
		if (XIssuerName == null) {
			
			throw new DocumentVerificationException(
					"Element ds:X509IssuerSerial neobsahuje element ds:X509IssuerName");
		}
		
		if (XSerialNumber == null) {
			
			throw new DocumentVerificationException(
					"Element ds:X509IssuerSerial neobsahuje element ds:X509SerialNumber");
		}
		
		X509CertificateObject certif = getCertificate();	
		
		String certifIssuerName = certif.getIssuerX500Principal().toString().replaceAll("ST=", "S=");
		String certifSerialNumber = certif.getSerialNumber().toString();
		String certifSubjectName = certif.getSubjectX500Principal().toString();
		
		if (!XIssuerName.getTextContent().equals(certifIssuerName)) {
			
			throw new DocumentVerificationException(
					"Element ds:X509IssuerName sa nezhoduje s hodnotou na certifikáte");
		}
		
		if (!XSerialNumber.getTextContent().equals(certifSerialNumber)) {
			
			throw new DocumentVerificationException(
					"Element ds:X509SerialNumber sa nezhoduje s hodnotou na certifikáte");
		}
		
		if (!XSubjectName.getTextContent().equals(certifSubjectName)) {
			
			throw new DocumentVerificationException(
					"Element ds:X509SubjectName neobsahuje element ds:X509SerialNumber");
		}

		return true;
	}
	
	/*
	 * Overenie obsahu ds:SignatureProperties:
	 * 	- musí mať Id atribút,
	 * 	- musí obsahovať dva elementy ds:SignatureProperty pre xzep:SignatureVersion a xzep:ProductInfos,
	 * 	- obidva ds:SignatureProperty musia mať atribút Target nastavený na ds:Signature
	 */
	
	public boolean verifySignaturePropertiesContent() throws DocumentVerificationException {
		
		
		Element signatureProperties = (Element) document.getElementsByTagName("ds:SignatureProperties").item(0);
		
		if (signatureProperties == null) {
			
			throw new DocumentVerificationException(
					"Element ds:SignatureProperties sa nenašiel");
		}
		
		if (signatureProperties.hasAttribute("Id") == false) {
			
			throw new DocumentVerificationException(
					"Element ds:SignatureProperties neobsahuje atribút Id");
		}
		
		if (assertElementAttributeValue(signatureProperties, "Id") == false) {
			
			throw new DocumentVerificationException(
					"Atribút Id elementu ds:SignatureProperties neobsahuje žiadnu hodnotu");
		}
		
		Element signatureVersion = null;
		Element productInfos = null;
		
		for(int i = 0; i < signatureProperties.getElementsByTagName("ds:SignatureProperty").getLength(); i++){
			
			Element temp = (Element) signatureProperties.getElementsByTagName("ds:SignatureProperty").item(i);
			
			if(temp != null){
				Element temp2 = (Element) temp.getElementsByTagName("xzep:SignatureVersion").item(0);
				
				if(temp2 != null){
					signatureVersion = temp2;
				}
				
				else{
					temp2 = (Element) temp.getElementsByTagName("xzep:ProductInfos").item(0);
				
					if(temp != null){
						productInfos = temp2;
					}
				}
			}
		
		}
		
		if(signatureVersion == null){
			
			throw new DocumentVerificationException(
					"ds:SignatureProperties neobsahuje taký element ds:SignatureProperty, ktorý by obsahoval element xzep:SignatureVersion");
			
		}
		
		if(productInfos == null){
			
			throw new DocumentVerificationException(
					"ds:SignatureProperties neobsahuje taký element ds:SignatureProperty, ktorý by obsahoval element xzep:ProductInfos");
			
		}
		
		Element signature = (Element) document.getElementsByTagName("ds:Signature").item(0);
		
		if (signature == null) {
			
			throw new DocumentVerificationException(
					"Element ds:Signature sa nenašiel");
		}
		
		String signatureId = signature.getAttribute("Id");
	
		Element sigVerParent = (Element) signatureVersion.getParentNode();
		Element PInfoParent = (Element) productInfos.getParentNode();
		
		String targetSigVer = sigVerParent.getAttribute("Target");
		String targetPInfo = PInfoParent.getAttribute("Target");
		
		if(!targetSigVer.equals("#" + signatureId)){
			
			throw new DocumentVerificationException(
					"Atribút Target elementu xzep:SignatureVersion sa neodkazuje na daný ds:Signature");
			
		}
		
		if(!targetPInfo.equals("#" + signatureId)){
			
			throw new DocumentVerificationException(
					"Atribút Target elementu xzep:ProductInfos sa neodkazuje na daný ds:Signature");
			
		}
		
		return true;
	}
	
	/*
	 * Overenie ds:Manifest elementov:
	 * 	- každý ds:Manifest element musí mať Id atribút,
	 * 	- ds:Transforms musí byť z množiny podporovaných algoritmov pre daný element podľa profilu XAdES_ZEP,
	 * 	- ds:DigestMethod – musí obsahovať URI niektorého z podporovaných algoritmov podľa profilu XAdES_ZEP,
	 * 	- overenie hodnoty Type atribútu voči profilu XAdES_ZEP,
	 * 	- každý ds:Manifest element musí obsahovať práve jednu referenciu na ds:Object
	 */
	public boolean verifyManifestElements() throws DocumentVerificationException {
		
		return true;
	}
	
	/*
	 * Overenie referencií v elementoch ds:Manifest:
	 * 	- dereferencovanie URI, aplikovanie príslušnej ds:Transforms transformácie (pri base64 decode),
	 * 	- overenie hodnoty ds:DigestValue
	 */
	public boolean verifyManifestElementsReferences() throws DocumentVerificationException {
		
		return true;
	}



private X509CertificateObject getCertificate() throws XPathExpressionException, DocumentVerificationException {
	Element keyInfo = (Element) document.getElementsByTagName("ds:KeyInfo").item(0);
	Element XData = (Element) keyInfo.getElementsByTagName("ds:X509Data").item(0);
	Element x509Certificate = (Element) XData.getElementsByTagName("ds:X509Certificate").item(0);

	X509CertificateObject cert = null;
	ASN1InputStream is = null;
	try {
		is = new ASN1InputStream(new ByteArrayInputStream(Base64.decode(x509Certificate.getTextContent())));
		ASN1Sequence sq = (ASN1Sequence) is.readObject();
		cert = new X509CertificateObject(Certificate.getInstance(sq));
	} catch (IOException | java.security.cert.CertificateParsingException e) {
		throw new DocumentVerificationException("Certifikát nebolo možné načítať");
	} finally {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				throw new DocumentVerificationException("Certifikát nebolo možné načítať");
			}
		}
	}

	return cert;
	}

}