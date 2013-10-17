package com.canoo.solar

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
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


    static public Pane createTreePane(TreeItem root, TreeView tree, Button close, Pane pane, CheckBox checkBox){

        root.setExpanded(true);
        tree.setRoot(root);
        tree.setShowRoot(true);
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
