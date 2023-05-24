package fi.csc.pid.oai.verbs;

import fi.csc.pid.oai.model.Aika;
import fi.csc.pid.oai.model.Tietue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fi.csc.pid.oai.verbs.ListMetadataFormats.OAIDC;
import static fi.csc.pid.oai.verbs.List.LM;

public class ListIdentifiers {
    public String now = Aika.utcnow();
    public String from;
    public String metadataPrefix;
    public Tietue[] ra;
    public List ral;

    public ListIdentifiers(Tietue[] ra, String metadataPrefix,String from) {
        this.ra = ra;
        ral = Arrays.stream(ra).collect(Collectors.toList());
        if (null == metadataPrefix || metadataPrefix.isEmpty()) //ei mahdollinen, sillä
            this.metadataPrefix = OAIDC; //tämä on required speksissä
        else {
            if (metadataPrefix.equals(OAIDC))
                this.metadataPrefix = metadataPrefix;
            else // pitäisi lähettää cannotDisseminateFormat
                this.metadataPrefix = "cannotDisseminateFormat";
        }
        this.from = from;
    }

    public boolean fromExists() {
        if (null == from || from.isEmpty()) {
            return false;
        }
        return true;
    }

    public String getFrom() {
        if (fromExists()) {
            return "from="+LM+from+LM;
        }
        return "";
    }

}
