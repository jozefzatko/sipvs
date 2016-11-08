package sk.fiit.sipvs.ar.logic.sign;

import java.io.*;

import org.apache.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.LibraryLoader;
import com.jacob.com.Variant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * JAVA-COM bridge to call DSigner via ActiveX
 * 
 * @author Jozef Zaťko
 */
public class DSignerBridge {

	final static Logger logger = Logger.getLogger(DSignerBridge.class);
	
	private static final String JACOB_DLL_X86 = "jacob-1.18-x86.dll";
	private static final String JACOB_DLL_X64 = "jacob-1.18-x64.dll";
	
	private static final String XADES_PROGRAM_ID = "DSig.XadesSig";
	private static final String XML_PLUGIN_PROGRAM_ID = "DSig.XmlPlugin";
	
	private ActiveXComponent dSignerComponent;
	private ActiveXComponent xmlComponent;
	
	private Object xmlObject;
	
	public DSignerBridge() throws IOException {
		
		includeJacobDLLs();
		
		// Start of Single Thread Apartment (STA) section
		ComThread.startMainSTA();
		ComThread.InitSTA();
		
		// Initialize ActiveX components for DSigner and XML plugin
		this.dSignerComponent = new ActiveXComponent(XADES_PROGRAM_ID);
		this.xmlComponent = new ActiveXComponent(XML_PLUGIN_PROGRAM_ID);
	}
	
	
	/**
	 * Send created XML object into DSigner application
	 */
	public void addXMLToDSigner(String objId, String objDescr, String xml, String xsd, String namespace, String xsdRef, String xslt, String xsltRef) throws SignException {
		
		this.xmlObject = Dispatch.call(this.xmlComponent, "CreateObject2", objId, objDescr, xml, xsd, namespace, xsdRef, xslt, xsltRef, "HTML");
		
		if (this.xmlObject == null) {
			logger.error("Cannot create XML object via Dispatch call");
			throw new SignException(this.xmlComponent.getProperty("ErrorMessage").toString());
		}

		Variant addOperationMsg = Dispatch.call(this.dSignerComponent, "AddObject", this.xmlObject);
		
		if (addOperationMsg.getInt() != 0) {
			logger.error("Cannot send XML object into Dsig app");
			logger.error("Aplication returned exit code " + addOperationMsg.getInt());
			throw new SignException(this.dSignerComponent.getProperty("ErrorMessage").toString());
		}
	}
	
	
	/**
	 * Send created XML to DSigner application to sign
	 * 
	 * @param signatureId unique XML id of element ds:Signature
	 * @param hashAlg type of hash algorithm, e.g. sha1, sha256
	 * @param policyId unique signature policy identifier
	 * @return signed XML
	 * @throws SignException 
	 */
	public String signXML(String signatureId, String hashAlg, String policyId) throws SignException {
		
		Variant signOperationMsg = Dispatch.call(this.dSignerComponent, "Sign", signatureId, hashAlg, policyId);
		
		if (signOperationMsg.getInt() != 0) {
			logger.error("Cannot sign XML document");
			logger.error("Aplication returned exit code " + signOperationMsg.getInt());
		}
		
		return this.dSignerComponent.getProperty("SignedXmlWithEnvelope").getString();
	}

	/**
	 * Send created XML to DSigner application to sign and get timestamp from TSA
	 *
	 * @param signatureId unique XML id of element ds:Signature
	 * @param hashAlg type of hash algorithm, e.g. sha1, sha256
	 * @param policyId unique signature policy identifier
	 * @return signed XML with timestamp
	 * @throws SignException
	 */
	public String signXMLwithTimestamp(String signatureId, String hashAlg, String policyId) throws SignException {
		String signedXML = signXML(signatureId, hashAlg, policyId);

		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			InputSource source = new InputSource(new StringReader(signedXML));
			Document document = documentBuilder.parse(source);

			Node qualifyingProperties = document.getElementsByTagName("xades:QualifyingProperties").item(0);

			if (qualifyingProperties == null) {
				logger.error("nenajdeny xades:QualifyingProperties element");
				return null;
			}

			Element unsignedProperties = document.createElement("xades:UnsignedProperties");
			Element unsignedSignatureProperties = document.createElement("xades:UnsignedSignatureProperties");
			Element signatureTimestamp = document.createElement("xades:SignatureTimeStamp");
			Element encapsulatedTimeStamp = document.createElement("xades:EncapsulatedTimeStamp");

			unsignedProperties.appendChild(unsignedSignatureProperties);
			unsignedSignatureProperties.appendChild(signatureTimestamp);
			signatureTimestamp.appendChild(encapsulatedTimeStamp);

			Node signatureValue = document.getElementsByTagName("ds:SignatureValue").item(0);

			if (signatureValue == null) {
				logger.error("nenajdeny ds:SignatureValue element");
				return null;
			}

			TSAConnector tsaConnector = new TSAConnector();
			String timestamp = tsaConnector.getTimeStampToken(signatureValue.getTextContent());

			Text timestampNode = document.createTextNode(timestamp);
			encapsulatedTimeStamp.appendChild(timestampNode);
			qualifyingProperties.appendChild(unsignedProperties);

			//vytvorenie konecneho XML
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(new DOMSource(document), result);

			return result.getWriter().toString();

		} catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
			e.printStackTrace();
		}


		return null;
	}

	
	
	/**
	 * Include DLL libraries of JACOB library
	 */
	private void includeJacobDLLs() throws IOException {
		
		File temporaryDll = readDllFile(getJacobDllName());
		
		System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll.getAbsolutePath());
		LibraryLoader.loadJacobLibrary();
		
		temporaryDll.deleteOnExit();
	}
	
	
	/**
	 * Return DLL path according to OS architecture 32/64 bit
	 * 
	 * @return path to JACOB DLL plugin
	 */
	private String getJacobDllName() {
		
		if (System.getProperty("os.arch").equals("amd64")) {
			
			return "/jacob-1.18/" + JACOB_DLL_X64;
		}
		return "/jacob-1.18/" + JACOB_DLL_X86;
	}
	
	
	/**
	 * Read JACOB DLL file
	 * Magic according to http://www.javaquery.com/2013/12/getting-started-with-jacob-example-with.html
	 * 
	 * @param dllPath path to JACOB DLL
	 * @return temporary file with loaded DLL
	 * @throws IOException
	 */
	private File readDllFile(String dllPath) throws IOException {
		
		InputStream inputStream = null;
		File temporaryDll = null;
		FileOutputStream outputStream = null;
		
		try {
			
			inputStream = getClass().getResourceAsStream(dllPath);
			temporaryDll = File.createTempFile("jacob", ".dll");
			outputStream = new FileOutputStream(temporaryDll);
			
			byte[] array = new byte[8192];
			
			for (int i = inputStream.read(array); i != -1; i = inputStream.read(array)) {
				outputStream.write(array, 0, i);
			}
			
		} finally {
			
			if (inputStream != null) {
				inputStream.close();
			}
			
			if (outputStream != null) {
				outputStream.close();
			}
		}
		
		return temporaryDll;
	}
	
}
