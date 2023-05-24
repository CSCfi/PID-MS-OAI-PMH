package fi.csc.pid.oai.model;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Aika {
    /*static final Duration localcachevalid = Duration.ofHours(6); // RA is valid

    static LocalDateTime edellinen;

    public static void setEdellinen(LocalDateTime now) {
        edellinen = now;
    }


    public static Tietue[] päivitä(Tietue[] ra) {
        if (LocalDateTime.now().minus(localcachevalid).isBefore(edellinen)) {
            return ra;
        } else { //aika päivittää
            return ApplicationLifecycle.päivitä();
        }

    }
    */
    public static String utcnow() {
        return ZonedDateTime.now(ZoneOffset.UTC).toString();
    }
}
