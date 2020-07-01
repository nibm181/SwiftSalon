package lk.nibm.swiftsalon.util;

public class Constants {

    public static final String BASE_URL = "http://10.0.2.2/API/swiftsalon-api/api/";
    public static final int NETWORK_TIMEOUT = 3000;

    public static final int CONNECTION_TIMEOUT = 10; //10 seconds
    public static final int READ_TIMEOUT = 2;
    public static final int WRITE_TIMEOUT = 2;

    public static final int REFRESH_TIME = 60; //1 minute in second

    //for appointment viewHolders
    public static final String NORMAL_APPOINTMENT = "normal";
    public static final String NEW_APPOINTMENT = "new";
    public static final String SCHEDULED_APPOINTMENT = "scheduled";

    public static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static final String MOBILE_NUMBER_REGEX = "^\\d{10}$|^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$|^\\+\\d{11}$";
}
