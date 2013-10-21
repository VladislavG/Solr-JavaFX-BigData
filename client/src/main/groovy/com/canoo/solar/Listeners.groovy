package com.canoo.solar

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.*
import org.opendolphin.core.PresentationModel

public class Listeners {

    static public void setChoiceBoxListener(CheckBox cb, TableView table, TableColumn col, String propertyName, PresentationModel colOrder){
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    table.getColumns().add(col)
                    Application.addHeaderListener(table.getColumns().size()-1, propertyName)
                    def i=-1
                    colOrder.getAttributes().each {
                        if(it.value > -1){
                        i++
                            Application.addHeaderListener(i, it.getPropertyName())
                        }
                    }

                    colOrder.findAttributeByPropertyName(propertyName).setValue(i)
                }

                else {
                    table.getColumns().remove(col)

                    def order = colOrder.findAttributeByPropertyName(propertyName).getValue()
                    colOrder.findAttributeByPropertyName(propertyName).setValue(-1)

                    colOrder.getAttributes().each {
                        if(it.value > order){
                            it.setValue(it.getValue()-1)
                        }
                    }
                    colOrder.getAttributes().each {
                        if(it.value > -1){
                            Application.addHeaderListener(Integer.parseInt(it.getValue().toString()), it.getPropertyName())
                        }
                    }
                }
            }
        });
    }


}
