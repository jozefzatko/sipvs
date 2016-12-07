package sk.fiit.sipvs.sv.verify.verifications;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import it.svario.xpathapi.jaxp.XPathAPI;
import sk.fiit.sipvs.sv.utils.Converter;
import sk.fiit.sipvs.sv.verify.DocumentVerificationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.bouncycastle.jce.provider.X509CertificateObject;
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
	
	private List<String> manifestTransformMethods = new ArrayList<String>(Arrays.asList(
			
			new String[] {
					"http://www.w3.org/TR/2001/REC-xml-c14n-20010315",
					"http://www.w3.org/2000/09/xmldsig#base64"
			}
	));
	
	private static final Map<String, String> DIGEST_ALG;
	
	static {
		DIGEST_ALG = new HashMap<String, String>();
		DIGEST_ALG.put("http://www.w3.org/2000/09/xmldsig#sha1", "SHA-1");
		DIGEST_ALG.put("http://www.w3.org/2001/04/xmldsig-more#sha224", "SHA-224");
		DIGEST_ALG.put("http://www.w3.org/2001/04/xmlenc#sha256", "SHA-256");
		DIGEST_ALG.put("http://www.w3.org/2001/04/xmldsig-more#sha384", "SHA-384");
		DIGEST_ALG.put("http://www.w3.org/2001/04/xmlenc#sha512", "SHA-512");
	}
	
	private static final Map<String, String> SIGN_ALG;
	
	static {
		SIGN_ALG = new HashMap<String, String>();
		SIGN_ALG.put("http://www.w3.org/2000/09/xmldsig#dsa-sha1", "SHA1withDSA");
		SIGN_ALG.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", "SHA1withRSA/ISO9796-2");
		SIGN_ALG.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", "SHA256withRSA");
		SIGN_ALG.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", "SHA384withRSA");
		SIGN_ALG.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", "SHA512withRSA");
}
	
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

		Element signedInfo = (Element) document.getElementsByTagName("ds:SignedInfo").item(0);
		
		NodeList referencesElements = signedInfo.getElementsByTagName("ds:Reference");
		
		for (int i=0; i<referencesElements.getLength(); i++) {
			
			Element referenceElement = (Element) referencesElements.item(i);
			String uri = referenceElement.getAttribute("URI").substring(1);
			
			Element manifestElement = this.elementFinder.findByAttributeValue("ds:Manifest", "Id", uri);
				
			if (manifestElement == null) {
				continue;
			}
			
			Element digestValueElement = (Element) referenceElement.getElementsByTagName("ds:DigestValue").item(0);
			String expectedDigestValue = digestValueElement.getTextContent();
			
			Element digestMethodElement = (Element) referenceElement.getElementsByTagName("ds:DigestMethod").item(0);
			
			if (assertElementAttributeValue(digestMethodElement, "Algorithm", digestMethods) == false) {
				
				throw new DocumentVerificationException(
						"Atribút Algorithm elementu ds:DigestMethod (" + digestMethodElement.getAttribute("Algorithm") +
						") neobsahuje URI niektorého z podporovaných algoritmov");
			}
			
			String digestMethod = digestMethodElement.getAttribute("Algorithm");
			digestMethod = DIGEST_ALG.get(digestMethod);
			
			
			byte[] manifestElementBytes = null;
					
			try {
				manifestElementBytes = Converter.fromElementToString(manifestElement).getBytes();
			
			} catch (TransformerException e) {
				
				throw new DocumentVerificationException(
						"Core validation zlyhala. Chyba pri tranformacii z Element do String", e);
			}
			
			NodeList transformsElements = manifestElement.getElementsByTagName("ds:Transforms");
			
			for (int j=0; j<transformsElements.getLength(); j++) {
				
				Element transformsElement = (Element) transformsElements.item(j);
				Element transformElement = (Element) transformsElement.getElementsByTagName("ds:Transform").item(0);
				String transformMethod = transformElement.getAttribute("Algorithm");
				
				if ("http://www.w3.org/TR/2001/REC-xml-c14n-20010315".equals(transformMethod)) {
					
					try {
						Canonicalizer canonicalizer = Canonicalizer.getInstance(transformMethod);
						manifestElementBytes = canonicalizer.canonicalize(manifestElementBytes);
						
					} catch (SAXException | InvalidCanonicalizerException | CanonicalizationException | ParserConfigurationException | IOException e) {
						
						throw new DocumentVerificationException("Core validation zlyhala. Chyba pri kanonikalizacii", e);
					}
				}
			}
			
			MessageDigest messageDigest = null;
			
			try {
				messageDigest = MessageDigest.getInstance(digestMethod);
				
			} catch (NoSuchAlgorithmException e) {
				
				throw new DocumentVerificationException(
						"Core validation zlyhala. Neznamy algoritmus " + digestMethod, e);
			}
			String actualDigestValue = new String(Base64.encode(messageDigest.digest(manifestElementBytes)));
			
			
			if (expectedDigestValue.equals(actualDigestValue) == false) {
				
				throw new DocumentVerificationException(
						"Hodnota ds:DigestValue elementu ds:Reference sa nezhoduje s hash hodnotou elementu ds:Manifest");
			}
			
		}
		
		return true;
	}
	
	/*
	 * Core validation (podľa špecifikácie XML Signature)
	 * Kanonikalizácia ds:SignedInfo a overenie hodnoty ds:SignatureValue
	 * pomocou pripojeného podpisového certifikátu v ds:KeyInfo
	 */
	public boolean verifyCoreSignatureValue() throws DocumentVerificationException {
		
		Element signatureElement = (Element) document.getElementsByTagName("ds:Signature").item(0);
		
		Element signedInfoElement = (Element) signatureElement.getElementsByTagName("ds:SignedInfo").item(0);
		Element canonicalizationMethodElement = (Element) signedInfoElement.getElementsByTagName("ds:CanonicalizationMethod").item(0);
		Element signatureMethodElement = (Element) signedInfoElement.getElementsByTagName("ds:SignatureMethod").item(0);
		Element signatureValueElement = (Element) signatureElement.getElementsByTagName("ds:SignatureValue").item(0);
		
		
		byte[] signedInfoElementBytes = null;
		try {
			signedInfoElementBytes = Converter.fromElementToString(signedInfoElement).getBytes();
		} catch (TransformerException e) {
			
			throw new DocumentVerificationException(
					"Core validation zlyhala. Chyba pri tranformacii z Element do String", e);
		}
		
		String canonicalizationMethod = canonicalizationMethodElement.getAttribute("Algorithm");
		
		try {
			Canonicalizer canonicalizer = Canonicalizer.getInstance(canonicalizationMethod);
			signedInfoElementBytes = canonicalizer.canonicalize(signedInfoElementBytes);
			
		} catch (SAXException | InvalidCanonicalizerException | CanonicalizationException | ParserConfigurationException | IOException e) {
			
			throw new DocumentVerificationException("Core validation zlyhala. Chyba pri kanonikalizacii", e);
		}
		
		X509CertificateObject certificate = null;
		try {
			certificate = this.certFinder.getCertificate();
			
		} catch (XPathExpressionException e) {
			
			throw new DocumentVerificationException(
					"X509 certifikát sa v dokumente nepodarilo nájsť", e);
		}
		
		String signatureMethod = signatureMethodElement.getAttribute("Algorithm");
		signatureMethod = SIGN_ALG.get(signatureMethod);
		
		Signature signer = null;
		try {
			signer = Signature.getInstance(signatureMethod);
			signer.initVerify(certificate.getPublicKey());
			signer.update(signedInfoElementBytes);
			
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			
			throw new DocumentVerificationException(
					"Core validation zlyhala. Chyba pri inicializacii prace s digitalnym podpisom", e);
		}
		
		byte[] signatureValueBytes = signatureValueElement.getTextContent().getBytes();
		byte[] decodedSignatureValueBytes = Base64.decode(signatureValueBytes);
		
		boolean verificationResult = false;
		
		try {
			verificationResult = signer.verify(decodedSignatureValueBytes);
			
		} catch (SignatureException e) {
			
			throw new DocumentVerificationException(
					"Core validation zlyhala. Chyba pri verifikacii digitalneho podpisu", e);
		}
		
		if (verificationResult == false) {
			
			throw new DocumentVerificationException(
					"Podpisana hodnota ds:SignedInfo sa nezhoduje s hodnotou v elemente ds:SignatureValue");
		}
		
		return true;
	}
	
	/*
	 * Element ds:Signature:
	 * 	- musí mať Id atribút,
	 * 	- musí mať špecifikovaný namespace xmlns:ds
	 */
	public boolean verifySignature() throws DocumentVerificationException {
		
		Element signatureElement = (Element) document.getElementsByTagName("ds:Signature").item(0);
		
		if (signatureElement == null) {
			
			throw new DocumentVerificationException(
					"Element ds:Signature sa nenašiel");
		}
		
		if (signatureElement.hasAttribute("Id") == false) {
			
			throw new DocumentVerificationException(
					"Element ds:Signature neobsahuje atribút Id");
		}
		
		if (assertElementAttributeValue(signatureElement, "Id") == false) {
			
			throw new DocumentVerificationException(
					"Atribút Id elementu ds:Signature neobsahuje žiadnu hodnotu");
		}
		
		if (assertElementAttributeValue(signatureElement, "xmlns:ds", "http://www.w3.org/2000/09/xmldsig#") == false) {
			
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
		
		Element signatureValueElement = (Element) document.getElementsByTagName("ds:SignatureValue").item(0);
		
		if (signatureValueElement == null) {
			
			throw new DocumentVerificationException(
					"Element ds:SignatureValue sa nenašiel");
		}
		
		if (signatureValueElement.hasAttribute("Id") == false) {
			
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
	public boolean verifyKeyInfoContent() throws DocumentVerificationException {
				
		Element keyInfoElement = (Element) document.getElementsByTagName("ds:KeyInfo").item(0);
		
		if (keyInfoElement == null) {
			
			throw new DocumentVerificationException(
					"Element ds:Signature sa nenašiel");
		}
		
		if (keyInfoElement.hasAttribute("Id") == false) {
			
			throw new DocumentVerificationException(
					"Element ds:Signature neobsahuje atribút Id");
		}
		
		if (assertElementAttributeValue(keyInfoElement, "Id") == false) {
			
			throw new DocumentVerificationException(
					"Atribút Id elementu ds:Signature neobsahuje žiadnu hodnotu");
		}
		
		Element xDataElement = (Element) keyInfoElement.getElementsByTagName("ds:X509Data").item(0);
		
		if (xDataElement == null) {
			
			throw new DocumentVerificationException(
					"Element ds:KeyInfo neobsahuje element ds:X509Data");
		}
		
		Element xCertificateElement = (Element) xDataElement.getElementsByTagName("ds:X509Certificate").item(0);
		Element xIssuerSerialElement = (Element) xDataElement.getElementsByTagName("ds:X509IssuerSerial").item(0);
		Element xSubjectNameElement = (Element) xDataElement.getElementsByTagName("ds:X509SubjectName").item(0);
		
		if (xCertificateElement == null) {
			
			throw new DocumentVerificationException(
					"Element ds:X509Data neobsahuje element ds:X509Certificate");
		}

		if (xIssuerSerialElement == null) {
			
			throw new DocumentVerificationException(
					"Element ds:X509Data neobsahuje element ds:X509IssuerSerial");
		}
		
		if (xSubjectNameElement == null) {
			
			throw new DocumentVerificationException(
					"Element ds:X509Data neobsahuje element ds:X509SubjectName");
		}
		
		Element xIssuerNameElement = (Element) xIssuerSerialElement.getElementsByTagName("ds:X509IssuerName").item(0);
		Element xSerialNumberElement = (Element) xIssuerSerialElement.getElementsByTagName("ds:X509SerialNumber").item(0);
		
		if (xIssuerNameElement == null) {
			
			throw new DocumentVerificationException(
					"Element ds:X509IssuerSerial neobsahuje element ds:X509IssuerName");
		}
		
		if (xSerialNumberElement == null) {
			
			throw new DocumentVerificationException(
					"Element ds:X509IssuerSerial neobsahuje element ds:X509SerialNumber");
		}
		
		X509CertificateObject certificate = null;
		try {
			certificate = this.certFinder.getCertificate();
			
		} catch (XPathExpressionException e) {
			
			throw new DocumentVerificationException(
					"X509 certifikát sa v dokumente nepodarilo nájsť", e);
		}	
		
		String certifIssuerName = certificate.getIssuerX500Principal().toString().replaceAll("ST=", "S=");
		String certifSerialNumber = certificate.getSerialNumber().toString();
		String certifSubjectName = certificate.getSubjectX500Principal().toString();
		
		if (xIssuerNameElement.getTextContent().equals(certifIssuerName) == false) {
			
			throw new DocumentVerificationException(
					"Element ds:X509IssuerName sa nezhoduje s hodnotou na certifikáte");
		}
		
		if (xSerialNumberElement.getTextContent().equals(certifSerialNumber) == false) {
			
			throw new DocumentVerificationException(
					"Element ds:X509SerialNumber sa nezhoduje s hodnotou na certifikáte");
		}
		
		if (xSubjectNameElement.getTextContent().equals(certifSubjectName) == false) {
			
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
		
		Element signaturePropertiesElement = (Element) document.getElementsByTagName("ds:SignatureProperties").item(0);
		
		if (signaturePropertiesElement == null) {
			
			throw new DocumentVerificationException(
					"Element ds:SignatureProperties sa nenašiel");
		}
		
		if (signaturePropertiesElement.hasAttribute("Id") == false) {
			
			throw new DocumentVerificationException(
					"Element ds:SignatureProperties neobsahuje atribút Id");
		}
		
		if (assertElementAttributeValue(signaturePropertiesElement, "Id") == false) {
			
			throw new DocumentVerificationException(
					"Atribút Id elementu ds:SignatureProperties neobsahuje žiadnu hodnotu");
		}
		
		Element signatureVersionElement = null;
		Element productInfosElement = null;
		
		for (int i = 0; i < signaturePropertiesElement.getElementsByTagName("ds:SignatureProperty").getLength(); i++) {
			
			Element tempElement = (Element) signaturePropertiesElement.getElementsByTagName("ds:SignatureProperty").item(i);
			
			if (tempElement != null) {
				
				Element tempElement2 = (Element) tempElement.getElementsByTagName("xzep:SignatureVersion").item(0);
				
				if (tempElement2 != null) {
					signatureVersionElement = tempElement2;
				}
				
				else {
					tempElement2 = (Element) tempElement.getElementsByTagName("xzep:ProductInfos").item(0);
				
					if (tempElement != null) {
						productInfosElement = tempElement2;
					}
				}
			}
		}
		
		if (signatureVersionElement == null) {
			
			throw new DocumentVerificationException(
					"ds:SignatureProperties neobsahuje taký element ds:SignatureProperty, ktorý by obsahoval element xzep:SignatureVersion");
			
		}
		
		if (productInfosElement == null) {
			
			throw new DocumentVerificationException(
					"ds:SignatureProperties neobsahuje taký element ds:SignatureProperty, ktorý by obsahoval element xzep:ProductInfos");
			
		}
		
		Element signature = (Element) document.getElementsByTagName("ds:Signature").item(0);
		
		if (signature == null) {
			
			throw new DocumentVerificationException(
					"Element ds:Signature sa nenašiel");
		}
		
		String signatureId = signature.getAttribute("Id");
	
		Element sigVerParentElement = (Element) signatureVersionElement.getParentNode();
		Element pInfoParentElement = (Element) productInfosElement.getParentNode();
		
		String targetSigVer = sigVerParentElement.getAttribute("Target");
		String targetPInfo = pInfoParentElement.getAttribute("Target");
		
		if (targetSigVer.equals("#" + signatureId) == false) {
			
			throw new DocumentVerificationException(
					"Atribút Target elementu xzep:SignatureVersion sa neodkazuje na daný ds:Signature");
			
		}
		
		if(targetPInfo.equals("#" + signatureId) == false) {
			
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
		
		NodeList manifestElements = null;
		try {
			manifestElements = XPathAPI.selectNodeList(document.getDocumentElement(), "//ds:Signature/ds:Object/ds:Manifest");
			
		} catch (XPathException e) {
			
			throw new DocumentVerificationException("Chyba pri hladanie ds:Manifest elementov v dokumente", e);
		}
		
		for (int i=0; i<manifestElements.getLength(); i++) {
			
			Element manifestElement = (Element) manifestElements.item(i);
			
			/*
			 * každý ds:Manifest element musí mať Id atribút,
			 */
			if (manifestElement.hasAttribute("Id") == false) {
				
				throw new DocumentVerificationException(
						"Element ds:Manifest nema atribut Id");
			}
			
			NodeList referenceElements = null;
			try {
				referenceElements = XPathAPI.selectNodeList(manifestElement, "ds:Reference");
				
			} catch (XPathException e) {
				
				throw new DocumentVerificationException("Chyba pri hladanii ds:Reference elementov v ds:Manifest elemente", e);
			}
			
			/*
			 * každý ds:Manifest element musí obsahovať práve jednu referenciu na ds:Object
			 */
			if (referenceElements.getLength() != 1) {
				
				throw new DocumentVerificationException("ds:Manifest element neobsahuje prave jednu referenciu na objekt");
			}
		}
		
		
		NodeList referenceElements = null;
		try {
			referenceElements = XPathAPI.selectNodeList(document.getDocumentElement(), "//ds:Signature/ds:Object/ds:Manifest/ds:Reference");
		} catch (XPathException e) {
			
			throw new DocumentVerificationException("Chyba pri hladanii ds:Reference elementov v dokumente", e);
		}
		
		for (int i=0; i<referenceElements.getLength(); i++) {
			
			Element referenceElement = (Element) referenceElements.item(i);
			
			NodeList transformsElements = null;
			try {
				transformsElements = XPathAPI.selectNodeList(referenceElement, "ds:Transforms/ds:Transform");
				
			} catch (XPathException e) {
				
				throw new DocumentVerificationException(
						"Chyba pri hladanie ds:Transform elementov v dokumente", e);
			}
			
			for (int j=0; j<transformsElements.getLength(); j++) {
				
				Element transformElement = (Element) transformsElements.item(j);
				
				/*
				 * ds:Transforms musí byť z množiny podporovaných algoritmov pre daný element podľa profilu XAdES_ZEP
				 */
				if (assertElementAttributeValue(transformElement, "Algorithm", manifestTransformMethods) == false) {
					
					throw new DocumentVerificationException(
							"Element ds:Transform obsahuje nepovoleny typ algoritmu");
				}
			}
			
			Element digestMethodElement = null;
			try {
				digestMethodElement = (Element) XPathAPI.selectSingleNode(referenceElement, "ds:DigestMethod");
				
			} catch (XPathException e) {
				
				throw new DocumentVerificationException(
						"Chyba pri hladanie ds:DigestMethod elementov v dokumente", e);
			}
			
			/*
			 * ds:DigestMethod – musí obsahovať URI niektorého z podporovaných algoritmov podľa profilu XAdES_ZEP
			 */
			if (assertElementAttributeValue(digestMethodElement, "Algorithm", digestMethods) == false) {
				
				throw new DocumentVerificationException(
						"Atribút Algorithm elementu ds:DigestMethod neobsahuje URI niektorého z podporovaných algoritmov");
			}
			
			/*
			 * overenie hodnoty Type atribútu voči profilu XAdES_ZEP
			 */
			if (assertElementAttributeValue(referenceElement, "Type", "http://www.w3.org/2000/09/xmldsig#Object") == false) {
				
				throw new DocumentVerificationException(
						"Atribút Type elementu ds:Reference neobsahuje hodnotu http://www.w3.org/2000/09/xmldsig#Object");
			}
			
		}
		
		return true;
	}
	
	/*
	 * Overenie referencií v elementoch ds:Manifest:
	 * 	- dereferencovanie URI, aplikovanie príslušnej ds:Transforms transformácie (pri base64 decode),
	 * 	- overenie hodnoty ds:DigestValue
	 */
	public boolean verifyManifestElementsReferences() throws DocumentVerificationException {
		
		NodeList referenceElements = null;
		try {
			referenceElements = XPathAPI.selectNodeList(document.getDocumentElement(), "//ds:Signature/ds:Object/ds:Manifest/ds:Reference");
			
		} catch (XPathException e) {
			
			throw new DocumentVerificationException("Chyba pri hladanii ds:Reference elementov v dokumente", e);
		}
		
		for (int i=0; i<referenceElements.getLength(); i++) {
			
			Element referenceElement = (Element) referenceElements.item(i);
			String uri = referenceElement.getAttribute("URI").substring(1);
			
			Element objectElement = this.elementFinder.findByAttributeValue("ds:Object", "Id", uri);
			
			Element digestValueElement = (Element) referenceElement.getElementsByTagName("ds:DigestValue").item(0);
			Element digestMethodlement = (Element) referenceElement.getElementsByTagName("ds:DigestMethod").item(0);
			
			String digestMethod = digestMethodlement.getAttribute("Algorithm");
			digestMethod = DIGEST_ALG.get(digestMethod);
			
			NodeList transformsElements = referenceElement.getElementsByTagName("ds:Transforms");
			
			for (int j=0; j<transformsElements.getLength(); j++) {
				
				Element transformsElement = (Element) transformsElements.item(j);
				Element transformElement = (Element) transformsElement.getElementsByTagName("ds:Transform").item(j);
				
				String transformMethod = transformElement.getAttribute("Algorithm");
				
				byte[] objectElementBytes = null;
				
				try {
					objectElementBytes = Converter.fromElementToString(objectElement).getBytes();
				
				} catch (TransformerException e) {
					
					throw new DocumentVerificationException(
							"Overenie referencií v elementoch ds:Manifest zlyhalo. Chyba pri tranformacii z Element do String", e);
				}
				
				if ("http://www.w3.org/TR/2001/REC-xml-c14n-20010315".equals(transformMethod)) {
					
					try {
						Canonicalizer canonicalizer = Canonicalizer.getInstance(transformMethod);
						objectElementBytes = canonicalizer.canonicalize(objectElementBytes);
						
					} catch (SAXException | InvalidCanonicalizerException | CanonicalizationException | ParserConfigurationException | IOException e) {
						
						throw new DocumentVerificationException("Core validation zlyhala. Chyba pri kanonikalizacii", e);
					}
				}
				
				if ("http://www.w3.org/2000/09/xmldsig#base64".equals(transformMethod)) {
					
					objectElementBytes = Base64.decode(objectElementBytes);
				}
				
				MessageDigest messageDigest = null;
				try {
					messageDigest = MessageDigest.getInstance(digestMethod);
					
				} catch (NoSuchAlgorithmException e) {
					
					throw new DocumentVerificationException(
							"Core validation zlyhala. Neznamy algoritmus " + digestMethod, e);
				}
				
				String actualDigestValue = new String(Base64.encode(messageDigest.digest(objectElementBytes)));
				String expectedDigestValue = digestValueElement.getTextContent();
				
				if (expectedDigestValue.equals(actualDigestValue) == false) {
					
					throw new DocumentVerificationException(
							"Hodnota ds:DigestValue elementu ds:Reference sa nezhoduje s hash hodnotou elementu ds:Manifest.");
				}
			}
		}
		
		return true;
	}
	
}