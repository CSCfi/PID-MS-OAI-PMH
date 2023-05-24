package fi.csc.pid.oai;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.StringReader;

public class ValidateXML {

    /* huomaa: toinen luettavista shcemoita includella ja toinen importilla: monimutkaista!
    toisessa täytyy olla namespace ja toisessa ei saa olla, jatkossa olisi import */
static final String W3C_XSD_TOP_ELEMENT =
"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
+ "<xs:schema targetNamespace=\"http://www.openarchives.org/OAI/2.0/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">\n"
+ "<xs:include schemaLocation=\"/home/pj/tyo/pid/oai-pmh/OAI-PMH.xsd\"/>\n"
+ "<xs:import schemaLocation=\"/home/pj/tyo/pid/oai-pmh/oai_dc.xsd\" namespace=\"http://www.openarchives.org/OAI/2.0/oai_dc/\"/>\n"
+"</xs:schema>";

    static boolean validate(String file) {
        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            //new File("/home/pj/tyo/pid/oai-pmh/OAI-PMH.xsd")
            Schema schema = factory.newSchema(new StreamSource(new StringReader(W3C_XSD_TOP_ELEMENT), "xsdTop"));
            Validator validator = schema.newValidator();
            PekanErrorHandler peh = new PekanErrorHandler();
            validator.setErrorHandler(peh);
            validator.validate(new StreamSource(new File("/home/pj/tyo/pid/oai-pmh/validoi/"+file)));
            return true;
        }
        catch(Exception ex) {
            System.err.println(ex.toString());
            return false;
        }
    }

    public static void main(String[] args) {
        if (validate(args[0])) {
           System.out.println("Valid!");
        } else {
            System.out.println("EI kelpaa");
        }
    }
}

class PekanErrorHandler implements ErrorHandler {
     public void fatalError( SAXParseException e ) throws SAXException {
         System.err.println("Fatal: "+e.toString() );
         System.err.println(e.getLineNumber());
         throw e;
     }
     public void error( SAXParseException e ) throws SAXException {
         System.err.println(e.toString() );
         System.err.println(e.getLineNumber());
         throw e;
     }
     public void warning( SAXParseException e ) throws SAXException {
         System.err.println("Varoitus: "+e.toString() );
         System.err.println(e.getLineNumber());
     }
 }