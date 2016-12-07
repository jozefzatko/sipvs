package sk.fiit.sipvs.ar.logic.sign;

import sk.ditec.TS;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.springframework.security.crypto.codec.Base64;

import java.io.IOException;

import static sk.fiit.sipvs.ar.logic.sign.DSignerBridge.logger;

/**
 * Created by Erik on 27.10.2016.
 */
public class TSAConnector {

    public String getTimestamp (String document){
        TS ts = new TS();
        String timestampB64 = null;

        try {
            timestampB64 = ts.getTSSoap().getTimestamp(document);

            logger.info("Casova peciatka (base64): " + timestampB64);
        } catch (Exception e) {
            logger.error("Neobdrzana casova peciatka z Ditec TSA: ", e);
        }

        return timestampB64;
    }

    public String getTimeStampToken(String document) {

        TimeStampToken timestampToken = null;
        String timeStampB64 = getTimestamp(document);

        try {
            TimeStampResponse response = new TimeStampResponse(Base64.decode(timeStampB64.getBytes()));
            timestampToken = response.getTimeStampToken();

            logger.info("Cas generovania: " + timestampToken.getTimeStampInfo().getGenTime());
            logger.info("Presnost generovaneho casu: " + timestampToken.getTimeStampInfo().getGenTimeAccuracy().getMillis() + " ms");
            logger.info("TSA: " + timestampToken.getTimeStampInfo().getTsa());
            logger.info("Seriove cislo: " + timestampToken.getTimeStampInfo().getSerialNumber());
        } catch (TSPException | IOException e) {
            logger.error("Timestamp token nebol ziskany: " + e);
        }

        try {
            return new String(Base64.encode(timestampToken.getEncoded()));
        } catch (IOException e) {
            logger.error("Timestamp token sa neda encodnut: " + e);
        } catch (NullPointerException e) {
            logger.error("Timestamp nemoze byt encodnuty, pretoze nebol ziskany: " + e);
        }

        return null;
    }

}
