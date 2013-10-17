package com.canoo.solar
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.*

/**
 * Created with IntelliJ IDEA.
 * User: vladislav
 * Date: 17.10.13
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class Listeners {

    static public void ChoiceBoxListener(CheckBox cb, TableView table, TableColumn col){
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    table.getColumns().add(col)
                }
                else {
                    table.getColumns().remove(col)

                    def size = table.getColumns().size()-1
                    (0..size).each {
                        Application.addHeaderListener(it)
                    }
                }
            }
        });
    }


}
