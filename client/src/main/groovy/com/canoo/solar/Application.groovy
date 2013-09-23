package com.canoo.solar;

import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.geometry.Insets;
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
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
    Label cityLabel = new Label("City: ")
    Label zipLabel = new Label("Zip Code: ")
    Label typeLabel = new Label("Type: ")
    Label nominalLabel = new Label("Power: ")
    Label cityLabelforDetail = new Label("City:       ")
    Label zipLabelforDetail = new Label("Zip Code:")
    Label typeLabelforDetail = new Label("Type:      ")
    Label nominalLabelforDetail = new Label("Power:    ")
    Label idLabelforDetail = new Label("Id:          ")
    Label nominalLabelTooltip = new Label(" e.g. [2 TO 35]")

    TextField cityLabelDetail = new TextField("City")
    TextField idLabelDetail = new TextField("Id")
    TextField zipLabelDetail = new TextField("Zip Code")
    TextField typeLabelDetail = new TextField("Type")
    TextField nominalLabelDetail = new TextField("Power")
    AutoFillTextBox typeChoiceBox = new AutoFillTextBox(observableListTypes)
    AutoFillTextBox cityText = new AutoFillTextBox(observableListCities)
    Label loading = new Label("Loading Data from Solr")
    Label noData = new Label("No Data Found")







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
        addClientSideAction();
        setupBinding();

        Scene scene = new Scene(root, 850, 305)
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
        Rectangle border = new Rectangle()
        border.setStroke(Color.DARKCYAN)
        border.setStrokeWidth(2)
        border.setWidth(310)
        border.setHeight(255)
        border.setFill(Color.TRANSPARENT)
        border.setOpacity(0.5)
        TableColumn idCol = new TableColumn("Position");
        idCol.setMinWidth(100)
        TableColumn typeCol = new TableColumn("Plant Type");
        typeCol.setMinWidth(120)
        TableColumn cityCol = new TableColumn("City");
        cityCol.setMinWidth(100)
        TableColumn zipCol = new TableColumn("ZIP Code");
        zipCol.setMinWidth(100)
        TableColumn nominalCol = new TableColumn("Nominal Power");
        nominalCol.setMinWidth(100)


        table.getColumns().addAll(idCol, typeCol, cityCol, zipCol, nominalCol);
        table.items = observableList
        table.setPrefSize(250,250)
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
            def placeholder = new SimpleStringProperty("Not Loaded");

            clientDolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
                void onFinished(ClientPresentationModel presentationModel) {
                    placeholder.setValue( presentationModel.getAt("position").value.toString() ) // fill async lazily
                }
            } )
            return placeholder
        } as Callback
        typeCol.cellValueFactory = {
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



        HBox cityLabelText = new HBox()
        cityLabelText.setSpacing(5);
        cityLabelText.setPadding(new Insets(10, 0, 0, 10));
        cityLabelText.getChildren().addAll(cityLabel, cityText)

        HBox zipLabelText = new HBox()
        zipLabelText.setSpacing(5);
        zipLabelText.setPadding(new Insets(10, 0, 0, 10));
        zipLabelText.getChildren().addAll(zipLabel, zipText)

        HBox typeLabelText = new HBox()
        typeLabelText.setSpacing(5);
        typeLabelText.setPadding(new Insets(10, 0, 0, 10));
        typeLabelText.getChildren().addAll(typeLabel, typeChoiceBox)

        nominalLabelTooltip.setTextFill(Color.GRAY)
        nominalLabelTooltip.setOpacity(0.7)

        HBox nominalLabelText = new HBox()
        nominalLabelText.setSpacing(5);
        nominalLabelText.setPadding(new Insets(10, 0, 0, 10));
        nominalLabelText.getChildren().addAll(nominalLabel, nominalText, nominalLabelTooltip)

        final HBox filterBox = new HBox();
        filterBox.setSpacing(5);
        filterBox.setPadding(new Insets(10, 0, 10, 10));
        filterBox.getChildren().addAll(cityLabelText,zipLabelText, typeLabelText, nominalLabelText);

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
            println observableListCities
        }

        clientDolphin.data GET_TYPE, { data ->
           observableListTypes.addAll( data.get(0).get("ids")  )
            println observableListTypes
        }







        Pane all = new Pane();
        all.getChildren().addAll(details, border)


        BorderPane borderPane = new BorderPane();
        borderPane.setTop(filterBox);

        borderPane.setCenter(table);
        borderPane.setRight(all);

        VBox vBox = new VBox()

        vBox.getChildren().addAll(
                borderPane);
       return vBox
    }

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
//        bindAttribute(clientDolphin[FILTER][CITY], {
//            observableList.clear()
//            clientDolphin.data GET, { data ->
//                observableList.clear()
//                observableList.addAll( data.get(0).get("ids")  )
//            }
//        })
//        bindAttribute(clientDolphin[FILTER][PLANT_TYPE], {
//            observableList.clear()
//            clientDolphin.data GET, { data ->
//                observableList.clear()
//                observableList.addAll( data.get(0).get("ids")  )
//            }
//        })
//        bindAttribute(clientDolphin[FILTER][NOMINAL_POWER], {
//            observableList.clear()
//            clientDolphin.data GET, { data ->
//                observableList.clear()
//                observableList.addAll( data.get(0).get("ids")  )
//            }
//        })
//        bindAttribute(clientDolphin[FILTER][ZIP], {
//            observableList.clear()
//            clientDolphin.data GET, { data ->
//                observableList.clear()
//                observableList.addAll( data.get(0).get("ids")  )
//            }
//        })
    }
    public static void bindAttribute(Attribute attribute, Closure closure) {
        final listener = closure as PropertyChangeListener
        attribute.addPropertyChangeListener('value', listener)
//        listener.propertyChange(new PropertyChangeEvent(attribute, 'value', attribute.value, attribute.value))
    }
    private void addClientSideAction() {

    }
}
