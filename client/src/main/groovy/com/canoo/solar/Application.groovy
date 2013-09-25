package com.canoo.solar

import com.sun.javafx.scene.control.skin.TableHeaderRow
import com.sun.javafx.scene.control.skin.TableViewSkin
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets;
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import javafx.util.Callback
import np.com.ngopal.control.AutoFillTextBox
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.WithPresentationModelHandler

import java.beans.PropertyChangeListener

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
    private PresentationModel textAttributeModel;
    private TableView table = new TableView();
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
    Label loading = new Label("Loading Data from Solr")
    Label noData = new Label("No Data Found")
    Label search = new Label("Search: ")
    Button button = new Button("ViewPort")


    TextField zipText = new TextField()

    TextField nominalText = new TextField()
    TextField typeLabelforBinding = typeChoiceBox.getTextbox()
    TextField cityLabelforBinding = cityText.getTextbox()

    public Application() {
        textAttributeModel = clientDolphin.presentationModel(PM_APP, new ClientAttribute(ATT_ATTR_ID, null));
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Application Title");
        initializePresentationModels();
        Pane root = setupStage();
        setupBinding();

        Scene scene = new Scene(root, 850, 270)
        scene.stylesheets << 'demo.css'

        stage.setScene(scene);
        stage.setTitle(getClass().getName());
        stage.show();
    }

    private static void initializePresentationModels () {
        clientDolphin.presentationModel(FILTER, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER]);
        clientDolphin.presentationModel(SELECTED_POWERPLANT, [ID, CITY, PLANT_TYPE, ZIP, NOMINAL_POWER]);
        clientDolphin.presentationModel(STATE, [TRIGGER])[TRIGGER].value=0


    }

    private Pane setupStage() {


        loading.setScaleX(1.2)
        loading.setScaleY(1.2)
        loading.setTextFill(Color.BLUE)
        table.setEditable(true);
        table.setPrefHeight(250)
        Rectangle border = new Rectangle()
        border.setStroke(Color.DARKCYAN)
        border.setStrokeWidth(2)
        border.setWidth(280)
        border.setHeight(255)
        border.setFill(Color.TRANSPARENT)
        border.setOpacity(0.5)
        TableColumn idCol = new TableColumn("Position");
        idCol.setMinWidth(100)
        idCol.setResizable(false)
        TableColumn typeCol = new TableColumn("Plant Type");
        typeChoiceBox.setMinWidth(120)
        typeCol.setMinWidth(120)
        typeCol.setResizable(false)
        TableColumn cityCol = new TableColumn("City");
        cityText.setMinWidth(100)
        cityCol.setMinWidth(100)
        cityCol.setResizable(false)
        TableColumn zipCol = new TableColumn("ZIP Code");
        zipText.setMaxWidth(100)
        zipCol.setMinWidth(100)
        zipCol.setResizable(false)
        TableColumn nominalCol = new TableColumn("Nominal Power");
        nominalText.setMaxWidth(100)
        nominalCol.setMinWidth(100)
        nominalCol.setResizable(false)


        table.getColumns().addAll(idCol, typeCol, cityCol, zipCol, nominalCol);
        table.items = observableList
        table.setPlaceholder(loading)
        table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->
            if (selectedPm==null) return;
            clientDolphin.clientModelStore.withPresentationModel(selectedPm.toString(), new WithPresentationModelHandler() {
                void onFinished(ClientPresentationModel presentationModel) {
                    clientDolphin.apply presentationModel to clientDolphin[SELECTED_POWERPLANT]

                }
            } )
        } as ChangeListener )

        idCol.cellValueFactory = {
            String lazyId = it.value
            println "************** /" + it.value + "/"
            def placeholder = new SimpleStringProperty("Not Loaded");


            clientDolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
                void onFinished(ClientPresentationModel presentationModel) {
                    placeholder.setValue( presentationModel.getAt("position").value.toString() ) // fill async lazily
                }
            } )
            return placeholder
        } as Callback


        typeCol.cellValueFactory = {
//            String lazyId = cell.getTableView().getItems().get(cell.getIndex());
            String lazyId = it.value
            def placeholder = new SimpleStringProperty("Not Loaded")
            clientDolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
                void onFinished(ClientPresentationModel presentationModel) {
                    placeholder.setValue( presentationModel.getAt(PLANT_TYPE).value.toString() ) // fill async lazily
                }
            } )
            return placeholder
        } as Callback

        cityCol.cellValueFactory = {
            String lazyId = it.value
            def placeholder = new SimpleStringProperty("Not Loaded")
            clientDolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
                void onFinished(ClientPresentationModel presentationModel) {
                    placeholder.setValue( presentationModel.getAt(CITY).value.toString() ) // fill async lazily
                }
            } )
            return placeholder
        } as Callback
        zipCol.cellValueFactory = {
            String lazyId = it.value
            def placeholder = new SimpleStringProperty("Not Loaded")
            clientDolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
                void onFinished(ClientPresentationModel presentationModel) {
                    placeholder.setValue( presentationModel.getAt(ZIP).value.toString() ) // fill async lazily
                }
            } )
            return placeholder
        } as Callback
        nominalCol.cellValueFactory = {
            String lazyId = it.value
            def placeholder = new SimpleStringProperty("Not Loaded")
            clientDolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
                void onFinished(ClientPresentationModel presentationModel) {
                    placeholder.setValue(presentationModel.getAt(NOMINAL_POWER).value.toString()) // fill async lazily
                }
            } )
            return placeholder
        } as Callback

        HBox nominalLabelTextDetail = new HBox()
        nominalLabelTextDetail.setSpacing(5);
        nominalLabelTextDetail.setPadding(new Insets(10, 0, 0, 10));
        nominalLabelTextDetail.getChildren().addAll(nominalLabelforDetail, nominalLabelDetail)

        HBox cityLabelTextDetail = new HBox()
        cityLabelTextDetail.setSpacing(5);
        cityLabelTextDetail.setPadding(new Insets(10, 0, 0, 10));
        cityLabelTextDetail.getChildren().addAll(cityLabelforDetail, cityLabelDetail)

        HBox typeLabelTextDetail = new HBox()
        typeLabelTextDetail.setSpacing(5);
        typeLabelTextDetail.setPadding(new Insets(10, 0, 0, 10));
        typeLabelTextDetail.getChildren().addAll(typeLabelforDetail, typeLabelDetail)

        HBox zipLabelTextDetail = new HBox()
        zipLabelTextDetail.setSpacing(5);
        zipLabelTextDetail.setPadding(new Insets(10, 0, 0, 10));
        zipLabelTextDetail.getChildren().addAll(zipLabelforDetail, zipLabelDetail)


        HBox idLabelTextDetail = new HBox()
        idLabelTextDetail.setSpacing(5);
        idLabelTextDetail.setPadding(new Insets(10, 0, 0, 10));
        idLabelTextDetail.getChildren().addAll(idLabelforDetail, idLabelDetail)


        HBox filters = new HBox()
        filters.setPadding(new Insets(10, 2, 5, 59));

        filters.getChildren().addAll(search, typeChoiceBox, cityText, zipText, nominalText,button)

        VBox details = new VBox()
        details.setSpacing(5);
        details.setPadding(new Insets(10, 0, 0, 10));
        details.getChildren().addAll(cityLabelTextDetail, idLabelTextDetail, typeLabelTextDetail, nominalLabelTextDetail, zipLabelTextDetail)

        clientDolphin.data GET, { data ->
            observableList.clear()
           observableList.addAll( data.get(0).get("ids")  )
        }

        clientDolphin.data GET_CITIES, { data ->
           observableListCities.addAll( data.get(0).get("ids")  )
        }

        clientDolphin.data GET_TYPE, { data ->
           observableListTypes.addAll( data.get(0).get("ids")  )
        }



        Pane all = new Pane();
        all.getChildren().addAll(details, border)


        BorderPane borderPane = new BorderPane();
        borderPane.setTop(filters);

        borderPane.setCenter(table);
        borderPane.setRight(all);

        VBox vBox = new VBox()

        vBox.getChildren().addAll(
                borderPane);
       return vBox
    }



//    public static class TableViewInfo {
//        private final VirtualFlow virtualFlow;
//        private final TableHeaderRow tableHeaderRow;
//
//        private TableViewInfo(final VirtualFlow virtualFlow, final TableHeaderRow tableHeaderRow) {
//            this.virtualFlow = virtualFlow;
//            this.tableHeaderRow = tableHeaderRow;
//        }
//        public VirtualFlow getVirtualFlow() {
//
//            return virtualFlow;
//
//        }
//        public TableHeaderRow getTableHeaderRow() {
//            return tableHeaderRow;
//        }
//    }
//
//    public static TableViewInfo getTableViewInfo(TableView tableView) {
//        TableViewSkin tableViewSkin = (TableViewSkin) tableView.getSkin();
//        ObservableList<Node> children = tableViewSkin.getChildren();
//        VirtualFlow virtualFlow = (VirtualFlow) children.get(1);
//
//        TableHeaderRow tableHeaderRow = (TableHeaderRow) children.get(0);
//
//        return new TableViewInfo(virtualFlow, tableHeaderRow);
//
//    }

    private void setupBinding() {
        bind 'text' of zipText to ZIP of clientDolphin[FILTER]
        bind 'text' of cityLabelforBinding to CITY of clientDolphin[FILTER]
        bind 'text' of typeLabelforBinding to PLANT_TYPE of clientDolphin[FILTER]
        bind 'text' of nominalText to NOMINAL_POWER of clientDolphin[FILTER]

        bind ZIP of clientDolphin[SELECTED_POWERPLANT] to 'text' of zipLabelDetail
        bind CITY of clientDolphin[SELECTED_POWERPLANT] to'text' of cityLabelDetail
        bind ID of clientDolphin[SELECTED_POWERPLANT] to 'text' of idLabelDetail
        bind NOMINAL_POWER of clientDolphin[SELECTED_POWERPLANT] to 'text' of nominalLabelDetail
        bind PLANT_TYPE of clientDolphin[SELECTED_POWERPLANT] to 'text' of typeLabelDetail

        bindAttribute(clientDolphin[STATE][TRIGGER], {
            observableList.clear()
            clientDolphin.data GET, { data ->
                observableList.clear()
                observableList.addAll( data.get(0).get("ids"))
                if (observableList.size()==0){table.setPlaceholder(noData)}
                else{table.setPlaceholder(loading)}

            }
        })
    }
    public static void bindAttribute(Attribute attribute, Closure closure) {
        final listener = closure as PropertyChangeListener
        attribute.addPropertyChangeListener('value', listener)
//        listener.propertyChange(new PropertyChangeEvent(attribute, 'value', attribute.value, attribute.value))
    }

//    public Integer getFirstCellIndex() {
//        return getTableViewInfo(table).getVirtualFlow().getFirstVisibleCellWithinViewPort().getIndex()
//    }
//
//    public Integer getLastCellIndex() {
//        return getTableViewInfo(table).getVirtualFlow().getLastVisibleCellWithinViewPort().getIndex()
//    }
}
