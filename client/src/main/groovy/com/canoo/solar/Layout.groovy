package com.canoo.solar

import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.Image
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
import javafx.scene.shape.Rectangle
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
    static public Rectangle createCBsBorder(){
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


    static public Pane createTreePane(TreeItem root, TreeView tree, Button close, Pane pane, CheckBox checkBox, Attribute orderAtt, HBox grid){
        Rectangle dragBorder = new Rectangle()
        dragBorder.setHeight(30)
        dragBorder.setWidth(250)
        dragBorder.setFill(Color.WHITESMOKE)
        dragBorder.setStroke(Color.INDIANRED)
        dragBorder.setStrokeWidth(1)
        VBox treeAndDrag = new VBox()
        root.setExpanded(true);
        tree.setRoot(root);
        tree.setShowRoot(true);
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

        treeAndDrag.getChildren().addAll(dragBorder, tree)
        pane.getChildren().addAll(treeAndDrag, close)
        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            void handle(MouseEvent t) {
                checkBox.setSelected(false)
            }
        })
        close.relocate(200,0)
        return pane;
    }

    static public HBox createPair(TextField detail, Label detailTooltip){

        HBox hBox = new HBox()
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(10, 0, 0, 10));
        hBox.getChildren().addAll(detailTooltip, detail)
        return hBox

    }




}
