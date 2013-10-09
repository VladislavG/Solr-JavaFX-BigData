package com.canoo.solar

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
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
import javafx.util.Duration
import np.com.ngopal.control.AutoFillTextBox
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
import static com.canoo.solar.Constants.CMD.GET_FIFTY
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
    AutoFillTextBox typeChoiceBox = new AutoFillTextBox(observableListTypes)
    AutoFillTextBox cityText = new AutoFillTextBox(observableListCities)
    TableColumn idCol = new TableColumn("Position");
    TableColumn typeCol = new TableColumn("Plant Type");
    TableColumn cityCol = new TableColumn("City");
    TableColumn zipCol = new TableColumn("ZIP Code");
    TableColumn nominalCol = new TableColumn("Nominal Power");
    Label loading = new Label("Loading Data from Solr")
    Label noData = new Label("No Data Found")
    Label search = new Label("Selection Details \n")
    Label columns = new Label("Columns")
    Label filter = new Label("Filters")
    private Rectangle2D boxBounds = new Rectangle2D(600, 100, 650, 200);
    int f, c = 0
    CheckBox cityCB = new CheckBox("Show Cities");
    CheckBox typeCB = new CheckBox("Show Types");
    CheckBox cityFilterCB = new CheckBox("Filter by City");
    CheckBox typeFilterCB = new CheckBox("Filter by Type");
    CheckBox zipFilterCB = new CheckBox("Filter by Zip");
    CheckBox zipCB = new CheckBox("Show Zip-Codes");
    CheckBox nominalCB = new CheckBox("Show Nominal Powers");
    final TreeItem<String> allItem = new TreeItem<String>("All");
    Pane columnStack = new Pane()
    Pane filterStack = new Pane()
    Pane tableStack = new Pane()
    Pane pane = new Pane()
    HBox trees = new HBox()
    private Rectangle clipRect;
    private Timeline timelineLeft;
    private Timeline timelineRight;
    private Timeline timelineLeftFilters;
    private Timeline timelineRightFilters;
    final TreeView treeCities = new TreeView();
    final TreeView treeTypes = new TreeView();
    final TreeView treeZip = new TreeView();
    TextField zipText = new TextField()
    final Label plantTypes = new Label();
    final Label city = new Label();
    final Label zip = new Label();
    final Separator separator = new Separator();
    final Separator separator2 = new Separator();
    TextField nominalText = new TextField()

    TextField typeLabelforBinding = typeChoiceBox.getTextbox()
    TextField cityLabelforBinding = cityText.getTextbox()

    PowerPlantList fakedPersonList = new PowerPlantList(1370000, new OurConsumer<Integer>(){
        @Override
        void accept(Integer rowIndex) {
            loadPresentationModel(rowIndex)
        }
    });
    javafx.collections.ObservableList<PowerPlant> items = FakeCollections.newObservableList(fakedPersonList);

    public Application() {
        textAttributeModel = clientDolphin.presentationModel(PM_APP, new ClientAttribute(ATT_ATTR_ID, null));
    }
//
//    public static class PP {
//        ObservableStringValue zipProperty = new SimpleStringProperty()
//        ObservableStringValue cityProperty = new SimpleStringProperty()
//        ObservableStringValue typeProperty = new SimpleStringProperty()
//        ObservableIntegerValue idProperty = new SimpleIntegerProperty()
//        ObservableStringValue nominalProperty = new SimpleStringProperty()
//    }

//    public static ObservableList items(int howMany) {
//        ObservableList result = FXCollections.observableArrayList();
//        for (int i = 1; i < howMany; i++) {
//            result.add(new PP());
//
//        }
//
//        return result;
//    }

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
            observableListCities.addAll(data.get(2).get("ids"))
            observableListCitiesCount.addAll(data.get(2).get("numCount"))
            treeCities.getRoot().getChildren().clear()
            observableListCities.each {

                if(observableListCitiesCount.get(cityCount).toString().equals("0")) return;
                cityCount++
                final TreeItem<String> checkBoxTreeItem =
                    new TreeItem<String>(it.toString() + " (" + observableListCitiesCount.get(cityCount-1).toString() + ")");
                treeCities.getRoot().getChildren().add(checkBoxTreeItem);

            }

            observableListTypes.addAll(data.get(1).get("ids"))
            observableListTypesCount.addAll(data.get(1).get("numCount"))
            treeTypes.getRoot().getChildren().clear()
            observableListTypes.each {
                if(observableListTypesCount.get(typeCount).toString().equals("0")) return;
                typeCount++
                final TreeItem<String> checkBoxTreeItem =
                    new TreeItem<String>(it.toString() + " (" +  observableListTypesCount.get(typeCount-1).toString() + ")");
                treeTypes.getRoot().getChildren().add(checkBoxTreeItem);
            }

            observableListZips.addAll(data.get(3).get("ids"))
            observableListZipsCount.addAll(data.get(3).get("numCount"))
            treeZip.getRoot().getChildren().clear()
            observableListZips.each {
                    if(observableListZipsCount.get(zipCount).toString().equals("0")) return;
                zipCount++
                final TreeItem<String> checkBoxTreeItem =
                    new TreeItem<String>(it.toString() + " (" + observableListZipsCount.get(zipCount-1).toString() + ")");
                treeZip.getRoot().getChildren().add(checkBoxTreeItem);
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
        clientDolphin.presentationModel(ORDER, [CITY, PLANT_TYPE, ZIP])[ZIP].value=0
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(CITY).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).setValue(0)
        clientDolphin.presentationModel(STATE, [TRIGGER, START_INDEX])[TRIGGER].value=0
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(START_INDEX).setValue(0)

    }

   public static TableView getTable() {
        return table;
    }

    TableColumn getIdCol() {
        return idCol;
    }

    TableColumn getNominalCol() {
        return nominalCol;
    }

    TableColumn getTypeCol() {
        return typeCol;
    }

    TableColumn getCityCol() {
        return cityCol;
    }

    TableColumn getZipCol() {
        return zipCol;
    }

    private Pane setupStage() {

        loading.setScaleX(1.2)
        loading.setScaleY(1.2)
        loading.setTextFill(Color.BLUE)
        idCol.setMinWidth(100)
        nominalCol.setMinWidth(100)
        typeCol.setMinWidth(100)
        zipCol.setMinWidth(100)
        cityCol.setMinWidth(100)
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
        Rectangle columnCBBorder = createCBsBorder()
        Rectangle columnEventBorder = createEventBorder()
        columnEventBorder.relocate(0, 290)
        Rectangle filtersCBBorder = createCBsBorder()
        Rectangle filtersEventBorder = createEventBorder()
        filtersEventBorder.relocate(0, 420)

        table.setMinHeight(620)
        table.setItems(items)
        table.setPlaceholder(loading)
        table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->

            if (selectedPm==null) return;
                    String id = selectedPm.getAt(POSITION).getValue()
                    clientDolphin.apply clientDolphin.findPresentationModelById(id) to clientDolphin[SELECTED_POWERPLANT]

        } as ChangeListener )
        table.getColumns().addAll(firstColumn(), secondColumn(), thirdColumn(), fourthColumn(), fithColumn());

//
//        table.setRowFactory(new Callback<TableView<PP>, TableRow<PP>>() {
//            @Override
//            public TableRow<PP> call(final TableView<PP> param) {
//                TableRow<PP> tableRow = new TableRow<PP>();
//                tableRow.indexProperty().addListener(new ChangeListener<Number>() {
//                    @Override
//                    public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number rowIdx) {
//
//                        if (rowIdx == -1) return
//
//                        getClientDolphin().clientModelStore.withPresentationModel(rowIdx.toString(), new WithPresentationModelHandler() {
//                            void onFinished(ClientPresentationModel pm) {
//                                println "created pm: " + pm.id
//                                PP pp = getTable().items[rowIdx.toInteger()]
//                                ObservableStringValue zipString = new SimpleStringProperty(pm[ZIP].getValue().toString())
//                                pp.setZipProperty(zipString)
//                                ObservableStringValue cityString = new SimpleStringProperty(pm[CITY].getValue().toString())
//                                pp.setCityProperty(cityString)
//                                ObservableIntegerValue idString = new SimpleIntegerProperty(pm[POSITION].getValue())
//                                pp.setIdProperty(idString)
//                                ObservableStringValue nominalString = new SimpleStringProperty(pm[NOMINAL_POWER].getValue().toString())
//                                pp.setNominalProperty(nominalString)
//                                ObservableStringValue typeString = new SimpleStringProperty(pm[PLANT_TYPE].getValue().toString())
//                                pp.setTypeProperty(typeString)
//                                getTable().getColumns().clear()
//                                getTable().getColumns().addAll(getIdCol(), getTypeCol(), getCityCol(), getZipCol(), getNominalCol());
//                            }
//                        })
//
//                    }
//                });
//
//                return tableRow;
//            }
//        });
//
//        idCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PP, Integer>, ObservableValue<Integer>>() {
//            @Override
//            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<PP, Integer> param) {
//                def value = param.getValue();
//
//                return value.getIdProperty()
//
//
//            }
//        });
//
//        zipCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PP, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<PP, String> param) {
//                PP pp = param.getValue()
//                return pp.getZipProperty()
//            }
//        });
//
//        nominalCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PP, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<PP, String> param) {
//                return param.getValue().getNominalProperty()
//
//            }
//        });
//
//        typeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PP, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<PP, String> param) {
//                return  param.getValue().getTypeProperty()
//
//            }
//        });
//
//        cityCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PP, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<PP, String> param) {
//               return param.getValue().getCityProperty()
//            }
//        });
//        table.getColumns().addAll(idCol, typeCol, cityCol, zipCol, nominalCol);
//
//        idCol.cellValueFactory = {
//            String lazyId = it.value
//            def placeholder = new SimpleStringProperty("Not Loaded");
//            clientDolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
//                void onFinished(ClientPresentationModel presentationModel) {
//                    placeholder.setValue(presentationModel.getAt("position").value.toString() ) // fill async lazily
//                }
//            } )
//            return placeholder
//        } as Callback
//        typeCol.cellValueFactory = {
//            String lazyId = it.value
//            def placeholder = new SimpleStringProperty("Not Loaded")
//            clientDolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
//                void onFinished(ClientPresentationModel presentationModel) {
//                    placeholder.setValue(presentationModel.getAt(PLANT_TYPE).value.toString() ) // fill async lazily
//                }
//            } )
//            return placeholder
//        } as Callback
//        cityCol.cellValueFactory = {
//            String lazyId = it.value
//            def placeholder = new SimpleStringProperty("Not Loaded")
//            clientDolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
//                void onFinished(ClientPresentationModel presentationModel) {
//                    placeholder.setValue(presentationModel.getAt(CITY).value.toString() ) // fill async lazily
//                }
//            } )
//            return placeholder
//        } as Callback
//        zipCol.cellValueFactory = {
//            String lazyId = it.value
//            def placeholder = new SimpleStringProperty("Not Loaded")
//            clientDolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
//                void onFinished(ClientPresentationModel presentationModel) {
//                    placeholder.setValue(presentationModel.getAt(ZIP).value.toString() ) // fill async lazily
//                }
//            } )
//            return placeholder
//        } as Callback
//        nominalCol.cellValueFactory = {
//            String lazyId = it.value
//            def placeholder = new SimpleStringProperty("Not Loaded")
//            clientDolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
//                void onFinished(ClientPresentationModel presentationModel) {
//                    placeholder.setValue(presentationModel.getAt(NOMINAL_POWER).value.toString()) // fill async lazily
//                }
//            } )
//            return placeholder
//        } as Callback

//        show/hide table Columns on checkbox event
        ChoiceBoxListener(cityCB, table, cityCol)
        ChoiceBoxListener(typeCB, table, typeCol)
        ChoiceBoxListener(nominalCB, table, nominalCol)
        ChoiceBoxListener(zipCB, table, zipCol)

        HBox nominalLabelTextDetail = createPair(nominalLabelDetail, nominalLabelforDetail)
        HBox typeLabelTextDetail = createPair(typeLabelDetail, typeLabelforDetail)
        HBox cityLabelTextDetail = createPair(cityLabelDetail, cityLabelforDetail)
        HBox zipLabelTextDetail = createPair(zipLabelDetail, zipLabelforDetail)
        HBox idLabelTextDetail = createPair(idLabelDetail, idLabelforDetail)

        VBox columnsCBs = new VBox()
        columnsCBs.setPadding(new Insets(20, 0, 0, 10));
        columnsCBs.getChildren().addAll(cityCB, typeCB, zipCB, nominalCB)

        VBox filtersCBs = new VBox()
        filtersCBs.setPadding(new Insets(25, 0, 0, 10));
        filtersCBs.getChildren().addAll(cityFilterCB, typeFilterCB, zipFilterCB)

        TreeItem<String> rootItem =
            new TreeItem<String>("All");
        rootItem.setExpanded(true);
        treeTypes.setRoot(rootItem);
        treeTypes.setShowRoot(true);

        TreeItem<String> rootItemZip =
            new TreeItem<String>("All");
        rootItemZip.setExpanded(true);
        treeZip.setRoot(rootItemZip);
        treeZip.setShowRoot(true);

        TreeItem<String> rootItemCities =
            new TreeItem<String>("All");
        rootItemCities.setExpanded(true);
        treeCities.setRoot(rootItemCities);
        treeCities.setShowRoot(true);


        trees.setSpacing(5)
        separator.setMinWidth(trees.getTranslateX())
        separator2.setMinWidth(trees.getTranslateX())


        VBox details = new VBox()
        details.setSpacing(5);
        details.setPadding(new Insets(20, 0, 0, 10));
        details.getChildren().addAll(search, cityLabelTextDetail, idLabelTextDetail, typeLabelTextDetail, nominalLabelTextDetail, zipLabelTextDetail, separator, trees/*, separator2, autofillHbox*/)

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
                        observableList.clear()
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
                        }
                    }
        });


        setAnimation();
        setAnimationFilters();
        setMouseEventSliding(columnEventBorder, columnStack, timelineRight, timelineLeft, columns)
        setMouseEventSliding(filtersEventBorder, filterStack, timelineRightFilters, timelineLeftFilters, filter)


        Pane all = new Pane();
        all.getChildren().addAll(details)

        columnStack.getChildren().addAll( columnCBBorder, columnsCBs)
        columnStack.relocate(0, 290)

        filterStack.getChildren().addAll( filtersCBBorder, filtersCBs)
        filterStack.relocate(0, 420)
        columns.relocate(5,340)
        columns.setRotate(90)
        filter.relocate(13,470)
        filter.setRotate(90)
        tableStack.getChildren().addAll(table,columns,filter, columnEventBorder, filtersEventBorder, columnStack, filterStack)

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableStack);
        borderPane.setRight(all);

        pane.getChildren().addAll(borderPane)

        return pane

    }

    /*METHODS*/
    static private Rectangle createEventBorder(){

        Rectangle eventBorder = new Rectangle()
        eventBorder.setStroke(Color.INDIANRED)
        eventBorder.setStrokeWidth(2)
        eventBorder.setWidth(40)
        eventBorder.setHeight(100)
        eventBorder.setFill(Color.WHITESMOKE)
        eventBorder.setArcWidth(10)
        eventBorder.setArcHeight(10)
        eventBorder.setOpacity(0.3)
        return eventBorder
    }

     static private Rectangle createCBsBorder(){
        Rectangle border = new Rectangle()
         border.setStroke(Color.INDIANRED)
         border.setStrokeWidth(2)
         border.setWidth(170)
         border.setHeight(100)
         border.setFill(Color.WHITESMOKE)
         border.setArcWidth(10)
         border.setArcHeight(10)
         border.setOpacity(0.8)
        return border
    }

    static private void setMouseEventSliding(Rectangle border, Pane pane, Timeline show, Timeline hide, Label tooltip){

        border.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                border.setVisible(false)
                border.setDisable(true)
                show.play();
                tooltip.setVisible(false)


            }
        });

        pane.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                border.setVisible(true)
                border.setDisable(false)
                hide.play();
                tooltip.setVisible(true)

            }
        });
    }

    static private HBox createPair(TextField detail, Label detailTooltip){

        HBox hBox = new HBox()
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(10, 0, 0, 10));
        hBox.getChildren().addAll(detailTooltip, detail)
        return hBox
    }

    private void setAnimation(){
        // Initially hiding the Pane
        clipRect = new Rectangle();
        clipRect.setWidth(30);
        clipRect.setHeight(boxBounds.getHeight());
        clipRect.translateXProperty().set(boxBounds.getWidth());
        columnStack.setClip(clipRect);
        columnStack.translateXProperty().set(-boxBounds.getWidth());

        // Animation for bouncing effect.
        final Timeline timelineBounce = new Timeline();
        timelineBounce.setCycleCount(2);
        timelineBounce.setAutoReverse(true);
        final KeyValue kv1 = new KeyValue(clipRect.widthProperty(), (boxBounds.getWidth()-15));
        final KeyValue kv2 = new KeyValue(clipRect.translateXProperty(), 15);
        final KeyValue kv3 = new KeyValue(columnStack.translateXProperty(), -15);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(100), kv1, kv2, kv3);
        timelineBounce.getKeyFrames().add(kf1);

        // Event handler to call bouncing effect after the scroll right is finished.
        EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                timelineBounce.play();
            }
        };

        timelineRight = new Timeline();
        timelineLeft = new Timeline();

        // Animation for scroll right.
        timelineRight.setCycleCount(1);
        timelineRight.setAutoReverse(true);
        final KeyValue kvDwn1 = new KeyValue(clipRect.widthProperty(), boxBounds.getWidth());
        final KeyValue kvDwn2 = new KeyValue(clipRect.translateXProperty(), 0);
        final KeyValue kvDwn3 = new KeyValue(columnStack.translateXProperty(), 0);
        final KeyFrame kfDwn = new KeyFrame(Duration.millis(200), onFinished, kvDwn1, kvDwn2, kvDwn3);
        timelineRight.getKeyFrames().add(kfDwn);

        // Animation for scroll left.
        timelineLeft.setCycleCount(1);
        timelineLeft.setAutoReverse(true);
        final KeyValue kvUp1 = new KeyValue(clipRect.widthProperty(), 0);
        final KeyValue kvUp2 = new KeyValue(clipRect.translateXProperty(), boxBounds.getWidth());
        final KeyValue kvUp3 = new KeyValue(columnStack.translateXProperty(), -boxBounds.getWidth());
        final KeyFrame kfUp = new KeyFrame(Duration.millis(200), kvUp1, kvUp2, kvUp3);
        timelineLeft.getKeyFrames().add(kfUp);
    }

    private void updateCityTree(LinkedList data) {
        treeCities.getSelectionModel().clearSelection()
        observableListCitiesCount.clear()
        observableListCities.clear()
        observableListCitiesCount.addAll(data.get(2).get("numCount"))
        observableListCities.addAll(data.get(2).get("ids"))
        Map<Integer,Integer> map = new LinkedHashMap<Integer,Integer>();
        for (int i=0; i<observableListCities.size(); i++) {
            map.put(observableListCities.get(i), observableListCitiesCount.get(i));
        }
        List<TreeItem<String>> iteratedList = new ArrayList<TreeItem<String>>()
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
        treeCities.getRoot().getChildren().addAll(iteratedList)    }
    private void updateZipTree(LinkedList data) {
        treeZip.getSelectionModel().clearSelection()
        observableListZips.clear()
        observableListZips.addAll(data.get(3).get("ids"))
        observableListZipsCount.clear()
        observableListZipsCount.addAll(data.get(3).get("numCount"))
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
        Collections.sort(iteratedList, new TreeItemNumberComparator())
        treeZip.getRoot().getChildren().clear()
        treeZip.getRoot().getChildren().addAll(iteratedList)

    }
    private void updateTypeTree(LinkedList data) {
        treeTypes.getSelectionModel().clearSelection()
        observableListTypes.clear()
        observableListTypes.addAll(data.get(1).get("ids"))
        observableListTypesCount.clear()
        observableListTypesCount.addAll(data.get(1).get("numCount"))
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

        bindAttribute(clientDolphin[ORDER][PLANT_TYPE],{
            if(it.newValue==0){
                treeTypes.getSelectionModel().clearSelection()
                trees.getChildren().remove(treeTypes)
            }
            else if(it.oldValue==0) {trees.getChildren().add(treeTypes)}
                plantTypes.setText("")
                clientDolphin.data GET, { data ->
                updateTypeTree(data)

            }
        })
        bindAttribute(clientDolphin[ORDER][CITY],{
            if(it.newValue==0){
                treeCities.getSelectionModel().clearSelection()
                trees.getChildren().remove(treeCities)
            }
            else if(it.oldValue==0) { trees.getChildren().add(treeCities)}
                city.setText("")
                clientDolphin.data GET, {data ->
                updateCityTree(data)

            }
        })
        bindAttribute(clientDolphin[ORDER][ZIP],{
            if(it.newValue==0){
                treeZip.getSelectionModel().clearSelection()
                trees.getChildren().remove(treeZip)
            }
            else if(it.oldValue==0) {trees.getChildren().add(treeZip)}
                zip.setText("")
                clientDolphin.data GET, {data ->
                updateZipTree(data)

            }
        })
//        bindAttribute(clientDolphin[STATE][TRIGGER], {
//            observableList.clear()
//            observableListCities.clear()
//            observableListTypes.clear()
//            observableListZips.clear()
//            clientDolphin.data GET, { data ->
//
//                observableListCities.addAll(data.get(2).get("ids"))
//                treeCities.getRoot().getChildren().clear()
//                observableListCities.each {
//                    if(it.toString().endsWith("(0)")) return;
//                    final TreeItem<String> checkBoxTreeItem =
//                        new TreeItem<String>(it.toString());
//                    treeCities.getRoot().getChildren().add(checkBoxTreeItem);
//
//                }
//
//                observableListTypes.addAll(data.get(1).get("ids"))
//                treeTypes.getRoot().getChildren().clear()
//                observableListTypes.each {
////                    if(it.toString().endsWith("(0)")) return;
//                    final TreeItem<String> checkBoxTreeItem =
//                        new TreeItem<String>(it.toString());
//                    treeTypes.getRoot().getChildren().add(checkBoxTreeItem);
//                }
//
//                observableListZips.addAll(data.get(3).get("ids"))
//                treeZip.getRoot().getChildren().clear()
//                observableListZips.each {
////                    if(it.toString().endsWith("(0)")) return;
//                    final TreeItem<String> checkBoxTreeItem =
//                        new TreeItem<String>(it.toString());
//                    treeZip.getRoot().getChildren().add(checkBoxTreeItem);
//                }
//
//                observableList.addAll( data.get(0).get("ids"))
//                if (observableList.size()==0){table.setPlaceholder(noData)}
//                else{table.setPlaceholder(loading)}
//
//            }
//        })
    }

    private void setAnimationFilters(){
        // Initially hiding the Pane
        clipRect = new Rectangle();
        clipRect.setWidth(30);
        clipRect.setHeight(boxBounds.getHeight());
        clipRect.translateXProperty().set(boxBounds.getWidth());
        filterStack.setClip(clipRect);
        filterStack.translateXProperty().set(-boxBounds.getWidth());

        // Animation for bouncing effect.
        final Timeline timelineBounce = new Timeline();
        timelineBounce.setCycleCount(2);
        timelineBounce.setAutoReverse(true);
        final KeyValue kv1 = new KeyValue(clipRect.widthProperty(), (boxBounds.getWidth()-15));
        final KeyValue kv2 = new KeyValue(clipRect.translateXProperty(), 15);
        final KeyValue kv3 = new KeyValue(filterStack.translateXProperty(), -15);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(100), kv1, kv2, kv3);
        timelineBounce.getKeyFrames().add(kf1);

        // Event handler to call bouncing effect after the scroll right is finished.
        EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                timelineBounce.play();
            }
        };

        timelineRightFilters = new Timeline();
        timelineLeftFilters = new Timeline();

        // Animation for scroll right.
        timelineRightFilters.setCycleCount(1);
        timelineRightFilters.setAutoReverse(true);
        final KeyValue kvDwn1 = new KeyValue(clipRect.widthProperty(), boxBounds.getWidth());
        final KeyValue kvDwn2 = new KeyValue(clipRect.translateXProperty(), 0);
        final KeyValue kvDwn3 = new KeyValue(filterStack.translateXProperty(), 0);
        final KeyFrame kfDwn = new KeyFrame(Duration.millis(200), onFinished, kvDwn1, kvDwn2, kvDwn3);
        timelineRightFilters.getKeyFrames().add(kfDwn);

        // Animation for scroll left.
        timelineLeftFilters.setCycleCount(1);
        timelineLeftFilters.setAutoReverse(true);
        final KeyValue kvUp1 = new KeyValue(clipRect.widthProperty(), 0);
        final KeyValue kvUp2 = new KeyValue(clipRect.translateXProperty(), boxBounds.getWidth());
        final KeyValue kvUp3 = new KeyValue(filterStack.translateXProperty(), -boxBounds.getWidth());
        final KeyFrame kfUp = new KeyFrame(Duration.millis(200), kvUp1, kvUp2, kvUp3);
        timelineLeftFilters.getKeyFrames().add(kfUp);
    }

    public static void bindAttribute(Attribute attribute, Closure closure) {
        final listener = closure as PropertyChangeListener
        attribute.addPropertyChangeListener('value', listener)
    }

    static private void ChoiceBoxListener(CheckBox cb, TableView table, TableColumn col){
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    table.getColumns().add(col)
                }
                else table.getColumns().remove(col)
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
        result.setMinWidth(100)
        return result;
    }

    public static TableColumn<PowerPlant, String> fourthColumn() {
        TableColumn<PowerPlant, String> result = new TableColumn<>("Type");
        result.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PowerPlant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PowerPlant, String> param) {
                return param.getValue().typeProperty();
            }
        });
        result.setMinWidth(100)
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
        result.setMinWidth(100)
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
        result.setMinWidth(100)
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
        result.setMinWidth(100)
        return result;
    }

    private static void loadPresentationModel(int rowIdx) {
        if (rowIdx == -1) return;
        System.out.println("loadPresentationModel: rowIdx = " + rowIdx);
        PowerPlant initialPlant = getTable().getItems().get(rowIdx)
        if (initialPlant.getLoadState() == LoadState.LOADED) return;
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
//                getTable().getColumns().clear()
//                getTable().getColumns().addAll(getIdCol(), getTypeCol(), getCityCol(), getZipCol(), getNominalCol());
                }
            })

        } else {
            initialPlant.setLoadState(LoadState.LOADING);
            clientDolphin.findPresentationModelById(STATE).findAttributeByPropertyName(START_INDEX).setValue(rowIdx)
            clientDolphin.send GET_FIFTY, { pms ->
                pms.each { pm ->
                    PowerPlant plant = getTable().getItems().get(Integer.parseInt(pm.getId()))
                    plant.setDbId(rowIdx)
                    plant.typeProperty().setValue(pm[PLANT_TYPE].getValue().toString())
                    plant.zipProperty().setValue(pm[ZIP].getValue().toString())
                    plant.cityProperty().setValue(pm[CITY].getValue().toString())
                    plant.nominalProperty().setValue(pm[NOMINAL_POWER].getValue().toString())
                    plant.positionProperty().setValue(pm[POSITION].getValue().toString())
                    plant.setLoadState(LoadState.LOADED);

                }

            }
        }



    }


}
