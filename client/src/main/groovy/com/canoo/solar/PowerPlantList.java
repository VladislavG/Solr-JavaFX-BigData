package com.canoo.solar;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;

import static com.canoo.solar.Constants.FilterConstants.BATCH_SIZE;

public class PowerPlantList extends AbstractList<PowerPlant> {
    private final int size;
    private final OurConsumer<Integer> getAtConsumer;

    private final IntegerProperty fillSize = new SimpleIntegerProperty(0);

    private final Map<Integer, PowerPlant> powerPlantMap = new HashMap<>();

    private PowerPlantList(final int size, OurConsumer<Integer> getAtConsumer) {
        this.size = size;
        this.getAtConsumer = getAtConsumer;
    }

    @Override
    public PowerPlant get(final int index) {
        if (powerPlantMap.containsKey(index)) {
            return powerPlantMap.get(index);
        }
            PowerPlant powerPlant = new PowerPlant(index, -1, "not loaded" , "not loaded", "not loaded","not loaded","not loaded");
            powerPlantMap.put(index, powerPlant);
            fillSize.setValue(fillSize.getValue() + 1);

        getAtConsumer.accept(index);
        return powerPlantMap.get(index);
    }

    @Override
    public int size() {
        return size;
    }

    public void removePlant(Integer idx){
         powerPlantMap.remove(idx);
    }

    private IntegerProperty fillSizeProperty() {
        return fillSize;
    }
}
