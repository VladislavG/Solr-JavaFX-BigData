package com.canoo.solar;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import static com.canoo.solar.Constants.FilterConstants.BATCH_SIZE;

public class PowerPlantList extends AbstractList<PowerPlant> {
    private final int size;
    private final OurConsumer<Integer> getAtConsumer;

    private final IntegerProperty fillSize = new SimpleIntegerProperty(0);

    private final Map<Integer, PowerPlant> people = new HashMap<>();

    private PowerPlantList(final int size, OurConsumer<Integer> getAtConsumer) {
        this.size = size;
        this.getAtConsumer = getAtConsumer;
    }

    @Override
    public PowerPlant get(final int index) {
        if (people.containsKey(index)) {
            return people.get(index);
        }
        int idx2 = index;
        int stopPoint = index + BATCH_SIZE;
        while(idx2 < stopPoint) {

            System.out.println("creating person for rowIdx " + index + ". size: " + people.size());
            PowerPlant powerPlant = new PowerPlant(index, -1, "not loaded" , "not loaded", "not loaded","not loaded","not loaded");
            people.put(index, powerPlant);
            fillSize.setValue(fillSize.getValue() + 1);
            idx2++;

        }

        getAtConsumer.accept(index);
        return people.get(index);
    }

    @Override
    public int size() {
        return size;
    }

    private IntegerProperty fillSizeProperty() {
        return fillSize;
    }
}
