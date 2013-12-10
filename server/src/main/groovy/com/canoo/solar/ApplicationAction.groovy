package com.canoo.solar

import org.apache.commons.collections.MultiHashMap
import org.apache.commons.collections.MultiMap
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
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler
import org.opendolphin.core.server.comm.NamedCommandHandler

import static com.canoo.solar.Constants.CMD.*;
import static com.canoo.solar.Constants.FilterConstants.*

public class ApplicationAction extends DolphinServerAction{
    private static final String SOLR_INDEX_DIR = "solr";
    private static final String CORE_NAME = "eeg";
    private SolrServer solrServer;
    private Boolean ignore = false;


    public void registerIn(ActionRegistry registry) {
        registry.register(GET, filter)
        registry.register(GET_ROW, getPms)
        registry.register(ValueChangedCommand.class, trigger)


    }
    private final CommandHandler trigger = new CommandHandler<ValueChangedCommand>() {
        @Override
        public void handleCommand(final ValueChangedCommand command, final List<Command> response) {
            PresentationModel orderPm = getServerDolphin()[ORDER]
            PresentationModel statePm = getServerDolphin()[STATE]
            if (command.getOldValue()==null)return;
            if(!orderPm.findAttributeById(command.attributeId))  return;
            changeValue statePm[TRIGGER], (statePm[TRIGGER].value)+2

        }
    }
    private final NamedCommandHandler filter = new NamedCommandHandler() {
        @Override
        void handleCommand(NamedCommand command, List<Command> response) {

            println "Solr query from: " + command.getId()

            def orderPM = getServerDolphin().findPresentationModelById(ORDER)
            def filterPM = getServerDolphin().findPresentationModelById(FILTER)
            def filterAutoPM = getServerDolphin().findPresentationModelById(FILTER_AUTOFILL)
            SolrQuery solrQuery = new SolrQuery("*:*")
            solrQuery.addField(POSITION)
            solrQuery.setSort(POSITION, SolrQuery.ORDER.asc)
            solrQuery.setFacet(true);
            Map orders = new HashMap()
            MultiMap ordersWithQueries = new MultiHashMap()

            orderPM.attributes.each {
                def value = it.getValue()
                orders.put(it.propertyName, value)
            }
            filterPM.attributes.each {
                if (it.propertyName == ALL)return;
                def value = it.value
                if (value=="" || value==null || value.toString().contains("Plant Types") || value.toString().contains("Zip-Codes") || value.toString().contains("Cities")) value = "*"
                String query = it.getPropertyName() + ":" + value.toString()
                ordersWithQueries.put(orders.get(it.propertyName), query)
            }


            (0..ordersWithQueries.size()-1).each {

                List sameOrder = ordersWithQueries.get(it)
                if(sameOrder==null)return;
                String query;
                query = sameOrder.get(0)
                if (sameOrder.size()>1){
                    sameOrder.each {
                        if (sameOrder.get(0) == it) return;
                        query = query + " OR " + it
                    }
                }
                solrQuery.addFilterQuery(query)
            }

            filterAutoPM.attributes.each {

                def value = it.value
                if (value=="" || value==null || value.toString().contains("Plant Types") || value.toString().contains("Zip-Codes") || value.toString().contains("Cities")) value = "*"
                String query = it.getPropertyName().substring(0, it.getPropertyName().lastIndexOf("_")) + ":" + value.toString()
                solrQuery.addFilterQuery(query)
            }


            String freeSearchString = ""
            int i = 2;
            filterPM.attributes.each {
                i++;
                if (it.propertyName == ALL || it.propertyName == NOMINAL_POWER)return
                Object value = filterPM.findAttributeByPropertyName(ALL).getValue()
                if (value == null || value == "") value = "*"
                String query  = it.propertyName + ":" + value.toString()
                if (i < filterPM.attributes.size()){
                    query = query + " OR "
                }
                freeSearchString = freeSearchString + query
            }
            solrQuery.addFilterQuery(freeSearchString)

//            filterPM.attributes.each {
//                def value = it.value
//                if (value=="" || value==null || value.toString().contains("Plant Type") || value.toString().contains("Zip-Codes") || value.toString().contains("Cities")) value = "*"
//                solrQuery.addFilterQuery(it.getPropertyName() + ":" + value)
//            }

            solrQuery.setParam("facet.field", CITY);
            solrQuery.addFacetField(PLANT_TYPE);
            solrQuery.addFacetField(ZIP)
            solrQuery.setRows(10000)
            solrQuery.setFacetLimit(Integer.MAX_VALUE)
            def start = System.currentTimeMillis()
            QueryResponse queryResponse = getSolrServer().query(solrQuery)
            def result = queryResponse.getResults()
//            println "Solr took " + (System.currentTimeMillis() -  start )
            FacetField field = queryResponse.getFacetField(CITY);
            FacetField fieldtypes = queryResponse.getFacetField(PLANT_TYPE);
            FacetField fieldzip = queryResponse.getFacetField(ZIP);

            response.add(new DataCommand(new HashMap(size: result.getNumFound() )))
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
            return new InitializeAttributeCommand(pmId, attributeName, null, attributeValue, POWERPLANT)
        }
        @Override
        void handleCommand(NamedCommand command, List<Command> response) {
            Integer rowIdx = Integer.parseInt(getServerDolphin().findPresentationModelById(STATE).findAttributeByPropertyName(START_INDEX).getValue().toString())
            def reverse = getServerDolphin().findPresentationModelById(STATE).findAttributeByPropertyName(REVERSER_ORDER)
            if (rowIdx == null) {
                return
            }

            if (getServerDolphin().getAt(rowIdx.toString()) == null) {

                    def filterPM = getServerDolphin().findPresentationModelById(FILTER)
                    def filterAutoPM = getServerDolphin().findPresentationModelById(FILTER_AUTOFILL)
                    def orderPM = getServerDolphin().findPresentationModelById(ORDER)
                    SolrQuery solrQuery = new SolrQuery("*:*")
                if (reverse.getValue()) {
                    solrQuery.setSort(getServerDolphin().findPresentationModelById(STATE).findAttributeByPropertyName(SORT).getValue().toString(), SolrQuery.ORDER.desc)
                } else {
                    solrQuery.setSort(getServerDolphin().findPresentationModelById(STATE).findAttributeByPropertyName(SORT).getValue().toString(), SolrQuery.ORDER.asc)
                }
                Map orders = new HashMap()
                MultiMap ordersWithQueries = new MultiHashMap()

                orderPM.attributes.each {
                    def value = it.getValue()
                    orders.put(it.propertyName, value)
                }

                filterPM.attributes.each {

                    def value = it.value

                    if (value=="" || value==null || value.toString().contains("Plant Type") || value.toString().contains("Zip-Codes") || value.toString().contains("Cities")) value = "*"
                    String query = it.getPropertyName() + ":" + value.toString()
                    ordersWithQueries.put(orders.get(it.propertyName), query)
                }

                (0..ordersWithQueries.size()-1).each {

                    List sameOrder = ordersWithQueries.get(it)
                    if(sameOrder==null)return;
                    String query;
                    query = sameOrder.get(0)
                    if (sameOrder.size()>1){
                        sameOrder.each {
                            if (sameOrder.get(0) == it) return;
                            query = query + " OR " + it
                        }
                    }

                    solrQuery.addFilterQuery(query)
                }

                filterAutoPM.attributes.each {

                    def value = it.value
                    if (value=="" || value==null || value.toString().contains("Plant Types") || value.toString().contains("Zip-Codes") || value.toString().contains("Cities")) value = "*"
                    String query = it.getPropertyName().substring(0, it.getPropertyName().lastIndexOf("_")) + ":" + value.toString()
                    solrQuery.addFilterQuery(query)
                }

                String freeSearchString = ""
                int i = 2;
                filterPM.attributes.each {
                    i++;
                    if (it.propertyName == ALL || it.propertyName == NOMINAL_POWER)return
                    Object value = filterPM.findAttributeByPropertyName(ALL).getValue()
                    if (value == null || value == "") value = "*"
                    String query  = it.propertyName + ":" + value.toString()
                    if (i < filterPM.attributes.size()){
                        query = query + " OR "
                    }
                    freeSearchString = freeSearchString + query
                }
                solrQuery.addFilterQuery(freeSearchString)

                    solrQuery.setStart(rowIdx)
                    solrQuery.setRows(1);
                    def start = System.currentTimeMillis()
                    QueryResponse solrResponse = getSolrServer().query(solrQuery);
                    def result = solrResponse.getResults().get(0)
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), ID, result.getFieldValue(ID)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), POSITION, result.getFieldValue(POSITION)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), NOMINAL_POWER, result.getFieldValue(NOMINAL_POWER)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), PLANT_TYPE, result.getFieldValue(PLANT_TYPE)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), CITY, result.getFieldValue(CITY)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), ZIP, result.getFieldValue(ZIP)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), AVGKWH, result.getFieldValue(AVGKWH)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), GPS_LAT, result.getFieldValue(GPS_LAT)))
                    response.add(createInitializeAttributeCommand(rowIdx.toString(), GPS_LON, result.getFieldValue(GPS_LON)))

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
