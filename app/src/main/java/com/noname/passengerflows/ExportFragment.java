package com.noname.passengerflows;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Фрагмент для экспорта данных.
 * Располагается внутри MainActivity.
 * Позволяет пользователю выбирать формат файла и временной промежуток для экспорта.
 */
public class ExportFragment extends Fragment implements View.OnClickListener {
    /**
     * Необходимый конструктор фрагмента, сгенерированный Android Studio.
     */
    public ExportFragment() {
        // Required empty public constructor
    }

    /**
     * Поле выбора даты начала экспорта
     */
    private EditText dateBeginField;

    /**
     * Поле выбора даты окончания экспорта
     */
    private EditText dateEndField;

    /**
     * Активное поле выбора даты, с которым пользователь работает в данный момент
     */
    private EditText activeField;

    /**
     * Кнопка "Экспортировать"
     */
    private Button exportClick;

    /**
     * Группа переключателей
     */
    private RadioGroup formatSelector;

    /**
     * Объект типа View. Необходим для прорисовки пользовательского интерфейса фрагмента.
     */
    private View view;

    /**
     * Объект типа Calendar. Необходим для получения даты.
     */
    private Calendar dateAndTime = Calendar.getInstance();

    File file;

    class Control {
        private Control(String time, String route, int in, int out, int rate) {
            this.time = time;
            this.route = route;
            this.countIn = in;
            this.countOut = out;
            this.rate = rate;
        }

        private String time;
        private String route;
        private Integer countIn;
        private Integer countOut;
        private Integer rate;
    }

    /**
     * Переопределяемый метод.
     * Система вызывает этот метод при первом отображении пользовательского интерфейса фрагмента на дисплее.
     *
     * @param inflater           Объект типа LayoutInflater, который может быть использован для "раздувания" интерфейса пользователя.
     * @param container          Родительский View, к которому будет прикреплен интерфейс фрагмента.
     * @param savedInstanceState сохраненное состояние экземпляра.
     * @return готовый объект view, к которому уже привязян xml файл с интерфейсом и слушателем нажатий на кнопки, переключатель и поля ввода.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_export, container, false);
        dateBeginField = (EditText) view.findViewById(R.id.dateBegin);
        dateBeginField.setOnClickListener(this);
        dateEndField = (EditText) view.findViewById(R.id.dateEnd);
        dateEndField.setOnClickListener(this);
        exportClick = (Button) view.findViewById(R.id.exportData);
        exportClick.setOnClickListener(this);
        formatSelector = (RadioGroup) view.findViewById(R.id.radioGroup);
        formatSelector.check(R.id.jsonButton);
        formatSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.csvButton:
                        Toast.makeText(getContext(), "csv выбран", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.jsonButton:
                        Toast.makeText(getContext(), "json выбран", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }

    /**
     * Переопределяемый метод.
     * Отвечает за обработку нажатий на кнопки и на поля выбора даты.
     * При нажатии на кнопку "Экспортировать" данные экспортируются в заданном формате.
     * При нажатии на поля ввода появляется календарь, в котором можно выбрать дату.
     *
     * @param v Объект, на который нажал пользоваель(кнопка или поле выбора даты)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exportData:
                Date dateFrom = null, dateTo = null;
                String dtStart = dateBeginField.getText().toString();
                String dtEnd = dateEndField.getText().toString();

                SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy");

                try {
                    dateFrom = format.parse(dtStart);
                    dateTo = format.parse(dtEnd);
                } catch (ParseException e) {
                    e.printStackTrace();
                    break;
                }

                if (dateBeginField.getText().toString().isEmpty() || dateEndField.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Установите временной промежуок для экспорта", Toast.LENGTH_SHORT).show();
                    break;
                } else if (dateTo.compareTo(dateFrom) < 0) {
                    Toast.makeText(getContext(), "Путешествуем во времени?", Toast.LENGTH_SHORT).show();
                    break;
                } else {

                    try {
                        DBDbHelper mDbHelper = new DBDbHelper(getContext());
                        SQLiteDatabase db = mDbHelper.getReadableDatabase();

                        SimpleDateFormat format_1 = new SimpleDateFormat();
                        format_1.applyPattern("dd.MM.yyyy");
                        Date docDate = format_1.parse(dateBeginField.getText().toString());
                        Date docDate_1 = format_1.parse(dateEndField.getText().toString());

                        Log.d("-----", "++++++");

                        long date_from = docDate.getTime();
                        Log.d("date", Long.toString(date_from));
                        long date_to = docDate_1.getTime();
                        Log.d("date", Long.toString(date_to));

                        String query = "SELECT " +
                                DBContract.tblStats.TABLE_NAME + "." + DBContract.tblStats.COLUMN_DATE + ", " +
                                DBContract.tblStop.TABLE_NAME + "." + DBContract.tblStop.COLUMN_NAME + ", " +
                                DBContract.tblStats.TABLE_NAME + "." + DBContract.tblStats.COLUMN_TIME + ", " +
                                DBContract.tblRoute.TABLE_NAME + "." + DBContract.tblRoute.COLUMN_NUMBER + ", " +
                                DBContract.tblType.TABLE_NAME + "." + DBContract.tblType.COLUMN_NAME + ", " +
                                DBContract.tblStats.TABLE_NAME + "." + DBContract.tblStats.COLUMN_IN + ", " +
                                DBContract.tblStats.TABLE_NAME + "." + DBContract.tblStats.COLUMN_OUT + ", " +
                                DBContract.tblStats.TABLE_NAME + "." + DBContract.tblStats.COLUMN_RATE + " " +
                                "FROM " + DBContract.tblStop.TABLE_NAME + ", " + DBContract.tblRoute.TABLE_NAME + ", " + DBContract.tblType.TABLE_NAME + ", " + DBContract.tblStats.TABLE_NAME +
                                " WHERE (" + DBContract.tblStats.TABLE_NAME + "." + DBContract.tblStats.COLUMN_ROUTE + " = " + DBContract.tblRoute.TABLE_NAME + "." + DBContract.tblRoute._ID + ") AND (" +
                                DBContract.tblStats.TABLE_NAME + "." + DBContract.tblStats.COLUMN_STOP + " = " + DBContract.tblStop.TABLE_NAME + "." + DBContract.tblStop._ID + ") AND (" +
                                DBContract.tblRoute.TABLE_NAME + "." + DBContract.tblRoute.COLUMN_TYPE + " = " + DBContract.tblType.TABLE_NAME + "." + DBContract.tblType._ID + ") AND (" +
                                DBContract.tblStats.TABLE_NAME + "." + DBContract.tblStats.COLUMN_DATE + " BETWEEN " + date_from + " AND " + date_to + ")" +
                                "ORDER BY " + DBContract.tblStats.TABLE_NAME + "." + DBContract.tblStats.COLUMN_DATE + " ASC, " +
                                DBContract.tblStop.TABLE_NAME + "." + DBContract.tblStop.COLUMN_NAME + " ASC";

                        Log.d("SQL", query);
                        Cursor Cursor = db.rawQuery(query, null);

                        if (Cursor.getCount() == 0) {
                            Toast.makeText(getContext(), "Данные по заданному промежутку отсутствуют" + Cursor.getCount(), Toast.LENGTH_SHORT).show();
                        } else {
                            RadioButton json = view.findViewById(R.id.jsonButton);
                            RadioButton csv = view.findViewById(R.id.csvButton);
                            String filename = dateBeginField.getText().toString() + "-" + dateEndField.getText().toString();
                            if (csv.isChecked()) {
                                if (!createFile(filename, "csv")) {
                                    return;
                                }
                            } else if (json.isChecked()) {
                                if (!createFile(filename, "json")) {
                                    return;
                                }
                            }


                            ArrayList<Control> exportData = new ArrayList<>();

                            // Создаем Шаблон вывода Даты
                            DateFormat TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd");
                            String previousDate = null;
                            String previousStop = null;
                            while (Cursor.moveToNext()) {
                                // Необходимо добавить условие на выбранный формат
                                // Реализовано только для формата CSV - присваивание из выборки вероятно пригодится и для json
                                long date = Cursor
                                        .getLong(Cursor.getColumnIndex(DBContract.tblStats.COLUMN_DATE));

                                String DATE = TIMESTAMP.format(date);

                                String time = Cursor
                                        .getString(Cursor.getColumnIndex(DBContract.tblStats.COLUMN_TIME));

                                String stop = Cursor
                                        .getString(Cursor.getColumnIndex(DBContract.tblStop.COLUMN_NAME));

                                String route = Cursor
                                        .getString(Cursor.getColumnIndex(DBContract.tblRoute.COLUMN_NUMBER)) + " " +
                                        Cursor
                                                .getString(Cursor.getColumnIndex(DBContract.tblType.COLUMN_NAME));

                                int in = Cursor
                                        .getInt(Cursor.getColumnIndex(DBContract.tblStats.COLUMN_IN));

                                int out = Cursor
                                        .getInt(Cursor.getColumnIndex(DBContract.tblStats.COLUMN_OUT));

                                int rate = Cursor
                                        .getInt(Cursor.getColumnIndex(DBContract.tblStats.COLUMN_RATE));


                                String final_str_csv = "\"" + MainActivity.name + "\"; " + "\"" + MainActivity.phone + "\"; " + "\"" + DATE + "\"; " + "\"" + time + "\"; " + "\"" + stop + "\"; " + "\"" + route + "\"; " + in + "; " + out + "; " + rate;
                                Log.d("DATE", final_str_csv);
                                Log.d("INT_DATE", Long.toString(date));

                                if (csv.isChecked()) {
                                    csvExport(DATE, time, stop, route, in, out, rate);
                                } else if (json.isChecked()) {
                                    if (Cursor.getPosition() == 0) {
                                        previousDate = DATE;
                                        previousStop = stop;
                                        exportData.add(new Control(time, route, in, out, rate));
                                    } else if (Cursor.getPosition() == Cursor.getCount() - 1) {
                                        exportData.add(new Control(time, route, in, out, rate));
                                        jsonExport(previousDate, previousStop, exportData);
                                    } else {
                                        if (!Objects.equals(previousDate, DATE) || !Objects.equals(previousStop, stop)) {
                                            jsonExport(previousDate, previousStop, exportData);
                                            exportData.clear();
                                            previousDate = DATE;
                                            previousStop = stop;
                                            exportData.add(new Control(time, route, in, out, rate));
                                        } else {
                                            exportData.add(new Control(time, route, in, out, rate));
                                        }
                                    }
                                }

                            }

                            Toast.makeText(getContext(), "Выгрузка данных прошла успешно" + Cursor.getCount(), Toast.LENGTH_SHORT).show();
                        }

                        Cursor.close();
                        break;

                    } catch (ParseException e) {
                        Toast.makeText(getContext(), "В ходе выгрузки данных произошла ошибка!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            case R.id.dateBegin:
                activeField = dateBeginField;
                setDate();
                break;
            case R.id.dateEnd:
                activeField = dateEndField;
                setDate();
                break;
        }
    }

    /**
     * Обработчик выбора даты, который изменяет дату в текстовом поле.
     */
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    /**
     * Вызывается по нажатию на поле выбора даты, отображает окно для выбора даты.
     */
    private void setDate() {
        new DatePickerDialog(getContext(), listener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    /**
     * Выводит в выбранное поле установленную пользователем дату.
     */
    private void setInitialDateTime() {

        activeField.setText(DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    private void csvExport(String date, String time, String stop, String route, int countIn, int countOut, int rate) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file, true);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
            String result = MainActivity.name + "; " + MainActivity.phone + "; " + String.valueOf(date) + "; " + time + "; " + stop + "; " + route + "; " +
                    String.valueOf(countIn) + "; " + String.valueOf(countOut) + "; " + String.valueOf(rate);

            writer.append(result);
            writer.append("\n");
            writer.close();
            outputStream.close();
        } catch (IOException ex) {

            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void jsonExport(String date, String stop, ArrayList<Control> exportData) {
        try {

            FileOutputStream outputStream = new FileOutputStream(file, true);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
            JsonWriter jsonWriter = new JsonWriter(writer);

            jsonWriter.setIndent(" ");
            jsonWriter.beginObject();
            jsonWriter.name("name").value(MainActivity.name);
            jsonWriter.name("phone").value(MainActivity.phone);
            jsonWriter.name("date").value(date);
            jsonWriter.name("stop").value(stop);
            jsonWriter.name("control");
            jsonWriter.beginArray();
            for (int i = 0; i < exportData.size(); i++) {
                jsonWriter.beginObject();
                jsonWriter.name("time").value(exportData.get(i).time);
                jsonWriter.name("route").value(exportData.get(i).route);
                jsonWriter.name("countIn").value(exportData.get(i).countIn);
                jsonWriter.name("countOut").value(exportData.get(i).countOut);
                jsonWriter.name("rate").value(exportData.get(i).rate);
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.endObject();

            jsonWriter.close();
            writer.close();
            outputStream.close();
        } catch (IOException ex) {

            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean createFile(String filename, String extension) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/PassengerFlows/");
            folder.mkdir();
            file = new File(Environment.getExternalStorageDirectory().toString() + "/PassengerFlows/" + filename + "." + extension);
            if (file.exists()) {
                if (!file.delete()) {
                    Toast.makeText(getContext(), "Файл с данным именем уже существует и удалить его невозможно", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (!file.createNewFile()) {
                    Toast.makeText(getContext(), "Невозможно создать файл с данными", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return true;
                }
            } else {
                if (!file.createNewFile()) {
                    Toast.makeText(getContext(), "Невозможно создать файл с данными", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return true;
                }
            }
        } catch (IOException ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}