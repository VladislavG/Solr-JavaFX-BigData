package com.canoo.solar

/**
 * Created with IntelliJ IDEA.
 * User: vladislav
 * Date: 16.09.13
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public class Constants {

   static class FilterConstants {

    public static final String ADD = "add"
    public static final String REMOVE = "remove"
    public static final String ID = "id"
    public static final String IDS = "ids"
    public static final String POSITION = "position"
    public static final String CITY = "city"
    public static final String ALL = "all"
    public static final String PLANT_TYPE = "plantType"
    public static final String ZIP = "zipCode"
    public static final String TABLE = "table"
    public static final String AVGKWH = "average_kWh";
    public static final String GPS_LAT = "latitude";
    public static final String GPS_LON = "longitude";
    public static final String NOMINAL_POWER = "nominalPower"
    public static final String FILTER = 'FILTER'
//    public static final String FILTER_NOT = 'FILTER_NOT'
//    public static final String TYPE_NOT = 'TYPE_NOT'
//    public static final String CITY_NOT = 'CITY_NOT'
//    public static final String ZIP_NOT = 'ZIP_NOT'
    public static final String CITY_AUTOFILL = "city_a"
    public static final String PLANT_TYPE_AUTOFILL = "plantType_a"
    public static final String ZIP_AUTOFILL = "zipCode_a"
    public static final String FILTER_AUTOFILL = 'FILTER_AUTOFILL'
    public static final String SELECTED_POWERPLANT = 'SELECTED_POWERPLANT'
    public static final String POWERPLANT = 'POWERPLANT'
    public static final String ORDER = 'ORDER'
    public static final String ORDER_COLUMN = 'ORDER_COLUMN'
    public static final String ZIP_COLUMN = 'ZIP_COLUMN'
    public static final String CITY_COLUMN = 'CITY_COLUMN'
    public static final String POSITION_COLUMN = 'POSITION_COLUMN'
    public static final String NOMINAL_COLUMN = 'NOMINAL_COLUMN'
    public static final String TYPE_COLUMN = 'TYPE_COLUMN'
    public static final String AVGKWH_COLUMN = 'AVGKWH_COLUMN'
    public static final String LAT_COLUMN = 'LAT_COLUMN'
    public static final String LON_COLUMN = 'LON_COLUMN'
    public static final String REVERSER_ORDER = 'REVERSE_ORDER'
    public static final String CHANGE_FROM = 'CHANGE_FROM'
    public static final String SORT = 'SORT'
    public static final String HOLD = 'HOLD'
    public static final String TOTAL_NOMINAL = 'totalNominal'
    public static final String SIZE = 'size'
    public static final String TRIGGER = 'TRIGGER'
    public static final String START_INDEX = 'START_INDEX'
    public static final String STATE = 'STATE'
    public static final String IGNORE = 'IGNORE'
    public static final Integer BATCH_SIZE = 1
    public static final String NUM_COUNT = "numCount"
}
    static class CMD {

    public static final String GET = "fullDataRequest"
    public static final String GET_ROW = "rowDataRequest"

}
}