package fi.csc.pid.oai;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import fi.csc.pid.oai.model.Tietue;
import org.jboss.logging.Logger;

public class SQL {
    private static final String QUERY = "SELECT dp.identifier_string, "+
            "    dp.created, dp.internal_id, " +
            "dci.digital_object_type, " +
            "dps.pid_type, " +
            "dps.landing_page, " +
            "u.url AS identifier_url, " +
            "ds.name AS service_name, " +
            "u2.url AS service_url, " +
            "org.name AS id_owner_service_organization_name " +
            "FROM fact_pid_interlinkage AS f " +
            "INNER JOIN dim_PID AS dp ON f.dim_PIDinternal_id = dp.internal_id " +
            "INNER JOIN dim_CSC_info AS dci ON f.dim_CSC_infoPIDMiSe_suffix = dci.PIDMiSe_suffix " +
            "INNER JOIN dim_url AS u ON f.url_id = u.id " +
            "INNER JOIN dim_pid_scheme AS dps ON dp.dim_pid_scheme_id = dps.id " +
            "INNER JOIN dim_service AS ds ON dp.dim_serviceid = ds.id " +
            "INNER JOIN dim_url AS u2 ON ds.url_id = u2.id " +
            "INNER JOIN br_dim_service_dim_organization AS bdsdo ON ds.id = bdsdo.dim_serviceid " +
            "INNER JOIN dim_organization AS org ON bdsdo.dim_organizationid = org.id";

    //private static final String  SUURINQUERY = "SELECT MAX(internal_id) FROM dim_PID";

    private static long suurin = 0;
    private static Connection conn;

     private static final Logger LOG = Logger.getLogger(SQL.class);

    /**
     * Lukee tietokannan yllä olevalla tehokkaalla kyselyllä (yes, 17 riviä SQLaa!!)
     *
     * @param conn Connection SQL connection from datasource
     * @return Record[] databasecontent as Record-objects
     */
    public static Tietue[] hae(Connection conn) {

        Statement stmt;
        try {
            try {
            if (!conn.isValid(1)) {
                LOG.error("Tarkistalisäys: Yhteyden validiustarkistus ei mennyt läpi (ja mitäs nyt tehdään???)");
            }
            } catch (java.sql.SQLException e) {
                LOG.error("Tarkistalisäys: Yhteyden validiustarkistus aiheutti virheen " + e);
            }
            try {
                stmt =  conn.createStatement();
            } catch (java.sql.SQLNonTransientConnectionException e) {
                LOG.info("createStatement epäonnistui ekalla kertaa, yritetään uudestaan");
                stmt =  conn.createStatement();
                LOG.info("Toka luonti onnistui");
            }
            ResultSet rs;
            try {
                rs = stmt.executeQuery(QUERY);
            } catch (java.sql.SQLNonTransientConnectionException e) {
                LOG.info("The First try on query failed");
                rs = stmt.executeQuery(QUERY);
                LOG.info("Toka kysely onnistui");
            }
            ArrayList<Tietue> al = new   ArrayList<Tietue>(30000);
            while(rs.next()) {
                long iid = rs.getLong(3);
                Tietue fb = new Tietue(iid, rs.getString(1), rs.getDate(2), rs.getString("identifier_url"));
                fb.setPid_type(rs.getString(5)); //dps.pid_type
                fb.setCreator(rs.getString("service_name"));
                fb.setPublisher(rs.getString("id_owner_service_organization_name"));
                al.add(fb);
                if (suurin < iid) {
                    suurin = iid;
                }
            }
            LOG.info("Suurin on "+suurin);
            rs.close();
            stmt.close();
            //LOG.info("SQL success");
             Tietue[] FBA = al.toArray(new Tietue[al.size()]);
            return FBA;
        } catch (java.sql.SQLException e){
            LOG.error("SQL error: "+e);
            e.printStackTrace();
        }
        LOG.warn("SQL erroria?");
        return null;
    }

    /*
    public static boolean tarkistalisäys() {
        boolean ontullutlisää = false;
        try {
            if (!conn.isValid(1)) {
                LOG.error("Tarkistalisäys: Yhteyden validiustarkistus ei mennyt läpi (ja mitäs nyt tehdään???)");
            }
            } catch (java.sql.SQLException e) {
                LOG.error("Tarkistalisäys: Yhteyden validiustarkistus aiheutti virheen " + e);
                e.printStackTrace();
            }
            try {

                Statement stmt = conn.createStatement();
                ResultSet rs;
                try {
                    rs = stmt.executeQuery(SUURINQUERY);
                } catch (java.sql.SQLNonTransientConnectionException e) {
                    LOG.info("The First try on MAX query failed");
                    rs = stmt.executeQuery(QUERY);
                    LOG.info("Toka MAX kysely onnistui");
                }
                if (rs.next()) {
                    if (rs.getLong(1) > suurin) {
                        ontullutlisää = true;
                    }
                } else {
                    LOG.error("No next Resultset!");
                }
                rs.close();
                stmt.close();
            } catch (java.sql.SQLException e) {
                LOG.error("tarkistalisäsy error: " + e);
                e.printStackTrace();
            }
        return ontullutlisää;
    }

    public static Connection getConnection() {
       return conn;
    }

     */
}
