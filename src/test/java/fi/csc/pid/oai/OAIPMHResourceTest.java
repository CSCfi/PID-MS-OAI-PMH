package fi.csc.pid.oai;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
/*
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
*/
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
//import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.hasItem;

@QuarkusTest
public class OAIPMHResourceTest {

    //final static String PATH = "src/test/resources/";

    @Test
    public void testIdentifyEndpoint() {
        given()
          .when().get("/v2/oai?verb=Identify")
          .then()
             .statusCode(200)
             .body("baseURL", hasItem("https://oai-pmh.2.rahtiapp.fi/v2/oai"));
    }

    @Test
    public void testListMetadataFormatsEndpoint() {
        given()
          .when().get("/v2/oai?verb=ListMetadataFormats")
          .then()
             .statusCode(200)
                .body(containsString("oai_dc"));
    }
    @Test
    public void testListIdenfiersEndpoint() {
        given()
          .when().get("/v2/oai?verb=ListIdentifiers")
          .then()
             .statusCode(200)
                .body(containsString("<identifier>"));
    }

    @Test
    public void testGetRecorsEndpoint() {
        given()
          .when().get("/v2/oai?verb=ListRecords")
          .then()
             .statusCode(200)
                .body(containsString("<ListRecords>"));
    }
/*
    private String lueTiedostoLevyltä(String filename) {
        Path path = Path.of(PATH+filename);
        try {
            return new String(Files.readAllBytes(path));
                    /*replaceAll(Matcher.quoteReplacement("\n"), "\n").
                    replaceAll(Matcher.quoteReplacement("\""),"\"");*/
       /* } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    */
}