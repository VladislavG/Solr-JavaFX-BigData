package com.canoo.solar

import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.SnapshotParameters
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.LinearGradientBuilder
import javafx.scene.paint.Stop
import javafx.scene.shape.Rectangle
import np.com.ngopal.control.AutoFillTextBox
import org.opendolphin.core.Attribute
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientDolphin
import static com.canoo.solar.Constants.FilterConstants.*

/**
 * Created with IntelliJ IDEA.
 * User: vladislav
 * Date: 17.10.13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class Layout {
    static ClientDolphin clientDolphin;


    static public Rectangle createEventBorder(){

        Rectangle eventBorder = new Rectangle()
        eventBorder.setStroke(Color.ALICEBLUE)
        eventBorder.setStrokeWidth(2)
        eventBorder.setWidth(20)
        eventBorder.setHeight(100)
        eventBorder.setFill(Color.WHITESMOKE)
        eventBorder.setArcWidth(10)
        eventBorder.setArcHeight(10)
        eventBorder.setOpacity(0.5)
        return eventBorder
    }
    static public Rectangle createCBsBorder(){
        Rectangle border = new Rectangle()
        border.setStroke(Color.ALICEBLUE)
        border.setStrokeWidth(2)
        border.setWidth(170)
        border.setHeight(100)
        border.setFill(Color.WHITESMOKE)
        border.setArcWidth(10)
        border.setArcHeight(10)
        border.setOpacity(0.8)
        return border
    }


    static public Pane createTreePane(TreeItem root, TreeView tree, Button close, Pane pane, TextField comboBox, Attribute orderAtt){

        Rectangle dragBorder = new Rectangle()

        dragBorder.setHeight(22)
        dragBorder.setWidth(200)
        LinearGradient linearGrad = LinearGradientBuilder.create()
                .startX(0)
                .startY(0)
                .endX(0)
                .endY(22)
                .proportional(false)
                .cycleMethod(CycleMethod.NO_CYCLE)
                .stops( new Stop(0.1f, Color.rgb(245, 245, 245, 1)),
                new Stop(1.0f, Color.rgb(179, 179, 179, 1)))
                .build();
        Rectangle r = new Rectangle(200, 445)
        r.setMouseTransparent(true)
        r.setFill(linearGrad)
        r.setStroke(Color.BLACK)
        r.setStrokeWidth(0.5)
        r.setOpacity(0.2)
        dragBorder.setFill(linearGrad)
        dragBorder.setStroke(Color.BLACK)
        dragBorder.setStrokeWidth(0.5)
        dragBorder.setArcWidth(3)
        dragBorder.setArcHeight(3)
        VBox treeAndDrag = new VBox()
        root.setExpanded(true);
        tree.setRoot(root);
        tree.setShowRoot(true);
        tree.setMaxWidth(200)
//        pane.setMaxHeight(500)


        treeAndDrag.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = pane.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cc = new ClipboardContent();
                cc.putString(String.valueOf(treeAndDrag.getParent().toString()));
                db.setContent(cc);
                event.consume();
            }
        });

        pane.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            void handle(DragEvent t) {
                Pane draggedpane = (Pane) t.getGestureSource();
                VBox draggedBox = draggedpane.getParent()
                HBox bigBox = draggedBox.getParent()
                orderAtt.setValue(bigBox.getChildren().findIndexOf {it.equals(draggedBox)}+1)
            }
        })

        pane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            void handle(DragEvent t) {
                if (pane.getChildren().contains(r))return;
                int size = 445.div(pane.getParent().getChildren().size()*2)
                r.setHeight(size)
                println size
                r.setTranslateY(size)
                pane.getChildren().add(r)
            }
        })
        pane.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            void handle(DragEvent t) {
                pane.getChildren().remove(r)
            }
        })

        treeAndDrag.getChildren().addAll(dragBorder, tree)
        pane.getChildren().addAll(treeAndDrag, close)
        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            void handle(MouseEvent t) {
                Application.facetAddRemove(orderAtt.getPropertyName(), comboBox, REMOVE)

            }
        })
        close.relocate(170,-7)
        return pane;
    }

    static public HBox createPair(TextField detail, Label detailTooltip){

        HBox hBox = new HBox()
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.getChildren().addAll(detailTooltip, detail)
        return hBox

    }




}
