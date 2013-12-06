package com.canoo.solar

import com.sun.javafx.scene.control.skin.TableHeaderRow
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.EventHandler
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
import javafx.scene.layout.GridPane
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.LinearGradientBuilder
import javafx.scene.paint.Stop
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import np.com.ngopal.control.AutoFillTextBox

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
    javafx.collections.ObservableList<Integer> observableListCities
    javafx.collections.ObservableList<Integer> observableListCitiesCount
    javafx.collections.ObservableList<Integer> observableListTypes
    javafx.collections.ObservableList<Integer> observableListTypesCount
    javafx.collections.ObservableList<Integer> observableListZips
    javafx.collections.ObservableList<Integer> observableListZipsCount

    private PresentationModel textAttributeModel;
    public static TableView<PowerPlant> table
    Label cityLabelforDetail
    Label zipLabelforDetail
    Label typeLabelforDetail
    Label nominalLabelforDetail
    Label idLabelforDetail
    Label noData

    TextField cityLabelDetail
    TextField idLabelDetail
    TextField zipLabelDetail
    TextField typeLabelDetail
    TextField nominalLabelDetail

    static AutoFillTextBox zipTextAutoForSearch
    static AutoFillTextBox typeTextAutoForSearch
    static AutoFillTextBox cityTextAutoForSearch
    static AutoFillTextBox nominalTextAutoForSearch

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

    TreeView treeCities
    TreeView treeTypes
    TreeView treeZip

    Label plantTypes
    Label city
    Label zip

    Label plantTypes_auto
    Label city_auto
    Label zip_auto

    public static Label total
    public static Label totalCount

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
        disableQuery()

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

            clientDolphin.findPresentationModelById(ORDER_COLUMN).getAttributes().each {
                addHeaderListener(Integer.parseInt(it.getValue().toString()), it.getPropertyName())
            }
        }

        Pane root = setupStage();
        setupBinding();

        Scene scene = new Scene(root, 780, 470)
        scene.stylesheets << 'demo.css'
        stage.minWidthProperty().bind(searchAndAll.widthProperty().add(20))
        stage.maxWidthProperty().bind(searchAndAll.widthProperty().add(20))
        stage.setScene(scene);
//        stage.initStyle(StageStyle.UTILITY);
//        stage.setTitle(getClass().getName());
        stage.setTitle("Power Plants Explorer")

        stage.show();
    }

    private static void initializePresentationModels () {
        clientDolphin.presentationModel(FILTER, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER, ALL]);
        clientDolphin.presentationModel(FILTER_AUTOFILL, [CITY_AUTOFILL, PLANT_TYPE_AUTOFILL, ZIP_AUTOFILL]);
        clientDolphin.presentationModel(SELECTED_POWERPLANT, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER]);
        clientDolphin.presentationModel(ORDER, [CITY, PLANT_TYPE, ZIP])
        clientDolphin.presentationModel(ORDER_COLUMN, [CITY_COLUMN, TYPE_COLUMN, ZIP_COLUMN, NOMINAL_COLUMN, POSITION_COLUMN, AVGKWH_COLUMN, LAT_COLUMN, LON_COLUMN])
        clientDolphin.presentationModel(STATE, [TRIGGER, START_INDEX, SORT, REVERSER_ORDER])[TRIGGER].value=0

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
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(SORT).setValue(POSITION)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(REVERSER_ORDER).setValue(false)
    }

    public static TableView getTable() {
        return table;
    }

    /*METHODS*/

    public void startCity() {
        cityTextAutoForSearch.setVisible(false)
        updateFacets(cityPane, 1, 0, treeCities)
    }

    public static PowerPlantList getFakeList() {
        return fakedPlantList;
    }

    private void updateTree(LinkedList data, TreeView tree, List idsList, List countList, Integer dataIndex, String property) {

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

    private void updateFacets(Pane pane, Integer newValue, Integer oldValue, TreeView tree) {
        tree.getSelectionModel().clearSelection()
        VBox paneContainer = pane.getParent()
        List values = new ArrayList()
        if(newValue==0){
            pane.getParent().getChildren().remove(pane)
            VBox originBox = facetBox.getChildren().get(oldValue-1)
            if (originBox.getChildren().size() > 0) {
                originBox.getChildren().each {
                    TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                    Integer selectedIdx = treeView.getSelectionModel().getSelectedIndex()
                    if (selectedIdx == -1) {
                        treeView.getSelectionModel().select(treeView.getRoot());
                        treeView.getSelectionModel().clearSelection()
                    }
                    treeView.getSelectionModel().clearSelection()
                    treeView.getSelectionModel().select(selectedIdx)
                }
            }
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

        if (oldValue >= 1 && newValue >= 1){
            if (oldValue < newValue){
                VBox originBox = facetBox.getChildren().get(oldValue-1)
                originBox.getChildren().each {

                    TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                    Integer selectedIdx = treeView.getSelectionModel().getSelectedIndex()
                    if (selectedIdx == -1) {
                        treeView.getSelectionModel().select(treeView.getRoot());
                        treeView.getSelectionModel().clearSelection()
                    }
                    treeView.getSelectionModel().clearSelection()
                    treeView.getSelectionModel().select(selectedIdx)
                }
            } else if(oldValue > newValue){
                try {
                    (newValue..(oldValue-2)).each {
                        VBox betweenBox = facetBox.getChildren().get(it)
                        betweenBox.getChildren().each {
                            TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                            treeView.getSelectionModel().clearSelection()
                        }
                    }
                }catch (Exception e){
                    println "Exception $e"
                }

                tree.getSelectionModel().clearSelection()
                VBox targetBox = facetBox.getChildren().get(newValue-1)
                targetBox.getChildren().each {
                    TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                    Integer selectedIdx = treeView.getSelectionModel().getSelectedIndex()
                    if (selectedIdx == -1) {
                        treeView.getSelectionModel().select(treeView.getRoot());
                        treeView.getSelectionModel().clearSelection()
                    }
                    treeView.getSelectionModel().clearSelection()
                    treeView.getSelectionModel().select(selectedIdx)
                }
            }
        }

        if (oldValue >= 1){

            paneContainer.getChildren().each {
                int newHeight = (paneContainer.getHeight())/(paneContainer.getChildren().size())-10
                it.setPrefHeight(newHeight)
                it.getChildren().get(0).setPrefHeight(newHeight)
            }
            VBox originBox = facetBox.getChildren().get(oldValue-1)
            if(originBox.getChildren().size() == 0){
                if (oldValue == 1) {
                    if (newValue == 0){
                        tree.getSelectionModel().select(tree.getRoot());
                        tree.getSelectionModel().clearSelection()
                    }
                    else{
                        VBox targetBox = facetBox.getChildren().get(newValue-1)
                        targetBox.getChildren().each {
                            TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                            Integer selectedIdx = treeView.getSelectionModel().getSelectedIndex()
                            if (selectedIdx == -1) {
                                treeView.getSelectionModel().select(treeView.getRoot());
                                treeView.getSelectionModel().clearSelection()
                            }
                            treeView.getSelectionModel().clearSelection(selectedIdx)
                            treeView.getSelectionModel().select(selectedIdx)
                        }
                    }
                }
                else{
                    VBox previousBox = facetBox.getChildren().get(oldValue-2)
                    previousBox.getChildren().each {
                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                        Integer selectedIdx = treeView.getSelectionModel().getSelectedIndex()
                        if (selectedIdx == -1) {
                            treeView.getSelectionModel().select(treeView.getRoot());
                            treeView.getSelectionModel().clearSelection()
                        }
                        treeView.getSelectionModel().clearSelection()
                        treeView.getSelectionModel().select(selectedIdx)
                    }
                }

                clientDolphin.findPresentationModelById(ORDER).getAttributes().each {
                    if(it.value >= oldValue){
                        it.setValue(it.value-1)
                    }
                }
                facetBox.getChildren().remove(originBox)
            }
        }

        addDragging()
    }
    private Pane setupStage() {

        facetBox.getChildren().addAll(col1, col2, col3, col4, col5, col6, col7, col8, col9)
        facetBox.setSpacing(5)
        facetBox.setMinHeight(445)
        col1.setMinHeight(445)
        col1.setSpacing(2)
        col2.setSpacing(2)
        col2.setMinHeight(445)
        col4.setSpacing(2)
        col4.setMaxWidth(200)
        col4.setMinHeight(445)
        col5.setSpacing(2)
        col5.setMaxWidth(200)
        col5.setMinHeight(445)
        col7.setSpacing(2)
        col7.setMaxWidth(200)
        col7.setMinHeight(445)
        col8.setSpacing(2)
        col8.setMaxWidth(200)
        col8.setMinHeight(445)
        col9.setSpacing(2)
        col9.setMaxWidth(200)
        col9.setMinHeight(445)
        col6.setSpacing(2)
        col6.setMaxWidth(200)
        col6.setMinHeight(445)
        col3.setSpacing(2)
        col3.setMaxWidth(200)
        col3.setMinHeight(445)
        col1.setMaxWidth(200)
        col2.setMaxWidth(200)

        closeZip.setScaleX(0.8)
        closeZip.setScaleY(0.8)
        closeCity.setScaleX(0.8)
        closeCity.setScaleY(0.8)
        closeType.setScaleX(0.8)
        closeType.setScaleY(0.8)

        Image image = new Image("search.png");
        ImageView iv1 = new ImageView();
        iv1.setImage(image);
        iv1.setFitHeight(15)
        iv1.setFitWidth(15)



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

        columnEventBorder.relocate(0, 140)
        filtersEventBorder.relocate(0, 270)

        table.setMaxHeight(381)
        table.setMinHeight(381)
        table.setMaxWidth(690)
//        table.setMinWidth(690)
        table.setItems(items)
        table.setPlaceholder(noData)

        table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->
            if (selectedPm==null) return;
            String id = selectedPm.getDbId().toString()
            clientDolphin.apply clientDolphin.findPresentationModelById(id) to clientDolphin[SELECTED_POWERPLANT]
        } as ChangeListener )

        TableColumn<PowerPlant, String> positionColumn = firstColumn()
        TableColumn<PowerPlant, String> zipColumn = secondColumn()
        zipColumn.setGraphic(zipTextAutoForSearch)
        zipTextAutoForSearch.setMaxWidth(zipColumn.getWidth()-55)

        zipTextAutoForSearch.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (KeyCode.ENTER == event.getCode() || (KeyCode.BACK_SPACE == event.getCode() && zipTextAutoForSearch.getText().size() == 1)) {
                    if (KeyCode.BACK_SPACE == event.getCode()){
                        zip_auto.setText("")
                    }else{zip_auto.setText(zipTextforSearch.getText())}
                    treeZip.getSelectionModel().clearSelection()
                    disableQuery()
                    clearPmsAndPowerPlants()
                    refreshTable()
                    enableQuery()
                }
            }
        });

        zipTextAutoForSearch.getTextbox().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if(mouseEvent.getClickCount() == 2){
                    zipTextAutoForSearch.setVisible(false)
                    zipFilterCB.setSelected(true)
                }
            }
        });

        cityTextAutoForSearch.setData(observableListCities)
        typeTextAutoForSearch.setData(observableListTypes)
        zipTextAutoForSearch.setData(observableListZips)

        TableColumn<PowerPlant, String> cityColumn = thirdColumn()
        cityColumn.setGraphic(cityTextAutoForSearch)

        cityTextAutoForSearch.setMaxWidth(cityColumn.getWidth()-55)
        cityTextAutoForSearch.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (KeyCode.ENTER == event.getCode()|| (KeyCode.BACK_SPACE == event.getCode() && cityTextAutoForSearch.getText().size() == 1)) {
                    if (KeyCode.BACK_SPACE == event.getCode()){
                        city_auto.setText("")
                    }else{city_auto.setText(cityTextAutoForSearch.getText())}
                    treeCities.getSelectionModel().clearSelection()
                    disableQuery()
                    clearPmsAndPowerPlants()
                    refreshTable()
                    enableQuery()
                }
            }
        });

        cityTextAutoForSearch.getTextbox().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if(mouseEvent.getClickCount() == 2){
                    cityTextAutoForSearch.setVisible(false)
                    cityFilterCB.setSelected(true)
                }

            }
        });

        TableColumn<PowerPlant, String> typeColumn = fourthColumn()
        typeColumn.setGraphic(typeTextAutoForSearch)

        typeTextAutoForSearch.setMaxWidth(typeColumn.getWidth()-55)
        typeTextAutoForSearch.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (KeyCode.ENTER == event.getCode()|| (KeyCode.BACK_SPACE == event.getCode() && typeTextAutoForSearch.getText().size() == 1)) {
                    if (KeyCode.BACK_SPACE == event.getCode()){
                        plantTypes_auto.setText("")
                    }else{plantTypes_auto.setText(typeTextAutoForSearch.getText())}
                    treeTypes.getSelectionModel().clearSelection()
                    disableQuery()
                    clearPmsAndPowerPlants()
                    refreshTable()
                    enableQuery()
                }
            }
        });

        typeTextAutoForSearch.getTextbox().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if(mouseEvent.getClickCount() == 2){
                    typeTextAutoForSearch.setVisible(false)
                    typeFilterCB.setSelected(true)
                }

            }
        });

        TableColumn<PowerPlant, String> nominalColumn = fithColumn()
        TableColumn<PowerPlant, String> subtypeColumn = sixthColumn()
        TableColumn<PowerPlant, String> latitudeColumn = seventhColumn()
        TableColumn<PowerPlant, String> longitudeColumn = eighthColumn()
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
        Layout.createTreePane(rootItemCities, treeCities, closeCity, cityPane, cityFilterCB, orderPm[CITY], cityTextAutoForSearch)
        Layout.createTreePane(rootItemZip, treeZip, closeZip, zipPane, zipFilterCB, orderPm[ZIP], zipTextAutoForSearch)
        Layout.createTreePane(rootItem, treeTypes, closeType, typePane, typeFilterCB, orderPm[PLANT_TYPE], typeTextAutoForSearch)

        addDragging()

        columnsCBs.setPadding(new Insets(20, 0, 0, 10));
        columnsCBs.getChildren().addAll(cityCB, typeCB, zipCB, nominalCB, positionCB/*, avgkwhCB, latitudeCB, longitudeCB*/)

        filtersCBs.setPadding(new Insets(25, 0, 0, 10));
        filtersCBs.getChildren().addAll(cityFilterCB, typeFilterCB, zipFilterCB)
        separator.setMinWidth(facetBox.getTranslateX())

        details.setSpacing(5);
        details.setPadding(new Insets(20, 10, 10, 10));
        details.getChildren().addAll(selectionDetailsLabel, cityLabelTextDetail, idLabelTextDetail, typeLabelTextDetail, nominalLabelTextDetail, zipLabelTextDetail)
        facetBox.setPadding(new Insets(0, 0, 0, 0));
        setFilterCBListener(typeFilterCB, PLANT_TYPE, typeTextAutoForSearch)
        setFilterCBListener(cityFilterCB, CITY, cityTextAutoForSearch)
        setFilterCBListener(zipFilterCB, ZIP, zipTextAutoForSearch)

        searchField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (KeyCode.ENTER == event.getCode() || (KeyCode.BACK_SPACE == event.getCode() && searchField.getText().size() == 1)) {
                    if (KeyCode.BACK_SPACE == event.getCode()){
                        searchText.setText("")
                    }else{searchText.setText(searchField.getText())}
                    disableQuery()
                    clearPmsAndPowerPlants()
                    refreshTable()
                    enableQuery()
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
                            if (newItems.size()==0) {enableQuery()}
                            table.setItems(newItems)
                            totalCount.setText(newItems.size() + "/1370000")
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
                            if (newItems.size()==0) {enableQuery()}
                            table.setItems(newItems)
                            totalCount.setText(newItems.size() + "/1370000")
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
                            if (newItems.size()==0) {enableQuery()}
                            table.setItems(newItems)
                            totalCount.setText(newItems.size() + "/1370000")
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
//        tableStack.getChildren().addAll(table)
//        facetBox.getChildren().add(placeholder)
        tableStack.getChildren().addAll(detailsContainer, total, totalCount)
        totalCount.relocate(590, 10)
        total.relocate(10, 10)
        tableBox.getChildren().addAll(table, tableStack)
        all.getChildren().addAll(facetBox, placeholder,facetTableSeparator,tableBox)
        all.setSpacing(5)
        searchBox.getChildren().addAll(iv1, searchField)
        searchBox.setAlignment(Pos.CENTER_RIGHT)
        searchBox.setSpacing(5)
//        all.setPadding(new Insets(10, 10, 0, 25))
        searchAndAll.getChildren().addAll(searchBox, all)
        searchAndAll.setPadding(new Insets(10, 10, 0, 25))
        searchAndAll.setSpacing(5)

        searchField.setMaxWidth(150)
//        borderPane.setBottom(details)
        startCity()
        pane.getChildren().addAll(searchAndAll, columns, filter, columnEventBorder, filtersEventBorder, columnStack, filterStack)
        return pane
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


        bindAttribute(clientDolphin[STATE][SORT], {
            if (clientDolphin[STATE][SORT].getValue().toString()==IGNORE)return;
            disableQuery()
            clearPmsAndPowerPlants()
            refreshTable()
             
        })

        bindAttribute(clientDolphin[FILTER][PLANT_TYPE], {
            disableQuery()
            clearPmsAndPowerPlants()
        })

        bindAttribute(clientDolphin[FILTER][CITY], {
            disableQuery()
            clearPmsAndPowerPlants()
        })

        bindAttribute(clientDolphin[FILTER][ZIP], {
            disableQuery()
            clearPmsAndPowerPlants()
        })

        bindAttribute(clientDolphin[ORDER][PLANT_TYPE],{
            plantTypes.setText("")
            updateFacets(typePane, it.newValue, it.oldValue, treeTypes)
            println "TYPE: " + " fromm " + it.oldValue + " to " + it.newValue
            clientDolphin.data GET, { data ->
                updateTree(data,treeTypes, observableListTypes, observableListTypesCount, 1, "Plant Types")
            }
        })

        bindAttribute(clientDolphin[ORDER][CITY],{
            city.setText("")
            updateFacets(cityPane, it.newValue, it.oldValue, treeCities)
            println "CITY: " + " fromm " + it.oldValue + " to " + it.newValue
            clientDolphin.data GET, {data ->
                updateTree(data,treeCities,observableListCities,observableListCitiesCount,2,"Cities")
            }
        })

        bindAttribute(clientDolphin[ORDER][ZIP],{
            zip.setText("")
            updateFacets(zipPane, it.newValue, it.oldValue, treeZip)
            println "ZIP: " + " fromm " + it.oldValue + " to " + it.newValue
            clientDolphin.data GET, {data ->
                updateTree(data, treeZip, observableListZips, observableListZipsCount, 3, "Zip-Codes")

            }
        })

        bindAttribute(clientDolphin[STATE][TRIGGER], {
          disableQuery()
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

    public static void setFilterCBListener(CheckBox checkBox, String propertyName, AutoFillTextBox autoFillTextBox){
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                def orderPm = clientDolphin.findPresentationModelById(ORDER)
                def filterPm = clientDolphin.findPresentationModelById(FILTER)
                if (newValue) {
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

                    def order = orderPm.findAttributeByPropertyName(propertyName).getValue()
                    orderPm.findAttributeByPropertyName(propertyName).setValue(0)
                    autoFillTextBox.setVisible(true)

//                    orderPm.getAttributes().each {
//                        if(it.value > order){                                       //no need to do it again, as its done in the bind attribute
//                            filterPm.findAttributeByPropertyName(it.getPropertyName()).setValue("")
//                            it.setValue(it.getValue()-1)
//                            println it.getPropertyName() + " " + it.getValue()
//
//                        }
//                    }
                }

            }
        });
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
            totalCount.setText(newItems.size() + "/1370000")
            treesGrid.setDisable(false)
            table.getSelectionModel().clearSelection()

        }
    }

    public static void bindAttribute(Attribute attribute, Closure closure) {
        final listener = closure as PropertyChangeListener
        attribute.addPropertyChangeListener('value', listener)
    }

    public static addHeaderListener(Integer i, String sortCase) {
        TableHeaderRow rowHeader = TableViews.getTableViewInfo(table).tableHeaderRow
        def header = rowHeader.getChildren().get(1).getColumnHeaders().get(i)
        def sort = clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(SORT)
        def reverse = clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(REVERSER_ORDER)
        header.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                  
                switch (sortCase){
                    case POSITION_COLUMN:
                        if (sort.getValue().toString()==POSITION){
                            if (reverse.getValue()){
                                reverse.setValue(false)
                            } else reverse.setValue(true)
                        }
                        else{
                            reverse.setValue(false)
                        }
                        sort.setValue(IGNORE)      //changing an atttribute to the same value does not trigged value changed command.
                        sort.setValue(POSITION)
                        break;
                    case ZIP_COLUMN:
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

                    case CITY_COLUMN:
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

                    case TYPE_COLUMN:
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
                    case NOMINAL_COLUMN:
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
                    case AVGKWH_COLUMN:
                        if (sort.getValue().toString()==AVGKWH){
                            if (reverse.getValue()){
                                reverse.setValue(false)
                            } else reverse.setValue(true)
                        }
                        else{
                            reverse.setValue(false)
                        }
                        sort.setValue(IGNORE)
                        sort.setValue(AVGKWH)
                        break;
                    case LAT_COLUMN:
                        if (sort.getValue().toString()==GPS_LAT){
                            if (reverse.getValue()){
                                reverse.setValue(false)
                            } else reverse.setValue(true)
                        }
                        else{
                            reverse.setValue(false)
                        }
                        sort.setValue(IGNORE)
                        sort.setValue(GPS_LAT)
                        break;
                    case LON_COLUMN:
                        if (sort.getValue().toString()==GPS_LON){
                            if (reverse.getValue()){
                                reverse.setValue(false)
                            } else reverse.setValue(true)
                        }
                        else{
                            reverse.setValue(false)
                        }
                        sort.setValue(IGNORE)
                        sort.setValue(GPS_LON)
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
        result.setSortable(false)
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
        result.setSortable(false)
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
        result.setSortable(false)
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
        result.setSortable(false)
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
        result.setSortable(false)
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
        result.setPrefWidth(150)
        return result;
    }

    public static void enableQuery() {
        treesGrid.setDisable(false)
        filtersCBs.setDisable(false)
        closeType.setDisable(false)
        closeCity.setDisable(false)
        closeZip.setDisable(false)
        facetBox.setDisable(false)
        zipTextAutoForSearch.setDisable(false)
        cityTextAutoForSearch.setDisable(false)
        typeTextAutoForSearch.setDisable(false)
        searchBox.setDisable(false)
        pane.setCursor(Cursor.DEFAULT)
    }
    public static void disableQuery() {
        treesGrid.setDisable(true)
        filtersCBs.setDisable(true)
        closeType.setDisable(true)
        closeCity.setDisable(true)
        closeZip.setDisable(true)
        facetBox.setDisable(true)
        zipTextAutoForSearch.setDisable(true)
        cityTextAutoForSearch.setDisable(true)
        typeTextAutoForSearch.setDisable(true)
        searchBox.setDisable(true)
        pane.setCursor(Cursor.WAIT)


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
                enableQuery()
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

        zipTextAutoForSearch = new AutoFillTextBox()
        cityTextAutoForSearch = new AutoFillTextBox()
        typeTextAutoForSearch = new AutoFillTextBox()
        nominalTextAutoForSearch = new AutoFillTextBox()

        zipTextforSearch = zipTextAutoForSearch.textbox
        typeTextforSearch = typeTextAutoForSearch.textbox
        cityTextforSearch = cityTextAutoForSearch.textbox
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
        totalCount = new Label("1370000/1370000")

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



        fakedPlantList = new PowerPlantList(1370000, new OurConsumer<Integer>(){
            @Override
            void accept(Integer rowIndex) {
                loadPresentationModel(rowIndex)
            }
        });
        items = FakeCollections.newObservableList(fakedPlantList);

    }


}


