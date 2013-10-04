package com.canoo.solar;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;

public class PowerPlantList extends AbstractList<PowerPlant> {
    private final int size;
    private final OurBiConsumer<Integer, PowerPlant> getAtConsumer;

    private final IntegerProperty fillSize = new SimpleIntegerProperty(0);

    private final Map<Integer, PowerPlant> people = new HashMap<>();

    private PowerPlantList(final int size, OurBiConsumer<Integer, PowerPlant> getAtConsumer) {
        this.size = size;
        this.getAtConsumer = getAtConsumer;
    }

    @Override
    public PowerPlant get(final int index) {
        if (people.containsKey(index)) {
            System.out.println("map hit for index = " + index);
            return people.get(index);
        }

        System.out.println("creating person for rowIdx " + index + ". size: " + people.size());
        PowerPlant powerPlant = new PowerPlant(index, -1, "not loaded" , "not loaded", "not loaded","not loaded","not loaded");
        people.put(index, powerPlant);
        fillSize.setValue(fillSize.getValue() + 1);

        getAtConsumer.accept(index, powerPlant);

        return powerPlant;
    }

    @Override
    public int size() {
        return size;
    }

    private IntegerProperty fillSizeProperty() {
        return fillSize;
    }
}
