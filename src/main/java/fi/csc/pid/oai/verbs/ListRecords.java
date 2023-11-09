package fi.csc.pid.oai.verbs;

import fi.csc.pid.oai.XML;
import fi.csc.pid.oai.model.Tietue;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static fi.csc.pid.oai.XML.BASEURL;
import static fi.csc.pid.oai.XML.ETAGA;
import static fi.csc.pid.oai.XML.NL;
import static fi.csc.pid.oai.XML.OAIDC;
import static fi.csc.pid.oai.XML.OAI_PMH_END;
import static fi.csc.pid.oai.XML.REQUESTEND;
import static fi.csc.pid.oai.XML.REQUESTVERB;
import static fi.csc.pid.oai.XML.SISENNYS;
import static fi.csc.pid.oai.XML.TAGA;
import static fi.csc.pid.oai.XML.TAGEND;
import static fi.csc.pid.oai.verbs.List.LISTRECORDS;
import static fi.csc.pid.oai.verbs.List.LM;

public class ListRecords {

    final static String REQUESTALKU = REQUESTVERB+LISTRECORDS+LM;
    final static String REQUESTLOPPU = TAGEND + BASEURL +REQUESTEND;
    private static final Logger LOG = Logger.getLogger(ListRecords.class);

    Tietue[] ra;

    public ListRecords(Tietue[] fba) {
        this.ra = fba;
    }

     public String  listRecords(String metadataPrefix, String from, String until) {
        if (!tarkista(metadataPrefix)) {
            return XML.error("cannotDisseminateFormat", "MetadataPrefix not supported, we support only oai_dc");
        }
        if (null == ra) {
            XML xml = new XML();
           StringBuilder sb = xml.newBuilder(parametrit(metadataPrefix, from, until));
            sb.append("<ListRecords/>");
            sb.append(OAI_PMH_END);
            return sb.toString();
        }

        ra = tarkistafrom(from);
        ra = tarkistauntil(until);
        if (null == ra)
            return XML.error("badArgument", "from or until can't parsed.");
        XML xml = new XML();
        StringBuilder sb = xml.newBuilder(parametrit(metadataPrefix, from, until));
        sb.append("<ListRecords>");
        sb.append(Arrays.stream(ra).filter(t -> t.identifier != null)
                .filter(t -> t.identifier.startsWith("urn:nbn:fi:")).map(this::käsittele).collect(Collectors.joining()));
        sb.append("</ListRecords>");
        sb.append(OAI_PMH_END);
        return sb.toString();
    }

    /**
     * Tuottaa vastauksen request tagin atribuutit
     *
     * @param metadataPrefix String käyttäjän syöte esim. oai_dc
     * @param from String käyttäjän syöte UTC alkuaika
     * @param until String käyttäjän syöte UTC loppuaika
     * @return String kootut parametrit esim. from="1998-01-15"
     */
    private String parametrit(String metadataPrefix, String from, String until) {
        StringBuilder sb = new  StringBuilder(REQUESTALKU);
        parametritulostus(sb, "metadataPrefix", metadataPrefix);
        parametritulostus(sb, "from", from);
        parametritulostus(sb, "until", until);
        sb.append(REQUESTLOPPU);
        return sb.toString();
    }

    /**
     * filter records according from parameter
     *
     * @param from String like 2021-01-01T15:40:42Z
     * @return Record[] which are after from
     */
    private Tietue[] tarkistafrom(String from) {
        if (null == from) {
            LOG.warn("from was null");
            return ra; // from is optional, no problem when it's missing
        }
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(from);
            //LOG.info("ztd was "+zdt);
            return Arrays.stream(ra).filter(r -> zdt.isBefore(r.getDate().atStartOfDay(ZoneId.of("UTC")))).toArray(Tietue[]::new);
        } catch (DateTimeParseException e) {
            LOG.warn("Error in parsing from: "+e);
            return null;
        }
    }


    /**
     * Vaikka protokollassa metadataPrefix on REQUIRED parameteri, me emme vaadi sitä,
     * eli sekä tyhjä ja oai_dc kelpaavat, kaikki muu johtaa virheeseen.
     *
     * @param metadataPrefix String MUST be oai_dc (but we accept missing one too)
     * @return Boolean case supported true, all rubbish false
     */
    private boolean tarkista(String metadataPrefix) {
        if (null ==  metadataPrefix)
            return true; // tämä on väärin protokollan mukaan
        return metadataPrefix.equals("oai_dc"); //ainut tukemamme!
    }

    /**
     * Convert Record object to XML string
     *
     * @param fb Record Object to convert
     * @return String XML
     */
    String käsittele(Tietue fb) {
        if (null != fb) {
            StringBuilder sb = new  StringBuilder("  <record>");
            sb.append(NL); sb.append(SISENNYS);
            sb.append("<header>"); sb.append(NL);
            lisää(sb, Long.toString(fb.getId()), "identifier");
            LocalDate zdt = fb.getDate();
            lisää(sb, zdt.toString(), "datestamp");
            sb.append(SISENNYS);
            sb.append("</header>"); sb.append(NL);
            sb.append(SISENNYS); sb.append("<metadata>");
            sb.append(NL);sb.append(OAIDC);
            lisää(sb, fb.getCreator(), "dc:creator");
            lisää(sb, fb.getPublisher(), "dc:publisher");
            lisää(sb, escape(fb.getPid_type()), "dc:type");
            lisää(sb, fb.getIdentifier(), "dc:identifier");
            lisää(sb, fb.getIdentifier_url(), "dc:identifier");
            // Vaihtoehtoja edelliselle subject, title, description...
            lisää(sb, zdt.toString(), "dc:date");
            sb.append(SISENNYS); sb.append("  </oai_dc:dc>"); sb.append(NL);
            sb.append(SISENNYS); sb.append("</metadata>"); sb.append(NL);
            sb.append("  </record>"); sb.append(NL);
            return sb.toString();
        }
        return "Error, fb was null ";
    }

    private String escape(String pidType) {
        if (pidType.contains("&"))
            return pidType.replace("&", "&amp;");
        else return pidType;
    }

    /**
     * Lisää merkkijonoon tagi-parin, jonka sisällä on arvo
     * siis muodostaa <tag>arvo</tag> rakenteen
     *
     * @param sb StringBuilder merkkojono, johon lisätään
     * @param arvo String sisältö
     * @param tag String tag name
     */
    void lisää(StringBuilder sb, String arvo, String tag) {
        sb.append(SISENNYS);
        sb.append(TAGA);
        sb.append(tag);
        sb.append(TAGEND);
        sb.append(arvo);
        sb.append(ETAGA);
        sb.append(tag);
        sb.append(TAGEND);
        sb.append(NL);
    }

       /**
     * filter records according from parameter
     *
     * @param until String like 2021-01-01T15:40:42Z
     * @return Record[] which are after from
     */
    private Tietue[] tarkistauntil(String until) {
        if (null == until) {
            return ra; // until is optional, no problem when it's missing
        }
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(until);
            //LOG.info("ztd was "+zdt);
            return Arrays.stream(ra).filter(r -> zdt.isAfter(r.getDate().atStartOfDay(ZoneId.of("UTC")))).toArray(Tietue[]::new);
        } catch (DateTimeParseException e) {
            LOG.warn("Error in parsing until: "+e);
            return null;
        }
    }

    /**
     * Lisää merkkijonoon XML attribuutin
     *
     * @param sb StringBuilder merkkojono, johon lisätään
     * @param nimi String attribuutin nimi
     * @param arvo String attribuutin arvo, jos null ei tehdä mitään
     */
    void parametritulostus(StringBuilder sb, String nimi, String arvo) {
        if (null != arvo) {
            sb.append(" ");
            sb.append(nimi);
            sb.append("=");
            sb.append(LM);
            sb.append(arvo);
            sb.append(LM);
        }
    }
}
