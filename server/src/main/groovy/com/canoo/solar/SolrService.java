package com.canoo.solar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.ValueChangedCommand;
import org.opendolphin.core.server.*;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;
import org.opendolphin.core.server.comm.SimpleCommandHandler;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.DateUtil;
import org.apache.solr.core.CoreContainer;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.*;
import com.canoo.solar.Constants.*;


public class SolrService {
    private static final String SOLR_INDEX_DIR = "/solr";
    private static final String CORE_NAME = "eeg";
    static ServerDolphin serverDolphin;
    private SolrServer solrServer;





    public SolrService() {
        super();

    }

    public SolrServer getSolrServer() throws SolrServerException {
        if (null == solrServer) {
            String solrHome = (SolrService.class.getResource(SOLR_INDEX_DIR)).getPath();
            CoreContainer coreContainer = new CoreContainer(solrHome);
            coreContainer.load();
            solrServer = new EmbeddedSolrServer(coreContainer, CORE_NAME);
        }

        return solrServer;
    }


    public static void main (String[] args) throws SolrServerException, FileNotFoundException, IOException{
        long start = System.currentTimeMillis();
        SolrService solrService = new SolrService();
        SolrServer solrServer2 = solrService.getSolrServer();

//		solrService.indexAll();


        SolrQuery solrQuery = new SolrQuery("*:*" );    //get all
        solrQuery.setStart(0);                          //startign index
        solrQuery.setRows(50);                          //number of rows
        //solrQuery.setSort("latitude", ORDER.asc);        //sort by their latitude in ascending order
        //solrQuery.addSort(PowerPlant._ATTR_starting, ORDER.desc);   //secondarily sort by the starting in a descending order

        QueryResponse response = solrServer2.query(solrQuery);          //make a response based on the above query


        System.out.println("Time take to query everything: " + (System.currentTimeMillis() - start) + "ms");


        DateFormat dateFormat = DateUtil.getThreadLocalDateFormat();
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));                       //create and instantiate a dateformat
        String startDate = dateFormat.format(new Date(2000 - 1900, 0, 1));
        String endDate = dateFormat.format(new Date(2001 - 1900, 0, 1));


        solrQuery.setQuery("*:*");                                //get all data
        solrQuery.setStart(0);                                     //start at the beginning
        solrQuery.setSort("latitude", ORDER.asc);     //sort the query in ascending order based on the latitude value
        solrQuery.setQuery("starting"+":[" + startDate + " TO " + endDate +  "]" );    //add additional sorting based on the starting time and in descending order this time
        response = solrServer2.query(solrQuery);                       //creates response from query
//
        System.out.println("hits between 2000 and 2001 : " + response.getResults().getNumFound());
        SolrDocumentList results = response.getResults();
        System.out.println("results: " + results.size());
//
        long start2 = System.currentTimeMillis();
        solrQuery = new SolrQuery("*:*" );
        solrQuery.setRows(0);
        solrQuery.setFacetLimit(-1);
        solrQuery.setFacet(true);
        solrQuery.setParam("facet.field", "city");
        solrQuery.addFilterQuery("plantType:Biomasse");
        solrQuery.setFacetSort(true);

        response = solrServer2.query(solrQuery);
        FacetField field = response.getFacetField("city");
        System.out.println("\nTime taken to query all cities and count Biomasse plants in each: "+ (System.currentTimeMillis() - start2) + "ms");
        System.out.println("\nCities with more than 30 Biomasse plants:");
        List<FacetField.Count> values = field.getValues();

        for(FacetField.Count count : values){
            if (count.getCount()>30)

                System.out.println(count.getCount() + " " + count.getName());
        }

        solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(0);
        solrQuery.setFacetLimit(-1);
        solrQuery.setFacet(true);
        solrQuery.setParam("facet.field", "plantType");
        solrQuery.addFilterQuery("city:Berlin");
        solrQuery.setFacetSort(true);

        response = solrServer2.query(solrQuery);
        FacetField field2 = response.getFacetField("plantType");
        System.out.println("\nPlants in Berlin:");
        List<FacetField.Count> values2 = field2.getValues();

        for(FacetField.Count count : values2){
            if (count.getCount()>0)

                System.out.println(count.getCount() + " " + count.getName());
        }

        solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(0);
        solrQuery.setFacetLimit(-1);
        solrQuery.setFacet(true);
        solrQuery.setParam("facet.field", "plantType");
        solrQuery.addFilterQuery("nominalPower:[* TO 1000]");
        solrQuery.setFacetSort(true);
        response = solrServer2.query(solrQuery);
        FacetField fieldPower = response.getFacetField("plantType");
        System.out.println("\nPlants with nominal power between * and 1000:");
        List<FacetField.Count> valuesPower = fieldPower.getValues();

        for(FacetField.Count count : valuesPower){
            if (count.getCount()>0)

                System.out.println(count.getCount() + " " + count.getName());
        }

        solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(0);
        solrQuery.setFacetLimit(-1);
        solrQuery.setFacet(true);
        solrQuery.setParam("facet.field", "city");
        solrQuery.addFilterQuery("voltageLevel:[0 TO 500]");
        solrQuery.setFacetSort(true);
        response = solrServer2.query(solrQuery);
        FacetField fieldVoltage = response.getFacetField("city");
        System.out.println("\nPlants with voltage level between 0 and 500:");
        List<FacetField.Count> valuesVoltage = fieldVoltage.getValues();

        for(FacetField.Count count : valuesVoltage){
            if (count.getCount()>2000)

                System.out.println(count.getCount() + " " + count.getName());
        }

        solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(0);
        solrQuery.setFacetLimit(-1);
        solrQuery.setFacet(true);
        // solrQuery.setQuery("city:B[*]");
        response = solrServer2.query(solrQuery);

        System.out.println(response.getResults());

        solrServer2.shutdown();
    }

    public class PowerPlantDTO {
        private static final String ID = "id";
        private static final String POSITION = "position";
        private static final String INBETRIEBNAHME = "starting";
        private static final String GEODEBID = "geoDBId";
        private static final String PLZ = "zipCode";
        private static final String ORT = "city";
        private static final String STRASSE = "street";
        private static final String ANLAGENSCHLUESSEL = "plantKey";
        private static final String ANLAGENTYP = "plantType";
        private static final String ANLAGENUNTERTYP = "subtype";
        private static final String NENNLEISTUNG = "nominalPower";
        private static final String EINSPEISUNGSEBENE = "voltageLevel";
        private static final String DSO_EIC = "DSO_EIC";
        private static final String DSO = "DSO";
        private static final String TSO_EIC = "TSO_EIC";
        private static final String TSO = "TSO";
        private static final String KWH = "actual_kWh";
        private static final String KWH_AVRG = "average_kWh";
        private static final String KWH_KW = "kWh_By_kW";
        private static final String EEG_VERGUETUNGSCHLUESSEL = "compensationKey";
        private static final String GEMEINDESCHLUESSEL = "regionCode";
        private static final String GPS_LAT = "latitude";
        private static final String GPS_LON = "longitude";
        private static final String GPS_GENAUIGKEIT = "GPS_Precision";
        private static final String VALIDIERUNG = "validation";

        @Field(ID)
        private String id;

        @Field(POSITION)
        private int position;

        @Field(INBETRIEBNAHME)
        private Date Inbetriebnahme;

        @Field(GEODEBID)
        private String GeoDBID;

        @Field(PLZ)
        private String plz;

        @Field(ORT)
        private String Ort;

        @Field(STRASSE)
        private String Strasse;

        @Field(ANLAGENSCHLUESSEL)
        private String Anlagenschluessel;

        @Field(ANLAGENTYP)
        private String Anlagentyp;

        @Field(ANLAGENUNTERTYP)
        private String Anlagenuntertyp;

        @Field(NENNLEISTUNG)
        private float Nennleistung;

        @Field(EINSPEISUNGSEBENE)
        private String Einspeisespannungsebene;

        @Field(DSO_EIC)
        private String dso_eic;

        @Field(DSO)
        private String dso;

        @Field(TSO_EIC)
        private String tso_eic;

        @Field(TSO)
        private String tso;

        @Field(KWH)
        private float kWh;

        @Field(KWH_AVRG)
        private float kWh_avrg;

        @Field(KWH_KW)
        private float kWh_kW;

        @Field(EEG_VERGUETUNGSCHLUESSEL)
        private String EEG_Verguetungsschluessel;

        @Field(GEMEINDESCHLUESSEL)
        private String Gemeindeschluessel;

        @Field(GPS_LAT)
        private float GPS_Lat;

        @Field(GPS_LON)
        private float GPS_Lon;

        @Field(GPS_GENAUIGKEIT)
        private int GPS_Genauigkeit_dm;

        @Field(VALIDIERUNG)
        private String Validierung;

        /*public PowerPlantDTO(UniqueId id, int position, String inbetriebnahme, String geoDBID, String plz, String ort, String strasse,
                String anlagenschluessel, String anlagentyp, String anlagenuntertyp, float nennleistung, String einspeisespannungsebene,
                String dso_eic, String dso, String tso_eic, String tso, float kWh, float kWh_avrg, float kWh_kW,
                String eEG_Verguetungsschluessel, String gemeindeschluessel, float gPS_Lat, float gPS_Lon, int gPS_Genauigkeit_dm,
                String validierung) {
            super();
            this.id = id.toHexString();
            this.position = position;
            try {
                Inbetriebnahme = new SimpleDateFormat("dd.MM.yyyy").parse(inbetriebnahme);
            } catch (ParseException e) {
                System.out.println("wrong date format" + inbetriebnahme);
                Inbetriebnahme = new Date();
            }
            GeoDBID = geoDBID;
            this.plz = plz;
            Ort = ort;
            Strasse = strasse;
            Anlagenschluessel = anlagenschluessel;
            Anlagentyp = anlagentyp;
            Anlagenuntertyp = anlagenuntertyp;
            Nennleistung = nennleistung;
            Einspeisespannungsebene = einspeisespannungsebene;
            this.dso_eic = dso_eic;
            this.dso = dso;
            this.tso_eic = tso_eic;
            this.tso = tso;
            this.kWh = kWh;
            this.kWh_avrg = kWh_avrg;
            this.kWh_kW = kWh_kW;
            EEG_Verguetungsschluessel = eEG_Verguetungsschluessel;
            Gemeindeschluessel = gemeindeschluessel;
            GPS_Lat = gPS_Lat;
            GPS_Lon = gPS_Lon;
            GPS_Genauigkeit_dm = gPS_Genauigkeit_dm;
            Validierung = validierung;
        }*/

        public String getId() {
            return id;
        }

        public int getPosition(){
            return position;
        }

        public Date getInbetriebnahme() {
            return Inbetriebnahme;
        }

        public String getGeoDBID() {
            return GeoDBID;
        }

        public String getPlz() {
            return plz;
        }

        public String getOrt() {
            return Ort;
        }

        public String getStrasse() {
            return Strasse;
        }

        public String getAnlagenschluessel() {
            return Anlagenschluessel;
        }

        public String getAnlagentyp() {
            return Anlagentyp;
        }

        public String getAnlagenuntertyp() {
            return Anlagenuntertyp;
        }

        public float getNennleistung() {
            return Nennleistung;
        }

        public String getEinspeisespannungsebene() {
            return Einspeisespannungsebene;
        }

        public String getDso_eic() {
            return dso_eic;
        }

        public String getDso() {
            return dso;
        }

        public String getTso_eic() {
            return tso_eic;
        }

        public String getTso() {
            return tso;
        }

        public float getkWh() {
            return kWh;
        }

        public float getkWh_avrg() {
            return kWh_avrg;
        }

        public float getkWh_kW() {
            return kWh_kW;
        }

        public String getEEG_Verguetungsschluessel() {
            return EEG_Verguetungsschluessel;
        }

        public String getGemeindeschluessel() {
            return Gemeindeschluessel;
        }

        public float getGPS_Lat() {
            return GPS_Lat;
        }

        public float getGPS_Lon() {
            return GPS_Lon;
        }

        public int getGPS_Genauigkeit_dm() {
            return GPS_Genauigkeit_dm;
        }

        public String getValidierung() {
            return Validierung;
        }



    }
}
