package com.canoo.solar

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.VPos
import javafx.scene.Group
import javafx.scene.GroupBuilder
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
import javafx.scene.shape.RectangleBuilder
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextBuilder
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
    static List<Range<Integer>> bounds = new ArrayList<Range<Integer>>()
    static List<Range<Integer>> realBounds = new ArrayList<Range<Integer>>()
    static List<Double> widths

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
    static TableColumn<PowerPlant, String> nominalColumn

    static Rectangle dragBorder
    static Rectangle detailsBorder

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
    Group group
    Group groupChoice

    HBox positionBox
    HBox zipBox
    HBox cityBox
    HBox typeBox
    HBox nominalBox
    HBox averageKWHBox
    HBox latitudeBox
    HBox longitudeBox

    HBox choicepositionBox
    HBox choicezipBox
    HBox choicecityBox
    HBox choicetypeBox
    HBox choicenominalBox
    HBox choiceaverageKWHBox
    HBox choicelatitudeBox
    HBox choicelongitudeBox

    public static Rectangle detailsContainer

    VBox details
    VBox tableBox
    static Pane tablePane

    static TreeView treeCities
    static TreeView treeTypes
    static TreeView treeZip
    static HBox detailsContainerHbox
    static HBox choiceContainerHbox

    Label plantTypes
    Label city
    Label zip
    Label tableLabel

    Label plantTypes_auto
    Label city_auto
    Label zip_auto
    String message
    ChoiceBox nominalChoice
    ChoiceBox averageChoice
    ChoiceBox cityChoice
    ChoiceBox typeChoice
    ChoiceBox zipChoice
    ChoiceBox latChoice
    ChoiceBox lonChoice

    ProgressBar progressBar
    SimpleDoubleProperty progress

    public static Label total
    public static Label totalCount

    Timeline progressLine
    static Rectangle rectangleClipBottom
    static Rectangle rectangleClipTop

    static Separator separator
    static Separator facetTableSeparator
    TextField nominalText
    TextField searchField
    Label searchText
    static Text nominalTotalText
    static Text nominalAverageText
    static Text averageAVGKWH
    static Text typeDistribution
    static Text typeMost
    static Text cityDistribution
    static Text cityMost
    static Text totalAVGKWH

    static String nominalTotalString
    static String nominalAverageString
    static String averageAVGKWHString
    static String typeDistributionString
    static String typeMostString
    static String cityDistributionString
    static String cityMostString
    static String totalAVGKWHString

    static Pane typePane
    static Button closeType
    static Pane zipPane
    static Button closeZip
    static Pane cityPane
    static Button closeCity
    static Button clearTableSelectButton

    LinearGradient linearGradDark
    LinearGradient linearGrad

    public static PowerPlantList fakedPlantList
    javafx.collections.ObservableList<PowerPlant> items

    public Application() {
        textAttributeModel = clientDolphin.presentationModel(PM_APP, new ClientAttribute(ATT_ATTR_ID, null));
    }

    @Override
    public void start(Stage stage) throws Exception {
        initializeComponents();
        initializePresentationModels();

        observableListCities.clear()
        observableListTypes.clear()
        observableListZips.clear()
        disableControls.setValue(true)
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
            disableControls.setValue(false)
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
        clientDolphin.presentationModel(SEARCH, [SEARCH_ATT]);
        clientDolphin.presentationModel(FILTER_AUTOFILL, [CITY_AUTOFILL, PLANT_TYPE_AUTOFILL, ZIP_AUTOFILL]);
        clientDolphin.presentationModel(SELECTED_POWERPLANT, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER]);
        clientDolphin.presentationModel(ORDER_CHANGE, [VALUE, SCENEX, SCENEY, DRAGGEDPANE]);
        clientDolphin.presentationModel(TABLE_FILTER, [POSITION_FILTER]);
        clientDolphin.presentationModel(STATE, [DISABLECONTROLS, TRIGGER, START_INDEX, SORT, REVERSER_ORDER, CHANGE_FROM, HOLD, TOTAL_NOMINAL, AVERAGE_KWH, AVERAGE_NOMINAL, TOTAL_KWH, TYPE_DISTRIBUTION, CITY_DISTRIBUTION, TYPE_MOST, CITY_MOST])[TRIGGER].value=0


        Map<String, Object> attributeMap = [:]
        attributeMap.put(ORDER, 0)
        attributeMap.put(SELECTED_ITEMS, "")
        attributeMap.put(PANE, "")

        clientDolphin.presentationModel(CITY, FACET, attributeMap)[ORDER].value =  1
        clientDolphin.presentationModel(PLANT_TYPE, FACET, attributeMap)
        clientDolphin.presentationModel(ZIP, FACET, attributeMap)
        clientDolphin.presentationModel(TABLE, FACET, attributeMap)

        clientDolphin[CITY][PANE].setValue(cityPane.toString())
        clientDolphin[PLANT_TYPE][PANE].setValue(typePane.toString())
        clientDolphin[ZIP][PANE].setValue(zipPane.toString())
        clientDolphin[TABLE][PANE].setValue(tablePane.toString())


        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(START_INDEX).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(SORT).setValue("Position - DESCENDING")
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(REVERSER_ORDER).setValue(false)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(HOLD).setValue(false)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(CHANGE_FROM).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(TOTAL_NOMINAL).setValue(0.0)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(AVERAGE_NOMINAL).setValue(0.0)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(AVERAGE_KWH).setValue(0.0)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(TOTAL_KWH).setValue(0.0)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(TYPE_DISTRIBUTION).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(CITY_DISTRIBUTION).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(STATE).findAttributeByPropertyName(DISABLECONTROLS).setValue(0)
        clientDolphin.getClientModelStore().findPresentationModelById(SEARCH).findAttributeByPropertyName(SEARCH_ATT).setValue("")

    }

    private Pane setupStage() {

        message = ""

        detailsContainerHbox.getChildren().addAll(positionBox, zipBox, cityBox, typeBox, nominalBox, averageKWHBox, latitudeBox, longitudeBox)
        choiceContainerHbox.getChildren().addAll(choicezipBox, choicecityBox, choicetypeBox, choicenominalBox, choiceaverageKWHBox, choicelatitudeBox, choicelongitudeBox)

        nominalBox = Layout.createChoiceBoxBox(nominalChoice, choicenominalBox, nominalBox, "Total", "Average", nominalTotalText, nominalAverageText)
        cityBox = Layout.createChoiceBoxBox(cityChoice, choicecityBox, cityBox, "Distribution", "Most Common", cityDistribution, cityMost)
        typeBox = Layout.createChoiceBoxBox(typeChoice, choicetypeBox, typeBox, "Distribution", "Most Common", typeDistribution, typeMost)
        averageKWHBox = Layout.createChoiceBoxBox(averageChoice, choiceaverageKWHBox, averageKWHBox, "Total", "Average", totalAVGKWH, averageAVGKWH)

        Group myGroup = GroupBuilder.create()
                .children(detailsContainerHbox)
                .clip(rectangleClipBottom)
                .build();

        Group myChoiceGroup = GroupBuilder.create()
                .children(choiceContainerHbox)
                .clip(rectangleClipTop)
                .build();

        group.getChildren().add(myGroup)
        group.setTranslateX(-35)
        groupChoice.getChildren().add(myChoiceGroup)
        myChoiceGroup.maxHeight(25)
        choiceContainerHbox.setMaxHeight(25)
        detailsContainerHbox.getChildren().each {
            it.setTranslateX(-30)
        }
        groupChoice.maxHeight(25)

        choicezipBox.getChildren().add(zipChoice)
        choicelatitudeBox.getChildren().add(latChoice)
        choicelongitudeBox.getChildren().add(lonChoice)

        facetBox.getChildren().addAll(col5, col1, col2, col3, col4)
        facetBox.getChildren().each {
            it.setSpacing(5)
        }

        facetBox.setSpacing(1)
        facetBox.setMinHeight(445)
        searchBox.setTranslateX(690)
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
                    progressLine.setRate(9)
                }
            }
        })

        progressBar.setVisible(false)
        progressBar.setMinHeight(600)
        progressBar.setMaxHeight(600)
        progressBar.setOpacity(0.6)
        progressBar.setBorder(Border.EMPTY)
        progressBar.minWidthProperty().bind(searchAndAll.widthProperty().add(5))
        progressBar.maxWidthProperty().bind(searchAndAll.widthProperty().add(5))
        final KeyValue kv1 = new KeyValue(progress, 0.0);
        final KeyValue kv2 = new KeyValue(progress, 0.5);
        final KeyValue kv3 = new KeyValue(progress, 1.0);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(7000), kv1, kv2, kv3);
        progressLine.getKeyFrames().add(kf1);
        progressLine.setCycleCount(1)

        progressLine.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            void handle(ActionEvent actionEvent) {
                progressBar.setVisible(false)
            }
        })
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
                UpdateActions.refreshTable()
                return true;
            }
        })

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setMaxWidth(650)
        table.setMaxHeight(344)
        table.setItems(items)
        table.setPlaceholder(noData)

        TableColumn<PowerPlant, String> positionColumn = TableFactory.firstColumn()
        positionBox.prefWidthProperty().bind(positionColumn.widthProperty())
        choicepositionBox.prefWidthProperty().bind(positionColumn.widthProperty())

        TableColumn<PowerPlant, String> zipColumn = TableFactory.secondColumn()
        zipBox.prefWidthProperty().bind(zipColumn.widthProperty())
        choicezipBox.prefWidthProperty().bind(zipColumn.widthProperty())
        zipChoice.prefWidthProperty().bind(zipColumn.widthProperty())

        TableColumn<PowerPlant, String> cityColumn = TableFactory.thirdColumn()
        cityBox.prefWidthProperty().bind(cityColumn.widthProperty())
        choicecityBox.prefWidthProperty().bind(cityColumn.widthProperty())
        cityChoice.prefWidthProperty().bind(cityColumn.widthProperty())

        TableColumn<PowerPlant, String> typeColumn = TableFactory.fourthColumn()
        typeBox.prefWidthProperty().bind(typeColumn.widthProperty())
        choicetypeBox.prefWidthProperty().bind(typeColumn.widthProperty())
        typeChoice.prefWidthProperty().bind(typeColumn.widthProperty())

        nominalColumn = TableFactory.fithColumn()
        nominalBox.prefWidthProperty().bind(nominalColumn.widthProperty())
        choicenominalBox.prefWidthProperty().bind(nominalColumn.widthProperty())
        nominalChoice.prefWidthProperty().bind(nominalColumn.widthProperty())

        TableColumn<PowerPlant, String> subtypeColumn = TableFactory.sixthColumn()
        averageKWHBox.prefWidthProperty().bind(subtypeColumn.widthProperty())
        choiceaverageKWHBox.prefWidthProperty().bind(subtypeColumn.widthProperty())
        averageChoice.prefWidthProperty().bind(subtypeColumn.widthProperty())

        TableColumn<PowerPlant, String> latitudeColumn = TableFactory.seventhColumn()
        latitudeBox.prefWidthProperty().bind(latitudeColumn.widthProperty())
        choicelatitudeBox.prefWidthProperty().bind(latitudeColumn.widthProperty())
        latChoice.prefWidthProperty().bind(latitudeColumn.widthProperty())

        TableColumn<PowerPlant, String> longitudeColumn = TableFactory.eighthColumn()
        longitudeBox.prefWidthProperty().bind(longitudeColumn.widthProperty())
        choicelongitudeBox.prefWidthProperty().bind(longitudeColumn.widthProperty())
        lonChoice.prefWidthProperty().bind(longitudeColumn.widthProperty())

        dragBorder = Layout.createRectangle(22.0, linearGradDark, Color.BLACK, 0.5, 3.0)
        detailsContainer = Layout.createRectangle(31.0, linearGrad, Color.BLACK, 0.5, 3.0)
        detailsBorder = Layout.createRectangle(24.0, linearGrad, Color.BLACK, 0.5, 3.0)

        detailsBorder.widthProperty().bind(positionColumn.widthProperty())
        dragBorder.widthProperty().bind(table.widthProperty())
        detailsContainer.widthProperty().bind(table.widthProperty())

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

//        assemble the treeViews and buttons together into a draggable pane
        Layout.createTreePane(rootItemCities, treeCities, closeCity, cityPane, cityTextAutoForSearch, clientDolphin[CITY], bounds, realBounds, widths)
        Layout.createTreePane(rootItemZip, treeZip, closeZip, zipPane, zipTextAutoForSearch, clientDolphin[ZIP], bounds, realBounds, widths)
        Layout.createTreePane(rootItem, treeTypes, closeType, typePane, typeTextAutoForSearch, clientDolphin[PLANT_TYPE], bounds, realBounds, widths)

        addDragging()

        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                if (s2.equals("")){
                    searchText.setText("")
                    disableControls.setValue(true)
                    UpdateActions.clearPmsAndPowerPlants()
                    clientDolphin[STATE][HOLD].setValue(true)
                    UpdateActions.refreshTable()
                }
            }
        })

        searchField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (KeyCode.ENTER == event.getCode()) {
                    searchText.setText(searchField.getText())
                    disableControls.setValue(true)
                    UpdateActions.clearPmsAndPowerPlants()
                    clientDolphin[STATE][HOLD].setValue(true)
                    UpdateActions.refreshTable()
                }
            }
        });

        table.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<PowerPlant>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends PowerPlant> change) {
                def cityValue = clientDolphin.findPresentationModelById(CITY).findAttributeByPropertyName(ORDER).getValue()
                def typeValue = clientDolphin.findPresentationModelById(PLANT_TYPE).findAttributeByPropertyName(ORDER).getValue()
                def zipValue = clientDolphin.findPresentationModelById(ZIP).findAttributeByPropertyName(ORDER).getValue()
                def tableValue = clientDolphin.findPresentationModelById(TABLE).findAttributeByPropertyName(ORDER).getValue()

                disableControls.setValue(true)
                if (cityValue > tableValue){city.setText("")}
                if (zipValue > tableValue){zip.setText("")}
                if (typeValue > tableValue){plantTypes.setText("")}
                if (table.getSelectionModel().getSelectedItems().size().equals(0)){
                    clientDolphin[TABLE_FILTER][POSITION_FILTER].setValue("")
                }else{
                    String fullString = ""
                    int i = 0
                    table.getSelectionModel().getSelectedItems().each {PowerPlant pp ->
                        i++
                        if (i > 1){
                            String part1 = " OR " +  pp.getPositionProperty()
                            fullString = fullString + part1
                        }
                        else{
                            String part1 =  pp.getPositionProperty()
                            fullString = fullString + part1
                        }
                    }
                    clientDolphin[TABLE_FILTER][POSITION_FILTER].setValue("(" + fullString + ")")
                }
                clientDolphin.data GET, { data ->

                    if (cityValue > tableValue){
                        city.setText("")
                        UpdateActions.updateTree(data,treeCities,observableListCities,observableListCitiesCount,2,"Cities")
                    }

                    if (zipValue > tableValue){
                        zip.setText("")
                        UpdateActions.updateTree(data, treeZip, observableListZips, observableListZipsCount, 3, "Zip-Codes")
                    }
                    def size = data.get(0).get("size")
                    if (typeValue > tableValue){
                        plantTypes.setText("")
                        UpdateActions.updateTree(data, treeTypes, observableListTypes, observableListTypesCount, 1, "Plant Types")

                    }

                    clientDolphin.findPresentationModelById(SELECTED_POWERPLANT).getAttributes().each {
                        it.setValue("")
                    }
                    disableControls.setValue(false)
                }
            }
        })

        treeTypes.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends TreeItem> change) {
                def cityValue = clientDolphin.findPresentationModelById(CITY).findAttributeByPropertyName(ORDER).getValue()
                def typeValue = clientDolphin.findPresentationModelById(PLANT_TYPE).findAttributeByPropertyName(ORDER).getValue()
                def zipValue = clientDolphin.findPresentationModelById(ZIP).findAttributeByPropertyName(ORDER).getValue()
                def tableValue = clientDolphin.findPresentationModelById(TABLE).findAttributeByPropertyName(ORDER).getValue()

                if (treeTypes.getSelectionModel().getSelectedItems().size().equals(0)){
                    plantTypes.setText("")
                }else{
                    String fullString = "";
                    int i = 0
                    treeTypes.getSelectionModel().getSelectedItems().each {TreeItem<String> treeItem ->
                        i++
                        if (i > 1){
                            String part1 = " OR " + treeItem.getValue().toString().substring(0, treeItem.getValue().toString().lastIndexOf(' ('))
                            fullString = fullString + part1
                        }
                        else{
                            String part1 = treeItem.getValue().toString().substring(0, treeItem.getValue().toString().lastIndexOf(' ('))
                            fullString = fullString + part1
                        }
                    }
                    plantTypes.setText("(" + fullString + ")")

                disableControls.setValue(true)
                if (cityValue > typeValue){city.setText("")}
                if (zipValue > typeValue){zip.setText("")}
                if (tableValue > typeValue){clientDolphin[TABLE_FILTER][POSITION_FILTER].setValue("")}

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
                    if (tableValue > typeValue){
                        PowerPlantList newFakeList = new PowerPlantList((Integer)size, new OurConsumer<Integer>(){
                            @Override
                            void accept(Integer rowIndex) {
                                loadPresentationModel(rowIndex)
                            }
                        });
                        javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
                        if (newItems.size()==0) { }
                        table.setItems(newItems)
                        totalCount.setText(newItems.size() + "/1377475")
                        table.getSelectionModel().clearSelection()
                        updateDetails()

                    }
                     
                    clientDolphin.findPresentationModelById(SELECTED_POWERPLANT).getAttributes().each {
                        it.setValue("")
                    }
                    disableControls.setValue(false)
                }
                }
            }
        });

        treeCities.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends TreeItem> change) {

                def cityValue = clientDolphin.findPresentationModelById(CITY).findAttributeByPropertyName(ORDER).getValue()
                def typeValue = clientDolphin.findPresentationModelById(PLANT_TYPE).findAttributeByPropertyName(ORDER).getValue()
                def zipValue = clientDolphin.findPresentationModelById(ZIP).findAttributeByPropertyName(ORDER).getValue()
                def tableValue = clientDolphin.findPresentationModelById(TABLE).findAttributeByPropertyName(ORDER).getValue()


                if (treeCities.getSelectionModel().getSelectedItems().size().equals(0)){
                    city.setText("")
                }else{
                    disableControls.setValue(true)
                    String fullString = "";
                    int i = 0
                    treeCities.getSelectionModel().getSelectedItems().each {TreeItem<String> treeItem ->
                        i++
                        if (i > 1){
                            String part1 = " OR " + treeItem.getValue().toString().substring(0, treeItem.getValue().toString().lastIndexOf(' ('))
                            fullString = fullString + part1
                        }
                        else{
                            String part1 = treeItem.getValue().toString().substring(0, treeItem.getValue().toString().lastIndexOf(' ('))
                            fullString = fullString + part1
                        }
                    }
                    city.setText("(" + fullString + ")");

                if (typeValue > cityValue){plantTypes.setText("")}
                if (zipValue > cityValue){zip.setText("")}
                if (tableValue > cityValue){clientDolphin[TABLE_FILTER][POSITION_FILTER].setValue("")}

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
                    if (tableValue > cityValue){
                        PowerPlantList newFakeList = new PowerPlantList((Integer)size, new OurConsumer<Integer>(){
                            @Override
                            void accept(Integer rowIndex) {
                                loadPresentationModel(rowIndex)
                            }
                        });
                        javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
                        if (newItems.size()==0) { }
                        table.setItems(newItems)
                        totalCount.setText(newItems.size() + "/1377475")
                        table.getSelectionModel().clearSelection()
                        updateDetails()

                    }

                    clientDolphin.findPresentationModelById(SELECTED_POWERPLANT).getAttributes().each {
                        it.setValue("")
                    }
                    disableControls.setValue(false)
                }
                }
                    }
                });

        treeZip.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends TreeItem> change) {
                        def cityValue = clientDolphin.findPresentationModelById(CITY).findAttributeByPropertyName(ORDER).getValue()
                        def typeValue = clientDolphin.findPresentationModelById(PLANT_TYPE).findAttributeByPropertyName(ORDER).getValue()
                        def zipValue = clientDolphin.findPresentationModelById(ZIP).findAttributeByPropertyName(ORDER).getValue()
                        def tableValue = clientDolphin.findPresentationModelById(TABLE).findAttributeByPropertyName(ORDER).getValue()

                        if (treeZip.getSelectionModel().getSelectedItems().size().equals(0)) {
                            zip.setText("")
                        }else{
                            disableControls.setValue(true)
                            String fullString = "";
                            int i = 0
                            treeZip.getSelectionModel().getSelectedItems().each {TreeItem<String> treeItem ->
                                i++
                                if (i > 1){
                                    String part1 = " OR " + treeItem.getValue().toString().substring(0, treeItem.getValue().toString().lastIndexOf(' ('))
                                    fullString = fullString + part1
                                }
                                else{
                                    String part1 = treeItem.getValue().toString().substring(0, treeItem.getValue().toString().lastIndexOf(' ('))
                                    fullString = fullString + part1
                                }
                            }
                            zip.setText("(" + fullString + ")");

                        if (typeValue > zipValue){plantTypes.setText("")}
                        if (cityValue > zipValue){city.setText("")}
                        if (tableValue > zipValue){clientDolphin[TABLE_FILTER][POSITION_FILTER].setValue("")}
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
                            if (tableValue > zipValue){
                                PowerPlantList newFakeList = new PowerPlantList((Integer)size, new OurConsumer<Integer>(){
                                    @Override
                                    void accept(Integer rowIndex) {
                                        loadPresentationModel(rowIndex)
                                    }
                                });
                                javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
                                if (newItems.size()==0) { }
                                table.setItems(newItems)
                                totalCount.setText(newItems.size() + "/1377475")
                                table.getSelectionModel().clearSelection()
                                updateDetails()

                            }
                             
                            clientDolphin.findPresentationModelById(SELECTED_POWERPLANT).getAttributes().each {
                                it.setValue("")
                            }

                            disableControls.setValue(false)
                        }

                        }
                }
        });

        tableStack.getChildren().addAll(detailsContainer, total, totalCount, group)
        totalCount.translateXProperty().bind(table.widthProperty().divide(2).subtract(55))
        total.translateXProperty().bind(table.widthProperty().divide(2).subtract(20).multiply(-1))
        progressBar.translateYProperty().bind(pane.heightProperty().subtract(6))
        StackPane detailsStack = new StackPane(detailsBorder, new Label("Aggregate Filters:"))
        detailsStack.prefWidthProperty().bind(positionColumn.widthProperty())
        HBox choiceBoxAll = new HBox(detailsStack, groupChoice)
        tableBox.getChildren().addAll(dragBorder, table, choiceBoxAll, tableStack)
        tablePane.getChildren().addAll(tableBox)
        tablePane.setPadding(new Insets(0, 0, 0, 6))
        searchBox.getChildren().addAll(iv1, searchField)
        searchBox.setSpacing(5)
        searchAndAll.getChildren().addAll(searchBox, facetBox)
        searchAndAll.setPadding(new Insets(10, 10, 0, 25))
        searchAndAll.setSpacing(5)

        startCity()
        pane.getChildren().addAll(searchAndAll, progressBar)
        return pane
    }

    /*METHODS*/
    public static TableView getTable() {
        return table;
    }

    public void startCity() {
        cityTextAutoForSearch.setVisible(false)
        updateFacets(cityPane, 1, 0)
        clientDolphin.getClientModelStore().findPresentationModelById(TABLE)[ORDER].setValue(2)
        updateFacets(tablePane, 2, 0)
        UpdateActions.refreshTable()
    }

    public static void updateDetails() {
        nominalTotalText.setText(nominalTotalString)
        nominalAverageText.setText(nominalAverageString)
        averageAVGKWH.setText(averageAVGKWHString)
        typeDistribution.setText(typeDistributionString)
        typeMost.setText(typeMostString)
        cityDistribution.setText(cityDistributionString)
        cityMost.setText(cityMostString)
        totalAVGKWH.setText(totalAVGKWHString)
        if(table.getItems().size() == 0){
            disableControls.setValue(false)
        }
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
        bind ALL of clientDolphin[FILTER] to 'text' of searchText

        bind ZIP of clientDolphin[SELECTED_POWERPLANT] to 'text' of zipLabelDetail
        bind CITY of clientDolphin[SELECTED_POWERPLANT] to 'text' of cityLabelDetail
        bind ID of clientDolphin[SELECTED_POWERPLANT] to 'text' of idLabelDetail
        bind NOMINAL_POWER of clientDolphin[SELECTED_POWERPLANT] to 'text' of nominalLabelDetail
        bind PLANT_TYPE of clientDolphin[SELECTED_POWERPLANT] to 'text' of typeLabelDetail

//        bind ORDER of clientDolphin[TABLE] to ORDER of clientDolphin[ALL]
//        bind ORDER of clientDolphin[ALL] to ORDER of clientDolphin[TABLE]

        progressBar.progressProperty().bind(progress)
        closeType.disableProperty().bind(disableControls)
        closeCity.disableProperty().bind(disableControls)
        closeZip.disableProperty().bind(disableControls)
        facetBox.disableProperty().bind(disableControls)
        zipTextAutoForSearch.disableProperty().bind(disableControls)
        cityTextAutoForSearch.disableProperty().bind(disableControls)
        typeTextAutoForSearch.disableProperty().bind(disableControls)
        searchBox.disableProperty().bind(disableControls)

        bindAttribute(clientDolphin[STATE][SORT], {
            if (clientDolphin[STATE][SORT].getValue().toString()==IGNORE)return;
            UpdateActions.clearPmsAndPowerPlants()
            clientDolphin[STATE][HOLD].setValue(true)
        })

        bindAttribute(clientDolphin[FILTER][PLANT_TYPE], {
            UpdateActions.clearPmsAndPowerPlants()
        })

        bindAttribute(clientDolphin[FILTER][CITY], {
            UpdateActions.clearPmsAndPowerPlants()
        })

        bindAttribute(clientDolphin[FILTER][ZIP], {
            UpdateActions.clearPmsAndPowerPlants()
        })

        bindAttribute(clientDolphin[PLANT_TYPE][ORDER],{
            plantTypes.setText("")
            updateFacets(typePane, it.newValue, it.oldValue)
            disableControls.setValue(true)
            if (it.oldValue.equals(0)){
                clientDolphin.data GET, { data ->
                    UpdateActions.updateTree(data, treeTypes, observableListTypes, observableListTypesCount, 1, "Plant Types")
                    disableControls.setValue(false)
                }
            }else if(it.newValue.equals(0)){
                try{
                   UpdateActions.reselect(it.oldValue)
                }catch (Exception e){
                    UpdateActions.resetEverything()
                }
            }
        })

        bindAttribute(clientDolphin[CITY][ORDER],{
            city.setText("")
            updateFacets(cityPane, it.newValue, it.oldValue)
            if (it.oldValue.equals(0)){
                disableControls.setValue(true)
                clientDolphin.data GET, { data ->
                    UpdateActions.updateTree(data, treeCities,observableListCities,observableListCitiesCount,2,"Cities")
                    disableControls.setValue(false)
                }
            }else if(it.newValue.equals(0)){
                try{
                   UpdateActions.reselect(it.oldValue)
                }catch (Exception e){
                   UpdateActions.resetEverything()
                }

            }
        })

        bindAttribute(clientDolphin[ZIP][ORDER],{
            zip.setText("")
            updateFacets(zipPane, it.newValue, it.oldValue)
            if (it.oldValue.equals(0)){
                disableControls.setValue(true)
                clientDolphin.data GET, { data ->
                    UpdateActions.updateTree(data, treeZip, observableListZips, observableListZipsCount, 3, "Zip-Codes")
                    disableControls.setValue(false)
                }
            }else if(it.newValue.equals(0)){
                try{
                    UpdateActions.reselect(it.oldValue)
                }catch (Exception e){
                    UpdateActions.resetEverything()
                }
            }
        })

        bindAttribute(clientDolphin[TABLE][ORDER],{
            searchText.setText("")
            table.getSelectionModel().clearSelection()
            updateFacets(tablePane, it.newValue, it.oldValue)
            Double translate = it.newValue.minus(1).multiply(206).plus(480)
            searchBox.setTranslateX(translate)

        })



        bindAttribute(clientDolphin[ORDER_CHANGE][VALUE], {

            if (it.newValue.equals(IGNORE) || it.oldValue.equals(IGNORE))return;
            searchText.setText("")
            int sceneX = clientDolphin[ORDER_CHANGE][SCENEX].getValue()
            int sceneY = clientDolphin[ORDER_CHANGE][SCENEY].getValue()
            String draggedPane = clientDolphin[ORDER_CHANGE][DRAGGEDPANE].getValue()
            int value = 0
            boolean firstMoveThenClear = true
            int childSize = 0
            int originSize = facetBox.getChildren().get(it.oldValue.minus(1)).getChildren().size()
            if (it.oldValue > it.newValue){
                value = it.newValue
                firstMoveThenClear = true
                childSize = facetBox.getChildren().get(it.newValue.minus(1)).getChildren().size()
            }else{
                value = it.oldValue
                firstMoveThenClear = false
                childSize = facetBox.getChildren().get(it.oldValue.minus(1)).getChildren().size()
            }
            for (int i = 0; i < facetBox.getChildren().size(); i++){
                if (i+1 >= value){
                    facetBox.getChildren().get(i).getChildren().each{Pane pane ->
                        pane.getChildren().get(0).getChildren().get(1).getSelectionModel().clearSelection()
                    }
                }
            }
            if (it.newValue.equals(1) || it.oldValue.equals(1)){
               UpdateActions.resetEverything()
            }else{
                try{
                    UpdateActions.reselect(value)
                }catch (Exception e){
                    UpdateActions.resetEverything()
                }
            }
        })

        bindAttribute(clientDolphin[STATE][TRIGGER], {
            UpdateActions.clearPmsAndPowerPlants()
            facetBox.getChildren().each {VBox vBox ->
                if (vBox.getChildren().size().equals(0))return;
                int newHeight = 445.div(vBox.getChildren().size())-15

                vBox.getChildren().each {
                     it.setPrefHeight(newHeight)
                     it.getChildren().get(0).setPrefHeight(newHeight)
                }
            }
        })

        bindAttribute(clientDolphin[STATE][DISABLECONTROLS],{
            disableControls.setValue(false)
        })

        bindAttribute(clientDolphin[STATE][TOTAL_NOMINAL],{
             Integer value = it.newValue.round(0)

             nominalTotalString = "Total nominal: $value"
        })

        bindAttribute(clientDolphin[STATE][AVERAGE_KWH],{
             Integer value = it.newValue.round(0)
            averageAVGKWHString = "Average kW/h: $value"
        })

        bindAttribute(clientDolphin[STATE][AVERAGE_NOMINAL],{
             Integer value = it.newValue
             nominalAverageString = "Average nominal: $value"
        })

        bindAttribute(clientDolphin[STATE][TOTAL_KWH],{
             Integer value = it.newValue.round(0)
             totalAVGKWHString = "Total kW/h: $value"
        })

        bindAttribute(clientDolphin[STATE][TYPE_DISTRIBUTION],{
             Integer value = it.newValue
             typeDistributionString = "Different Types: $value"
        })

        bindAttribute(clientDolphin[STATE][CITY_DISTRIBUTION],{
             Integer value = it.newValue
             cityDistributionString = "Different Cities: $value"
        })

        bindAttribute(clientDolphin[STATE][CITY_MOST],{
             String value = it.newValue
            if (value.equals("")){
                cityMostString = "No Data"
            }else{
                String output = Character.toUpperCase(value.charAt(0)).toString() + value.substring(1);
                cityMostString = "Most Common: $output"
            }
        })

        bindAttribute(clientDolphin[STATE][TYPE_MOST],{
             String value = it.newValue
            if (value.equals("")){
                typeMostString = "No Data"
            }else{
                String output = Character.toUpperCase(value.charAt(0)).toString() + value.substring(1);
                typeMostString = "Most Common: $output"
            }
        })
    }

    public static void bindAttribute(Attribute attribute, Closure closure) {
        final listener = closure as PropertyChangeListener
        attribute.addPropertyChangeListener('value', listener)
    }

    public static void addDragging() {
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
                    def newVal
                    def oldVal
                    if (dragEvent.getSceneY().toInteger() > 250 ) {
                        for (int i = 0; i < realBounds.size(); i++){
                            if (realBounds.get(i).containsWithinBounds(dragEvent.getSceneX().toInteger())){
                                oldVal = 0
                                clientDolphin.findAllPresentationModelsByType(FACET).each {
                                    if (it[PANE].getValue().equals(draggedElement.toString())){
                                        oldVal = it[ORDER].getValue()
                                    }
                                }
                                newVal = i+1
                            }
                        }
                    }
                    else{
                        for (int i = 0; i < bounds.size(); i++){
                            if (bounds.get(i).containsWithinBounds(dragEvent.getSceneX().toInteger())){
                                oldVal =0
                                clientDolphin.findAllPresentationModelsByType(FACET).each {
                                    if (it[PANE].getValue().equals(draggedElement.toString())){
                                        oldVal = it[ORDER].getValue()
                                    }
                                }
                                newVal = i+1
                            }
                        }
                    }
                    clientDolphin.findPresentationModelById(ORDER_CHANGE)[SCENEX].setValue(dragEvent.getSceneX().toInteger())
                    clientDolphin.findPresentationModelById(ORDER_CHANGE)[SCENEY].setValue(dragEvent.getSceneY().toInteger())
                    clientDolphin.findPresentationModelById(ORDER_CHANGE)[DRAGGEDPANE].setValue(draggedElement.toString())
                    clientDolphin.findPresentationModelById(ORDER_CHANGE)[VALUE].setValue(IGNORE)
                    clientDolphin.findPresentationModelById(ORDER_CHANGE)[VALUE].setValue(oldVal)
                    clientDolphin.findPresentationModelById(ORDER_CHANGE)[VALUE].setValue(newVal)

                    if (dragEvent.getSceneY().toInteger() > 250 ) {
                        for (int i = 0; i < realBounds.size(); i++){
                            if (realBounds.get(i).containsWithinBounds(dragEvent.getSceneX().toInteger())){
                                def oldOrder = 0
                                clientDolphin.findAllPresentationModelsByType(FACET).each {
                                    if (it[PANE].getValue().equals(draggedElement.toString())){
                                        oldOrder = it[ORDER].getValue()
                                    }
                                }
                                def newOrder = i+1

                                if (draggedElement.getParent().getChildren().size() > 1){
                                    clientDolphin.findAllPresentationModelsByType(FACET).each {
                                        if (it[PANE].getValue().equals(draggedElement.toString())){
                                            it[ORDER].setValue(newOrder)
                                        }
                                    }
                                }else{
                                    clientDolphin.findAllPresentationModelsByType(FACET).each {
                                        if (it[PANE].getValue().equals(draggedElement.toString())){
                                            it[ORDER].setValue(newOrder)
                                        }
                                    }
                                    clientDolphin.findAllPresentationModelsByType(FACET).each {
                                        int paneOrder = it[ORDER].getValue()
                                        if (paneOrder > oldOrder){
                                            it[ORDER].setValue(it[ORDER].getValue()-1)
                                        }
                                    }
                                }

                            }
                        }
                    }
                    else{
                        for (int i = 0; i < bounds.size(); i++){
                            if (bounds.get(i).containsWithinBounds(dragEvent.getSceneX().toInteger())){
                                def oldOrder = 0
                                clientDolphin.findAllPresentationModelsByType(FACET).each {
                                    if (it[PANE].getValue().equals(draggedElement.toString())){
                                        oldOrder = it[ORDER].getValue()
                                    }
                                }
                                def newOrder = i+1
                                if (draggedElement.getParent().getChildren().size() > 1){
                                    clientDolphin.findAllPresentationModelsByType(FACET).each {
                                        int paneOrder = it[ORDER].getValue()
                                        if (it[PANE].getValue().equals(draggedElement.toString())){
                                            it[ORDER].setValue(i+1)
                                        }else{
                                            if (paneOrder >= newOrder){
                                                it[ORDER].setValue(paneOrder+1)
                                            }
                                        }
                                    }

                                } else{

                                    clientDolphin.findAllPresentationModelsByType(FACET).each {
                                        int paneOrder = it[ORDER].getValue()

                                        if (paneOrder >= newOrder && paneOrder <= oldOrder){
                                            if (it[PANE].getValue().equals(draggedElement.toString())){
                                                it[ORDER].setValue(i+1)
                                            }else{
                                                it[ORDER].setValue(paneOrder+1)
                                            }
                                        }
                                        else if (paneOrder >= oldOrder && paneOrder < newOrder){
                                            if (it[PANE].getValue().equals(draggedElement.toString())){
                                                it[ORDER].setValue(i)
                                            }else{
                                                it[ORDER].setValue(paneOrder-1)
                                            }
                                        }
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
                realBounds.clear()

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

                widths.clear()
                facetBox.getChildren().each {


                    def width = it.getBoundsInLocal().getWidth().round(0)
                    if(width == 0.0){
                        return;
                    }else{
                        widths.add(width)
                    }
                }

                Integer realStarting = 25
                for (int c = 0; c < widths.size(); c++){
                    Range<Integer> range = new IntRange(realStarting, realStarting.plus(widths.get(c).toInteger()))
                    realBounds.add(range)
                    realStarting = realStarting.plus(widths.get(c).toInteger())
                }
                println realBounds
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
                LinearGradient linearGradBlue = LinearGradientBuilder.create()
                        .startX(0)
                        .startY(0)
                        .endX(0)
                        .endY(50)
                        .proportional(false)
                        .cycleMethod(CycleMethod.NO_CYCLE)
                        .stops( new Stop(0.1f, Color.rgb(30, 144, 255, 1)),
                        new Stop(1.0f, Color.rgb(100, 149, 237, 1)))
                        .build();
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

                if (dragEvent.getSceneY() > 250){

                    for (int i = 0; i < realBounds.size(); i++){
                        if (realBounds.get(i).containsWithinBounds(dragEvent.getSceneX().toInteger())){
                            Rectangle r2 = new Rectangle()
                            r2.setFill(linearGradBlue)
                            r2.setOpacity(0.3)

                            r2.setWidth(facetBox.getChildren().get(i).getWidth().minus(6))
                            r2.setHeight(400.div(facetBox.getChildren().get(i).getChildren().size()+1))
                            r2.relocate(realBounds.get(i).getFrom(), 400.div(facetBox.getChildren().get(i).getChildren().size()+1).multiply(facetBox.getChildren().get(i).getChildren().size()).plus(62))
                            pane.getChildren().add(r2)
                            r2.setMouseTransparent(true)
                            r2.setFocusTraversable(true)
                        }
                    }

                }else{

                    for (int i = 0; i < bounds.size(); i++){
                        if (bounds.get(i).containsWithinBounds(dragEvent.getSceneX().toInteger())){

                            Rectangle r2 = new Rectangle(6, 422)
                            r2.setFill(linearGradBlue)
                            r2.setOpacity(0.8)
                            r2.setMouseTransparent(true)
                            try{
                                r2.relocate(bounds.get(i).getTo().minus(widths.get(i).div(2)).minus(3*i).minus(7),40)
                                pane.getChildren().add(r2)
                            }catch (Exception e){
                                r2.relocate(bounds.get(i).getTo().minus(3*i).minus(7),40)
                                pane.getChildren().add(r2)
                            }
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
//        nominalTotalText.layoutXProperty().unbind()
        for (ScrollBar n : table.lookupAll(".scroll-bar")){
            if (n.getOrientation().equals(Orientation.HORIZONTAL)){
                detailsContainerHbox.translateXProperty().bind(n.valueProperty().multiply(-1.0))
                choiceContainerHbox.translateXProperty().bind(n.valueProperty().multiply(-1.0))
            }
        }

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
            }
            disableControls.setValue(false)
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
        message = new String()

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
        group = new Group();
        groupChoice = new Group();
        choiceContainerHbox = new HBox()

        positionBox = new HBox()
        zipBox = new HBox()
        cityBox = new HBox()
        typeBox = new HBox()
        nominalBox = new HBox()
        averageKWHBox = new HBox()
        latitudeBox = new HBox()
        longitudeBox = new HBox()

        choicepositionBox = new HBox()
        choicezipBox = new HBox()
        choicecityBox = new HBox()
        choicetypeBox = new HBox()
        choicenominalBox = new HBox()
        choiceaverageKWHBox = new HBox()
        choicelatitudeBox = new HBox()
        choicelongitudeBox = new HBox()

        detailsContainer = new Rectangle()
        detailsBorder = new Rectangle()

        details = new VBox()

        treeCities = new TreeView();
        treeTypes = new TreeView();
        treeZip = new TreeView();
        nominalChoice = new ChoiceBox()
        averageChoice = new ChoiceBox()
        cityChoice = new ChoiceBox()
        typeChoice = new ChoiceBox()
        zipChoice = new ChoiceBox()
        lonChoice = new ChoiceBox()
        latChoice = new ChoiceBox()

        plantTypes_auto = new Label();
        city_auto = new Label();
        zip_auto = new Label();

        plantTypes = new Label();
        city = new Label();
        zip = new Label();
        tableLabel = new Label()

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
        clearTableSelectButton = new Button("Clear Selection")

        panesToAttributes = new HashMap()

        nominalAverageText = TextBuilder.create()
                .text(message)
                .font(Font.font("SansSerif", 11))
                .textOrigin(VPos.TOP)
                .build();

        nominalTotalText = TextBuilder.create()
                .text(message)
                .font(Font.font("SansSerif", 11))
                .textOrigin(VPos.TOP)
                .build();

        averageAVGKWH = TextBuilder.create()
                .text(" ")
                .font(Font.font("SansSerif", 11))
                .textOrigin(VPos.TOP)
                .build();

        typeDistribution = TextBuilder.create()
                .text(" ")
                .font(Font.font("SansSerif", 11))
                .textOrigin(VPos.TOP)
                .build();

        typeMost = TextBuilder.create()
                .text(" ")
                .font(Font.font("SansSerif", 11))
                .textOrigin(VPos.TOP)
                .build();

        cityDistribution = TextBuilder.create()
                .text(" ")
                .font(Font.font("SansSerif", 11))
                .textOrigin(VPos.TOP)
                .build();

        cityMost = TextBuilder.create()
                .text(" ")
                .font(Font.font("SansSerif", 11))
                .textOrigin(VPos.TOP)
                .build();

        totalAVGKWH = TextBuilder.create()
                .text(" ")
                .font(Font.font("SansSerif", 11))
                .textOrigin(VPos.TOP)
                .build();

        nominalTotalString = new String()
        nominalAverageString = new String()
        averageAVGKWHString = new String()
        typeDistributionString = new String()
        typeMostString = new String()
        cityDistributionString = new String()
        cityMostString = new String()
        totalAVGKWHString = new String()

        rectangleClipBottom = RectangleBuilder.create()
                .width(500)
                .height(80)
                .build()

        rectangleClipTop = RectangleBuilder.create()
                .width(550)
                .height(25)
                .build()

        detailsContainerHbox = new HBox()

        disableControls = new SimpleBooleanProperty(false)

        linearGradDark = LinearGradientBuilder.create()
                .startX(0)
                .startY(0)
                .endX(0)
                .endY(22)
                .proportional(false)
                .cycleMethod(CycleMethod.NO_CYCLE)
                .stops( new Stop(0.1f, Color.rgb(245, 245, 245, 1)),
                new Stop(1.0f, Color.rgb(179, 179, 179, 1)))
                .build();

        linearGrad = LinearGradientBuilder.create()
                .startX(0)
                .startY(0)
                .endX(0)
                .endY(50)
                .proportional(false)
                .cycleMethod(CycleMethod.NO_CYCLE)
                .stops( new Stop(0.1f, Color.rgb(245, 245, 245, 1)),
                new Stop(1.0f, Color.rgb(179, 179, 179, 1)))
                .build();

        fakedPlantList = new PowerPlantList(1377475, new OurConsumer<Integer>(){
            @Override
            void accept(Integer rowIndex) {
                loadPresentationModel(rowIndex)
            }
        });
        items = FakeCollections.newObservableList(fakedPlantList);
    }

    private void updateFacets(Pane pane, Integer newValue, Integer oldValue) {

        if (newValue.equals(0)){
            facetBox.getChildren().get(oldValue.minus(1)).getChildren().remove(pane)
        } else if(oldValue.equals(0)){
            facetBox.getChildren().get(newValue.minus(1)).getChildren().add(pane)
        }else{
            facetBox.getChildren().get(newValue.minus(1)).getChildren().add(pane)
            facetBox.getChildren().get(oldValue.minus(1)).getChildren().remove(pane)
        }
        addDragging()
    }
}



