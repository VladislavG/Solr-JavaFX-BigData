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
import org.opendolphin.core.Attribute
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
            List<Long> facetPms = new ArrayList<Long>()
            getServerDolphin().findAllPresentationModelsByType(FACET).each {
                facetPms.add(it[ORDER].getId())
            }
            if (command.getOldValue()==null)return;
            if(!facetPms.contains(command.getAttributeId()))  return;
            changeValue statePm[TRIGGER], (statePm[TRIGGER].value)+2

        }
    }
    private final NamedCommandHandler filter = new NamedCommandHandler() {
        @Override
        void handleCommand(NamedCommand command, List<Command> response) {


            def orderPM = getServerDolphin().findPresentationModelById(ORDER)
            def statePM = getServerDolphin().findPresentationModelById(STATE)
            def filterPM = getServerDolphin().findPresentationModelById(FILTER)
            def filterAutoPM = getServerDolphin().findPresentationModelById(FILTER_AUTOFILL)
            SolrQuery solrQuery = new SolrQuery("*:*")
            solrQuery.addField(POSITION)
            solrQuery.setSort(POSITION, SolrQuery.ORDER.asc)
            solrQuery.setFacet(true);
            Map orders = new HashMap()
            MultiMap ordersWithQueries = new MultiHashMap()

            getServerDolphin().findAllPresentationModelsByType(FACET).each {
                def value = it[ORDER].getValue()
                orders.put(it.id, value)
            }
//            orderPM.attributes.each {
//                def value = it.getValue()
//                orders.put(it.propertyName, value)
//            }
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


            solrQuery.setParam("facet.field", CITY);
            solrQuery.addFacetField(PLANT_TYPE);
            solrQuery.addFacetField(ZIP)
            solrQuery.addFacetField(NOMINAL_POWER)
            solrQuery.addFacetField(AVGKWH)
            solrQuery.setRows(10000)
            solrQuery.setFacetLimit(Integer.MAX_VALUE)
            def start = System.currentTimeMillis()
            QueryResponse queryResponse = getSolrServer().query(solrQuery)
            def result = queryResponse.getResults()
            println "Solr took " + queryResponse.getQTime()
            FacetField field = queryResponse.getFacetField(CITY);
            FacetField fieldtypes = queryResponse.getFacetField(PLANT_TYPE);
            FacetField fieldzip = queryResponse.getFacetField(ZIP);
            FacetField fieldNominal = queryResponse.getFacetField(NOMINAL_POWER);
            FacetField fieldKWH = queryResponse.getFacetField(AVGKWH);

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
            List<FacetField.Count> valuesnominal = fieldNominal.getValues();
            List<FacetField.Count> valuesKWH = fieldKWH.getValues();

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
            Double totalNominal = new Double(0.0)
            Double totalKWH = new Double(0.0)
            int c = 0
            for(FacetField.Count count : valuesnominal){
                 totalNominal = totalNominal + (count.getName().toDouble() * count.getCount().toInteger())
            }

            for(FacetField.Count count : valuesKWH){
                totalKWH = totalKWH + (count.getName().toDouble() * count.getCount().toInteger())
                c++
            }
            Double averageKWH = totalKWH.div(c)
            changeValue statePM[TOTAL_NOMINAL], totalNominal
            changeValue statePM[AVERAGE_KWH], averageKWH

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
            if (rowIdx == null) {
                return
            }
            ArrayList<String> sortings = new ArrayList<String>()
            Map colNamestoSolrNames = new HashMap()
            colNamestoSolrNames.put("Position", POSITION)
            colNamestoSolrNames.put("Type", PLANT_TYPE)
            colNamestoSolrNames.put("City", CITY)
            colNamestoSolrNames.put("Zip", ZIP)
            colNamestoSolrNames.put("Nominal", NOMINAL_POWER)
            colNamestoSolrNames.put("Average kW/h", AVGKWH)
            colNamestoSolrNames.put("latitude", GPS_LAT)
            colNamestoSolrNames.put("Longitude", GPS_LON)
            colNamestoSolrNames.put("ASCENDING", SolrQuery.ORDER.desc)
            colNamestoSolrNames.put("DESCENDING", SolrQuery.ORDER.asc)

            if (getServerDolphin().getAt(rowIdx.toString()) == null) {

                SolrQuery solrQuery = new SolrQuery("*:*")
                def filterPM = getServerDolphin().findPresentationModelById(FILTER)
                def filterAutoPM = getServerDolphin().findPresentationModelById(FILTER_AUTOFILL)
                def orderPM = getServerDolphin().findPresentationModelById(ORDER)
                def sortString = getServerDolphin().findPresentationModelById(STATE).findAttributeByPropertyName(SORT).getValue().toString()


                String[] parts = sortString.split(", ");

                for(int i = 0; i <= parts.size()-1; i++){
                    sortings.add(i, parts[i])
                }
                sortings.each {
                    solrQuery.addSort(colNamestoSolrNames.get(it.substring(0, it.lastIndexOf(" - "))).toString(), SolrQuery.ORDER.((colNamestoSolrNames.get(it.substring(it.lastIndexOf(" - ")+3))).toString()))
                }

                Map orders = new HashMap()
                MultiMap ordersWithQueries = new MultiHashMap()

                getServerDolphin().findAllPresentationModelsByType(FACET).each {
                    def value = it[ORDER].getValue()
                    orders.put(it.id, value)
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
//                    println "solr took: " + solrResponse.getQTime()
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
