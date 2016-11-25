package sk.fiit.sipvs.sv.verify.verifications;

import org.w3c.dom.Document;

import sk.fiit.sipvs.sv.verify.DocumentVerificationException;

/**
 * Electronic signature related document verifications
 */
public class SignatureVerification extends Verification {

	public SignatureVerification(Document document) {
		super(document);
	}
	
	/*
	 * Kontrola obsahu ds:SignatureMethod a ds:CanonicalizationMethod
	 * Musia obsahovať URI niektorého z podporovaných algoritmov pre dané elementy podľa profilu XAdES_ZEP
	 */
	public boolean verifySignatureMethodAndCanonicalizationMethod() throws DocumentVerificationException {
		
		return true;
	}
	
	/*
	 * Kontrola obsahu ds:Transforms a ds:DigestMethod vo všetkých referenciách v ds:SignedInfo
	 * Musia obsahovať URI niektorého z podporovaných algoritmov podľa profilu XAdES_ZEP
	 */
	public boolean verifyTransformsAndDigestMethod() throws DocumentVerificationException {
		
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
		
		return true;
	}
	
	/*
	 * Element ds:SignatureValue
	 * 	– musí mať Id atribút
	 */
	public boolean verifySignatureValueId() throws DocumentVerificationException {
		
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
		
		return true;
	}
	
	/*
	 * Overenie obsahu ds:SignatureProperties:
	 * 	- musí mať Id atribút,
	 * 	- musí obsahovať dva elementy ds:SignatureProperty pre xzep:SignatureVersion a xzep:ProductInfos,
	 * 	- obidva ds:SignatureProperty musia mať atribút Target nastavený na ds:Signature
	 */
	public boolean verifySignaturePropertiesContent() throws DocumentVerificationException {
		
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
}
