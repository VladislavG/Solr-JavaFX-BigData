package com.canoo.solar

import com.sun.javafx.scene.control.skin.TableHeaderRow
import com.sun.xml.internal.bind.v2.schemagen.MultiMap
import groovyx.gpars.pa.PAWrapper
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.RowConstraints
import javafx.scene.layout.VBox
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
    Label noData = new Label("No Data")

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
    CheckBox positionCB = new CheckBox("Show Positions");

    CheckBox cityFilterCB = new CheckBox("Filter by City");
    CheckBox typeFilterCB = new CheckBox("Filter by Type");
    CheckBox zipFilterCB = new CheckBox("Filter by Zip");

    Pane columnStack = new Pane()
    Pane filterStack = new Pane()
    static Pane tableStack = new Pane()
    Pane pane = new Pane()
    static GridPane treesGrid = new GridPane()

    static HBox facetBox = new HBox()
    static VBox col1 = new VBox()
    static VBox col2 = new VBox()
    static VBox col3 = new VBox()

    VBox details = new VBox()
    static VBox filtersCBs = new VBox()
    static VBox columnsCBs = new VBox()
    public static Rectangle filtersCBBorder = Layout.createCBsBorder()
    public static Rectangle filtersEventBorder = Layout.createEventBorder()
    public static Rectangle columnCBBorder = Layout.createCBsBorder()
    public static Rectangle columnEventBorder = Layout.createEventBorder()

    final TreeView treeCities = new TreeView();
    final TreeView treeTypes = new TreeView();
    final TreeView treeZip = new TreeView();

    final Label plantTypes = new Label();
    final Label city = new Label();
    final Label zip = new Label();

    final Separator separator = new Separator();
    TextField nominalText = new TextField()

    Pane typePane = new Pane()
    static Button closeType = new Button("X")
    Pane zipPane = new Pane()
    static Button closeZip = new Button("X")
    Pane cityPane = new Pane()
    static Button closeCity = new Button("X")

    public static PowerPlantList fakedPlantList = new PowerPlantList(1370000, new OurConsumer<Integer>(){
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

        Scene scene = new Scene(root, 1280, 670)
        scene.stylesheets << 'demo.css'

        stage.setScene(scene);
        stage.setTitle(getClass().getName());
        stage.show();
    }

    private static void initializePresentationModels () {
        clientDolphin.presentationModel(FILTER, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER]);
        clientDolphin.presentationModel(SELECTED_POWERPLANT, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER]);
        clientDolphin.presentationModel(ORDER, [CITY, PLANT_TYPE, ZIP])
        clientDolphin.presentationModel(ORDER_COLUMN, [CITY_COLUMN, TYPE_COLUMN, ZIP_COLUMN, NOMINAL_COLUMN, POSITION_COLUMN])
        clientDolphin.presentationModel(STATE, [TRIGGER, START_INDEX, SORT, REVERSER_ORDER])[TRIGGER].value=0

        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(ZIP).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(CITY).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER).findAttributeByPropertyName(PLANT_TYPE).setValue(0)

        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(CITY_COLUMN).setValue(2)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(TYPE_COLUMN).setValue(3)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(ZIP_COLUMN).setValue(1)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(NOMINAL_COLUMN).setValue(4)
        clientDolphin.getClientModelStore().findPresentationModelById(ORDER_COLUMN).findAttributeByPropertyName(POSITION_COLUMN).setValue(0)

        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(START_INDEX).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(SORT).setValue(POSITION)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(REVERSER_ORDER).setValue(false)
    }

    private Pane setupStage() {

        facetBox.getChildren().addAll(col1, col2, col3)

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

        columnEventBorder.relocate(0, 290)
        filtersEventBorder.relocate(0, 420)

        table.setMinHeight(660)
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
        table.getColumns().addAll(positionColumn, zipColumn, cityColumn, typeColumn, nominalColumn);

//        show/hide table Columns on checkbox event
        PresentationModel colOrder = clientDolphin.findPresentationModelById(ORDER_COLUMN)
        Listeners.setChoiceBoxListener(cityCB, table, cityColumn, CITY_COLUMN, colOrder)
        Listeners.setChoiceBoxListener(typeCB, table, typeColumn, TYPE_COLUMN, colOrder)
        Listeners.setChoiceBoxListener(nominalCB, table, nominalColumn, NOMINAL_COLUMN, colOrder)
        Listeners.setChoiceBoxListener(zipCB, table, zipColumn, ZIP_COLUMN, colOrder)
        Listeners.setChoiceBoxListener(positionCB, table, positionColumn, POSITION_COLUMN, colOrder)

//        create selection details layout boxes
        HBox nominalLabelTextDetail = Layout.createPair(nominalLabelDetail, nominalLabelforDetail)
        HBox typeLabelTextDetail = Layout.createPair(typeLabelDetail, typeLabelforDetail)
        HBox cityLabelTextDetail = Layout.createPair(cityLabelDetail, cityLabelforDetail)
        HBox zipLabelTextDetail = Layout.createPair(zipLabelDetail, zipLabelforDetail)
        HBox idLabelTextDetail = Layout.createPair(idLabelDetail, idLabelforDetail)

//        assemble the treeViews and buttons together into a draggable pane
        def orderPm = clientDolphin.findPresentationModelById(ORDER)
        Layout.createTreePane(rootItemCities, treeCities, closeCity, cityPane, cityFilterCB, orderPm[CITY], facetBox)
        Layout.createTreePane(rootItemZip, treeZip, closeZip, zipPane, zipFilterCB, orderPm[ZIP], facetBox)
        Layout.createTreePane(rootItem, treeTypes, closeType, typePane, typeFilterCB, orderPm[PLANT_TYPE], facetBox)

        addDragging()

        columnsCBs.setPadding(new Insets(20, 0, 0, 10));
        columnsCBs.getChildren().addAll(cityCB, typeCB, zipCB, nominalCB, positionCB)

        filtersCBs.setPadding(new Insets(25, 0, 0, 10));
        filtersCBs.getChildren().addAll(cityFilterCB, typeFilterCB, zipFilterCB)
        separator.setMinWidth(facetBox.getTranslateX())

        details.setSpacing(5);
        details.setPadding(new Insets(20, 0, 0, 10));
        details.getChildren().addAll(selectionDetailsLabel, cityLabelTextDetail, idLabelTextDetail, typeLabelTextDetail, nominalLabelTextDetail, zipLabelTextDetail, separator, facetBox)

        setFilterCBListener(typeFilterCB, PLANT_TYPE)
        setFilterCBListener(cityFilterCB, CITY)
        setFilterCBListener(zipFilterCB, ZIP)

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
                                updateTree(data,treeCities,observableListCities,observableListCitiesCount,2,"Cities")
                                city.setText("")
                            }

                            if (zipValue > typeValue){
                                updateTree(data, treeZip, observableListZips, observableListZipsCount, 3, "Zip-Codes")
                                zip.setText("")
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
                            table.getSelectionModel().clearSelection()
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
                            table.getSelectionModel().clearSelection()
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
                            table.getSelectionModel().clearSelection()

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
        columnsCBs.relocate(0, -10)
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

    public static TableView getTable() {
        return table;
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

        VBox paneContainer = pane.getParent()
        List values = new ArrayList()
        if(newValue==0){
            tree.getSelectionModel().clearSelection()
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
                    VBox originBox = facetBox.getChildren().get(oldValue-1)
                    originBox.getChildren().each {
                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                        treeView.getSelectionModel().clearSelection()
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
                int newHeight = (paneContainer.getHeight())/(paneContainer.getChildren().size())
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
            clientDolphin.data GET, { data ->
                updateTree(data,treeTypes, observableListTypes, observableListTypesCount, 1, "Plant Types")
            }
        })

        bindAttribute(clientDolphin[ORDER][CITY],{
            city.setText("")
            updateFacets(cityPane, it.newValue, it.oldValue, treeCities)
            clientDolphin.data GET, {data ->
                updateTree(data,treeCities,observableListCities,observableListCitiesCount,2,"Cities")
            }
        })

        bindAttribute(clientDolphin[ORDER][ZIP],{
            zip.setText("")
            updateFacets(zipPane, it.newValue, it.oldValue, treeZip)
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
    public static void setFilterCBListener(CheckBox checkBox, String propertyName){
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
                }
                else {

                    def order = orderPm.findAttributeByPropertyName(propertyName).getValue()
                    orderPm.findAttributeByPropertyName(propertyName).setValue(0)
//                    orderPm.getAttributes().each {
//                        if(it.value > order){
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

    public static void enableQuery() {
        treesGrid.setDisable(false)
        filtersCBs.setDisable(false)
        closeType.setDisable(false)
        closeCity.setDisable(false)
        closeZip.setDisable(false)
        facetBox.setDisable(false)
    }
    public static void disableQuery() {
        treesGrid.setDisable(true)
        filtersCBs.setDisable(true)
        closeType.setDisable(true)
        closeCity.setDisable(true)
        closeZip.setDisable(true)
        facetBox.setDisable(true)
    }
    public static void addDragging() {
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
                    int newHeight = (it.getHeight())/(it.getChildren().size())
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
                initialPlant.setLoadState(LoadState.LOADED);
                enableQuery()
            }
        }

    }
}


