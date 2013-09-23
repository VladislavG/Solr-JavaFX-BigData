package com.canoo.solar

import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.response.FacetField
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.core.CoreContainer
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.DataCommand
import org.opendolphin.core.comm.GetPresentationModelCommand
import org.opendolphin.core.comm.InitializeAttributeCommand
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.Slot;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler
import org.opendolphin.core.server.comm.NamedCommandHandler
import org.opendolphin.core.server.comm.SimpleCommandHandler;

import static com.canoo.solar.Constants.CMD.*;
import static com.canoo.solar.Constants.FilterConstants.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ApplicationAction extends DolphinServerAction{
    private static final String SOLR_INDEX_DIR = "solr";
    private static final String CORE_NAME = "eeg";
    private SolrServer solrServer;


    public void registerIn(ActionRegistry registry) {
        registry.register(GET, filter)
        registry.register(GET_CITIES, getCitites)
        registry.register(GET_TYPE, getTypes)
        registry.register(ValueChangedCommand.class, trigger)

        registry.register(GetPresentationModelCommand.class, new CommandHandler<GetPresentationModelCommand>() {
            private InitializeAttributeCommand createInitializeAttributeCommand(String pmId, String attributeName, Object attributeValue) {
                return new InitializeAttributeCommand(pmId, attributeName, null, attributeValue, "PowerPlant")
            }
            public void handleCommand(GetPresentationModelCommand cmd, List<Command> response) {
                String pmId = cmd.pmId
                if (pmId == null) {
                    return
                }
                if (getServerDolphin().getAt(pmId) == null) {
                    def start = System.currentTimeMillis()
                    SolrQuery solrQuery = new SolrQuery("position:" + pmId);
                    solrQuery.setRows(1);
                    QueryResponse solrResponse = getSolrServer().query(solrQuery);
                    def result = solrResponse.getResults().get(0)

                    println "Solr took " + (System.currentTimeMillis() -  start )
                    response.add(createInitializeAttributeCommand(pmId, "id", result.getFieldValue("id")))
                    response.add(createInitializeAttributeCommand(pmId, "position", result.getFieldValue("position")))
                    response.add(createInitializeAttributeCommand(pmId, "nominalPower", result.getFieldValue("nominalPower")))
                    response.add(createInitializeAttributeCommand(pmId, "plantType", result.getFieldValue("plantType")))
                    response.add(createInitializeAttributeCommand(pmId, "city", result.getFieldValue("city")))
                    response.add(createInitializeAttributeCommand(pmId, "zipCode", result.getFieldValue("zipCode")))


//                    presentationModel(pmId, "PowerPlant", createPowerPlant(result.getFieldValue("id"), result.getFieldValue("position"), result.getFieldValue("nominalPower"), result.getFieldValue("plantType"), result.getFieldValue("city"), result.getFieldValue("zipCode")))
                }
            }
        })
    }
    private final CommandHandler trigger = new CommandHandler<ValueChangedCommand>() {

        @Override
        public void handleCommand(final ValueChangedCommand command, final List<Command> response) {
            PresentationModel filterPm = getServerDolphin()[FILTER]
            if(!filterPm.findAttributeById(command.attributeId))  return;
            changeValue getServerDolphin()[STATE][TRIGGER], (getServerDolphin()[STATE][TRIGGER].value)+2

        }
    }
    private final NamedCommandHandler filter = new NamedCommandHandler() {

        @Override
        void handleCommand(NamedCommand command, List<Command> response) {


            def filterPM = getServerDolphin().findPresentationModelById(FILTER)
            SolrQuery solrQuery = new SolrQuery("*:*")
            solrQuery.addField("position")
            solrQuery.setSort("position", SolrQuery.ORDER.asc)

            filterPM.attributes.each {
                if (it.value=="" || it.value==null) it.value = "*"
                solrQuery.addFilterQuery(it.getPropertyName() + ":" + it.value)
            }

            solrQuery.setRows(100)
            def start = System.currentTimeMillis()
            QueryResponse queryResponse = getSolrServer().query(solrQuery)
            def result = queryResponse.getResults()
            List<Integer> allPositions = new ArrayList<>()
            result.each {
                allPositions << it.getFieldValue("position")
            }
            response.add(new DataCommand(new HashMap(ids: allPositions )))
        }
    }

    private final NamedCommandHandler getCitites = new NamedCommandHandler() {

        @Override
        void handleCommand(NamedCommand command, List<Command> response) {


            SolrQuery solrQuery = new SolrQuery("*:*")
            solrQuery.setRows(0);
            solrQuery.setFacetLimit(-1);
            solrQuery.setFacet(true);
            solrQuery.setParam("facet.field", "city");
            solrQuery.setFacetSort(true);


            QueryResponse queryResponse = getSolrServer().query(solrQuery)
            List<String> allCities = new ArrayList<>()
            FacetField field = queryResponse.getFacetField("city");
            List<FacetField.Count> values = field.getValues();

            for(FacetField.Count count : values){

                allCities << count.getName()
            }


            response.add(new DataCommand(new HashMap(ids: allCities )))
        }
    }

    private final NamedCommandHandler getTypes = new NamedCommandHandler() {

        @Override
        void handleCommand(NamedCommand command, List<Command> response) {


            SolrQuery solrQuery = new SolrQuery("*:*")
            solrQuery.setRows(0);
            solrQuery.setFacetLimit(-1);
            solrQuery.setFacet(true);
            solrQuery.setParam("facet.field", "plantType");


            QueryResponse queryResponse = getSolrServer().query(solrQuery)
            List<String> allTypes = new ArrayList<>()
            FacetField field = queryResponse.getFacetField("plantType");
            List<FacetField.Count> values = field.getValues();

            for(FacetField.Count count : values){

                allTypes << count.getName()
            }


            response.add(new DataCommand(new HashMap(ids: allTypes )))
        }
    }


    private static DTO createPowerPlant(id, position, nominalPower, plantType, city, zipCode) {
        new DTO(

                createSlot("id", id, position),
                createSlot("position", position, position),
                createSlot("nominalPower", nominalPower, position),
                createSlot("plantType", plantType, position),
                createSlot("city", city, position),
                createSlot("zipCode", zipCode, position) )
    }

    private static Slot createSlot(String propertyName, Object value, int id) {
        new Slot(propertyName, value, id + '/' + propertyName)
    }
    private SolrServer getSolrServer() throws SolrServerException {
        if (null == solrServer) {
            String solrHome = (ApplicationAction.class.getResource("/"+SOLR_INDEX_DIR)).getPath();
            CoreContainer coreContainer = new CoreContainer(solrHome);
            coreContainer.load();
            solrServer = new EmbeddedSolrServer(coreContainer, CORE_NAME);
        }

        return solrServer;
    }



}
