package com.canoo.solar

import com.sun.javafx.scene.control.skin.TableHeaderRow
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.animation.TranslateTransition
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Cursor
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
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.LinearGradientBuilder
import javafx.scene.paint.Stop
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.util.Duration
import np.com.ngopal.control.AutoFillTextBox

import java.util.function.UnaryOperator

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

    static SimpleBooleanProperty disableControls
    CheckBox cityFilterCB
    CheckBox typeFilterCB
    CheckBox zipFilterCB

    Pane columnStack
    Pane filterStack
    static Pane tableStack
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
        disableControls.setValue(true)
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
        }

        Pane root = setupStage();
        setupBinding();

        Scene scene = new Scene(root, 780, 475)
        scene.stylesheets << 'demo.css'
        stage.minWidthProperty().bind(searchAndAll.widthProperty().add(20))
        stage.maxWidthProperty().bind(searchAndAll.widthProperty().add(20))
        stage.setScene(scene);
        stage.setTitle("Power Plants Explorer")

        stage.show();
    }

    private static void initializePresentationModels () {
        clientDolphin.presentationModel(FILTER, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER, ALL]);
        clientDolphin.presentationModel(FILTER_AUTOFILL, [CITY_AUTOFILL, PLANT_TYPE_AUTOFILL, ZIP_AUTOFILL]);
        clientDolphin.presentationModel(SELECTED_POWERPLANT, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER]);
        clientDolphin.presentationModel(ORDER, [CITY, PLANT_TYPE, ZIP])
        clientDolphin.presentationModel(ORDER_COLUMN, [CITY_COLUMN, TYPE_COLUMN, ZIP_COLUMN, NOMINAL_COLUMN, POSITION_COLUMN, AVGKWH_COLUMN, LAT_COLUMN, LON_COLUMN])
        clientDolphin.presentationModel(STATE, [TRIGGER, START_INDEX, SORT, REVERSER_ORDER, CHANGE_FROM, HOLD])[TRIGGER].value=0

        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(ZIP).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(CITY).setValue(1)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).setValue(0)

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

        facetBox.getChildren().addAll(col1, col2, col3, col4/*, col5, col6, col7, col8, col9*/)
        facetBox.setSpacing(5)
        facetBox.setMinHeight(445)
        col1.setMinHeight(445)
        col1.setSpacing(2)
        col2.setSpacing(2)
        col2.setMinHeight(445)
        col4.setSpacing(2)
        col4.setMaxWidth(200)
        col4.setMinHeight(445)
        col3.setSpacing(2)
        col3.setMaxWidth(200)
        col3.setMinHeight(445)
        col1.setMaxWidth(200)
        col2.setMaxWidth(200)

        closeZip.setScaleX(0.8)
        closeZip.setScaleY(0.6)
        closeCity.setScaleX(0.8)
        closeCity.setScaleY(0.6)
        closeType.setScaleX(0.8)
        closeType.setScaleY(0.6)

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

        tableBox.setSpacing(5)

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

        table.setMaxHeight(381)
        table.setMinHeight(381)
        table.setMaxWidth(690)
        table.setItems(items)
        table.setPlaceholder(noData)

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
        TableColumn<PowerPlant, String> subtypeColumn = sixthColumn()
        TableColumn<PowerPlant, String> latitudeColumn = seventhColumn()
        TableColumn<PowerPlant, String> longitudeColumn = eighthColumn()

        makeAutofillTextField(zipTextAutoForSearch, observableListZips, zip_auto, treeZip, ZIP)
        makeAutofillTextField(typeTextAutoForSearch, observableListTypes, plantTypes_auto, treeTypes, PLANT_TYPE)
        makeAutofillTextField(cityTextAutoForSearch, observableListCities, city_auto, treeCities, CITY)

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
        Layout.createTreePane(rootItemCities, treeCities, closeCity, cityPane, cityTextAutoForSearch, orderPm[CITY])
        Layout.createTreePane(rootItemZip, treeZip, closeZip, zipPane, zipTextAutoForSearch, orderPm[ZIP])
        Layout.createTreePane(rootItem, treeTypes, closeType, typePane, typeTextAutoForSearch, orderPm[PLANT_TYPE])

        addDragging()

        columnsCBs.setPadding(new Insets(20, 0, 0, 10));
        columnsCBs.getChildren().addAll(cityCB, typeCB, zipCB, nominalCB, positionCB/*, avgkwhCB, latitudeCB, longitudeCB*/)

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
                    clearPmsAndPowerPlants()
                    clientDolphin[STATE][HOLD].setValue(true)
                    refreshTable()

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
                    clearPmsAndPowerPlants()
                    clientDolphin[STATE][HOLD].setValue(true)
                    refreshTable()
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
                                updateTree(data,treeCities,observableListCities,observableListCitiesCount,2,"Cities")
                            }

                            if (zipValue > typeValue){
                                zip.setText("")
                                updateTree(data, treeZip, observableListZips, observableListZipsCount, 3, "Zip-Codes")
                            }
                            def size = data.get(0).get("size")
                            PowerPlantList newFakeList = new PowerPlantList((Integer)size, new OurConsumer<Integer>(){
                                @Override
                                void accept(Integer rowIndex) {
                                    loadPresentationModel(rowIndex)
                                }
                            });
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
                                updateTree(data,treeTypes, observableListTypes, observableListTypesCount, 1, "Plant Types")
                            }
                            if (zipValue > cityValue){
                                zip.setText("")
                                updateTree(data, treeZip, observableListZips, observableListZipsCount, 3, "Zip-Codes")
                            }
                            def size = data.get(0).get("size")
                            PowerPlantList newFakeList = new PowerPlantList((Integer)size, new OurConsumer<Integer>(){
                                @Override
                                void accept(Integer rowIndex) {
                                    loadPresentationModel(rowIndex)
                                }
                            });

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
                                updateTree(data,treeCities,observableListCities,observableListCitiesCount,2,"Cities")

                            }
                            if (typeValue > zipValue){
                                plantTypes.setText("")
                                updateTree(data,treeTypes, observableListTypes, observableListTypesCount, 1, "Plant Types")

                            }
                            def size = data.get(0).get(SIZE)
                            PowerPlantList newFakeList = new PowerPlantList((Integer)size, new OurConsumer<Integer>(){
                                @Override
                                void accept(Integer rowIndex) {
                                    loadPresentationModel(rowIndex)
                                }
                            });
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
        tableStack.getChildren().addAll(detailsContainer, total, totalCount)
        totalCount.relocate(590, 10)
        total.relocate(10, 10)
        progressBar.relocate(0,470)
        tableBox.getChildren().addAll(table, tableStack)
        all.getChildren().addAll(facetBox, placeholder, tableBox)
        all.setSpacing(5)
        searchBox.getChildren().addAll(iv1, searchField)
        searchBox.setAlignment(Pos.CENTER_RIGHT)
        searchBox.setSpacing(5)
        searchAndAll.getChildren().addAll(searchBox, all)
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
        refreshTable()
    }

    public static PowerPlantList getFakeList() {
        return fakedPlantList;
    }

    private static void updateTree(LinkedList data, TreeView tree, List idsList, List countList, Integer dataIndex, String property) {

        tree.getSelectionModel().clearSelection()
        idsList.clear()
        idsList.addAll(data.get(dataIndex).get(IDS))
        countList.clear()
        countList.addAll(data.get(dataIndex).get(NUM_COUNT))
        def size = data.get(0).get(SIZE)
        Map<Integer,Integer> map = new LinkedHashMap<Integer,Integer>();
        List<TreeItem<String>> iteratedList = new ArrayList<TreeItem<String>>()

        for (int i=0; i<idsList.size(); i++) {
            map.put(idsList.get(i), countList.get(i));
        }

        tree.getRoot().getChildren().each {
            String name = it.getValue().toString().substring(0,it.getValue().toString().lastIndexOf(' ('))

            def newCount = map.get(name)
            if (newCount==null)newCount=0;
            final TreeItem<String> checkBoxTreeItem =
                new TreeItem<String>(name + " (" + newCount + ")");
            iteratedList.add(checkBoxTreeItem);
        }

        tree.getRoot().setValue("$property ($size)")
        Collections.sort(iteratedList, new TreeItemNumberComparator())
        tree.getRoot().getChildren().clear()
        tree.getRoot().getChildren().addAll(iteratedList)

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
        table.disableProperty().bind(disableControls)


        bindAttribute(clientDolphin[STATE][SORT], {
            if (clientDolphin[STATE][SORT].getValue().toString()==IGNORE)return;
            disableControls.setValue(true)
            clearPmsAndPowerPlants()
            clientDolphin[STATE][HOLD].setValue(true)
            refreshTable()

        })

        bindAttribute(clientDolphin[FILTER][PLANT_TYPE], {
            disableControls.setValue(true)
            clearPmsAndPowerPlants()
        })

        bindAttribute(clientDolphin[FILTER][CITY], {
            disableControls.setValue(true)
            clearPmsAndPowerPlants()
        })

        bindAttribute(clientDolphin[FILTER][ZIP], {
            disableControls.setValue(true)
            clearPmsAndPowerPlants()
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

        bindAttribute(clientDolphin[STATE][TRIGGER], {

            disableControls.setValue(true)
          clearPmsAndPowerPlants()
          refreshTable()
        })
    }

    public static void clearPmsAndPowerPlants(){
        List<PresentationModel> pmsToRemove = new ArrayList<PresentationModel>()
        clientDolphin.getModelStore().findAllPresentationModelsByType(POWERPLANT).each {
            pmsToRemove.add(it)
        }
        clientDolphin.deleteAllPresentationModelsOfType(POWERPLANT)
        pmsToRemove.each {
            getFakeList().removePlant(Integer.parseInt(it.getId()))
        }

    }

    public static void facetAddRemove(String propertyName, TextField autoFillTextBox, String addRemove){

                def orderPm = clientDolphin.findPresentationModelById(ORDER)
                if (addRemove.equals("add")) {
                    int i = 1
                    List values = new ArrayList()
                    clientDolphin.findPresentationModelById(ORDER).getAttributes().each {
                        if(it.value > 0 && !values.contains(it.value)){
                            values.add(it.value)
                            i++
                        }
                    }
                    orderPm.findAttributeByPropertyName(propertyName).setValue(i)
                    println orderPm.findAttributeByPropertyName(propertyName)
                    autoFillTextBox.setVisible(false)
                }
                else {

                    orderPm.findAttributeByPropertyName(propertyName).setValue(0)
                    autoFillTextBox.setVisible(true)
                }
    }

    public static void refreshTable(){
        clientDolphin.data GET, { data ->
            def size = data.get(0).get(SIZE)
            PowerPlantList newFakeList = new PowerPlantList((Integer)size, new OurConsumer<Integer>(){
                @Override
                void accept(Integer rowIndex) {
                    loadPresentationModel(rowIndex)
                }
            });
            javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
            table.setItems(newItems)
            totalCount.setText(newItems.size() + "/1377475")
            treesGrid.setDisable(false)
            table.getSelectionModel().clearSelection()
            if (clientDolphin[STATE][HOLD].getValue()==false){

                def value = clientDolphin[STATE][CHANGE_FROM].getValue()
                if (value==-1 || value==-2){
                    updateTree(data,treeTypes, observableListTypes, observableListTypesCount, 1, "Plant Types")
                    updateTree(data, treeZip, observableListZips, observableListZipsCount, 3, "Zip-Codes")
                    updateTree(data,treeCities,observableListCities,observableListCitiesCount,2,"Cities")
                }else if(value==0){
                    VBox originBox = facetBox.getChildren().get(value)
                    originBox.getChildren().each {

                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                        Integer selectedIdx = treeView.getSelectionModel().getSelectedIndex()
                        if (selectedIdx == -1) {
                            treeView.getSelectionModel().select(treeView.getRoot());
                            treeView.getSelectionModel().clearSelection()
                        }else{

                            treeView.getSelectionModel().clearSelection()
                            treeView.getSelectionModel().select(selectedIdx)
                        }
                    }
                }
                else {

                    VBox originBox = facetBox.getChildren().get(value-1)
                    originBox.getChildren().each {

                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                        Integer selectedIdx = treeView.getSelectionModel().getSelectedIndex()
                        if (selectedIdx == -1) {
                            treeView.getSelectionModel().select(treeView.getRoot());
                            treeView.getSelectionModel().clearSelection()
                        }else{

                            treeView.getSelectionModel().clearSelection()
                            treeView.getSelectionModel().select(selectedIdx)
                        }
                    }
                }
                clientDolphin[STATE][CHANGE_FROM].setValue(-2)
            }
            clientDolphin[STATE][HOLD].setValue(false)
        }
    }

    public static void bindAttribute(Attribute attribute, Closure closure) {
        final listener = closure as PropertyChangeListener
        attribute.addPropertyChangeListener('value', listener)
    }

    public static TableColumn<PowerPlant, String> secondColumn() {
        TableColumn<PowerPlant, String> result = new TableColumn<>("Zip");
        result.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PowerPlant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PowerPlant, String> param) {
                return param.getValue().zipProperty();
            }
        });
        result.setPrefWidth(150)
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
        result.setPrefWidth(150)
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
        result.setPrefWidth(120)
        return result;
    }
    public static TableColumn<PowerPlant, String> sixthColumn() {
        TableColumn<PowerPlant, String> result = new TableColumn<>("Average kW/h");
        result.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PowerPlant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PowerPlant, String> param) {
                return param.getValue().avgkwhProperty();
            }
        });
        result.setPrefWidth(120)
        return result;
    }
    public static TableColumn<PowerPlant, String> seventhColumn() {
        TableColumn<PowerPlant, String> result = new TableColumn<>("Latitude");
        result.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PowerPlant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PowerPlant, String> param) {
                return param.getValue().gpslatProperty();
            }
        });
        result.setPrefWidth(120)
        return result;
    }
    public static TableColumn<PowerPlant, String> eighthColumn() {
        TableColumn<PowerPlant, String> result = new TableColumn<>("Longitude");
        result.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PowerPlant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PowerPlant, String> param) {
                return param.getValue().gpslonProperty();
            }
        });
        result.setPrefWidth(120)
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
        result.setPrefWidth(150)
        return result;
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
                public void handle(DragEvent event) {
                    Pane draggedElement = (Pane) event.getGestureSource()
                    VBox vBoxOrigin = draggedElement.getParent()
                    vBoxOrigin.getChildren().remove(draggedElement)
                    it.getChildren().add(draggedElement)
                    int newHeight = (it.getHeight())/(it.getChildren().size())-10
                    int newOriginHeight = (vBoxOrigin.getHeight())/(vBoxOrigin.getChildren().size())
                    vBoxOrigin.getChildren().each { originChildPane ->
                        originChildPane.setPrefHeight(newOriginHeight)
                        originChildPane.getChildren().get(0).setPrefHeight(newOriginHeight)
                    }

                    it.getChildren().each { childPane ->

                        childPane.setPrefHeight(newHeight)
                        childPane.getChildren().get(0).setPrefHeight(newHeight)

                    }

                    draggedElement.setOnDragDetected(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            Dragboard db = draggedElement.startDragAndDrop(TransferMode.MOVE);
                            ClipboardContent cc = new ClipboardContent();
                            cc.putString(it.toString());
                            db.setContent(cc);
                            e.consume();
                        }
                    });
                }
            });

        }
    }

    private static void loadPresentationModel(int rowIdx) {
        if (rowIdx == -1) return;
        clearPmsAndPowerPlants()
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

    private static void makeAutofillTextField(TextField textField, List items, Label label, TreeView treeView, String propertyName) {

        StringBuilder sb;
        int lastLength;
        sb = new StringBuilder();

        textField.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            void handle(KeyEvent keyEvent) {
                if (lastLength != (textField.getText().length() - textField.getSelectedText().length()))
                    lastLength = textField.getText().length() - textField.getSelectedText().length();

                if (keyEvent.isControlDown() || keyEvent.getCode() == KeyCode.BACK_SPACE ||
                        keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.LEFT ||
                        keyEvent.getCode() == KeyCode.DELETE || keyEvent.getCode() == KeyCode.HOME ||
                        keyEvent.getCode() == KeyCode.END || keyEvent.getCode() == KeyCode.TAB ||
                        keyEvent.getCode() == KeyCode.SHIFT
                )
                    return;

                sb.delete(0, sb.length());
                sb.append(textField.getText());
                // remove selected string index until end so only unselected text will be recorded

                for (int i=0; i<items.size(); i++) {
                    if (items.get(i).toString().toLowerCase().startsWith(textField.getText().toLowerCase()))
                    {

                        try {
                            textField.setText(sb.toString() + items.get(i).toString().substring(sb.toString().length()));
                        } catch (Exception e) {
                            textField.setText(sb.toString());
                        }
                        textField.selectPositionCaret(sb.toString().length());
                        textField.selectRange(textField.getCaretPosition(), textField.length)
                        break;
                    }
                }

            }
        })

        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                  if (s2.equals("")){
                      label.setText("")
                      treeView.getSelectionModel().clearSelection()
                      disableControls.setValue(true)
                      clearPmsAndPowerPlants()
                      clientDolphin[STATE][HOLD].setValue(true)
                      refreshTable()

                      disableControls.setValue(false)
                  }
            }
        })

        textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (KeyCode.ENTER == event.getCode()) {
                    label.setText(textField.getText())
                    treeView.getSelectionModel().clearSelection()
                    disableControls.setValue(true)
                    clearPmsAndPowerPlants()
                    clientDolphin[STATE][HOLD].setValue(true)
                    refreshTable()
                    disableControls.setValue(false)
                }
            }
        });

        textField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if(mouseEvent.getClickCount() == 2){
                    textField.setVisible(false)
                    label.setText("")
                    textField.setText("")
                    facetAddRemove(propertyName, textField, "add")
                }
            }
        });


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
        tableStack = new Pane()
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
        List values = new ArrayList()
        if(newValue==0){
            pane.getParent().getChildren().remove(pane)
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

        if (oldValue >= 1){

            paneContainer.getChildren().each {
                int newHeight = (paneContainer.getHeight())/(paneContainer.getChildren().size())-10
                it.setPrefHeight(newHeight)
                it.getChildren().get(0).setPrefHeight(newHeight)
            }
            VBox originBox = facetBox.getChildren().get(oldValue-1)
            if(originBox.getChildren().size() == 0){

                clientDolphin.findPresentationModelById(ORDER).getAttributes().each {
                    if(it.value >= oldValue){
                        it.setValue(it.value-1)
                    }
                }
                facetBox.getChildren().remove(originBox)
                facetBox.getChildren().add(new VBox())
            }
        }
        if (clientDolphin[STATE][CHANGE_FROM].getValue()==-1)return;
        if(newValue == 1){
            clientDolphin[STATE][CHANGE_FROM].setValue(-1)
            facetBox.getChildren().each {vBox ->
                vBox.getChildren().each{
                    TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                    treeView.getSelectionModel().clearSelection()
                }
            }
            clientDolphin[FILTER].getAttributes().each {
                it.setValue("")
            }
            return;
        }
        if (oldValue == 1 && facetBox.getChildren().get(oldValue-1).getChildren().size()==0){
            clientDolphin[STATE][CHANGE_FROM].setValue(-1)
            facetBox.getChildren().each {vBox ->
                vBox.getChildren().each{
                    TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                    treeView.getSelectionModel().clearSelection()
                }
            }
            clientDolphin[FILTER].getAttributes().each {
                it.setValue("")
            }
            return;
        }
        if (oldValue > 0 && newValue > 0){
            if(oldValue==1){
                clientDolphin[STATE][CHANGE_FROM].setValue(oldValue-1)
                (oldValue..(newValue-1)).each {
                    VBox betweenBox = facetBox.getChildren().get(it)
                    betweenBox.getChildren().each {
                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                        treeView.getSelectionModel().clearSelection()
                    }
                }
            }
            else if (oldValue > newValue){
                clientDolphin[STATE][CHANGE_FROM].setValue(newValue-1)

                (newValue-1..(oldValue-1)).each {
                    VBox betweenBox = facetBox.getChildren().get(it)
                    betweenBox.getChildren().each {
                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                        treeView.getSelectionModel().clearSelection()
                    }
                }


            }else{
                clientDolphin[STATE][CHANGE_FROM].setValue(oldValue-1)
                (oldValue..(newValue-1)).each {
                    VBox betweenBox = facetBox.getChildren().get(it)
                    betweenBox.getChildren().each {
                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                        treeView.getSelectionModel().clearSelection()
                    }
                }
            }

        }else if (newValue == 0){
            clientDolphin[STATE][CHANGE_FROM].setValue(oldValue-1)
        }else if (oldValue == 0){
            clientDolphin[STATE][CHANGE_FROM].setValue(newValue-1)
        }

        addDragging()
    }


}


