package com.canoo.solar

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TreeView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

/**
 * Created by vladislav on 16.12.13.
 */
class AutoFillTextField {
    static void makeAutofillTextField(Application application, TextField textField, List items, Label label, TreeView treeView, String propertyName) {

        StringBuilder sb;
        int lastLength;
        sb = new StringBuilder();

        textField.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            void handle(KeyEvent keyEvent) {
                if (lastLength != (textField.getText().length() - textField.getSelectedText().length()))
                    lastLength = textField.getText().length() - textField.getSelectedText().length();

                if (keyEvent.isControlDown() || keyEvent.getCode() == KeyCode.BACK_SPACE ||
                        keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.LEFT ||
                        keyEvent.getCode() == KeyCode.DELETE || keyEvent.getCode() == KeyCode.HOME ||
                        keyEvent.getCode() == KeyCode.END || keyEvent.getCode() == KeyCode.TAB ||
                        keyEvent.getCode() == KeyCode.SHIFT
                )
                    return;

                sb.delete(0, sb.length());
                sb.append(textField.getText());
                // remove selected string index until end so only unselected text will be recorded

                for (int i=0; i<items.size(); i++) {
                    if (items.get(i).toString().toLowerCase().startsWith(textField.getText().toLowerCase()))
                    {

                        try {
                            textField.setText(sb.toString() + items.get(i).toString().substring(sb.toString().length()));
                        } catch (Exception e) {
                            textField.setText(sb.toString());
                        }
                        textField.selectPositionCaret(sb.toString().length());
                        textField.selectRange(textField.getCaretPosition(), textField.length)
                        break;
                    }
                }

            }
        })

        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                  if (s2.equals("")){
                      label.setText("")
                      treeView.getSelectionModel().clearSelection()
                      application.disableControls.setValue(true)
                      UpdateActions.clearPmsAndPowerPlants()
                      application.clientDolphin[Constants.FilterConstants.STATE][Constants.FilterConstants.HOLD].setValue(true)
                      UpdateActions.refreshTable()

                      application.disableControls.setValue(false)
                  }
            }
        })

        textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (KeyCode.ENTER == event.getCode()) {
                    label.setText(textField.getText())
                    treeView.getSelectionModel().clearSelection()
                    application.disableControls.setValue(true)
                    UpdateActions.clearPmsAndPowerPlants()
                    application.clientDolphin[Constants.FilterConstants.STATE][Constants.FilterConstants.HOLD].setValue(true)
                    UpdateActions.refreshTable()
                    application.disableControls.setValue(false)
                }
            }
        });

        textField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if(mouseEvent.getClickCount() == 2){
                    textField.setVisible(false)
                    label.setText("")
                    textField.setText("")
                    UpdateActions.facetAddRemove(propertyName, textField, "add")
                }
            }
        });


    }
}
