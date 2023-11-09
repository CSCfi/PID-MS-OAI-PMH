package fi.csc.pid.oai;

import fi.csc.pid.oai.model.Aika;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.apache.commons.text.StringEscapeUtils;

import static fi.csc.pid.oai.verbs.List.LM;

/**
 * XMLaa ei käsitellä tässä rakenteisena tietona vaan merkkijonona
 */
public class XML {
    public final static String TAGA = "<";
    public final static String ETAGA = "</";
    public final static String TAGEND = ">";

    public final static String NL = "\n";
    public final static String SISENNYS = "    ";
    public final static String BASEURL = "https://oai-pmh.2.rahtiapp.fi/v2/oai";
    public final static String XMLSTART = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">\n"+
            "  <responseDate>";
    public final static String RESPONSEDATE_END = "</responseDate>"+NL;
    public final static String OAI_PMH_END = "</OAI-PMH>"+NL;

    public final static String OAIDC = "<oai_dc:dc "+
          "xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" "+
          "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "+
          "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
          "xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ "+
          "http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">" + NL;

    public final static String REQUESTVERB = "  <request verb="+LM;
    public final static String REQUESTEND =  "</request>"+NL;
    public final static String ERRORCODE = "<error code="+LM;
    public final static String ERROREND =  "</error>"+NL;

    /**
     * Kaikille OAI-protokollan vastauksille yhteinen alku
     *
     * @param request String OAI-PMH verbi
     * @return StringBuilder joka on alustettu OAI-PMH vastauksen alulla
     */
    public StringBuilder newBuilder(String request) {
        StringBuilder sb = new  StringBuilder(XMLSTART);
        sb.append(Aika.utcnow());
        sb.append(RESPONSEDATE_END);
        sb.append(request);
        return sb;
    }

    /**
     * Virheviesti, alku niin sama kuin edellinen että pitäisi refaktoroida...
     *
     * @param koodi String OAI-PMH errorcode
     * @param viesti String Explanation of the error
     * @return String XML virheviesti
     */
    public static String error(String koodi, String viesti) {
        StringBuilder sb = new  StringBuilder(XMLSTART);
        sb.append(Aika.utcnow());
        sb.append(RESPONSEDATE_END);
        sb.append(ERRORCODE);
        sb.append(koodi);
        sb.append(LM);
        sb.append(TAGEND);
	    //StringEscapeUtils seu = StringEscapeUtils.Builder();
	    String turvallinen = StringEscapeUtils.escapeHtml4(viesti);
        sb.append(turvallinen);
        sb.append(ERROREND);
        sb.append(OAI_PMH_END);
        return sb.toString();
    }

}
