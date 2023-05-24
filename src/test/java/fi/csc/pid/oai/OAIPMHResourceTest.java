package fi.csc.pid.oai;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.hasItem;

@QuarkusTest
public class OAIPMHResourceTest {

    final static String PATH = "src/test/resources/";

    @Test
    public void testIdentifyEndpoint() {
        given()
          .when().get("/v2/oai?verb=Identify")
          .then()
             .statusCode(200)
             .body("baseURL", hasItem("https://oai-pmh.rahtiapp.fi/v2/oai"));
    }

    private String lueTiedostoLevyltä(String filename) {
        Path path = Path.of(PATH+filename);
        try {
            return new String(Files.readAllBytes(path));
                    /*replaceAll(Matcher.quoteReplacement("\n"), "\n").
                    replaceAll(Matcher.quoteReplacement("\""),"\"");*/
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}