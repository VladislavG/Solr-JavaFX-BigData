package com.canoo.solar;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PowerPlant {
    private final int rowIndex;
    private int dbId;
    private LoadState loadState;
    private final StringProperty city;
    private final StringProperty type;
    private final StringProperty nominal;
    private final StringProperty zip;
    private final StringProperty position;
    private final StringProperty avgkwh;
    private final StringProperty gpslat;
    private final StringProperty gpslon;


    public PowerPlant(final int rowIndex, final int dbId,final String position, final String city, final String type, final String nominal, final String zip, String avgkwh, String gpslat, String gpslon) {
        this.rowIndex = rowIndex;
        this.dbId = dbId;
        this.city = new SimpleStringProperty(city);
        this.nominal = new SimpleStringProperty(nominal);
        this.zip = new SimpleStringProperty(zip);
        this.type = new SimpleStringProperty(type);
        this.position = new SimpleStringProperty(position);
        this.avgkwh = new SimpleStringProperty(avgkwh);
        this.gpslat = new SimpleStringProperty(gpslat);
        this.gpslon = new SimpleStringProperty(gpslon);
        loadState = LoadState.NOT_LOADED;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setAvgkwh(String avgkwh) {
        this.avgkwh.set(avgkwh);
    }

    public void setGpslat(String gpslat) {
        this.gpslat.set(gpslat);
    }

    public void setGpslon(String gpslon) {
        this.gpslon.set(gpslon);
    }

    public String getAvgkwh() {

        return avgkwh.get();
    }

    public StringProperty avgkwhProperty() {
        return avgkwh;
    }

    public String getGpslat() {
        return gpslat.get();
    }

    public StringProperty gpslatProperty() {
        return gpslat;
    }

    public String getGpslon() {
        return gpslon.get();
    }

    public StringProperty gpslonProperty() {
        return gpslon;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(final int dbId) {
        this.dbId = dbId;
    }

    public LoadState getLoadState() {
        return loadState;
    }

    public void setLoadState(final LoadState loadState) {
        this.loadState = loadState;
    }

    public String getTypeProperty() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public String getZipPropery() {
        return zip.get();
    }

    public StringProperty zipProperty() {
        return zip;
    }

    public String getNominalProperty() {
        return nominal.get();
    }

    public StringProperty nominalProperty() {
        return nominal;
    }

    public String getCityProperty() {
        return city.get();
    }

    public StringProperty cityProperty() {
        return city;
    }
    public String  getPositionProperty() {
        return position.get();
    }

    public StringProperty positionProperty() {
        return position;
    }
}

