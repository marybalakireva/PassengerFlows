package com.noname.passengerflows;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Класс для работы БД
 */
public class DBDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = DBDbHelper.class.getSimpleName();

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME = "PassengersFlow.db";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Конструктор {@link DBDbHelper}.
     *
     * @param context Контекст приложения
     */
    public DBDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Вызывается при создании базы данных
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /* Строка для создания таблицы */
        String SQL_CREATE_tblType_TABLE = "CREATE TABLE " + DBContract.tblType.TABLE_NAME + " ("
                + DBContract.tblType._ID + " integer PRIMARY KEY AUTOINCREMENT DEFAULT 1,"
                + DBContract.tblType.COLUMN_NAME + " text NOT NULL);";

        /* Запускаем создание таблицы */
        db.execSQL(SQL_CREATE_tblType_TABLE);

        /* Строка для создания таблицы */
        String SQL_CREATE_tblRate_TABLE = "CREATE TABLE " + DBContract.tblRate.TABLE_NAME + " ("
                + DBContract.tblRate._ID + " integer NOT NULL PRIMARY KEY,"
                + DBContract.tblRate.COLUMN_DESCRIPTION + " text);";

        /* Запускаем создание таблицы */
        db.execSQL(SQL_CREATE_tblRate_TABLE);

        /* Строка для создания таблицы */
        String SQL_CREATE_tblStop_TABLE = "CREATE TABLE " + DBContract.tblStop.TABLE_NAME + " ("
                + DBContract.tblStop._ID + " integer PRIMARY KEY AUTOINCREMENT DEFAULT 1,"
                + DBContract.tblStop.COLUMN_NAME + " text NOT NULL,"
                + DBContract.tblStop.COLUMN_ADRESS + " text);";

        /* Запускаем создание таблицы */
        db.execSQL(SQL_CREATE_tblStop_TABLE);

        /* Строка для создания таблицы */
        String SQL_CREATE_tblRoute_TABLE = "CREATE TABLE " + DBContract.tblRoute.TABLE_NAME + " ("
                + DBContract.tblRoute._ID + " integer PRIMARY KEY AUTOINCREMENT DEFAULT 1,"
                + DBContract.tblRoute.COLUMN_NUMBER + " integer NOT NULL,"
                + DBContract.tblRoute.COLUMN_TYPE + " integer NOT NULL,"
                + "FOREIGN KEY(" + DBContract.tblRoute.COLUMN_TYPE + ") REFERENCES " + DBContract.tblType.TABLE_NAME + "(" +
                DBContract.tblType._ID + "));";
        /* Запускаем создание таблицы */
        db.execSQL(SQL_CREATE_tblRoute_TABLE);

        /* Строка для создания таблицы */
        String SQL_CREATE_tblRoutesStops_TABLE = "CREATE TABLE " + DBContract.tblRoutesStops.TABLE_NAME + " ("
                + DBContract.tblRoutesStops._ID_ROUTE + " integer,"
                + DBContract.tblRoutesStops._ID_STOP + " integer,"
                + "FOREIGN KEY(" + DBContract.tblRoutesStops._ID_STOP + ") REFERENCES " + DBContract.tblStop.TABLE_NAME + "(" +
                DBContract.tblStop._ID + "),"
                + "FOREIGN KEY(" + DBContract.tblRoutesStops._ID_ROUTE + ") REFERENCES " + DBContract.tblRoute.TABLE_NAME + "(" +
                DBContract.tblRoute._ID + "));";

        /* Запускаем создание таблицы */
        db.execSQL(SQL_CREATE_tblRoutesStops_TABLE);

        /* Строка для создания таблицы */
        String SQL_CREATE_tblStats_TABLE = "CREATE TABLE " + DBContract.tblStats.TABLE_NAME + " ("
                + DBContract.tblStats._ID + " integer PRIMARY KEY AUTOINCREMENT DEFAULT 1,"
                + DBContract.tblStats.COLUMN_NAME + " text NOT NULL,"
                + DBContract.tblStats.COLUMN_PHONE + " text NOT NULL,"
                + DBContract.tblStats.COLUMN_TIME + " text NOT NULL,"
                + DBContract.tblStats.COLUMN_STOP + " integer NOT NULL,"
                + DBContract.tblStats.COLUMN_ROUTE + " integer NOT NULL,"
                + DBContract.tblStats.COLUMN_OUT + " integer NOT NULL,"
                + DBContract.tblStats.COLUMN_IN + " integer NOT NULL,"
                + DBContract.tblStats.COLUMN_RATE + " integer NOT NULL,"
                + "FOREIGN KEY(" + DBContract.tblStats.COLUMN_STOP + ") REFERENCES " + DBContract.tblStop.TABLE_NAME + "(" +
                DBContract.tblStop._ID + "),"
                + "FOREIGN KEY(" + DBContract.tblStats.COLUMN_RATE + ") REFERENCES " + DBContract.tblRate.TABLE_NAME + "(" +
                DBContract.tblRate._ID + "),"
                + "FOREIGN KEY(" + DBContract.tblStats.COLUMN_ROUTE + ") REFERENCES " + DBContract.tblRoute.TABLE_NAME + "(" +
                DBContract.tblRoute._ID + "));";

        /* Запускаем создание таблицы */
        db.execSQL(SQL_CREATE_tblStats_TABLE);

        /* Заполнение таблицы tblRate */
        String SQL_INSERT_INTO_tblRate = "INSERT INTO " + DBContract.tblRate.TABLE_NAME + " VALUES (1, 'занято не более 1/3 мест для сидения')," +
                " (2, 'занято от 1/3 до 2/3 мест для сидения')," +
                " (3, 'все места для сидения полностью заняты, стоящих пассажиров очень мало')," +
                " (4, 'все места для сидения заняты, стоящих людей достаточно много')," +
                " (5, 'все места для сидения заняты, много стоящих людей, но есть просветы между людьми')," +
                " (6, 'предельное наполнение салона, не видно просветов между людьми, посадка пассажиров невозможна или возможна в малом количестве')" +
                ";";

        db.execSQL(SQL_INSERT_INTO_tblRate);
    }

    /**
     * Вызывается при обновлении схемы базы данных
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
