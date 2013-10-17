package com.canoo.solar

import com.sun.javafx.scene.control.skin.TableHeaderRow
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import javafx.util.Callback
import org.opendolphin.core.Attribute
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.WithPresentationModelHandler
import java.beans.PropertyChangeListener
import static com.canoo.solar.ApplicationConstants.ATT_ATTR_ID
import static com.canoo.solar.ApplicationConstants.PM_APP
import static com.canoo.solar.Constants.CMD.GET
import static com.canoo.solar.Constants.CMD.GET_ROW
import static com.canoo.solar.Constants.FilterConstants.*
import static org.opendolphin.binding.JFXBinder.bind

public class Application extends javafx.application.Application {
    static ClientDolphin clientDolphin;
    javafx.collections.ObservableList<Integer> observableList = FXCollections.observableArrayList()
    javafx.collections.ObservableList<Integer> observableListCities = FXCollections.observableArrayList()
    javafx.collections.ObservableList<Integer> observableListCitiesCount = FXCollections.observableArrayList()
    javafx.collections.ObservableList<Integer> observableListTypes = FXCollections.observableArrayList()
    javafx.collections.ObservableList<Integer> observableListTypesCount = FXCollections.observableArrayList()
    javafx.collections.ObservableList<Integer> observableListZips = FXCollections.observableArrayList()
    javafx.collections.ObservableList<Integer> observableListZipsCount = FXCollections.observableArrayList()

    private PresentationModel textAttributeModel;
    public static TableView<PowerPlant> table = new TableView();
    Label cityLabelforDetail = new Label("City:       ")
    Label zipLabelforDetail = new Label("Zip Code:")
    Label typeLabelforDetail = new Label("Type:      ")
    Label nominalLabelforDetail = new Label("Power:    ")
    Label idLabelforDetail = new Label("Id:          ")
    TextField cityLabelDetail = new TextField("City")
    TextField idLabelDetail = new TextField("Id")
    TextField zipLabelDetail = new TextField("Zip Code")
    TextField typeLabelDetail = new TextField("Type")
    TextField nominalLabelDetail = new TextField("Power")
    Label loading = new Label("Loading Data from Solr")

    TreeItem<String> rootItemCities = new TreeItem<String>();
    TreeItem<String> rootItemZip = new TreeItem<String>();
    TreeItem<String> rootItem = new TreeItem<String>();

    Label selectionDetailsLabel = new Label("Selection Details \n")
    Label columns = new Label("Columns")
    Label filter = new Label("Filters")

    int c = 0
    CheckBox cityCB = new CheckBox("Show Cities");
    CheckBox typeCB = new CheckBox("Show Types");
    CheckBox nominalCB = new CheckBox("Show Nominal Powers");
    CheckBox zipCB = new CheckBox("Show Zip-Codes");
    
    CheckBox cityFilterCB = new CheckBox("Filter by City");
    CheckBox typeFilterCB = new CheckBox("Filter by Type");
    CheckBox zipFilterCB = new CheckBox("Filter by Zip");
    
    Pane columnStack = new Pane()
    Pane filterStack = new Pane()
    Pane tableStack = new Pane()
    Pane pane = new Pane()
    HBox trees = new HBox()

    final TreeView treeCities = new TreeView();
    final TreeView treeTypes = new TreeView();
    final TreeView treeZip = new TreeView();

    final Label plantTypes = new Label();
    final Label city = new Label();
    final Label zip = new Label();
    final Separator separator = new Separator();
    TextField nominalText = new TextField()
    
    Pane typePane = new Pane()
    Button closeType = new Button("X")
    Pane zipPane = new Pane()
    Button closeZip = new Button("X")
    Pane cityPane = new Pane()
    Button closeCity = new Button("X")

    PowerPlantList fakedPlantList = new PowerPlantList(1370000, new OurConsumer<Integer>(){
        @Override
        void accept(Integer rowIndex) {
            loadPresentationModel(rowIndex)
        }
    });
    javafx.collections.ObservableList<PowerPlant> items = FakeCollections.newObservableList(fakedPlantList);

    public Application() {
        textAttributeModel = clientDolphin.presentationModel(PM_APP, new ClientAttribute(ATT_ATTR_ID, null));
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Application Title");
        initializePresentationModels();

        observableList.clear()
        observableListCities.clear()
        observableListTypes.clear()
        observableListZips.clear()
        clientDolphin.data GET, { data ->
            def cityCount = 0
            def typeCount = 0
            def zipCount = 0
            def size = data.get(0).get("size")
            observableListCities.addAll(data.get(2).get(IDS))
            observableListCitiesCount.addAll(data.get(2).get("numCount"))

            treeCities.getRoot().getChildren().clear()
            observableListCities.each {

                if(observableListCitiesCount.get(cityCount).toString().equals("0")) return;
                cityCount++
                final TreeItem<String> checkBoxTreeItem =
                    new TreeItem<String>(it.toString() + " (" + observableListCitiesCount.get(cityCount-1).toString() + ")");
                treeCities.getRoot().getChildren().add(checkBoxTreeItem);

            }
            treeCities.getRoot().setValue("Citites: ($size)")
            observableListTypes.addAll(data.get(1).get(IDS))
            observableListTypesCount.addAll(data.get(1).get("numCount"))
            treeTypes.getRoot().getChildren().clear()
            observableListTypes.each {
                if(observableListTypesCount.get(typeCount).toString().equals("0")) return;
                typeCount++
                final TreeItem<String> checkBoxTreeItem =
                    new TreeItem<String>(it.toString() + " (" +  observableListTypesCount.get(typeCount-1).toString() + ")");
                treeTypes.getRoot().getChildren().add(checkBoxTreeItem);
            }
            treeTypes.getRoot().setValue("Plant Types ($size)")
            observableListZips.addAll(data.get(3).get(IDS))
            observableListZipsCount.addAll(data.get(3).get("numCount"))
            treeZip.getRoot().getChildren().clear()
            observableListZips.each {
                    if(observableListZipsCount.get(zipCount).toString().equals("0")) return;
                zipCount++
                def count = observableListZipsCount.get(zipCount-1)
                final TreeItem<String> checkBoxTreeItem =
                    new TreeItem<String>(it.toString() + " ($count)");
                treeZip.getRoot().getChildren().add(checkBoxTreeItem);
            }
            treeZip.getRoot().setValue("Zip-Code ($size)")
            def colMaxIndex = table.getColumns().size()-1
            (0..colMaxIndex).each {
                addHeaderListener(it)
            }
        }

        Pane root = setupStage();
        setupBinding();

        Scene scene = new Scene(root, 1280, 640)
        scene.stylesheets << 'demo.css'

        stage.setScene(scene);
        stage.setTitle(getClass().getName());
        stage.show();
    }

    private static void initializePresentationModels () {
        clientDolphin.presentationModel(FILTER, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER]);
        clientDolphin.presentationModel(SELECTED_POWERPLANT, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER]);
        clientDolphin.presentationModel(ORDER, [CITY, PLANT_TYPE, ZIP])
        clientDolphin.presentationModel(STATE, [TRIGGER, START_INDEX, SORT, REVERSER_ORDER])[TRIGGER].value=0

        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(ZIP).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(CITY).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).setValue(0)

        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(START_INDEX).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(SORT).setValue(POSITION)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(REVERSER_ORDER).setValue(false)
    }

   public static TableView getTable() {
        return table;
    }

    private Pane setupStage() {

        loading.setScaleX(1.2)
        loading.setScaleY(1.2)
        loading.setTextFill(Color.BLUE)

        typeLabelDetail.setEditable(false)
        cityLabelDetail.setEditable(false)
        zipLabelDetail.setEditable(false)
        nominalLabelDetail.setEditable(false)
        idLabelDetail.setEditable(false)

        cityCB.setSelected(true);
        typeCB.setSelected(true);
        nominalCB.setSelected(true);
        zipCB.setSelected(true);

        def orderPm = clientDolphin.findPresentationModelById(ORDER)
        def filterPm = clientDolphin.findPresentationModelById(FILTER)

//      create borders for sliding panes
        Rectangle columnCBBorder = Layout.createCBsBorder()
        Rectangle columnEventBorder = Layout.createEventBorder()
        columnEventBorder.relocate(0, 290)
        Rectangle filtersCBBorder = Layout.createCBsBorder()
        Rectangle filtersEventBorder = Layout.createEventBorder()
        filtersEventBorder.relocate(0, 420)
        table.setMinHeight(630)
        table.setItems(items)
        table.setPlaceholder(loading)
        table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->
            if (selectedPm==null) return;
           String id = selectedPm.getDbId().toString()
            
           clientDolphin.apply clientDolphin.findPresentationModelById(id) to clientDolphin[SELECTED_POWERPLANT]

        } as ChangeListener )

        TableColumn<PowerPlant, String> positionColumn = firstColumn()
        TableColumn<PowerPlant, String> zipColumn = secondColumn()
        TableColumn<PowerPlant, String> cityColumn = thirdColumn()
        TableColumn<PowerPlant, String> typeColumn = fourthColumn()
        TableColumn<PowerPlant, String> nominalColumn = fithColumn()
        table.getColumns().addAll(positionColumn, zipColumn, cityColumn, typeColumn, nominalColumn);

//        show/hide table Columns on checkbox event
        Listeners.ChoiceBoxListener(cityCB, table, cityColumn)
        Listeners.ChoiceBoxListener(typeCB, table, typeColumn)
        Listeners.ChoiceBoxListener(nominalCB, table, nominalColumn)
        Listeners.ChoiceBoxListener(zipCB, table, zipColumn)

        HBox nominalLabelTextDetail = Layout.createPair(nominalLabelDetail, nominalLabelforDetail)
        HBox typeLabelTextDetail = Layout.createPair(typeLabelDetail, typeLabelforDetail)
        HBox cityLabelTextDetail = Layout.createPair(cityLabelDetail, cityLabelforDetail)
        HBox zipLabelTextDetail = Layout.createPair(zipLabelDetail, zipLabelforDetail)
        HBox idLabelTextDetail = Layout.createPair(idLabelDetail, idLabelforDetail)

        Layout.createTreePane(rootItemCities, treeCities, closeCity, cityPane, cityFilterCB)
        Layout.createTreePane(rootItemZip, treeZip, closeZip, zipPane, zipFilterCB)
        Layout.createTreePane(rootItem, treeTypes, closeType, typePane, typeFilterCB)

        VBox columnsCBs = new VBox()
        columnsCBs.setPadding(new Insets(20, 0, 0, 10));
        columnsCBs.getChildren().addAll(cityCB, typeCB, zipCB, nominalCB)

        VBox filtersCBs = new VBox()
        filtersCBs.setPadding(new Insets(25, 0, 0, 10));
        filtersCBs.getChildren().addAll(cityFilterCB, typeFilterCB, zipFilterCB)

        trees.setSpacing(5)
        separator.setMinWidth(trees.getTranslateX())
        VBox details = new VBox()
        details.setSpacing(5);
        details.setPadding(new Insets(20, 0, 0, 10));
        details.getChildren().addAll(selectionDetailsLabel, cityLabelTextDetail, idLabelTextDetail, typeLabelTextDetail, nominalLabelTextDetail, zipLabelTextDetail, separator, trees)

        typeFilterCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                
                if (newValue) {
                    int i = 1
                    orderPm.getAttributes().each {if(it.value > 0) i++}
                    orderPm.findAttributeByPropertyName(PLANT_TYPE).setValue(i)

                }
                else {
                   def order = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).getValue()

                    orderPm.findAttributeByPropertyName(PLANT_TYPE).setValue(0)
                    orderPm.getAttributes().each {
                        if(it.value > order){

                            filterPm.findAttributeByPropertyName(it.getPropertyName()).setValue("")

                        }
                    }
                    orderPm.getAttributes().each {
                        if(it.value > order){
                            it.setValue(it.getValue()-1)
                        }
                    }
                }
            }
        });

        cityFilterCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {


                if (newValue) {
                    int i = 1
                    orderPm.getAttributes().each {if(it.value > 0) i++}
                    orderPm.findAttributeByPropertyName(CITY).setValue(i)
                }
                else {
                    def order = orderPm.findAttributeByPropertyName(CITY).getValue()
                    orderPm.findAttributeByPropertyName(CITY).setValue(0)
                    orderPm.getAttributes().each {
                        if(it.value > order){
                            filterPm.findAttributeByPropertyName(it.getPropertyName()).setValue("")

                        }
                    }
                    orderPm.getAttributes().each {
                        if(it.value > order){
                            it.setValue(it.getValue()-1)
                        }
                    }

                }
            }
        });

        zipFilterCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {


                if (newValue) {
                    int i = 1
                    orderPm.getAttributes().each {if(it.value > 0) i++}
                    orderPm.findAttributeByPropertyName(ZIP).setValue(i)
                }
                else {
                    def order = orderPm.findAttributeByPropertyName(ZIP).getValue()
                    orderPm.findAttributeByPropertyName(ZIP).setValue(0)

                    orderPm.getAttributes().each {
                        if(it.value > order){
                            filterPm.findAttributeByPropertyName(it.getPropertyName()).setValue("")

                        }
                    }
                    orderPm.getAttributes().each {
                        if(it.value > order){
                            it.setValue(it.getValue()-1)
                        }
                    }

                }
            }
        });

        treeTypes.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TreeItem <String>>() {
                    public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                                        TreeItem<String> oldItem, TreeItem<String> newItem) {
                        def cityValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(CITY).getValue()
                        def typeValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).getValue()
                        def zipValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(ZIP).getValue()
                        if (newItem==null)return;
                        String part1 = newItem.getValue().toString().substring(0, newItem.getValue().toString().lastIndexOf(' ('))
                        plantTypes.setText(part1);
                        if (cityValue > typeValue){city.setText("")}
                        if (zipValue > typeValue){zip.setText("")}
                        clientDolphin.data GET, { data ->

                            if (cityValue > typeValue){

                                updateCityTree(data)
                                city.setText("")
                            }

                            if (zipValue > typeValue){

                                updateZipTree(data)
                                zip.setText("")
                            }
                            def size = data.get(0).get("size")
                            PowerPlantList newFakeList = new PowerPlantList(size, new OurConsumer<Integer>(){
                                @Override
                                void accept(Integer rowIndex) {
                                    loadPresentationModel(rowIndex)
                                }
                            });
                            javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
                            table.setItems(newItems)
                        }
                    }
                });
        
        treeCities.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TreeItem <String>>() {
                    public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                                        TreeItem<String> oldItem, TreeItem<String> newItem) {
                        def cityValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(CITY).getValue()
                        def typeValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).getValue()
                        def zipValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(ZIP).getValue()
                        if (newItem==null) return;
                        String part1 = newItem.getValue().toString().substring(0, newItem.getValue().toString().lastIndexOf(' ('))
                        city.setText(part1);
                        if (typeValue > cityValue){plantTypes.setText("")}
                        if (zipValue > cityValue){zip.setText("")}
                        clientDolphin.data GET, { data ->
                            if (typeValue > cityValue){
                                plantTypes.setText("")
                                updateTypeTree(data)
                            }
                            if (zipValue > cityValue){
                                zip.setText("")
                                updateZipTree(data)
                            }
                            def size = data.get(0).get("size")
                            PowerPlantList newFakeList = new PowerPlantList(size, new OurConsumer<Integer>(){
                                @Override
                                void accept(Integer rowIndex) {
                                    loadPresentationModel(rowIndex)
                                }
                            });
                            javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
                            table.setItems(newItems)
                        }
                    }
                });

        treeZip.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<TreeItem <String>>() {

                    public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                                        TreeItem<String> oldItem, TreeItem<String> newItem) {
                        def cityValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(CITY).getValue()
                        def typeValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).getValue()
                        def zipValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(ZIP).getValue()
                        if (newItem==null) return;

                        String part1 = newItem.getValue().toString().substring(0, newItem.getValue().toString().lastIndexOf(' ('))
                        zip.setText(part1);
                        if (typeValue > zipValue){plantTypes.setText("")}
                        if (cityValue > zipValue){city.setText("")}
                        clientDolphin.data GET, { data ->
                            if (cityValue > zipValue){
                                city.setText("")
                                updateCityTree(data)

                            }
                            if (typeValue > zipValue){
                                plantTypes.setText("")
                                updateTypeTree(data)

                            }
                            def size = data.get(0).get(SIZE)
                            PowerPlantList newFakeList = new PowerPlantList(size, new OurConsumer<Integer>(){
                                @Override
                                void accept(Integer rowIndex) {
                                    loadPresentationModel(rowIndex)
                                }
                            });
                            javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
                            table.setItems(newItems)
                        }
                    }
        });

        Animation.setAnimation(columnStack);
        Animation.setAnimationFilters(filterStack);
        Animation.setMouseEventSliding(columnEventBorder, columnStack, Animation.timelineRight, Animation.timelineLeft, columns)
        Animation.setMouseEventSliding(filtersEventBorder, filterStack, Animation.timelineRightFilters, Animation.timelineLeftFilters, filter)

        Pane all = new Pane();
        all.getChildren().addAll(details)
        columnStack.getChildren().addAll(columnCBBorder, columnsCBs)
        columnStack.relocate(0, 290)
        filterStack.getChildren().addAll(filtersCBBorder, filtersCBs)
        filterStack.relocate(0, 420)
        columns.relocate(5, 340)
        columns.setRotate(90)
        filter.relocate(13, 470)
        filter.setRotate(90)
        tableStack.getChildren().addAll(table, columns, filter, columnEventBorder, filtersEventBorder, columnStack, filterStack)
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableStack);
        borderPane.setRight(all);
        pane.getChildren().addAll(borderPane)
        return pane
    }

    /*METHODS*/



    private void updateCityTree(LinkedList data) {
        treeCities.getSelectionModel().clearSelection()
        observableListCitiesCount.clear()
        observableListCities.clear()
        observableListCitiesCount.addAll(data.get(2).get("numCount"))
        observableListCities.addAll(data.get(2).get(IDS))
        def size = data.get(0).get(SIZE)
        Map<Integer,Integer> map = new LinkedHashMap<Integer,Integer>();
        for (int i=0; i<observableListCities.size(); i++) {
            map.put(observableListCities.get(i), observableListCitiesCount.get(i));
        }
        List<TreeItem<String>> iteratedList = new ArrayList<TreeItem<String>>()
        treeCities.getRoot().setValue("Cities ($size)")
        treeCities.getRoot().getChildren().each {
            String name = it.getValue().toString().substring(0,it.getValue().toString().lastIndexOf(' ('))
            def newCount = map.get(name)
            if (newCount==null)newCount=0;
            final TreeItem<String> checkBoxTreeItem =
                new TreeItem<String>(name + " (" + newCount + ")");
            iteratedList.add(checkBoxTreeItem);
        }
        Collections.sort(iteratedList, new TreeItemNumberComparator())

        treeCities.getRoot().getChildren().clear()
        treeCities.getRoot().getChildren().addAll(iteratedList)
    }
    private void updateZipTree(LinkedList data) {
        treeZip.getSelectionModel().clearSelection()
        observableListZips.clear()
        observableListZips.addAll(data.get(3).get(IDS))
        observableListZipsCount.clear()
        observableListZipsCount.addAll(data.get(3).get("numCount"))
        def size = data.get(0).get(SIZE)
        Map<Integer,Integer> map = new LinkedHashMap<Integer,Integer>();
        for (int i=0; i<observableListZips.size(); i++) {
            map.put(observableListZips.get(i), observableListZipsCount.get(i));
        }
        List<TreeItem<String>> iteratedList = new ArrayList<TreeItem<String>>()
        treeZip.getRoot().getChildren().each {
            String name = it.getValue().toString().substring(0,it.getValue().toString().lastIndexOf(' ('))

            def newCount = map.get(name)
            if (newCount==null)newCount=0;
            final TreeItem<String> checkBoxTreeItem =
                new TreeItem<String>(name + " (" + newCount + ")");
            iteratedList.add(checkBoxTreeItem);
        }
        treeZip.getRoot().setValue("Zip-Code ($size)")
        Collections.sort(iteratedList, new TreeItemNumberComparator())
        treeZip.getRoot().getChildren().clear()
        treeZip.getRoot().getChildren().addAll(iteratedList)

    }
    private void updateTypeTree(LinkedList data) {
        treeTypes.getSelectionModel().clearSelection()
        observableListTypes.clear()
        observableListTypes.addAll(data.get(1).get(IDS))
        observableListTypesCount.clear()
        observableListTypesCount.addAll(data.get(1).get("numCount"))
        def size = data.get(0).get(SIZE)
        Map<Integer,Integer> map = new LinkedHashMap<Integer,Integer>();
        for (int i=0; i<observableListTypes.size(); i++) {
            map.put(observableListTypes.get(i), observableListTypesCount.get(i));
        }
        List<TreeItem<String>> iteratedList = new ArrayList<TreeItem<String>>()
        treeTypes.getRoot().getChildren().each {
            String name = it.getValue().toString().substring(0,it.getValue().toString().lastIndexOf(' ('))
            def newCount = map.get(name)
            if (newCount==null)newCount=0;
            final TreeItem<String> checkBoxTreeItem =
                new TreeItem<String>(name + " (" + newCount + ")");
            iteratedList.add(checkBoxTreeItem);
        }
        treeTypes.getRoot().setValue("Plant Type ($size)")
        Collections.sort(iteratedList, new TreeItemNumberComparator())
        treeTypes.getRoot().getChildren().clear()
        treeTypes.getRoot().getChildren().addAll(iteratedList)
    }

    private void setupBinding() {

        bind 'text' of zip to ZIP of clientDolphin[FILTER]
        bind 'text' of city to CITY of clientDolphin[FILTER]
        bind 'text' of plantTypes to PLANT_TYPE of clientDolphin[FILTER]
        bind 'text' of nominalText to NOMINAL_POWER of clientDolphin[FILTER]

        bind ZIP of clientDolphin[FILTER] to 'text' of zip
        bind CITY of clientDolphin[FILTER] to 'text' of city
        bind PLANT_TYPE of clientDolphin[FILTER] to 'text' of plantTypes
        bind NOMINAL_POWER of clientDolphin[FILTER] to 'text' of nominalText

        bind ZIP of clientDolphin[SELECTED_POWERPLANT] to 'text' of zipLabelDetail
        bind CITY of clientDolphin[SELECTED_POWERPLANT] to 'text' of cityLabelDetail
        bind ID of clientDolphin[SELECTED_POWERPLANT] to 'text' of idLabelDetail
        bind NOMINAL_POWER of clientDolphin[SELECTED_POWERPLANT] to 'text' of nominalLabelDetail
        bind PLANT_TYPE of clientDolphin[SELECTED_POWERPLANT] to 'text' of typeLabelDetail

        bindAttribute(clientDolphin[STATE][SORT], {
            if (clientDolphin[STATE][SORT].getValue().toString()==IGNORE)return;
            List<PresentationModel> pmsToRemove = new ArrayList<PresentationModel>()
            clientDolphin.getModelStore().findAllPresentationModelsByType(POWERPLANT).each {
                pmsToRemove.add(it)
            }
            clientDolphin.deleteAllPresentationModelsOfType(POWERPLANT)
            pmsToRemove.each {
                fakedPlantList.removePlant(Integer.parseInt(it.getId()))
            }
            refreshTable()
        })

        bindAttribute(clientDolphin[FILTER][PLANT_TYPE], {
            def cityValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(CITY).getValue()
            def typeValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).getValue()
            def zipValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(ZIP).getValue()

            List<PresentationModel> pmsToRemove = new ArrayList<PresentationModel>()
            clientDolphin.getModelStore().findAllPresentationModelsByType(POWERPLANT).each {
                pmsToRemove.add(it)
            }
            clientDolphin.deleteAllPresentationModelsOfType(POWERPLANT)
            pmsToRemove.each {
                fakedPlantList.removePlant(Integer.parseInt(it.getId()))
            }
        })

        bindAttribute(clientDolphin[FILTER][CITY], {
            def cityValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(CITY).getValue()
            def typeValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).getValue()
            def zipValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(ZIP).getValue()

            List<PresentationModel> pmsToRemove = new ArrayList<PresentationModel>()
            clientDolphin.getModelStore().findAllPresentationModelsByType(POWERPLANT).each {
                pmsToRemove.add(it)
            }
            clientDolphin.deleteAllPresentationModelsOfType(POWERPLANT)
            pmsToRemove.each {
                fakedPlantList.removePlant(Integer.parseInt(it.getId()))
            }
        })

        bindAttribute(clientDolphin[FILTER][ZIP], {
            def cityValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(CITY).getValue()
            def typeValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).getValue()
            def zipValue = clientDolphin.findPresentationModelById(ORDER).findAttributeByPropertyName(ZIP).getValue()

            List<PresentationModel> pmsToRemove = new ArrayList<PresentationModel>()
            clientDolphin.getModelStore().findAllPresentationModelsByType(POWERPLANT).each {
                pmsToRemove.add(it)
            }
            clientDolphin.deleteAllPresentationModelsOfType(POWERPLANT)
            pmsToRemove.each {
                fakedPlantList.removePlant(Integer.parseInt(it.getId()))
            }
        })


        bindAttribute(clientDolphin[ORDER][PLANT_TYPE],{
            if(it.newValue==0){
                treeTypes.getSelectionModel().clearSelection()
                trees.getChildren().remove(typePane)
            }
            else if(it.oldValue==0) {
                trees.getChildren().add(typePane)
            }

            plantTypes.setText("")
            clientDolphin.data GET, { data ->
                updateTypeTree(data)



            }
        })

        bindAttribute(clientDolphin[ORDER][CITY],{
            if(it.newValue==0){
                treeTypes.getSelectionModel().clearSelection()
                trees.getChildren().remove(cityPane)
            }
            else if(it.oldValue==0) { trees.getChildren().add(cityPane)}

            city.setText("")
            clientDolphin.data GET, {data ->
            updateCityTree(data)


            }
        })


        bindAttribute(clientDolphin[ORDER][ZIP],{
            if(it.newValue==0){
                treeZip.getSelectionModel().clearSelection()
                trees.getChildren().remove(zipPane)
            }
            else if(it.oldValue==0) {trees.getChildren().add(zipPane)}


            zip.setText("")

            clientDolphin.data GET, {data ->
                updateZipTree(data)

            }

        })

        bindAttribute(clientDolphin[STATE][TRIGGER], {
            List<PresentationModel> pmsToRemove = new ArrayList<PresentationModel>()
            clientDolphin.getModelStore().findAllPresentationModelsByType(POWERPLANT).each {
                pmsToRemove.add(it)
            }
            clientDolphin.deleteAllPresentationModelsOfType(POWERPLANT)
            pmsToRemove.each {
                fakedPlantList.removePlant(Integer.parseInt(it.getId()))
            }
            refreshTable()

        })
    }

    public static void refreshTable(){

        clientDolphin.data GET, { data ->

            def size = data.get(0).get(SIZE)
            PowerPlantList newFakeList = new PowerPlantList(size, new OurConsumer<Integer>(){
                @Override
                void accept(Integer rowIndex) {
                    loadPresentationModel(rowIndex)
                }
            });
            javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
            table.setItems(newItems)
        }
    }

    public static void bindAttribute(Attribute attribute, Closure closure) {
        final listener = closure as PropertyChangeListener
        attribute.addPropertyChangeListener('value', listener)
    }

    public static addHeaderListener(Integer i) {
        TableHeaderRow rowHeader = TableViews.getTableViewInfo(Application.getTable()).tableHeaderRow
        def header = rowHeader.getChildren().get(1).getColumnHeaders().get(i)
        def sort = clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(SORT)
        def reverse = clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(REVERSER_ORDER)
        header.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                switch (i){
                    case 0:
                        if (sort.getValue().toString()==POSITION){
                            if (reverse.getValue()){
                                reverse.setValue(false)
                            } else reverse.setValue(true)
                        }
                        else{
                            reverse.setValue(false)
                        }
                        sort.setValue(IGNORE)
                        sort.setValue(POSITION)
                        break;
                    case 1:
                        if (sort.getValue().toString()==ZIP){
                            if (reverse.getValue()){
                                reverse.setValue(false)
                            } else reverse.setValue(true)
                        }
                        else{
                            reverse.setValue(false)
                        }
                        sort.setValue(IGNORE)
                        sort.setValue(ZIP)
                        break;
                    case 2:
                        if (sort.getValue().toString()==CITY){
                            if (reverse.getValue()){
                                reverse.setValue(false)
                            } else reverse.setValue(true)
                        }
                        else{
                            reverse.setValue(false)
                        }
                        sort.setValue(IGNORE)
                        sort.setValue(CITY)
                        break;
                    case 3:
                        if (sort.getValue().toString()==PLANT_TYPE){
                            if (reverse.getValue()){
                                reverse.setValue(false)
                            } else reverse.setValue(true)
                        }
                        else{
                            reverse.setValue(false)
                        }
                        sort.setValue(IGNORE)
                        sort.setValue(PLANT_TYPE)
                        break;
                    case 4:
                        if (sort.getValue().toString()==NOMINAL_POWER){
                            if (reverse.getValue()){
                                reverse.setValue(false)
                            } else reverse.setValue(true)
                        }
                        else{
                            reverse.setValue(false)
                        }
                        sort.setValue(IGNORE)
                        sort.setValue(NOMINAL_POWER)
                        break;
                }
            }
        });
    }

    public static TableColumn<PowerPlant, String> secondColumn() {
        TableColumn<PowerPlant, String> result = new TableColumn<>("Zip");
        result.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PowerPlant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PowerPlant, String> param) {
                return param.getValue().zipProperty();
            }
        });

        result.setSortable(false)
        result.setPrefWidth(100)
        return result
    }
    public static TableColumn<PowerPlant, String> fourthColumn() {
        TableColumn<PowerPlant, String> result = new TableColumn<>("Type");
        result.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PowerPlant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PowerPlant, String> param) {
                return param.getValue().typeProperty();
            }
        });
        result.setSortable(false)
        result.setPrefWidth(100)
        return result;
    }
    public static TableColumn<PowerPlant, String> fithColumn() {
        TableColumn<PowerPlant, String> result = new TableColumn<>("Nominal");
        result.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PowerPlant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PowerPlant, String> param) {
                return param.getValue().nominalProperty();
            }
        });
        result.setSortable(false)
        result.setPrefWidth(100)
        return result;
    }
    public static TableColumn<PowerPlant, String> firstColumn() {
        TableColumn<PowerPlant, String> result = new TableColumn<>("Position");
        result.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PowerPlant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PowerPlant, String> param) {
                return param.getValue().positionProperty()
            }
        });
        result.setSortable(false)
        result.setPrefWidth(100)
        return result;
    }
    public static TableColumn<PowerPlant, String> thirdColumn() {
        TableColumn<PowerPlant, String> result = new TableColumn<>("City");
        result.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PowerPlant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PowerPlant, String> param) {
                return param.getValue().cityProperty();
            }
        });
        result.setSortable(false)
        result.setPrefWidth(100)
        return result;
    }

    private static void loadPresentationModel(int rowIdx) {
        if (rowIdx == -1) return;
        System.out.println("loadPresentationModel: rowIdx = " + rowIdx);
        PowerPlant initialPlant = getTable().getItems().get(rowIdx)
//        if (initialPlant.getLoadState() == LoadState.LOADED) return;
        if (initialPlant.getLoadState() == LoadState.LOADING) return;
        boolean useWithPresentationModel = false
        if (useWithPresentationModel) {
            getClientDolphin().clientModelStore.withPresentationModel(rowIdx.toString(), new WithPresentationModelHandler() {
                void onFinished(ClientPresentationModel pm) {
                    PowerPlant plant = getTable().getItems().get(Integer.parseInt(pm.getId()))
                    plant.setDbId(rowIdx)
                    plant.typeProperty().setValue(pm[PLANT_TYPE].getValue().toString())
                    plant.zipProperty().setValue(pm[ZIP].getValue().toString())
                    plant.cityProperty().setValue(pm[CITY].getValue().toString())
                    plant.nominalProperty().setValue(pm[NOMINAL_POWER].getValue().toString())
                    plant.positionProperty().setValue(pm[POSITION].getValue().toString())
                    plant.setLoadState(LoadState.LOADED);
                }
            })
        } else {
            initialPlant.setLoadState(LoadState.LOADING);
            clientDolphin.findPresentationModelById(STATE).findAttributeByPropertyName(START_INDEX).setValue(rowIdx)
            clientDolphin.send GET_ROW, { pms ->
                pms.each { pm ->
//                    PowerPlant plant = getTable().getItems().get(Integer.parseInt(pm.getId()))
                    initialPlant.setDbId(rowIdx)
                    initialPlant.typeProperty().setValue(pm[PLANT_TYPE].getValue().toString())
                    initialPlant.zipProperty().setValue(pm[ZIP].getValue().toString())
                    initialPlant.cityProperty().setValue(pm[CITY].getValue().toString())
                    initialPlant.nominalProperty().setValue(pm[NOMINAL_POWER].getValue().toString())
                    initialPlant.positionProperty().setValue(pm[POSITION].getValue().toString())
                    initialPlant.setLoadState(LoadState.LOADED);
                }
            }
        }
    }
}
