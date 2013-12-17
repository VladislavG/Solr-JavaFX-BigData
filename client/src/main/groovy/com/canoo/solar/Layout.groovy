package com.canoo.solar

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.LinearGradientBuilder
import javafx.scene.paint.Stop
import javafx.scene.shape.Rectangle
import org.opendolphin.core.Attribute
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
    static SimpleDoubleProperty mousePosition = new SimpleDoubleProperty(0.0);

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


    static public Pane createTreePane(TreeItem root, TreeView tree, Button close, Pane pane, TextField comboBox, Attribute orderAtt, List bounds, List<Double> widths){

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
        Rectangle dragBorder = new Rectangle()

        dragBorder.setHeight(22)
        dragBorder.setWidth(200)
        dragBorder.setFill(linearGrad)
        dragBorder.setStroke(Color.BLACK)
        dragBorder.setStrokeWidth(0.5)
        dragBorder.setArcWidth(3)
        dragBorder.setArcHeight(3)
        VBox treeAndDrag = new VBox()
        Rectangle r = new Rectangle(200, 445)
        r.setMouseTransparent(true)
        r.setFill(linearGrad)
        r.setStroke(Color.BLACK)
        r.setStrokeWidth(0.5)
        r.setOpacity(0.2)
        root.setExpanded(true);
        tree.setRoot(root);
        tree.setShowRoot(true);
        tree.setMaxWidth(200)

        treeAndDrag.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = pane.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cc = new ClipboardContent();
                cc.putString(String.valueOf(treeAndDrag.getParent().toString()));
                db.setContent(cc);


                bounds.clear()

                widths.clear()
                pane.getParent().getParent().getChildren().each {


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
                pane.setDisable(true)
                println bounds
                db.setContent(cc);
                event.consume();
            }
        });


        pane.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            void handle(DragEvent t) {
                Pane draggedpane = (Pane) t.getGestureSource();
                draggedpane.setDisable(false)
//                VBox draggedBox = draggedpane.getParent()
//                HBox bigBox = draggedBox.getParent()
//                orderAtt.setValue(bigBox.getChildren().findIndexOf {it.equals(draggedBox)}+1)
            }
        })


//        pane.setOnDragOver(new EventHandler<DragEvent>() {
//            @Override
//            void handle(DragEvent t) {
//                if (pane.getChildren().contains(r))return;
//                int size = 445.div(pane.getParent().getChildren().size()*2)
//                r.setHeight(size)
//                r.setTranslateY(size)
//                pane.getChildren().add(r)
//            }
//        })
//        pane.setOnDragExited(new EventHandler<DragEvent>() {
//            @Override
//            void handle(DragEvent t) {
//                pane.getChildren().remove(r)
//            }
//        })

        treeAndDrag.getChildren().addAll(dragBorder, tree)
        pane.getChildren().addAll(treeAndDrag, close)
        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            void handle(MouseEvent t) {
                UpdateActions.facetAddRemove(orderAtt.getPropertyName(), comboBox, REMOVE)

            }
        })
        close.relocate(170,-7)
        close.setScaleX(0.8)
        close.setScaleY(0.6)
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
