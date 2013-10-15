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
        registry.register(GET_FIFTY, getPms)
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
                    SolrQuery solrQuery = new SolrQuery(POSITION + ":" + pmId);
                    solrQuery.setRows(1);
                    QueryResponse solrResponse = getSolrServer().query(solrQuery);
                    def result = solrResponse.getResults().get(0)

                    println "Solr took " + (System.currentTimeMillis() -  start )
                    response.add(createInitializeAttributeCommand(pmId, ID, result.getFieldValue(ID)))
                    response.add(createInitializeAttributeCommand(pmId, POSITION, result.getFieldValue(POSITION)))
                    response.add(createInitializeAttributeCommand(pmId, NOMINAL_POWER, result.getFieldValue(NOMINAL_POWER)))
                    response.add(createInitializeAttributeCommand(pmId, PLANT_TYPE, result.getFieldValue(PLANT_TYPE)))
                    response.add(createInitializeAttributeCommand(pmId, CITY, result.getFieldValue(CITY)))
                    response.add(createInitializeAttributeCommand(pmId, ZIP, result.getFieldValue(ZIP)))
                }
            }
        })
    }
    private final CommandHandler trigger = new CommandHandler<ValueChangedCommand>() {
        @Override
        public void handleCommand(final ValueChangedCommand command, final List<Command> response) {
            PresentationModel filterPm = getServerDolphin()[FILTER]
            PresentationModel statePm = getServerDolphin()[STATE]

            if(!filterPm.findAttributeById(command.attributeId))  return;
            changeValue statePm[TRIGGER], (statePm[TRIGGER].value)+2

        }
    }
    private final NamedCommandHandler filter = new NamedCommandHandler() {
        @Override
        void handleCommand(NamedCommand command, List<Command> response) {



            def filterPM = getServerDolphin().findPresentationModelById(FILTER)
            SolrQuery solrQuery = new SolrQuery("*:*")
            solrQuery.addField(POSITION)
            solrQuery.setSort(POSITION, SolrQuery.ORDER.asc)
            solrQuery.setFacet(true);
            filterPM.attributes.each {
                if (it.value=="" || it.value==null || it.value=="All") it.value = "*"
                solrQuery.addFilterQuery(it.getPropertyName() + ":" + it.value)
            }
            solrQuery.setParam("facet.field", CITY);
            solrQuery.addFacetField(PLANT_TYPE);
            solrQuery.addFacetField(ZIP)
            solrQuery.setRows(200000)
            solrQuery.setFacetLimit(Integer.MAX_VALUE)
            def start = System.currentTimeMillis()
            QueryResponse queryResponse = getSolrServer().query(solrQuery)
            def result = queryResponse.getResults()
            println "Solr took " + (System.currentTimeMillis() -  start )
            FacetField field = queryResponse.getFacetField(CITY);
            FacetField fieldtypes = queryResponse.getFacetField(PLANT_TYPE);
            FacetField fieldzip = queryResponse.getFacetField(ZIP);
            List<Integer> allPositions = new ArrayList<>()
            result.each {
                allPositions << it.getFieldValue(POSITION)
            }
            response.add(new DataCommand(new HashMap(ids: allPositions )))
            List<String> allCities = new ArrayList<>()
            List<String> allCitiesCount = new ArrayList<>()
            List<String> allTypes = new ArrayList<>()
            List<String> allTypesCount = new ArrayList<>()
            List<String> allZips = new ArrayList<>()
            List<String> allZipsCount = new ArrayList<>()

            List<FacetField.Count> values = field.getValues();
            List<FacetField.Count> valuestype = fieldtypes.getValues();
            List<FacetField.Count> valueszip = fieldzip.getValues();

            for(FacetField.Count count : values){
                allCities << count.getName()
                allCitiesCount << count.getCount()
            }
            for(FacetField.Count count : valuestype){
                allTypes << count.getName()
                allTypesCount << count.getCount()
            }
            for(FacetField.Count count : valueszip){
                allZips << count.getName()
                allZipsCount << count.getCount()
            }
            response.add(new DataCommand(new HashMap(ids: allTypes, numCount: allTypesCount )))
            response.add(new DataCommand(new HashMap(ids: allCities, numCount: allCitiesCount )))
            response.add(new DataCommand(new HashMap(ids: allZips, numCount: allZipsCount )))

        }
    }

    private final NamedCommandHandler getPms = new NamedCommandHandler() {
        private static InitializeAttributeCommand createInitializeAttributeCommand(String pmId, String attributeName, Object attributeValue) {
            return new InitializeAttributeCommand(pmId, attributeName, null, attributeValue, "PowerPlant")
        }
        @Override
        void handleCommand(NamedCommand command, List<Command> response) {
            Integer rowIdx = Integer.parseInt(getServerDolphin().findPresentationModelById(STATE).findAttributeByPropertyName(START_INDEX).getValue().toString())
            def reverse = getServerDolphin().findPresentationModelById(STATE).findAttributeByPropertyName(REVERSER_ORDER)
            if (rowIdx == null) {
                return
            }

            if (getServerDolphin().getAt(rowIdx.toString()) == null) {
                    def start = System.currentTimeMillis()
                    def filterPM = getServerDolphin().findPresentationModelById(FILTER)
                    SolrQuery solrQuery = new SolrQuery("*:*")
                if (reverse.getValue()) {
                    solrQuery.setSort(getServerDolphin().findPresentationModelById(STATE).findAttributeByPropertyName(SORT).getValue().toString(), SolrQuery.ORDER.desc)
                } else {
                    solrQuery.setSort(getServerDolphin().findPresentationModelById(STATE).findAttributeByPropertyName(SORT).getValue().toString(), SolrQuery.ORDER.asc)
                }
                    filterPM.attributes.each {
                        if (it.value=="" || it.value==null || it.value=="All") it.value = "*"
                        solrQuery.addFilterQuery(it.getPropertyName() + ":" + it.value)
                    }
                    solrQuery.setStart(rowIdx)
                    solrQuery.setRows(1);
                    QueryResponse solrResponse = getSolrServer().query(solrQuery);
                    def result = solrResponse.getResults().get(0)
                    println "currenty creating PM for index: $rowIdx"
                    println "Solr took " + (System.currentTimeMillis() -  start )
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), ID, result.getFieldValue(ID)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), POSITION, result.getFieldValue(POSITION)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), NOMINAL_POWER, result.getFieldValue(NOMINAL_POWER)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), PLANT_TYPE, result.getFieldValue(PLANT_TYPE)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), CITY, result.getFieldValue(CITY)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), ZIP, result.getFieldValue(ZIP)))

            }
        }
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
