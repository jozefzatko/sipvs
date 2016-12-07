package sk.fiit.sipvs.sv.verify;

/**
 * Generic exception for invalid Xades-T advanced electronic signature
 */
public class DocumentVerificationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6663323796434649460L;

	
	public DocumentVerificationException(String s) {
		
		super(s);
	}
	
	public DocumentVerificationException(String s, Exception e) {
		
		super(s, e);
	}
}
