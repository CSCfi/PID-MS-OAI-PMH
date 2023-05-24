package fi.csc.pid.oai.verbs;

import fi.csc.pid.oai.XML;

import static fi.csc.pid.oai.XML.BASEURL;
import static fi.csc.pid.oai.XML.ETAGA;
import static fi.csc.pid.oai.XML.NL;
import static fi.csc.pid.oai.XML.OAI_PMH_END;
import static fi.csc.pid.oai.XML.REQUESTEND;
import static fi.csc.pid.oai.XML.REQUESTVERB;
import static fi.csc.pid.oai.XML.TAGA;
import static fi.csc.pid.oai.XML.TAGEND;
import static fi.csc.pid.oai.verbs.List.LISTMETADATAFORMATS;
import static fi.csc.pid.oai.verbs.List.LM;

public class ListMetadataFormats {

    final static public String OAIDC = "oai_dc";
    final static String REQUEST = REQUESTVERB+LISTMETADATAFORMATS +LM+TAGEND + BASEURL + REQUESTEND;

    final static String LMF = TAGA +LISTMETADATAFORMATS+TAGEND+NL+
   "<metadataFormat>"+NL+
     "  <metadataPrefix>oai_dc</metadataPrefix>"+NL+
     "  <schema>http://www.openarchives.org/OAI/2.0/oai_dc.xsd </schema>"+NL+
     "  <metadataNamespace>http://www.openarchives.org/OAI/2.0/oai_dc/</metadataNamespace>"+NL+
   "</metadataFormat>"+NL;

    final static String XMLEND = ETAGA+LISTMETADATAFORMATS+TAGEND+NL+OAI_PMH_END;

    public static String listMetadataFormats() {
        XML xml = new XML();
        StringBuilder sb = xml.newBuilder(REQUEST);
        sb.append(LMF);
        sb.append(XMLEND);
        return sb.toString();
    }
}
