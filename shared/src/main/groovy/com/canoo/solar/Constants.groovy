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

    public static final String ID = "id"
    public static final String CITY = "city"
    public static final String PLANT_TYPE = "plantType"
    public static final String ZIP = "zipCode"
    public static final String NOMINAL_POWER = "nominalPower"
    public static final String FILTER = 'FILTER'
    public static final String SELECTED_POWERPLANT = 'SELECTED_POWERPLANT'
    public static final String TRIGGER = 'TRIGGER'
    public static final String STATE = 'STATE'
}
    static class CMD {

    public static final String GET = "fullDataRequest"
    public static final String GET_FILTERED = "filteredDataRequest"

}
}