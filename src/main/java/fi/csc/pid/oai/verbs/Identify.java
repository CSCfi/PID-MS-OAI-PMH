package fi.csc.pid.oai.verbs;

import fi.csc.pid.oai.XML;

import java.time.LocalDateTime;

import static fi.csc.pid.oai.XML.*;
import static fi.csc.pid.oai.verbs.List.IDENTIFY;
import static fi.csc.pid.oai.verbs.List.LM;

/**
 * Toteuttaa OAI-PMH:n verbin Identify
 *
 * Validius tarkistettu myös schemaa vasten!:
 * [pj@tavi java]$ javac fi/csc/pid/oai/ValidateXML.java
[pj@tavi java]$ java fi.csc.pid.oai.ValidateXML identify.xml
Valid!
 */
public class Identify {

    final static String REQUEST = REQUESTVERB+ IDENTIFY+ LM + TAGEND + BASEURL + REQUESTEND;

    final static String IDENTIFY1 = TAGA + IDENTIFY + TAGEND + NL +
    "  <repositoryName>CSC - PIDMS</repositoryName>\n" +
    "<baseURL>" + BASEURL + "</baseURL>\n"+
    "<protocolVersion>2.0</protocolVersion>\n" +
    "<adminEmail>pj@csc.fi</adminEmail>\n";

    final static String IDENTIFY2 = "<earliestDatestamp>2016-05-23T15:49:00Z</earliestDatestamp>\n" +
    "<deletedRecord>persistent</deletedRecord>\n" +
            "<granularity>YYYY-MM-DD</granularity>\n";

    final static String XMLEND = ETAGA + IDENTIFY + TAGEND + NL  + OAI_PMH_END;

    /**
     * Tyhmä merkkijono
     *
     * @return String Identify verbin vaatiman validin XML:n
     */
    public static String identify() {
        XML xml = new XML();
        StringBuilder sb = xml.newBuilder(REQUEST);
        sb.append(IDENTIFY1);
        sb.append(IDENTIFY2);
        sb.append(XMLEND);
        return sb.toString();
    }
}
