package com.noname.passengerflows;

import android.provider.BaseColumns;

/**
 * Класс для работы С БД
 */
public final class DBContract {
    private DBContract() {
    }

    /**
     * Таблица tblType
     */
    public static final class tblType implements BaseColumns {
        public final static String TABLE_NAME = "tblType";

        public final static String _ID = "Type_Id";
        public final static String COLUMN_NAME = "Type_Name";
    }

    /**
     * Таблица tblRate
     */
    public static final class tblRate implements BaseColumns {
        public final static String TABLE_NAME = "tblRate";

        public final static String _ID = "Rate_Id";
        public final static String COLUMN_DESCRIPTION = "Rate_Description";
    }

    /**
     * Таблица tblStop
     */
    public static final class tblStop implements BaseColumns {
        public final static String TABLE_NAME = "tblStop";

        public final static String _ID = "Stop_Id";
        public final static String COLUMN_NAME = "Stop_Name";
        public final static String COLUMN_ADRESS = "Stop_Address";
    }

    /**
     * Таблица tblRoute
     */
    public static final class tblRoute implements BaseColumns {
        public final static String TABLE_NAME = "tblRoute";

        public final static String _ID = "Route_Id";
        public final static String COLUMN_NUMBER = "Route_Number";
        public final static String COLUMN_TYPE = "Route_Type";
    }

    /**
     * Таблица tblRoutesStops
     */
    public static final class tblRoutesStops implements BaseColumns {
        public final static String TABLE_NAME = "tblRoutesStops";

        public final static String _ID_ROUTE = "Route_Id";
        public final static String _ID_STOP = "Stop_Id";
    }

    /**
     * Таблица tblRoutesStops
     */
    public static final class tblStats implements BaseColumns {
        public final static String TABLE_NAME = "tblStats";

        public final static String _ID = "Stats_Id";
        public final static String COLUMN_NAME = "User_Name";
        public final static String COLUMN_PHONE = "User_Phone";
        public final static String COLUMN_TIME = "Stats_Time";
        public final static String COLUMN_STOP = "Stop_Id";
        public final static String COLUMN_ROUTE = "Route_Id";
        public final static String COLUMN_OUT = "Stats_Out";
        public final static String COLUMN_IN = "Stats_In";
        public final static String COLUMN_RATE = "Rate_Id";
    }
}