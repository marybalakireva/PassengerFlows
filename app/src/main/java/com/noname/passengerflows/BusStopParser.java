package com.noname.passengerflows;

import android.content.Context;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

/**
 * Используется для хранения списка остановок
 * и импорта его из xml файла.
 *
 * @// TODO: 30.03.2018 Класс должен записывать данные сразу в БД, а не в какие-либо другие структуры данных.
 * @// TODO: 30.03.2018 Создавать БД каждый раз с нуля или реализовывать проверку актуальности xml файлов с данными?
 */
public class BusStopParser {

    /**
     * Используется для хранения данных об одной остановке
     */
    public static class busStop {
        /**
         * Широта
         */
        double latitude;
        /**
         * Долгота
         */
        double longitude;
        /**
         * Название остановки
         */
        String stopName;

        /**
         * Конструктор класса
         *
         * @param latitude  Широта
         * @param longitude Долгота
         * @param stopName  Название остановки
         */
        busStop(double latitude, double longitude, String stopName) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.stopName = stopName;
        }
    }

    /**
     * Список остановок
     */
    public static ArrayList<busStop> list = new ArrayList<busStop>();

    /**
     * Обновляет список остановок {@link BusStopParser#list},
     * заполняя его данными из файла list.xml используя XmlPullParser.
     *
     * @param context - контекст для получения доступа к файлам
     */
    public static void upload(Context context) {

        list.clear();

        try {
            XmlPullParser parser = context.getResources().getXml(R.xml.list);

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG
                        && parser.getName().equals("stop")) {
                    list.add(new busStop(
                            Double.parseDouble(parser.getAttributeValue(0)),
                            Double.parseDouble(parser.getAttributeValue(1)),
                            parser.getAttributeValue(2)
                    ));
                }
                parser.next();
            }
        } catch (Throwable t) {
            Toast.makeText(context, "Произошла ошибка при обновлении списка остановок", Toast.LENGTH_SHORT).show();
        }
    }

}
