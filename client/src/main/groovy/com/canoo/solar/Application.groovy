package com.canoo.solar

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableIntegerValue
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.Duration
import np.com.ngopal.control.AutoFillTextBox
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.WithPresentationModelHandler
import java.beans.PropertyChangeListener
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

import static com.canoo.solar.Constants.CMD.GET_CITIES
import static com.canoo.solar.Constants.CMD.GET_TYPE
import static com.canoo.solar.Constants.FilterConstants.CITY;
import static com.canoo.solar.Constants.FilterConstants.FILTER
import static com.canoo.solar.Constants.FilterConstants.ID
import static com.canoo.solar.Constants.FilterConstants.NOMINAL_POWER
import static com.canoo.solar.Constants.FilterConstants.PLANT_TYPE;
import static com.canoo.solar.Constants.FilterConstants.*
import static com.canoo.solar.Constants.CMD.GET
import static com.canoo.solar.Constants.FilterConstants.ZIP;
import static org.opendolphin.binding.JFXBinder.bind
import static com.canoo.solar.ApplicationConstants.*

public class Application extends javafx.application.Application {
    static ClientDolphin clientDolphin;
    javafx.collections.ObservableList<Integer> observableList = FXCollections.observableArrayList()
    javafx.collections.ObservableList<Integer> observableListCities = FXCollections.observableArrayList()
    javafx.collections.ObservableList<Integer> observableListTypes = FXCollections.observableArrayList()
    javafx.collections.ObservableList<Integer> observableListZips = FXCollections.observableArrayList()

    private PresentationModel textAttributeModel;
    private TableView<PP> table = new TableView();
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
    private Rectangle clipRect;
    private Timeline timelineLeft;
    private Timeline timelineRight;
    private Timeline timelineLeftFilters;
    private Timeline timelineRightFilters;
    final TreeView treeCities = new TreeView();
    final TreeView treeTypes = new TreeView();
    final TreeView treeZip = new TreeView();
    TextField zipText = new TextField()
    final Label selectionLabeltypes = new Label();
    final Label selectionLabelcities = new Label();
    final Label selectionLabelzips = new Label();
    final Separator separator = new Separator();
    final Separator separator2 = new Separator();
    TextField nominalText = new TextField()
    TextField typeLabelforBinding = typeChoiceBox.getTextbox()
    TextField cityLabelforBinding = cityText.getTextbox()


    public Application() {
        textAttributeModel = clientDolphin.presentationModel(PM_APP, new ClientAttribute(ATT_ATTR_ID, null));
    }

/*
    public static ObservableList<Integer> items(int howMany) {
        javafx.collections.ObservableList<Integer> result = FXCollections.observableArrayList();
        for (int i = 1; i < howMany; i++) {
            result.add(new Integer(i));

        }
        System.out.println("done");
        return result;
    }
*/

    public static class PP {
        ObservableStringValue zipProperty = new SimpleStringProperty()
        ObservableStringValue cityProperty = new SimpleStringProperty()
        ObservableStringValue typeProperty = new SimpleStringProperty()
        ObservableIntegerValue idProperty = new SimpleIntegerProperty()
        ObservableStringValue nominalProperty = new SimpleStringProperty()
    }

    public static ObservableList items(int howMany) {
        ObservableList result = FXCollections.observableArrayList();
        for (int i = 1; i < howMany; i++) {
            result.add(new PP());

        }

        return result;
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Application Title");
        initializePresentationModels();
//
//        observableList.clear()
//        observableListCities.clear()
//        observableListTypes.clear()
//        observableListZips.clear()
//        clientDolphin.data GET, { data ->
//
//            observableListCities.addAll(data.get(2).get("ids"))
//            treeCities.getRoot().getChildren().clear()
//            observableListCities.each {
//                if(it.toString().endsWith("(0)")) return;
//                final TreeItem<String> checkBoxTreeItem =
//                    new TreeItem<String>(it.toString());
//                treeCities.getRoot().getChildren().add(checkBoxTreeItem);
//
//            }
//
//            observableListTypes.addAll(data.get(1).get("ids"))
//            treeTypes.getRoot().getChildren().clear()
//            observableListTypes.each {
////                    if(it.toString().endsWith("(0)")) return;
//                final TreeItem<String> checkBoxTreeItem =
//                    new TreeItem<String>(it.toString());
//                treeTypes.getRoot().getChildren().add(checkBoxTreeItem);
//            }
//
//            observableListZips.addAll(data.get(3).get("ids"))
//            treeZip.getRoot().getChildren().clear()
//            observableListZips.each {
////                    if(it.toString().endsWith("(0)")) return;
//                final TreeItem<String> checkBoxTreeItem =
//                    new TreeItem<String>(it.toString());
//                treeZip.getRoot().getChildren().add(checkBoxTreeItem);
//            }
//
//
////            observableList.addAll( data.get(0).get("ids"))
////            if (observableList.size()==0){table.setPlaceholder(noData)}
////            else{table.setPlaceholder(loading)}
//
//        }
//        observableList.addAll(items(100))
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
        clientDolphin.presentationModel(STATE, [TRIGGER])[TRIGGER].value=0


    }

    TableView getTable() {
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
        def orderPm = clientDolphin.findPresentationModelById(ORDER)
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

//      create borders for sliding panes
        Rectangle columnCBBorder = createCBsBorder()
        Rectangle columnEventBorder = createEventBorder()
        columnEventBorder.relocate(0, 290)
        Rectangle filtersCBBorder = createCBsBorder()
        Rectangle filtersEventBorder = createEventBorder()
        filtersEventBorder.relocate(0, 420)

        table.setMinHeight(700)
        def allitems = items(1000)
//        table.items = observableList
        println allitems
        table.getItems().addAll(allitems)
        table.setPlaceholder(loading)
        table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->

            if (selectedPm==null) return;
            clientDolphin.clientModelStore.withPresentationModel(selectedPm.toString(), new WithPresentationModelHandler() {
                void onFinished(ClientPresentationModel presentationModel) {
                    clientDolphin.apply presentationModel to clientDolphin[SELECTED_POWERPLANT]

                }
            } )
        } as ChangeListener )

        final Map<Integer, CountDownLatch> rowLatches = new HashMap<>();

        table.setRowFactory(new Callback<TableView<PP>, TableRow<PP>>() {
            @Override
            public TableRow<PP> call(final TableView<PP> param) {
                TableRow<PP> tableRow = new TableRow<PP>();
                tableRow.indexProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number rowIdx) {

                        if (rowIdx == -1) return



                        getClientDolphin().clientModelStore.withPresentationModel(rowIdx.toString(), new WithPresentationModelHandler() {
                            void onFinished(ClientPresentationModel pm) {
                                println "created pm: " + pm.id
                                PP pp = getTable().items[rowIdx.toInteger()]
                                ObservableStringValue zipString = new SimpleStringProperty(pm[ZIP].getValue().toString())
                                pp.setZipProperty(zipString)
                                ObservableStringValue cityString = new SimpleStringProperty(pm[CITY].getValue().toString())
                                pp.setCityProperty(cityString)
                                ObservableIntegerValue idString = new SimpleIntegerProperty(pm[POSITION].getValue())
                                pp.setIdProperty(idString)
                                ObservableStringValue nominalString = new SimpleStringProperty(pm[NOMINAL_POWER].getValue().toString())
                                pp.setNominalProperty(nominalString)
                                ObservableStringValue typeString = new SimpleStringProperty(pm[PLANT_TYPE].getValue().toString())
                                pp.setTypeProperty(typeString)
                                getTable().getColumns().clear()
                                getTable().getColumns().addAll(getIdCol(), getTypeCol(), getCityCol(), getZipCol(), getNominalCol());
                            }
                        })

                    }
                });

                return tableRow;
            }
        });



        idCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PP, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<PP, Integer> param) {
                def value = param.getValue();

                return value.getIdProperty()


            }
        });


        zipCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PP, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PP, String> param) {
                PP pp = param.getValue()
                return pp.getZipProperty()
            }
        });

//
        nominalCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PP, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PP, String> param) {
                return param.getValue().getNominalProperty()

            }
        });
//

        typeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PP, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PP, String> param) {
                return  param.getValue().getTypeProperty()

            }
        });


        cityCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PP, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PP, String> param) {
               return param.getValue().getCityProperty()
            }
        });
        table.getColumns().addAll(idCol, typeCol, cityCol, zipCol, nominalCol);
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

        HBox trees = new HBox()
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
                    println "typeTree added with order: " + i
                    trees.getChildren().add(treeTypes)
                }
                else {
                    trees.getChildren().remove(treeTypes)
                   def order = orderPm.findAttributeByPropertyName(PLANT_TYPE).getValue()
                    orderPm.getAttributes().each {if(it.value > order) it.setValue(it.getValue()-1)}
                    orderPm.findAttributeByPropertyName(PLANT_TYPE).setValue(0)

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
                    println "cityTree added with order: " + i
                    trees.getChildren().add(treeCities)
                }
                else {
                    trees.getChildren().remove(treeCities)
                    def order = orderPm.findAttributeByPropertyName(CITY).getValue()
                    orderPm.getAttributes().each {if(it.value > order) it.setValue(it.getValue()-1)}
                    orderPm.findAttributeByPropertyName(CITY).setValue(0)
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
                    println "zipTree added with order: " + i
                    trees.getChildren().add(treeZip)
                }
                else {
                    trees.getChildren().remove(treeZip)
                    def order = orderPm.findAttributeByPropertyName(ZIP).getValue()
                    orderPm.getAttributes().each {if(it.value > order) it.setValue(it.getValue()-1)}
                    orderPm.findAttributeByPropertyName(ZIP).setValue(0)

                }
                            }
        });

        treeTypes.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TreeItem <String>>() {
                    public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                                        TreeItem<String> oldItem, TreeItem<String> newItem) {

                        String[] parts = newItem.getValue().toString().split(" \\(");
                        String part1 = parts[0];
                        selectionLabeltypes.setText(part1);
                        if (orderPm.findAttributeByPropertyName(CITY).getValue() > orderPm.findAttributeByPropertyName(PLANT_TYPE).getValue()){selectionLabelcities.setText("")}
                        if (orderPm.findAttributeByPropertyName(ZIP).getValue() > orderPm.findAttributeByPropertyName(PLANT_TYPE).getValue()){selectionLabelzips.setText("")}
                        observableListTypes.clear()
                        observableList.clear()
                        clientDolphin.data GET, { data ->
                            if (orderPm.findAttributeByPropertyName(CITY).getValue() > orderPm.findAttributeByPropertyName(PLANT_TYPE).getValue()){
                                observableListCities.clear()
                                selectionLabelcities.setText("")
                                observableListCities.addAll(data.get(2).get("ids"))
                                treeCities.getRoot().getChildren().clear()
                                observableListCities.each {
//                                    if(it.toString().endsWith("(0)")) return;
                                    final TreeItem<String> checkBoxTreeItem =
                                        new TreeItem<String>(it.toString());
                                    treeCities.getRoot().getChildren().add(checkBoxTreeItem);

                                }
                            }
                            observableListTypes.addAll(data.get(1).get("ids"))
                            treeTypes.getRoot().getChildren().clear()
                            observableListTypes.each {
//                    if(it.toString().endsWith("(0)")) return;
                                final TreeItem<String> checkBoxTreeItem =
                                    new TreeItem<String>(it.toString());
                                treeTypes.getRoot().getChildren().add(checkBoxTreeItem);
                            }
                            if (orderPm.findAttributeByPropertyName(ZIP).getValue() > orderPm.findAttributeByPropertyName(PLANT_TYPE).getValue()){
                                observableListZips.clear()

                                observableListZips.addAll(data.get(3).get("ids"))
                                treeZip.getRoot().getChildren().clear()
                                selectionLabelzips.setText("")
                                observableListZips.each {
    //                    if(it.toString().endsWith("(0)")) return;
                                    final TreeItem<String> checkBoxTreeItem =
                                        new TreeItem<String>(it.toString());
                                    treeZip.getRoot().getChildren().add(checkBoxTreeItem);
                                }
                            }
                            observableList.addAll( data.get(0).get("ids"))
                            if (observableList.size()==0){table.setPlaceholder(noData)}
                            else{table.setPlaceholder(loading)}

                        }
                    }
                });
        treeCities.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TreeItem <String>>() {
                    public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                                        TreeItem<String> oldItem, TreeItem<String> newItem) {

                        if (newItem==null) return;
                        String[] parts = newItem.getValue().toString().split(" \\(");
                        String part1 = parts[0];
                        selectionLabelcities.setText(part1);
                        if (orderPm.findAttributeByPropertyName(PLANT_TYPE).getValue() > orderPm.findAttributeByPropertyName(CITY).getValue()){selectionLabeltypes.setText("")}
                        if (orderPm.findAttributeByPropertyName(ZIP).getValue() > orderPm.findAttributeByPropertyName(CITY).getValue()){selectionLabelzips.setText("")}
                        observableListCities.clear()


                        observableList.clear()
                        clientDolphin.data GET, { data ->

                            observableListCities.addAll(data.get(2).get("ids"))
                            treeCities.getRoot().getChildren().clear()
                            observableListCities.each {
//                                if(it.toString().endsWith("(0)")) return;
                                final TreeItem<String> checkBoxTreeItem =
                                    new TreeItem<String>(it.toString());
                                treeCities.getRoot().getChildren().add(checkBoxTreeItem);

                            }
                            if (orderPm.findAttributeByPropertyName(PLANT_TYPE).getValue() > orderPm.findAttributeByPropertyName(CITY).getValue()){
                                observableListTypes.clear()
                                observableListTypes.addAll(data.get(1).get("ids"))
                                treeTypes.getRoot().getChildren().clear()
                                observableListTypes.each {
    //                    if(it.toString().endsWith("(0)")) return;
                                    final TreeItem<String> checkBoxTreeItem =
                                        new TreeItem<String>(it.toString());
                                    treeTypes.getRoot().getChildren().add(checkBoxTreeItem);
                                }
                            }
                            if (orderPm.findAttributeByPropertyName(ZIP).getValue() > orderPm.findAttributeByPropertyName(CITY).getValue()){
                                observableListZips.clear()
                                observableListZips.addAll(data.get(3).get("ids"))
                                treeZip.getRoot().getChildren().clear()
                                observableListZips.each {
    //                    if(it.toString().endsWith("(0)")) return;
                                    final TreeItem<String> checkBoxTreeItem =
                                        new TreeItem<String>(it.toString());
                                    treeZip.getRoot().getChildren().add(checkBoxTreeItem);
                                }
                            }
                            observableList.addAll( data.get(0).get("ids"))
                            if (observableList.size()==0){table.setPlaceholder(noData)}
                            else{table.setPlaceholder(loading)}

                        }
                    }
                });
        treeZip.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TreeItem <String>>() {
                    public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                                        TreeItem<String> oldItem, TreeItem<String> newItem) {

                        if (newItem==null) return;
                        String[] parts = newItem.getValue().toString().split(" \\(");
                        String part1 = parts[0];
                        selectionLabelzips.setText(part1);
                        if (orderPm.findAttributeByPropertyName(CITY).getValue() > orderPm.findAttributeByPropertyName(ZIP).getValue()){selectionLabelcities.setText("")}
                        if (orderPm.findAttributeByPropertyName(PLANT_TYPE).getValue() > orderPm.findAttributeByPropertyName(ZIP).getValue()){selectionLabeltypes.setText("")}
                        observableListZips.clear()

                        observableList.clear()
                        clientDolphin.data GET, { data ->
                            if (orderPm.findAttributeByPropertyName(CITY).getValue() > orderPm.findAttributeByPropertyName(ZIP).getValue()){
                                observableListCities.clear()
                                observableListCities.addAll(data.get(2).get("ids"))
                                treeCities.getRoot().getChildren().clear()
                                observableListCities.each {
//                                    if(it.toString().endsWith("(0)")) return;
                                    final TreeItem<String> checkBoxTreeItem =
                                        new TreeItem<String>(it.toString());
                                    treeCities.getRoot().getChildren().add(checkBoxTreeItem);

                                }
                            }
                            if (orderPm.findAttributeByPropertyName(PLANT_TYPE).getValue() > orderPm.findAttributeByPropertyName(ZIP).getValue()){
                                observableListTypes.clear()
                                observableListTypes.addAll(data.get(1).get("ids"))
                                treeTypes.getRoot().getChildren().clear()
                                observableListTypes.each {
    //                    if(it.toString().endsWith("(0)")) return;
                                    final TreeItem<String> checkBoxTreeItem =
                                        new TreeItem<String>(it.toString());
                                    treeTypes.getRoot().getChildren().add(checkBoxTreeItem);
                                }
                            }
                            observableListZips.addAll(data.get(3).get("ids"))
                            treeZip.getRoot().getChildren().clear()
                            observableListZips.each {
//                    if(it.toString().endsWith("(0)")) return;
                                final TreeItem<String> checkBoxTreeItem =
                                    new TreeItem<String>(it.toString());
                                treeZip.getRoot().getChildren().add(checkBoxTreeItem);
                            }

                            observableList.addAll( data.get(0).get("ids"))
                            if (observableList.size()==0){table.setPlaceholder(noData)}
                            else{table.setPlaceholder(loading)}

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

    private void setupBinding() {
        bind 'text' of selectionLabelzips to ZIP of clientDolphin[FILTER]
        bind 'text' of selectionLabelcities to CITY of clientDolphin[FILTER]
        bind 'text' of selectionLabeltypes to PLANT_TYPE of clientDolphin[FILTER]
        bind 'text' of nominalText to NOMINAL_POWER of clientDolphin[FILTER]

        bind ZIP of clientDolphin[SELECTED_POWERPLANT] to 'text' of zipLabelDetail
        bind CITY of clientDolphin[SELECTED_POWERPLANT] to 'text' of cityLabelDetail
        bind ID of clientDolphin[SELECTED_POWERPLANT] to 'text' of idLabelDetail
        bind NOMINAL_POWER of clientDolphin[SELECTED_POWERPLANT] to 'text' of nominalLabelDetail
        bind PLANT_TYPE of clientDolphin[SELECTED_POWERPLANT] to 'text' of typeLabelDetail

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

}
