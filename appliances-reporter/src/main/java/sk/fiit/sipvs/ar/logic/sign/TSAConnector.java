package sk.fiit.sipvs.ar.logic.sign;

import sk.ditec.TS;

import org.apache.log4j.Logger;

import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.springframework.security.crypto.codec.Base64;
import sk.fiit.sipvs.ar.logic.XMLSigner;

import java.io.File;
import java.io.IOException;

import static sk.fiit.sipvs.ar.logic.sign.DSignerBridge.logger;

/**
 * Created by Erik on 27.10.2016.
 */
public class TSAConnector {

    private static final String XML_FILE = "src//main//resources//file.xml";
    private static final String XSD_SCHEMA = "src//main//resources//appliances.xsd";
    private static final String XSLT_FILE = "src//main//resources//transformation.xslt";

    public String getTimestamp (String document){
        TS ts = new TS();

        String timestampB64 = null;
        timestampB64 = ts.getTSSoap().getTimestamp(document);

        return timestampB64;
    }

    public TimeStampToken getTimeStampToken(String message) {

        TimeStampToken timeStampToken = null;
        String timeStampBase64 = getTimestamp(message);

        byte[] responseByteData = Base64.decode(timeStampBase64.getBytes());

        try {
            TimeStampResponse response = new TimeStampResponse(responseByteData);
            timeStampToken = response.getTimeStampToken();

            logger.info("Serial number: " + timeStampToken.getTimeStampInfo().getSerialNumber());
            logger.info("TSA: " + timeStampToken.getTimeStampInfo().getTsa());
            logger.info("Gen time: " + timeStampToken.getTimeStampInfo().getGenTime());
        } catch (TSPException | IOException e) {
            logger.error("Cannot retrieve timestamp token: " + e.getLocalizedMessage());
        }

        return timeStampToken;
    }

    public String getTimeStampTokenBase64(String message) {

        TimeStampToken timeStampToken = getTimeStampToken(message);

        try {
            return new String(Base64.encode(timeStampToken.getEncoded()));
        } catch (IOException e) {
            logger.error("Cannot encode TimeStamp token: " + e.getLocalizedMessage());
        }

        return null;
    }

    public static void main(String[] args) {

        XMLSigner sc = new XMLSigner(XML_FILE, XSD_SCHEMA, XSLT_FILE);
        sc.run();
    }
}
