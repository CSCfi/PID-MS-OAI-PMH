package fi.csc.pid.oai;

import fi.csc.pid.oai.verbs.Identify;
import fi.csc.pid.oai.verbs.ListIdentifiers;
import fi.csc.pid.oai.verbs.ListMetadataFormats;
import fi.csc.pid.oai.verbs.ListRecords;
import io.agroal.api.AgroalDataSource;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.Template;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.jboss.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static fi.csc.pid.oai.verbs.List.IDENTIFY;
import static fi.csc.pid.oai.verbs.List.LISTIDENTIFIERS;
import static fi.csc.pid.oai.verbs.List.LISTMETADATAFORMATS;
import static fi.csc.pid.oai.verbs.List.LISTRECORDS;

/**
 * Pääohjelma
 *
 */
@Path("/v2/oai")
public class OAIPMHResource {

     @Inject
     AgroalDataSource defaultDataSource;

      private static final Logger LOG = Logger.getLogger(OAIPMHResource.class);

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance ListIdentifiersTemplate( ListIdentifiers li);
	}

    @GET
    @Produces(MediaType.TEXT_XML)
    public Response oai(@QueryParam String verb, @QueryParam String metadataPrefix, @QueryParam String from,
                        @QueryParam String until ) {
        switch (verb) {
            case LISTRECORDS:
                try {
                ListRecords lr = new ListRecords(SQL.hae(defaultDataSource.getConnection()));
                return ResponseOk(lr.listRecords(metadataPrefix, from, until));
                 } catch (java.sql.SQLException e){
                    LOG.error("SQL error: "+e);
                }
            case IDENTIFY:
                return ResponseOk(Identify.identify());
            case LISTMETADATAFORMATS:
                return ResponseOk(ListMetadataFormats.listMetadataFormats());
            // alkaa ehkä toimia quarkus 1.13 versiossa!
            case LISTIDENTIFIERS:
                try {
                ListIdentifiers li = new ListIdentifiers(SQL.hae(defaultDataSource.getConnection()), metadataPrefix, from);
                return ResponseOk(Templates.ListIdentifiersTemplate(li).render());
                } catch (java.sql.SQLException e){
                    LOG.error("LI SQL error: "+e);
                }
            default:
                return ResponseOk(XML.error("badVerb", "Bad verb. '" + verb + "' not implemented"));
        }
    }

    private Response ResponseOk(String content) {
        return Response.ok(content).header("X-Frame-Options", "DENY")
                .header("Content-Security-Policy", "default-src 'self'")
                .header("Strict-Transport-Security", "max-age=31536000")
                .header("X-Content-Type-Options", "nosniff")
                .header("X-XSS-Protection", "1; mode=block").build();

    }

}
