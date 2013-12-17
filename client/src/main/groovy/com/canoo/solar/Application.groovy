package com.canoo.solar

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Border
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.LinearGradientBuilder
import javafx.scene.paint.Stop
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.util.Duration

import static org.opendolphin.binding.JFXBinder.bind
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import javafx.util.Callback
import org.opendolphin.core.Attribute
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin

import java.beans.PropertyChangeListener
import static com.canoo.solar.ApplicationConstants.ATT_ATTR_ID
import static com.canoo.solar.ApplicationConstants.PM_APP
import static com.canoo.solar.Constants.CMD.GET
import static com.canoo.solar.Constants.CMD.GET_ROW
import static com.canoo.solar.Constants.FilterConstants.*
public class Application extends javafx.application.Application {

    static ClientDolphin clientDolphin;
    static javafx.collections.ObservableList<Integer> observableListCities
    static javafx.collections.ObservableList<Integer> observableListCitiesCount
    static javafx.collections.ObservableList<Integer> observableListTypes
    static javafx.collections.ObservableList<Integer> observableListTypesCount
    static javafx.collections.ObservableList<Integer> observableListZips
    static javafx.collections.ObservableList<Integer> observableListZipsCount

    private PresentationModel textAttributeModel;
    public static TableView<PowerPlant> table
    Label cityLabelforDetail
    Label zipLabelforDetail
    Label typeLabelforDetail
    Label nominalLabelforDetail
    Label idLabelforDetail
    Label noData
    static long start
    static List<Range<Integer>> bounds = new ArrayList<Range<Integer>>()
    static List<Double> widths

    Label totalNominalLabel
    TextField cityLabelDetail
    TextField idLabelDetail
    TextField zipLabelDetail
    TextField typeLabelDetail
    TextField nominalLabelDetail

    static TextField zipTextAutoForSearch
    static TextField typeTextAutoForSearch
    static TextField cityTextAutoForSearch

    TextField zipTextforSearch
    TextField typeTextforSearch
    TextField cityTextforSearch
    TextField nominalTextforSearch

    static Rectangle dragBorder

    TreeItem<String> rootItemCities
    TreeItem<String> rootItemZip
    TreeItem<String> rootItem

    Label selectionDetailsLabel
    Label columns
    Label filter

    int c
    CheckBox cityCB
    CheckBox typeCB
    CheckBox nominalCB
    CheckBox zipCB
    CheckBox positionCB
    CheckBox avgkwhCB
    CheckBox latitudeCB
    CheckBox longitudeCB
    static Map panesToAttributes

    static SimpleBooleanProperty disableControls
    CheckBox cityFilterCB
    CheckBox typeFilterCB
    CheckBox zipFilterCB

    Pane columnStack
    Pane filterStack
    static StackPane tableStack
    static Pane pane
    static GridPane treesGrid
    static HBox all

    static HBox facetBox
    static HBox searchBox
    static VBox col1
    static VBox col2
    static VBox col3
    static VBox col4
    static VBox col5
    static VBox col6
    static VBox col7
    static VBox col8
    static VBox col9
    static VBox searchAndAll

    public static Rectangle placeholder
    public static Rectangle detailsContainer

    VBox details
    VBox tableBox
    static Pane tablePane
    static VBox filtersCBs
    static VBox columnsCBs
    public static Rectangle filtersCBBorder
    public static Rectangle filtersEventBorder
    public static Rectangle columnCBBorder
    public static Rectangle columnEventBorder

    static TreeView treeCities
    static TreeView treeTypes
    static TreeView treeZip

    Label plantTypes
    Label city
    Label zip

    Label plantTypes_auto
    Label city_auto
    Label zip_auto

    ProgressBar progressBar
    SimpleDoubleProperty progress

    public static Label total
    public static Label totalCount

    static SimpleStringProperty totalNominal
    Timeline progressLine

    static Separator separator
    static Separator facetTableSeparator
    TextField nominalText
    TextField searchField
    Label searchText

    Pane typePane
    static Button closeType
    Pane zipPane
    static Button closeZip
    Pane cityPane
    static Button closeCity

    public static PowerPlantList fakedPlantList
    javafx.collections.ObservableList<PowerPlant> items

    public Application() {
        textAttributeModel = clientDolphin.presentationModel(PM_APP, new ClientAttribute(ATT_ATTR_ID, null));
    }

    @Override
    public void start(Stage stage) throws Exception {
        initializeComponents();
        initializePresentationModels();
//        disableControls.setValue(true)
//      disableQuery()

        observableListCities.clear()
        observableListTypes.clear()
        observableListZips.clear()
        clientDolphin.data GET, { data ->
            def cityCount = 0
            def typeCount = 0
            def zipCount = 0
            def size = data.get(0).get("size")
            observableListCities.addAll(data.get(2).get(IDS))
            observableListCitiesCount.addAll(data.get(2).get(NUM_COUNT))
            treeCities.getRoot().getChildren().clear()
            observableListCities.each {
                if(observableListCitiesCount.get(cityCount).toString().equals("0")) return;
                cityCount++
                final TreeItem<String> checkBoxTreeItem =
                    new TreeItem<String>(it.toString() + " (" + observableListCitiesCount.get(cityCount-1).toString() + ")");

                treeCities.getRoot().getChildren().add(checkBoxTreeItem);
            }
            treeCities.getRoot().setValue("Cities ($size)")
            observableListTypes.addAll(data.get(1).get(IDS))
            observableListTypesCount.addAll(data.get(1).get(NUM_COUNT))
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
            observableListZipsCount.addAll(data.get(3).get(NUM_COUNT))
            treeZip.getRoot().getChildren().clear()
            observableListZips.each {
                if(observableListZipsCount.get(zipCount).toString().equals("0")) return;
                zipCount++
                def count = observableListZipsCount.get(zipCount-1)
                final TreeItem<String> checkBoxTreeItem =
                    new TreeItem<String>(it.toString() + " ($count)");
                treeZip.getRoot().getChildren().add(checkBoxTreeItem);
            }
            treeZip.getRoot().setValue("Zip-Codes ($size)")

//            clientDolphin.findPresentationModelById(ORDER_COLUMN).getAttributes().each {
//                addHeaderListener(Integer.parseInt(it.getValue().toString()), it.getPropertyName())
//            }
            totalNominal.setValue(data.get(4).get("total").toString())

        }

        Pane root = setupStage();
        setupBinding();

        Scene scene = new Scene(root, 780, 475)
        scene.stylesheets << 'demo.css'
        stage.minWidthProperty().bind(facetBox.widthProperty().add(50))
        stage.maxWidthProperty().bind(facetBox.widthProperty().add(50))
        stage.setScene(scene);
        stage.setTitle("Power Plants Explorer")

        stage.show();
    }

    private static void initializePresentationModels () {
        clientDolphin.presentationModel(FILTER, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER, ALL]);
        clientDolphin.presentationModel(FILTER_AUTOFILL, [CITY_AUTOFILL, PLANT_TYPE_AUTOFILL, ZIP_AUTOFILL]);
        clientDolphin.presentationModel(SELECTED_POWERPLANT, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER]);
        clientDolphin.presentationModel(ORDER, [CITY, PLANT_TYPE, ZIP, TABLE])
        clientDolphin.presentationModel(ORDER_COLUMN, [CITY_COLUMN, TYPE_COLUMN, ZIP_COLUMN, NOMINAL_COLUMN, POSITION_COLUMN, AVGKWH_COLUMN, LAT_COLUMN, LON_COLUMN])
        clientDolphin.presentationModel(STATE, [TRIGGER, START_INDEX, SORT, REVERSER_ORDER, CHANGE_FROM, HOLD])[TRIGGER].value=0

        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(ZIP).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(CITY).setValue(1)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(TABLE).setValue(0)

        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(CITY_COLUMN).setValue(2)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(TYPE_COLUMN).setValue(3)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(ZIP_COLUMN).setValue(1)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(NOMINAL_COLUMN).setValue(4)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(AVGKWH_COLUMN).setValue(5)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(LAT_COLUMN).setValue(6)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(LON_COLUMN).setValue(7)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(POSITION_COLUMN).setValue(0)

        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(START_INDEX).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(SORT).setValue("Position - DESCENDING")
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(REVERSER_ORDER).setValue(false)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(HOLD).setValue(false)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(CHANGE_FROM).setValue(0)
    }

    private Pane setupStage() {

//        facetBox.getChildren().addAll(col1, col2)
        facetBox.setSpacing(1)
        facetBox.setMinHeight(445)


        Image image = new Image("search.png");
        ImageView iv1 = new ImageView();
        iv1.setImage(image);
        iv1.setFitHeight(15)
        iv1.setFitWidth(15)

        disableControls.addListener(new ChangeListener<Boolean>() {
            @Override
            void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {

                if (aBoolean2){
                    progressBar.setVisible(true)
                    progress.set(0.0)
                    progressLine.setRate(1)
                    progressLine.playFromStart()

                }else{
                    progressLine.setRate(8)
                }

            }
        })
        progressBar.setVisible(false)
        progressBar.setMinHeight(10)
        progressBar.setMaxHeight(10)
        progressBar.setOpacity(0.6)
        progressBar.setBorder(Border.EMPTY)
        progressBar.minWidthProperty().bind(searchAndAll.widthProperty().add(5))
        progressBar.maxWidthProperty().bind(searchAndAll.widthProperty().add(5))
        final KeyValue kv1 = new KeyValue(progress, 0.0);
        final KeyValue kv2 = new KeyValue(progress, 0.5);
        final KeyValue kv3 = new KeyValue(progress, 1.0);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(6000), kv1, kv2, kv3);
        progressLine.getKeyFrames().add(kf1);
        progressLine.setCycleCount(1)

        progressLine.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            void handle(ActionEvent actionEvent) {
                progressBar.setVisible(false)
            }
        })

        detailsContainer.widthProperty().bind(table.widthProperty())
        LinearGradient linearGradDark = LinearGradientBuilder.create()
                .startX(0)
                .startY(0)
                .endX(0)
                .endY(22)
                .proportional(false)
                .cycleMethod(CycleMethod.NO_CYCLE)
                .stops( new Stop(0.1f, Color.rgb(245, 245, 245, 1)),
                new Stop(1.0f, Color.rgb(179, 179, 179, 1)))
                .build();

        LinearGradient linearGrad = LinearGradientBuilder.create()
                .startX(0)
                .startY(0)
                .endX(0)
                .endY(50)
                .proportional(false)
                .cycleMethod(CycleMethod.NO_CYCLE)
                .stops( new Stop(0.1f, Color.rgb(245, 245, 245, 1)),
                new Stop(1.0f, Color.rgb(179, 179, 179, 1)))
                .build();


        dragBorder.setHeight(22)
        dragBorder.widthProperty().bind(table.widthProperty())
        dragBorder.setFill(linearGradDark)
        dragBorder.setStroke(Color.BLACK)
        dragBorder.setStrokeWidth(0.5)
        dragBorder.setArcWidth(3)
        dragBorder.setArcHeight(3)

        placeholder.setArcHeight(10)
        placeholder.setArcWidth(10)
        placeholder.setStroke(linearGrad)
        placeholder.setStrokeWidth(2)
        placeholder.setFill(Color.WHITE)
        placeholder.setOpacity(0.5)
        placeholder.getStrokeDashArray().addAll(15d, 15d, 15d, 15d);
        detailsContainer.setFill(linearGrad)
        detailsContainer.setHeight(36)
        detailsContainer.setStrokeWidth(0.5)
        detailsContainer.setStroke(Color.BLACK)
        detailsContainer.setArcWidth(3)
        detailsContainer.setArcHeight(3)


        treesGrid.setGridLinesVisible(true)
        typeLabelDetail.setEditable(false)
        cityLabelDetail.setEditable(false)
        zipLabelDetail.setEditable(false)
        nominalLabelDetail.setEditable(false)
        idLabelDetail.setEditable(false)

        cityCB.setSelected(true);
        typeCB.setSelected(true);
        nominalCB.setSelected(true);
        zipCB.setSelected(true);
        positionCB.setSelected(true);

        table.setSortPolicy(new Callback<TableView<PowerPlant>, Boolean>() {
            @Override
            Boolean call(TableView<PowerPlant> powerPlantTableView) {
                if (table.getSortOrder().size() > 0) {
                    String s = new String()
                    table.getSortOrder().each {
                        s = s + it.getText() + " - " + it.getSortType() + ", "
                    }
                    clientDolphin[STATE][SORT].setValue(s.substring(0, s.lastIndexOf(",")))

                }else{
                    clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(SORT).setValue("Position - DESCENDING")
                }
                return true;
            }
        })

        columnEventBorder.relocate(0, 140)
        filtersEventBorder.relocate(0, 270)

        table.setMaxHeight(364)
        table.setMinHeight(364)
        table.setMaxWidth(700)
        table.setItems(items)
        table.setPlaceholder(noData)

        table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->
            if (selectedPm==null) return;
            String id = selectedPm.getDbId().toString()
            clientDolphin.apply clientDolphin.findPresentationModelById(id) to clientDolphin[SELECTED_POWERPLANT]
        } as ChangeListener )

        TableColumn<PowerPlant, String> positionColumn = TableFactory.firstColumn()
        TableColumn<PowerPlant, String> zipColumn = TableFactory.secondColumn()
        TableColumn<PowerPlant, String> cityColumn = TableFactory.thirdColumn()
        TableColumn<PowerPlant, String> typeColumn = TableFactory.fourthColumn()
        TableColumn<PowerPlant, String> nominalColumn = TableFactory.fithColumn()
        TableColumn<PowerPlant, String> subtypeColumn = TableFactory.sixthColumn()
        TableColumn<PowerPlant, String> latitudeColumn = TableFactory.seventhColumn()
        TableColumn<PowerPlant, String> longitudeColumn = TableFactory.eighthColumn()

        AutoFillTextField.makeAutofillTextField(this, zipTextAutoForSearch, observableListZips, zip_auto, treeZip, ZIP)
        AutoFillTextField.makeAutofillTextField(this, typeTextAutoForSearch, observableListTypes, plantTypes_auto, treeTypes, PLANT_TYPE)
        AutoFillTextField.makeAutofillTextField(this, cityTextAutoForSearch, observableListCities, city_auto, treeCities, CITY)

        zipColumn.setGraphic(zipTextAutoForSearch)
        cityColumn.setGraphic(cityTextAutoForSearch)
        typeColumn.setGraphic(typeTextAutoForSearch)

        zipTextAutoForSearch.setMaxWidth(zipColumn.getWidth()-55)
        cityTextAutoForSearch.setMaxWidth(cityColumn.getWidth()-55)
        typeTextAutoForSearch.setMaxWidth(typeColumn.getWidth()-55)

        table.getColumns().addAll(positionColumn, zipColumn, cityColumn, typeColumn, nominalColumn, subtypeColumn, latitudeColumn, longitudeColumn);

//        show/hide table Columns on checkbox event
        PresentationModel colOrder = clientDolphin.findPresentationModelById(ORDER_COLUMN)
        Listeners.setChoiceBoxListener(cityCB, table, cityColumn, CITY_COLUMN, colOrder)
        Listeners.setChoiceBoxListener(typeCB, table, typeColumn, TYPE_COLUMN, colOrder)
        Listeners.setChoiceBoxListener(nominalCB, table, nominalColumn, NOMINAL_COLUMN, colOrder)
        Listeners.setChoiceBoxListener(zipCB, table, zipColumn, ZIP_COLUMN, colOrder)
        Listeners.setChoiceBoxListener(positionCB, table, positionColumn, POSITION_COLUMN, colOrder)
        Listeners.setChoiceBoxListener(avgkwhCB, table, subtypeColumn, AVGKWH_COLUMN, colOrder)
        Listeners.setChoiceBoxListener(latitudeCB, table, latitudeColumn, LAT_COLUMN, colOrder)
        Listeners.setChoiceBoxListener(longitudeCB, table, longitudeColumn, LON_COLUMN, colOrder)

//        create selection details layout boxes
        HBox nominalLabelTextDetail = Layout.createPair(nominalLabelDetail, nominalLabelforDetail)
        HBox typeLabelTextDetail = Layout.createPair(typeLabelDetail, typeLabelforDetail)
        HBox cityLabelTextDetail = Layout.createPair(cityLabelDetail, cityLabelforDetail)
        HBox zipLabelTextDetail = Layout.createPair(zipLabelDetail, zipLabelforDetail)
        HBox idLabelTextDetail = Layout.createPair(idLabelDetail, idLabelforDetail)

//        assemble the treeViews and buttons together into a draggable pane
        def orderPm = clientDolphin.findPresentationModelById(ORDER)
        Layout.createTreePane(rootItemCities, treeCities, closeCity, cityPane, cityTextAutoForSearch, orderPm[CITY], bounds, widths)
        Layout.createTreePane(rootItemZip, treeZip, closeZip, zipPane, zipTextAutoForSearch, orderPm[ZIP], bounds, widths)
        Layout.createTreePane(rootItem, treeTypes, closeType, typePane, typeTextAutoForSearch, orderPm[PLANT_TYPE], bounds, widths)

        addDragging()

        columnsCBs.setPadding(new Insets(20, 0, 0, 10));
        columnsCBs.getChildren().addAll(cityCB, typeCB, zipCB, nominalCB, positionCB/*, avgkwhCB, latitudeCB, longitudeCB*/)

        panesToAttributes.put(typePane, orderPm[PLANT_TYPE])
        panesToAttributes.put(zipPane, orderPm[ZIP])
        panesToAttributes.put(cityPane, orderPm[CITY])
        panesToAttributes.put(tablePane, orderPm[TABLE])

        filtersCBs.setPadding(new Insets(25, 0, 0, 10));
        separator.setMinWidth(facetBox.getTranslateX())

        details.setSpacing(5);
        details.setPadding(new Insets(20, 10, 10, 10));
        details.getChildren().addAll(selectionDetailsLabel, cityLabelTextDetail, idLabelTextDetail, typeLabelTextDetail, nominalLabelTextDetail, zipLabelTextDetail)
        facetBox.setPadding(new Insets(0, 0, 0, 0));

        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                if (s2.equals("")){
                    searchText.setText("")
                    disableControls.setValue(true)
                    UpdateActions.clearPmsAndPowerPlants()
                    clientDolphin[STATE][HOLD].setValue(true)
                    UpdateActions.refreshTable()

                    disableControls.setValue(false)
                }
            }
        })

        searchField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (KeyCode.ENTER == event.getCode()) {
                    searchText.setText(searchField.getText())
                    disableControls.setValue(true)
//                  disableQuery()
                    UpdateActions.clearPmsAndPowerPlants()
                    clientDolphin[STATE][HOLD].setValue(true)
                    UpdateActions.refreshTable()
                    disableControls.setValue(false)
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
                                city.setText("")
                                UpdateActions.updateTree(data,treeCities,observableListCities,observableListCitiesCount,2,"Cities")
                            }

                            if (zipValue > typeValue){
                                zip.setText("")
                                UpdateActions.updateTree(data, treeZip, observableListZips, observableListZipsCount, 3, "Zip-Codes")
                            }
                            def size = data.get(0).get("size")
                            PowerPlantList newFakeList = new PowerPlantList((Integer)size, new OurConsumer<Integer>(){
                                @Override
                                void accept(Integer rowIndex) {
                                    loadPresentationModel(rowIndex)
                                }
                            });
                            totalNominal.setValue(data.get(4).get("total").toString())
                            javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
                            if (newItems.size()==0) {disableControls.setValue(false)}
                            table.setItems(newItems)
                            totalCount.setText(newItems.size() + "/1377475")
                            table.getSelectionModel().clearSelection()
                            clientDolphin.findPresentationModelById(SELECTED_POWERPLANT).getAttributes().each {
                                it.setValue("")
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
                                UpdateActions.updateTree(data,treeTypes, observableListTypes, observableListTypesCount, 1, "Plant Types")
                            }
                            if (zipValue > cityValue){
                                zip.setText("")
                                UpdateActions.updateTree(data, treeZip, observableListZips, observableListZipsCount, 3, "Zip-Codes")
                            }
                            def size = data.get(0).get("size")
                            PowerPlantList newFakeList = new PowerPlantList((Integer)size, new OurConsumer<Integer>(){
                                @Override
                                void accept(Integer rowIndex) {
                                    loadPresentationModel(rowIndex)
                                }
                            });
                            totalNominal.setValue(data.get(4).get("total").toString())
                            javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
                            if (newItems.size()==0) {disableControls.setValue(false)}
                            table.setItems(newItems)
                            totalCount.setText(newItems.size() + "/1377475")
                            table.getSelectionModel().clearSelection()
                            clientDolphin.findPresentationModelById(SELECTED_POWERPLANT).getAttributes().each {
                                it.setValue("")
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
                                UpdateActions.updateTree(data,treeCities,observableListCities,observableListCitiesCount,2,"Cities")

                            }
                            if (typeValue > zipValue){
                                plantTypes.setText("")
                                UpdateActions.updateTree(data,treeTypes, observableListTypes, observableListTypesCount, 1, "Plant Types")

                            }
                            def size = data.get(0).get(SIZE)
                            PowerPlantList newFakeList = new PowerPlantList((Integer)size, new OurConsumer<Integer>(){
                                @Override
                                void accept(Integer rowIndex) {
                                    loadPresentationModel(rowIndex)
                                }
                            });
                            totalNominal.setValue(data.get(4).get("total").toString())

                            javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
                            if (newItems.size()==0) {disableControls.setValue(false)}
                            table.setItems(newItems)
                            totalCount.setText(newItems.size() + "/1377475")
                            table.getSelectionModel().clearSelection()
                            clientDolphin.findPresentationModelById(SELECTED_POWERPLANT).getAttributes().each {
                                it.setValue("")
                            }
                        }
                    }
                });

        Animation.setAnimation(columnStack);
        Animation.setAnimationFilters(filterStack);
        Animation.setMouseEventSliding(columnEventBorder, columnStack, Animation.timelineRight, Animation.timelineLeft, columns)
        Animation.setMouseEventSliding(filtersEventBorder, filterStack, Animation.timelineRightFilters, Animation.timelineLeftFilters, filter)



        facetTableSeparator.setOrientation(Orientation.VERTICAL)
        columnStack.getChildren().addAll(columnCBBorder, columnsCBs)
        columnsCBs.relocate(0, -10)
        columnStack.relocate(0, 140)
        filterStack.getChildren().addAll(filtersCBBorder, filtersCBs)
        filterStack.relocate(0, 270)
        columns.relocate(-9, 190)
        columns.setRotate(90)
        filter.relocate(-3, 320)
        filter.setRotate(90)
        tableStack.getChildren().addAll(detailsContainer, total, totalNominalLabel, totalCount)
        totalCount.translateXProperty().bind(table.widthProperty().divide(2).subtract(55))
        total.translateXProperty().bind(table.widthProperty().divide(2).subtract(20).multiply(-1))
        progressBar.translateYProperty().bind(pane.heightProperty().subtract(6))
        tableBox.getChildren().addAll(dragBorder, table, tableStack)
        tablePane.getChildren().add(tableBox)
        tablePane.setPadding(new Insets(0, 0, 0, 6))
        searchBox.getChildren().addAll(iv1, searchField)
        searchBox.setAlignment(Pos.CENTER_RIGHT)
        searchBox.setSpacing(5)
        searchAndAll.getChildren().addAll(searchBox, facetBox)
        searchAndAll.setPadding(new Insets(10, 10, 0, 25))
        searchAndAll.setSpacing(5)

        startCity()
        pane.getChildren().addAll(progressBar, searchAndAll)
        return pane
    }

    /*METHODS*/
    public static TableView getTable() {
        return table;
    }

    public void startCity() {
        cityTextAutoForSearch.setVisible(false)
        updateFacets(cityPane, 1, 0, treeCities)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(TABLE).setValue(2)
        updateFacets(tablePane, 2, 0, treeCities)
//        UpdateActions.facetAddRemove(TABLE, cityTextAutoForSearch, ADD)
        UpdateActions.refreshTable()
    }

    public static PowerPlantList getFakeList() {
        return fakedPlantList;
    }

    private void setupBinding() {

        bind 'text' of zip to ZIP of clientDolphin[FILTER]
        bind 'text' of city to CITY of clientDolphin[FILTER]
        bind 'text' of plantTypes to PLANT_TYPE of clientDolphin[FILTER]
        bind 'text' of nominalText to NOMINAL_POWER of clientDolphin[FILTER]
        bind 'text' of searchText to ALL of clientDolphin[FILTER]

        bind 'text' of zip_auto to ZIP_AUTOFILL of clientDolphin[FILTER_AUTOFILL]
        bind 'text' of city_auto to CITY_AUTOFILL of clientDolphin[FILTER_AUTOFILL]
        bind 'text' of plantTypes_auto to PLANT_TYPE_AUTOFILL of clientDolphin[FILTER_AUTOFILL]

        bind ZIP of clientDolphin[FILTER] to 'text' of zip
        bind CITY of clientDolphin[FILTER] to 'text' of city
        bind PLANT_TYPE of clientDolphin[FILTER] to 'text' of plantTypes
        bind NOMINAL_POWER of clientDolphin[FILTER] to 'text' of nominalText

        bind ZIP of clientDolphin[SELECTED_POWERPLANT] to 'text' of zipLabelDetail
        bind CITY of clientDolphin[SELECTED_POWERPLANT] to 'text' of cityLabelDetail
        bind ID of clientDolphin[SELECTED_POWERPLANT] to 'text' of idLabelDetail
        bind NOMINAL_POWER of clientDolphin[SELECTED_POWERPLANT] to 'text' of nominalLabelDetail
        bind PLANT_TYPE of clientDolphin[SELECTED_POWERPLANT] to 'text' of typeLabelDetail

        progressBar.progressProperty().bind(progress)
        closeType.disableProperty().bind(disableControls)
        closeCity.disableProperty().bind(disableControls)
        closeZip.disableProperty().bind(disableControls)
        facetBox.disableProperty().bind(disableControls)
        zipTextAutoForSearch.disableProperty().bind(disableControls)
        cityTextAutoForSearch.disableProperty().bind(disableControls)
        typeTextAutoForSearch.disableProperty().bind(disableControls)
        searchBox.disableProperty().bind(disableControls)

        totalNominalLabel.textProperty().bind(totalNominal)
//        table.disableProperty().bind(disableControls)


        bindAttribute(clientDolphin[STATE][SORT], {
            if (clientDolphin[STATE][SORT].getValue().toString()==IGNORE)return;
            disableControls.setValue(true)
            UpdateActions.clearPmsAndPowerPlants()
            clientDolphin[STATE][HOLD].setValue(true)
            UpdateActions.refreshTable()

        })

        bindAttribute(clientDolphin[FILTER][PLANT_TYPE], {
            disableControls.setValue(true)
            UpdateActions.clearPmsAndPowerPlants()
        })

        bindAttribute(clientDolphin[FILTER][CITY], {
            disableControls.setValue(true)
            UpdateActions.clearPmsAndPowerPlants()
        })

        bindAttribute(clientDolphin[FILTER][ZIP], {
            disableControls.setValue(true)
            UpdateActions.clearPmsAndPowerPlants()
        })
        bindAttribute(clientDolphin[ORDER][PLANT_TYPE],{
            plantTypes.setText("")

            updateFacets(typePane, it.newValue, it.oldValue, treeTypes)
        })

        bindAttribute(clientDolphin[ORDER][CITY],{
            city.setText("")
            updateFacets(cityPane, it.newValue, it.oldValue, treeCities)
        })

        bindAttribute(clientDolphin[ORDER][ZIP],{
            zip.setText("")
            updateFacets(zipPane, it.newValue, it.oldValue, treeZip)
        })

        bindAttribute(clientDolphin[ORDER][TABLE],{
            table.getSelectionModel().clearSelection()
            updateFacets(tablePane, it.newValue, it.oldValue, treeZip)
        })

        bindAttribute(clientDolphin[STATE][TRIGGER], {

            disableControls.setValue(true)
            UpdateActions.clearPmsAndPowerPlants()
            UpdateActions.refreshTable()
        })


    }

    public static void bindAttribute(Attribute attribute, Closure closure) {
        final listener = closure as PropertyChangeListener
        attribute.addPropertyChangeListener('value', listener)
    }

    public static void addDragging() {
        placeholder.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean accept = false;
                if (db.hasString()) {
                    String data = db.getString();
                    try {
                        if (event.getGestureSource() instanceof Pane) {
                            accept = true;
                        }
                    } catch (NumberFormatException exc) {
                        accept = false;
                    }
                }
                if (accept) {
                    event.acceptTransferModes(TransferMode.MOVE);

                }
                placeholder.setFill(Color.LIGHTGRAY)
            }
        });
        facetBox.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean accept = false;
                if (db.hasString()) {
                    String data = db.getString();
                    try {
                        if (event.getGestureSource() instanceof Pane) {
                            accept = true;
                        }
                    } catch (NumberFormatException exc) {
                        accept = false;
                    }
                }
                if (accept) {
                    event.acceptTransferModes(TransferMode.MOVE);

                }
            }
        });
        placeholder.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            void handle(DragEvent t) {
                  placeholder.setFill(Color.ALICEBLUE)
            }
        })
        placeholder.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Pane draggedElement = (Pane) event.getGestureSource()
                VBox vBoxOrigin = draggedElement.getParent()
                vBoxOrigin.getChildren().remove(draggedElement)
                    int i = 1
                    List values = new ArrayList()
                    clientDolphin.findPresentationModelById(ORDER).getAttributes().each {
                        if(it.value > 0 && !values.contains(it.value)){
                            values.add(it.value)
                            i++
                        }
                    }


                facetBox.getChildren().get(i-1).getChildren().add(draggedElement)
                int newOriginHeight = (vBoxOrigin.getHeight())/(vBoxOrigin.getChildren().size())
                vBoxOrigin.getChildren().each { originChildPane ->
                    originChildPane.setPrefHeight(newOriginHeight)
                    originChildPane.getChildren().get(0).setPrefHeight(newOriginHeight)
                }


                draggedElement.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        Dragboard db = draggedElement.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent cc = new ClipboardContent();
                        cc.putString(draggedElement.getParent().toString());
                        db.setContent(cc);

                        e.consume();
                    }
                });
            }
        });

        facetBox.getChildren().each {
            it.setOnDragOver(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    Dragboard db = event.getDragboard();
                    boolean accept = false;
                    if (db.hasString()) {
                        String data = db.getString();
                        try {

                            if (data != (it.toString())
                                    && event.getGestureSource() instanceof Pane) {
                                accept = true;
                            }
                        } catch (NumberFormatException exc) {
                            accept = false;
                        }
                    }
                    if (accept) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
            });

            it.setOnDragDropped(new EventHandler<DragEvent>() {
                @Override
                void handle(DragEvent dragEvent) {
                    Pane draggedElement = (Pane) dragEvent.getGestureSource()
                    VBox draggedBox = draggedElement.getParent()

                    for (int i = 0; i < bounds.size(); i++){
                        if (bounds.get(i).containsWithinBounds(dragEvent.getSceneX().toInteger())){

                            def oldVal = panesToAttributes.get(draggedElement).getValue()

                            def newVal = i+1

                            clientDolphin[ORDER].getAttributes().each {
                                int value = it.getValue()

                                if (value >= newVal && value <= oldVal){
                                    if (it.equals(panesToAttributes.get(draggedElement))){
                                        it.setValue(i+1)
                                    }else{
                                        it.setValue(value+1)
                                    }
                                }
                                else if (value >= oldVal && value < newVal){
                                    if (it.equals(panesToAttributes.get(draggedElement))){
                                        it.setValue(i)
                                    }else{
                                        it.setValue(value-1)
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }

        dragBorder.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = dragBorder.getParent().getParent().startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cc = new ClipboardContent();
                cc.putString(String.valueOf(dragBorder.getParent().toString()));
                db.setContent(cc);
                bounds.clear()

                widths.clear()
                facetBox.getChildren().each {


                    def width = it.getBoundsInLocal().getWidth().round(-1)
                    if(width == 0.0){
                        return;
                    }else{
                        widths.add(width)
                    }
                }
                double newStarting = 25.plus(widths.get(0).div(2))
                bounds.add(new IntRange(-75, newStarting.toInteger()))
                for (int c = 1; c < widths.size(); c++) {
                    Range<Integer> range = new IntRange(newStarting.toInteger(), newStarting.plus((widths.get(c-1) + widths.get(c)).div(2)).toInteger())
                    bounds.add(range)
                    newStarting =  newStarting.plus((widths.get(c-1) + widths.get(c)).div(2))
                }
                bounds.add(new IntRange(newStarting.toInteger(), newStarting.plus(widths.get(widths.size()-1).div(2)).toInteger()))
                tablePane.setDisable(true)
                println bounds
                event.consume();
            }
        });

        tablePane.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            void handle(DragEvent dragEvent) {
                dragEvent.getGestureSource().setDisable(false)

            }
        })

        facetBox.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            void handle(DragEvent dragEvent) {
                List<Rectangle> rectangles = new ArrayList<Rectangle>()
                pane.getChildren().each {rect ->
                    if (rect instanceof Rectangle){
                        rectangles.add(rect)
                    }
                }
                rectangles.each {
                    if (it.width > 50)return;
                    pane.getChildren().remove(it)
                }

            }
        })

        facetBox.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            void handle(DragEvent dragEvent) {
                List<Rectangle> rectangles = new ArrayList<Rectangle>()
                pane.getChildren().each {rect ->
                    if (rect instanceof Rectangle){
                        rectangles.add(rect)
                    }
                }

                rectangles.each {
                    pane.getChildren().remove(it)
                }
            }
        })

        facetBox.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            void handle(DragEvent dragEvent) {
                List<Rectangle> rectangles = new ArrayList<Rectangle>()
                pane.getChildren().each {rect ->
                    if (rect instanceof Rectangle){
                        rectangles.add(rect)
                    }
                }
                if (rectangles.size() > 1){
                    rectangles.each {
                        pane.getChildren().remove(it)
                    }
                }

                Rectangle rectangle = new Rectangle()
                rectangle.setFill(Color.DODGERBLUE)
                rectangle.setWidth(dragEvent.getGestureSource().getWidth())
                rectangle.setHeight(22)
                rectangle.setOpacity(0.3)
                pane.getChildren().add(rectangle)
                rectangle.setTranslateY(40)
                rectangle.setTranslateX(dragEvent.getSceneX()-dragEvent.getGestureSource().getWidth().div(2))
                rectangle.setMouseTransparent(true)

                for (int i = 0; i < bounds.size(); i++){
                    if (bounds.get(i).containsWithinBounds(dragEvent.getSceneX().toInteger())){

                        Rectangle r2 = new Rectangle(6, 422)
                        r2.setFill(Color.DODGERBLUE)
                        r2.setOpacity(0.8)
                        try{
                            r2.relocate(bounds.get(i).getTo().minus(widths.get(i).div(2)).minus(3*i).minus(5),40)
                            pane.getChildren().add(r2)
                        }catch (Exception e){
                            r2.relocate(bounds.get(i).getTo().minus(3*i).minus(5),40)
                            pane.getChildren().add(r2)
                        }


                    }

                }
            }
        })
    }

    private static void loadPresentationModel(int rowIdx) {
        if (rowIdx == -1) return;
        UpdateActions.clearPmsAndPowerPlants()
        PowerPlant initialPlant = getTable().getItems().get(rowIdx)
        if (initialPlant.getLoadState() == LoadState.LOADING) return;
        initialPlant.setLoadState(LoadState.LOADING);

        clientDolphin.findPresentationModelById(STATE).findAttributeByPropertyName(START_INDEX).setValue(rowIdx)
        clientDolphin.send GET_ROW, { pms ->
            pms.each { pm ->
                initialPlant.setDbId(rowIdx)
                initialPlant.typeProperty().setValue(pm[PLANT_TYPE].getValue().toString())
                initialPlant.zipProperty().setValue(pm[ZIP].getValue().toString())
                initialPlant.cityProperty().setValue(pm[CITY].getValue().toString())
                initialPlant.nominalProperty().setValue(pm[NOMINAL_POWER].getValue().toString())
                initialPlant.positionProperty().setValue(pm[POSITION].getValue().toString())
                initialPlant.avgkwhProperty().setValue(pm[AVGKWH].getValue().toString())
                initialPlant.gpslatProperty().setValue(pm[GPS_LAT].getValue().toString())
                initialPlant.gpslonProperty().setValue(pm[GPS_LON].getValue().toString())
                initialPlant.setLoadState(LoadState.LOADED);
                disableControls.setValue(false)
            }
        }

    }


    private void initializeComponents() {

        observableListCities = FXCollections.observableArrayList()
        observableListCitiesCount = FXCollections.observableArrayList()
        observableListTypes = FXCollections.observableArrayList()
        observableListTypesCount = FXCollections.observableArrayList()
        observableListZips = FXCollections.observableArrayList()
        observableListZipsCount = FXCollections.observableArrayList()


        table = new TableView();
        cityLabelforDetail = new Label("City:       ")
        zipLabelforDetail = new Label("Zip Code:")
        typeLabelforDetail = new Label("Type:      ")
        nominalLabelforDetail = new Label("Power:    ")
        idLabelforDetail = new Label("Id:          ")
        cityLabelDetail = new TextField("City")
        idLabelDetail = new TextField("Id")
        zipLabelDetail = new TextField("Zip Code")
        typeLabelDetail = new TextField("Type")
        nominalLabelDetail = new TextField("Power")
        noData = new Label("No Data")

        dragBorder = new Rectangle()

        cityTextAutoForSearch = new TextField()
        zipTextAutoForSearch = new TextField()
        typeTextAutoForSearch = new TextField()

        progressBar = new ProgressBar()
        progress = new SimpleDoubleProperty()
        progressLine = new Timeline()

        nominalTextforSearch = new TextField()

        rootItemCities = new TreeItem<String>();
        rootItemZip = new TreeItem<String>();
        rootItem = new TreeItem<String>();

        selectionDetailsLabel = new Label("Selection Details \n")
        columns = new Label("Columns")
        filter = new Label("Filters")

        c = 0
        cityCB = new CheckBox("Show Cities");
        typeCB = new CheckBox("Show Types");
        nominalCB = new CheckBox("Show Nominal Powers");
        zipCB = new CheckBox("Show Zip-Codes");
        positionCB = new CheckBox("Show Positions");
        avgkwhCB = new CheckBox("Show Avg kWh");
        latitudeCB = new CheckBox("Show Latitude");
        longitudeCB = new CheckBox("Show Longitude");

        cityFilterCB = new CheckBox("Filter by City");
        cityFilterCB.setSelected(true)
        typeFilterCB = new CheckBox("Filter by Type");
        zipFilterCB = new CheckBox("Filter by Zip");

        columnStack = new Pane()
        filterStack = new Pane()
        tableStack = new StackPane()
        pane = new Pane()
        treesGrid = new GridPane()
        all = new HBox();

        tableBox = new VBox()
        facetBox = new HBox()
        searchBox = new HBox()
        col1 = new VBox()
        col2 = new VBox()
        col3 = new VBox()
        col4 = new VBox()
        col5 = new VBox()
        col6 = new VBox()
        col7 = new VBox()
        col8 = new VBox()
        col9 = new VBox()
        searchAndAll = new VBox()

        placeholder = new Rectangle(25,420)
        detailsContainer = new Rectangle()

        totalNominal = new SimpleStringProperty()
        totalNominalLabel = new Label()

        details = new VBox()
        filtersCBs = new VBox()
        columnsCBs = new VBox()
        filtersCBBorder = Layout.createCBsBorder()
        filtersEventBorder = Layout.createEventBorder()
        columnCBBorder = Layout.createCBsBorder()
        columnEventBorder = Layout.createEventBorder()

        treeCities = new TreeView();
        treeTypes = new TreeView();
        treeZip = new TreeView();

        plantTypes_auto = new Label();
        city_auto = new Label();
        zip_auto = new Label();

        plantTypes = new Label();
        city = new Label();
        zip = new Label();

        tablePane = new Pane()
        widths = new ArrayList<Double>()

        total = new Label("Total:")
        totalCount = new Label("1377475/1377475")

        separator = new Separator();
        facetTableSeparator = new Separator();
        nominalText = new TextField()
        searchField = new TextField()
        searchText = new Label()

        typePane = new Pane()
        closeType = new Button("X")
        zipPane = new Pane()
        closeZip = new Button("X")
        cityPane = new Pane()
        closeCity = new Button("X")

        panesToAttributes = new HashMap()

        disableControls = new SimpleBooleanProperty(false)


        fakedPlantList = new PowerPlantList(1377475, new OurConsumer<Integer>(){
            @Override
            void accept(Integer rowIndex) {
                loadPresentationModel(rowIndex)
            }
        });
        items = FakeCollections.newObservableList(fakedPlantList);

    }

    private void updateFacets(Pane pane, Integer newValue, Integer oldValue, TreeView tree) {

        tree.getSelectionModel().clearSelection()
        VBox paneContainer = pane.getParent()

        if (newValue > 0 && oldValue > 0){
            paneContainer.getChildren().remove(pane)
            VBox targetBox = facetBox.getChildren().get(newValue - 1)
            targetBox.getChildren().add(pane)
        }
        List values = new ArrayList()
        if(newValue==0){
            paneContainer.getChildren().remove(pane)
            pane.setPrefHeight(paneContainer.getHeight())
            pane.getChildren().get(0).setPrefHeight(paneContainer.getHeight())
        }

        else if(oldValue==0) {
            int i = 0

            clientDolphin.findPresentationModelById(ORDER).getAttributes().each {
                if(it.value > 0 && !values.contains(it.value)){
                    values.add(it.value)
                    i++
                }
            }
            VBox targetBox = new VBox()
            try {
                targetBox = facetBox.getChildren().get(i-1)
            }
            catch (Exception e){
                VBox newVbox = new VBox()
                facetBox.getChildren().add(i-1, newVbox)
            }
            targetBox = facetBox.getChildren().get(i-1)

            targetBox.getChildren().add(pane)
        }
//
//        if (oldValue >= 1){
//
//            paneContainer.getChildren().each {
//                int newHeight = (paneContainer.getHeight())/(paneContainer.getChildren().size())-10
//                it.setPrefHeight(newHeight)
//                it.getChildren().get(0).setPrefHeight(newHeight)
//            }
//            VBox originBox = facetBox.getChildren().get(oldValue-1)
//            if(originBox.getChildren().size() == 0){
//
//                clientDolphin.findPresentationModelById(ORDER).getAttributes().each {
//                    if(it.value >= oldValue){
//                        it.setValue(it.value-1)
//                    }
//                }
//                facetBox.getChildren().remove(originBox)
//                facetBox.getChildren().add(new VBox())
//            }
//        }
//        if (clientDolphin[STATE][CHANGE_FROM].getValue()==-1)return;
//        if(newValue == 1){
//            clientDolphin[STATE][CHANGE_FROM].setValue(-1)
//            facetBox.getChildren().each {vBox ->
//                vBox.getChildren().each{
//                    TreeView treeView = it.getChildren().get(0).getChildren().get(1)
//                    treeView.getSelectionModel().clearSelection()
//                }
//            }
//            clientDolphin[FILTER].getAttributes().each {
//                it.setValue("")
//            }
//            return;
//        }
//        if (oldValue == 1 && facetBox.getChildren().get(oldValue-1).getChildren().size()==0){
//            clientDolphin[STATE][CHANGE_FROM].setValue(-1)
//            facetBox.getChildren().each {vBox ->
//                vBox.getChildren().each{
//                    TreeView treeView = it.getChildren().get(0).getChildren().get(1)
//                    treeView.getSelectionModel().clearSelection()
//                }
//            }
//            clientDolphin[FILTER].getAttributes().each {
//                it.setValue("")
//            }
//            return;
//        }
//        if (oldValue > 0 && newValue > 0){
//            if(oldValue==1){
//                clientDolphin[STATE][CHANGE_FROM].setValue(oldValue-1)
//                (oldValue..(newValue-1)).each {
//                    VBox betweenBox = facetBox.getChildren().get(it)
//                    betweenBox.getChildren().each {
//                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
//                        treeView.getSelectionModel().clearSelection()
//                    }
//                }
//            }
//            else if (oldValue > newValue){
//                clientDolphin[STATE][CHANGE_FROM].setValue(newValue-1)
//
//                (newValue-1..(oldValue-1)).each {
//                    VBox betweenBox = facetBox.getChildren().get(it)
//                    betweenBox.getChildren().each {
//                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
//                        treeView.getSelectionModel().clearSelection()
//                    }
//                }
//
//
//            }else{
//                clientDolphin[STATE][CHANGE_FROM].setValue(oldValue-1)
//                (oldValue..(newValue-1)).each {
//                    VBox betweenBox = facetBox.getChildren().get(it)
//                    betweenBox.getChildren().each {
//                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
//                        treeView.getSelectionModel().clearSelection()
//                    }
//                }
//            }
//
//        }else if (newValue == 0){
//            clientDolphin[STATE][CHANGE_FROM].setValue(oldValue-1)
//        }else if (oldValue == 0){
//            clientDolphin[STATE][CHANGE_FROM].setValue(newValue-1)
//        }

        addDragging()

    }


}


