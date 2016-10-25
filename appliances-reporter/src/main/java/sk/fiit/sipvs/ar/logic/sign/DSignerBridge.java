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
	
	private ActiveXComponent xadesAppComponent;
	private ActiveXComponent xmlComponent;
	
	private Object xmlObject;
	
	public void init() {
		
		try {
			initComponent(xadesAppComponent, XADES_PROGRAM_ID);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		try {
			initComponent(xmlComponent, XML_PLUGIN_PROGRAM_ID);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	public void addObject(String id, String descr, String xml, String xsd, String xslt) {
		
		this.xmlObject = Dispatch.call(xmlComponent, "CreateObject", id, descr, xml, xsd, "", "xsd", xslt, "");
		
		if (this.xmlObject == null) {
			logger.error("Cannot create XML object via Dipatch call");
			return;
		}
		
		Variant addOperationMsg = Dispatch.call(xadesAppComponent, "AddObject", this.xmlObject);
	}
	
	public String signXML() {
		
		Variant signOperationMsg = Dispatch.call(xadesAppComponent, "Sign", "FIIT_STU_Bratislava", "sha256", "urn:oid:1.3.158.36061701.1.2.1");
		
		return xadesAppComponent.getProperty("SignedXmlWithEnvelope").getString();
	}
	
	private void initComponent(ActiveXComponent component, String programId) throws IOException {
		
		File temporaryDll = null;
		InputStream inputStream = null;
		try {
			inputStream = getClass().getResourceAsStream(getJacobDllName());
			temporaryDll = createTmpResource(inputStream);

			System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll.getAbsolutePath());
			LibraryLoader.loadJacobLibrary();

			component = new ActiveXComponent(programId);
		} finally {
			if(inputStream != null) {
				inputStream.close();
			}
			
			if(temporaryDll != null) {
				temporaryDll.deleteOnExit();
			}
}
	}
	
	private String getJacobDllName() {
		
		if (System.getProperty("os.arch").equals("amd64")) {
			
			return "/jacob/" + JACOB_DLL_X64;
		}
		return "/jacob/" + JACOB_DLL_X86;
	}
	
	private File createTmpResource(InputStream inputStream) throws IOException {
		File temporaryDll = null;
		FileOutputStream outputStream = null;
		try {
			temporaryDll = File.createTempFile("jacob", ".dll");
			outputStream = new FileOutputStream(temporaryDll);
			byte[] array = new byte[8192];
			for (int i = inputStream.read(array); i != -1; i = inputStream.read(array)) {
				outputStream.write(array, 0, i);
			}
		} finally {
			if(outputStream != null){
				outputStream.close();
			}
		}
		
		return temporaryDll;
	}
}
