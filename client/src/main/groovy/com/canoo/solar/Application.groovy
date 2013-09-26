package com.canoo.solar

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Insets
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTreeCell
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
    Label columns = new Label("Columns")
    Label filter = new Label("Filters")
    Button button = new Button("Show Filters")
    Button button2 = new Button("Hide Filters")
    private Rectangle2D boxBounds = new Rectangle2D(600, 100, 650, 200);

    CheckBox cityCB = new CheckBox("Show Cities");
    CheckBox typeCB = new CheckBox("Show Types");
    CheckBox cityFilterCB = new CheckBox("Filter by City");
    CheckBox typeFilterCB = new CheckBox("Filter by Type");
    CheckBox zipCB = new CheckBox("Show Zip-Codes");
    CheckBox nominalCB = new CheckBox("Show Nominal Powers");
    final TreeItem<String> allItem = new TreeItem<String>("All");
    Pane filtersStack = new Pane()
    Pane columnsStack = new Pane()
    Pane tableStack = new Pane()
    Pane pane = new Pane()
    private Rectangle clipRect;
    private Timeline timelineLeft;
    private Timeline timelineRight;
    private Timeline timelineLeftFilters;
    private Timeline timelineRightFilters;
    final TreeView treeCities = new TreeView();
    final TreeView tree = new TreeView();
    TextField zipText = new TextField()
    final Label selectionLabeltypes = new Label();
    final Label selectionLabelcities = new Label();
    final Separator separator = new Separator();
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
//        clientDolphin.data GET, { data ->
//            observableList.clear()
//            observableList.addAll( data.get(0).get("ids")  )
//        }

        clientDolphin.data GET_CITIES, { data ->
            observableListCities.addAll( data.get(0).get("ids")  )
        }

        clientDolphin.data GET_TYPE, { data ->
            observableListTypes.addAll( data.get(0).get("ids")  )
        }
        Pane root = setupStage();
        setupBinding();

        Scene scene = new Scene(root, 800, 640)
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
        table.setMinHeight(700)

        Rectangle border = new Rectangle()
        border.setStroke(Color.INDIANRED)
        border.setStrokeWidth(2)
        border.setWidth(170)
        border.setHeight(100)
        border.setFill(Color.WHITESMOKE)
        border.setArcWidth(10)
        border.setArcHeight(10)
        border.setOpacity(0.8)


        Rectangle border2 = new Rectangle()
        border2.setStroke(Color.INDIANRED)
        border2.setStrokeWidth(2)
        border2.setWidth(25)
        border2.setHeight(100)
        border2.setFill(Color.WHITESMOKE)
        border2.setArcWidth(10)
        border2.setArcHeight(10)
        border2.setOpacity(0.3)
        border2.relocate(0, 290)

        Rectangle border3 = new Rectangle()
        border3.setStroke(Color.INDIANRED)
        border3.setStrokeWidth(2)
        border3.setWidth(170)
        border3.setHeight(100)
        border3.setFill(Color.WHITESMOKE)
        border3.setArcWidth(10)
        border3.setArcHeight(10)
        border3.setOpacity(0.8)


        Rectangle border4 = new Rectangle()
        border4.setStroke(Color.INDIANRED)
        border4.setStrokeWidth(2)
        border4.setWidth(25)
        border4.setHeight(100)
        border4.setFill(Color.WHITESMOKE)
        border4.setArcWidth(10)
        border4.setArcHeight(10)
        border4.setOpacity(0.3)
        border4.relocate(0, 420)

        cityCB.setSelected(true);
        typeCB.setSelected(true);
        nominalCB.setSelected(true);
        zipCB.setSelected(true);

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


        cityCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    table.getColumns().add(cityCol)
                }
                else table.getColumns().remove(cityCol)
            }
        });
        typeCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    table.getColumns().add(typeCol)
                }
                else table.getColumns().remove(typeCol)
            }
        });
        nominalCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    table.getColumns().add(nominalCol)
                }
                else table.getColumns().remove(nominalCol)
            }
        });
        zipCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    table.getColumns().add(zipCol)
                }
                else table.getColumns().remove(zipCol)
            }
        });

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
        filters.setPadding(new Insets(10, 0, 0, 10));
        filters.getChildren().addAll(search, typeChoiceBox, cityText, zipText, nominalText)

        VBox treeFilters = new VBox()
        treeFilters.setPadding(new Insets(20, 0, 0, 10));
        treeFilters.getChildren().addAll(cityCB, typeCB, zipCB, nominalCB)

        VBox treeFiltersBottom = new VBox()
        treeFiltersBottom.setPadding(new Insets(30, 0, 0, 10));
        treeFiltersBottom.getChildren().addAll(cityFilterCB, typeFilterCB)


        TreeItem<String> rootItem =
            new TreeItem<String>("Plant Types");
        rootItem.setExpanded(true);

        rootItem.getChildren().add(allItem)

        tree.setEditable(true);
        tree.setRoot(rootItem);
        tree.setShowRoot(true);
        tree.setMaxHeight(200)

        TreeItem<String> rootItemCities =
            new TreeItem<String>("Cities");
        rootItemCities.setExpanded(true);
        rootItemCities.getChildren().add(allItem)
        treeCities.setEditable(true);
        treeCities.setRoot(rootItemCities);
        treeCities.setShowRoot(true);
        treeCities.setMaxHeight(200)

        VBox trees = new VBox()
        trees.setSpacing(5)
        separator.setMinWidth(trees.getTranslateX())
        VBox details = new VBox()
        details.setSpacing(5);
        details.setPadding(new Insets(10, 0, 0, 10));
        details.getChildren().addAll(cityLabelTextDetail, idLabelTextDetail, typeLabelTextDetail, nominalLabelTextDetail, zipLabelTextDetail,separator, trees)

        typeFilterCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                rootItem.getChildren().clear()
                rootItem.getChildren().add(allItem)
                observableListTypes.each {
                    final TreeItem<String> checkBoxTreeItem =
                        new TreeItem<String>(it.toString());
                    rootItem.getChildren().add(checkBoxTreeItem);
                }
                if (newValue) {
                    trees.getChildren().add(tree)
                }
                else trees.getChildren().remove(tree)
            }
        });
        cityFilterCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                rootItemCities.getChildren().clear()
                rootItemCities.getChildren().add(allItem)
                observableListCities.each {
                    final TreeItem<String> checkBoxTreeItem =
                        new TreeItem<String>(it.toString());
                    rootItemCities.getChildren().add(checkBoxTreeItem);
                }
                if (newValue) {
                    trees.getChildren().add(treeCities)
                }
                else trees.getChildren().remove(treeCities)
            }
        });

        tree.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TreeItem <String>>() {
                    public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                                        TreeItem<String> oldItem, TreeItem<String> newItem) {
                        selectionLabeltypes.setText(newItem.getValue());
                    }
                });
        treeCities.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TreeItem <String>>() {
                    public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                                        TreeItem<String> oldItem, TreeItem<String> newItem) {
                        selectionLabelcities.setText(newItem.getValue());
                    }
                });


        border2.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                border2.setVisible(false)
                border2.setDisable(true)
                timelineRight.play();
                columns.setVisible(false)

            }
        });

        border4.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                border4.setVisible(false)
                border4.setDisable(true)
                timelineRightFilters.play();
                filter.setVisible(false)


            }
        });

        border3.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                border4.setVisible(true)
                border4.setDisable(false)
                timelineLeftFilters.play();
                filter.setVisible(true)

            }
        });
        border.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                timelineLeft.play();
                border2.setVisible(true)
                border2.setDisable(false)
                columns.setVisible(true)

            }
        });

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                timelineRight.play();

            }
        });

        button2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                timelineLeft.play();

            }
        });

        setAnimation();
        setAnimationFilters();
        Pane all = new Pane();
        all.getChildren().addAll(details)

        filtersStack.getChildren().addAll( border, treeFilters)
        filtersStack.relocate(0, 290)

        columnsStack.getChildren().addAll( border3, treeFiltersBottom)
        columnsStack.relocate(0, 420)
        columns.relocate(-10,340)
        columns.setRotate(90)
        filter.relocate(-2,470)
        filter.setRotate(90)
        tableStack.getChildren().addAll(table,columns,filter, border2, border4, filtersStack, columnsStack)


        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(tableStack);
        borderPane.setRight(all);


        pane.getChildren().addAll(borderPane)
       return pane

    }


    private void setAnimation(){
        // Initially hiding the Top Pane
        clipRect = new Rectangle();
        clipRect.setWidth(30);
        clipRect.setHeight(boxBounds.getHeight());
        clipRect.translateXProperty().set(boxBounds.getWidth());
        filtersStack.setClip(clipRect);
        filtersStack.translateXProperty().set(-boxBounds.getWidth());

        // Animation for bouncing effect.
        final Timeline timelineBounce = new Timeline();
        timelineBounce.setCycleCount(2);
        timelineBounce.setAutoReverse(true);
        final KeyValue kv1 = new KeyValue(clipRect.widthProperty(), (boxBounds.getWidth()-15));
        final KeyValue kv2 = new KeyValue(clipRect.translateXProperty(), 15);
        final KeyValue kv3 = new KeyValue(filtersStack.translateXProperty(), -15);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(100), kv1, kv2, kv3);
        timelineBounce.getKeyFrames().add(kf1);

        // Event handler to call bouncing effect after the scroll down is finished.
        EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                timelineBounce.play();
            }
        };

        timelineRight = new Timeline();
        timelineLeft = new Timeline();

        // Animation for scroll down.
        timelineRight.setCycleCount(1);
        timelineRight.setAutoReverse(true);
        final KeyValue kvDwn1 = new KeyValue(clipRect.widthProperty(), boxBounds.getWidth());
        final KeyValue kvDwn2 = new KeyValue(clipRect.translateXProperty(), 0);
        final KeyValue kvDwn3 = new KeyValue(filtersStack.translateXProperty(), 0);
        final KeyFrame kfDwn = new KeyFrame(Duration.millis(200), onFinished, kvDwn1, kvDwn2, kvDwn3);
        timelineRight.getKeyFrames().add(kfDwn);

        // Animation for scroll up.
        timelineLeft.setCycleCount(1);
        timelineLeft.setAutoReverse(true);
        final KeyValue kvUp1 = new KeyValue(clipRect.widthProperty(), 0);
        final KeyValue kvUp2 = new KeyValue(clipRect.translateXProperty(), boxBounds.getWidth());
        final KeyValue kvUp3 = new KeyValue(filtersStack.translateXProperty(), -boxBounds.getWidth());
        final KeyFrame kfUp = new KeyFrame(Duration.millis(200), kvUp1, kvUp2, kvUp3);
        timelineLeft.getKeyFrames().add(kfUp);
    }


    private void setupBinding() {
        bind 'text' of zipText to ZIP of clientDolphin[FILTER]
        bind 'text' of selectionLabelcities to CITY of clientDolphin[FILTER]
        bind 'text' of selectionLabeltypes to PLANT_TYPE of clientDolphin[FILTER]
        bind 'text' of nominalText to NOMINAL_POWER of clientDolphin[FILTER]

        bind ZIP of clientDolphin[SELECTED_POWERPLANT] to 'text' of zipLabelDetail
        bind CITY of clientDolphin[SELECTED_POWERPLANT] to 'text' of cityLabelDetail
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

    private void setAnimationFilters(){
        // Initially hiding the Top Pane
        clipRect = new Rectangle();
        clipRect.setWidth(30);
        clipRect.setHeight(boxBounds.getHeight());
        clipRect.translateXProperty().set(boxBounds.getWidth());
        columnsStack.setClip(clipRect);
        columnsStack.translateXProperty().set(-boxBounds.getWidth());

        // Animation for bouncing effect.
        final Timeline timelineBounce = new Timeline();
        timelineBounce.setCycleCount(2);
        timelineBounce.setAutoReverse(true);
        final KeyValue kv1 = new KeyValue(clipRect.widthProperty(), (boxBounds.getWidth()-15));
        final KeyValue kv2 = new KeyValue(clipRect.translateXProperty(), 15);
        final KeyValue kv3 = new KeyValue(columnsStack.translateXProperty(), -15);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(100), kv1, kv2, kv3);
        timelineBounce.getKeyFrames().add(kf1);

        // Event handler to call bouncing effect after the scroll down is finished.
        EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                timelineBounce.play();
            }
        };

        timelineRightFilters = new Timeline();
        timelineLeftFilters = new Timeline();

        // Animation for scroll down.
        timelineRightFilters.setCycleCount(1);
        timelineRightFilters.setAutoReverse(true);
        final KeyValue kvDwn1 = new KeyValue(clipRect.widthProperty(), boxBounds.getWidth());
        final KeyValue kvDwn2 = new KeyValue(clipRect.translateXProperty(), 0);
        final KeyValue kvDwn3 = new KeyValue(columnsStack.translateXProperty(), 0);
        final KeyFrame kfDwn = new KeyFrame(Duration.millis(200), onFinished, kvDwn1, kvDwn2, kvDwn3);
        timelineRightFilters.getKeyFrames().add(kfDwn);

        // Animation for scroll up.
        timelineLeftFilters.setCycleCount(1);
        timelineLeftFilters.setAutoReverse(true);
        final KeyValue kvUp1 = new KeyValue(clipRect.widthProperty(), 0);
        final KeyValue kvUp2 = new KeyValue(clipRect.translateXProperty(), boxBounds.getWidth());
        final KeyValue kvUp3 = new KeyValue(columnsStack.translateXProperty(), -boxBounds.getWidth());
        final KeyFrame kfUp = new KeyFrame(Duration.millis(200), kvUp1, kvUp2, kvUp3);
        timelineLeftFilters.getKeyFrames().add(kfUp);
    }

    public static void bindAttribute(Attribute attribute, Closure closure) {
        final listener = closure as PropertyChangeListener
        attribute.addPropertyChangeListener('value', listener)
    }

//    public Integer getFirstCellIndex() {
//        return getTableViewInfo(table).getVirtualFlow().getFirstVisibleCellWithinViewPort().getIndex()
//    }
//
//    public Integer getLastCellIndex() {
//        return getTableViewInfo(table).getVirtualFlow().getLastVisibleCellWithinViewPort().getIndex()
//    }
}
