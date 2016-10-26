package sk.fiit.sipvs.ar.logic.sign;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.LibraryLoader;
import com.jacob.com.Variant;

/**
 * JAVA-COM bridge to call Dsigner via ActiveX
 *
 */
public class DSignerBridge {

	final static Logger logger = Logger.getLogger(DSignerBridge.class);
	
	private static final String JACOB_DLL_X86 = "jacob-1.18-x86.dll";
	private static final String JACOB_DLL_X64 = "jacob-1.18-x64.dll";
	
	private static final String XADES_PROGRAM_ID = "DSig.XadesSig";
	private static final String XML_PLUGIN_PROGRAM_ID = "DSig.XmlPlugin";
	
	private ActiveXComponent dSigComponent;
	private ActiveXComponent xmlComponent;
	
	private Object xmlObject;
	
	public void init() throws IOException {
		
		this.dSigComponent = createActiveXComponent(XADES_PROGRAM_ID);
		this.xmlComponent = createActiveXComponent(XML_PLUGIN_PROGRAM_ID);
	}
	
	/**
	 * Send created XML object into Dsig application
	 * 
	 * @param objId
	 * @param objDescr
	 * @param xml
	 * @param xsd
	 * @param namespace
	 * @param xsdRef
	 * @param xslt
	 * @param xsltRef
	 * @throws SignException 
	 */
	public void addObject(String objId, String objDescr, String xml, String xsd, String namespace, String xsdRef, String xslt, String xsltRef) throws SignException {
		
		this.xmlObject = Dispatch.call(this.xmlComponent, "CreateObject2", objId, objDescr, xml, xsd, namespace, xsdRef, xslt, xsltRef, "HTML");
		
		if (this.xmlObject == null) {
			logger.error("Cannot create XML object via Dispatch call");
			throw new SignException(this.xmlComponent.getProperty("ErrorMessage").toString());
		}

		Variant addOperationMsg = Dispatch.call(this.dSigComponent, "AddObject", this.xmlObject);
		
		if (addOperationMsg.getInt() != 0) {
			logger.error("Cannot send XML object into Dsig app");
			throw new SignException(this.dSigComponent.getProperty("ErrorMessage").toString());
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
		
		Variant signOperationMsg = Dispatch.call(this.dSigComponent, "Sign", signatureId, hashAlg, policyId);
		
		if (signOperationMsg.getInt() != 0) {
			logger.error("Cannot sign XML document");
			throw new SignException(this.dSigComponent.getProperty("ErrorMessage").toString());
		}
		
		return this.dSigComponent.getProperty("SignedXmlWithEnvelope").getString();
	}
	
	/**
	 * Create object of ActiveX component
	 * 
	 * @param programId
	 * @return ActiveX component
	 * @throws IOException
	 */
	private ActiveXComponent createActiveXComponent(String programId) throws IOException {
		
		File temporaryDll = readDllFile(getJacobDllName());
		
		System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll.getAbsolutePath());
		LibraryLoader.loadJacobLibrary();

		ActiveXComponent component = new ActiveXComponent(programId);
				
		temporaryDll.deleteOnExit();
		
		return component;

	}
	
	/**
	 * Return DLL path according to OS architecture 32/64 bit
	 * 
	 * @return path to JACOB DLL plugin
	 */
	private String getJacobDllName() {
		
		if (System.getProperty("os.arch").equals("amd64")) {
			
			return "/jacob/" + JACOB_DLL_X64;
		}
		return "/jacob/" + JACOB_DLL_X86;
	}
	
	/**
	 * Read JACOB DLL file
	 * Magic according to http://www.javaquery.com/2013/12/getting-started-with-jacob-example-with.html
	 * 
	 * @param dllPath path to JACOB DLL
	 * @return Temp file with loaded DLL
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
