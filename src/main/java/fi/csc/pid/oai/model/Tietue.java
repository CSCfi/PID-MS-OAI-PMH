package fi.csc.pid.oai.model;

import io.quarkus.qute.TemplateData;
import io.quarkus.qute.TemplateExtension;

import java.time.LocalDate;

/**
 * Original neme was Record
 */


@TemplateData
@TemplateExtension(namespace = "tietue")
public class Tietue {

    long id;
    String identifier;
    LocalDate date;
    String identifier_url;
    String pid_type;
    String creator;
    String publisher;


    public Tietue(long id, String identifier, java.sql.Date date, String identifier_url) {
        this.id = id;
        this.identifier = identifier;
        this.date = date.toLocalDate();
        this.identifier_url = entities(identifier_url);
    }

    String entities(String orig) {
        return orig.replaceAll("&", "&amp;amp;");
    }

    public String getIdentifier() {
        return identifier;
    }
    public String getIdentifier(Tietue t) {
        return t.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getIdentifier_url() {
        return identifier_url;
    }

    public void setIdentifier_url(String identifier_url) {
        this.identifier_url = identifier_url;
    }

    public String getPid_type() {
        return pid_type;
    }

    public void setPid_type(String pid_type) {
        this.pid_type = pid_type;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String aika() { return date.toString();}
    public String epäsuoraaika(Tietue t) { return t.date.toString();}
}
