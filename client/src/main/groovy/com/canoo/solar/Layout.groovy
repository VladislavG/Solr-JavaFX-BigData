package com.canoo.solar

import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Insets
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
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 * Created with IntelliJ IDEA.
 * User: vladislav
 * Date: 17.10.13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class Layout {
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


    static public Pane createTreePane(TreeItem root, TreeView tree, Button close, Pane pane, CheckBox checkBox, int treeNumber, GridPane grid){

        root.setExpanded(true);
        tree.setRoot(root);
        tree.setShowRoot(true);
        pane.setMaxHeight(500)
        tree.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = pane.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cc = new ClipboardContent();
                cc.putString(String.valueOf(treeNumber));
                db.setContent(cc);

                event.consume();
            }
        });

        tree.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean accept = false;
                if (db.hasString()) {
                    String data = db.getString();
                    try {
                        int draggedtreeNumber = Integer.parseInt(data);
                        if (draggedtreeNumber != treeNumber
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
        tree.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Pane draggedpane = (Pane) event.getGestureSource();
                // switch panes:
                int draggedX = GridPane.getColumnIndex(draggedpane);
                int draggedY = GridPane.getRowIndex(draggedpane);
                int droppedX = GridPane.getColumnIndex(pane);
                int droppedY = GridPane.getRowIndex(pane);
                int droppedS = GridPane.getRowSpan(pane);

                GridPane.setColumnIndex(draggedpane, droppedX);
                GridPane.setRowSpan(draggedpane, droppedS)
                GridPane.setRowIndex(draggedpane, droppedY + 1);

                GridPane.setColumnIndex(pane, droppedX)
                GridPane.setRowIndex(pane, droppedY)
                GridPane.setRowSpan(pane, 1);


                pane.setPrefHeight(pane.getHeight()/2)
                pane.getChildren().get(0).setPrefHeight(pane.getHeight()/2)
                draggedpane.setPrefHeight(pane.getHeight()/2)
                draggedpane.getChildren().get(0).setPrefHeight(pane.getHeight()/2)
            }
        });

//        ColumnConstraints column1 = new ColumnConstraints();
//        column1.setPercentWidth(50);
//        ColumnConstraints column2 = new ColumnConstraints();
//        column2.setPercentWidth(50);
//        grid.getColumnConstraints().addAll(column1, column2);

        pane.getChildren().addAll(tree, close)
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
