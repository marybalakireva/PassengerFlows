package com.noname.passengerflows;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Актвити для сбора данных.
 */
public class DataCollectionActivity extends AppCompatActivity {
    /**
     * Экземпляр класса ActionBar для взаимодействия с панелью в верхней части приложения
     */
    private ActionBar toolbar;

    /**
     * Кнопка назад
     */
    private Button goBack;

    /**
     * Поле выбора времени
     */
    private EditText time;

    /**
     * Поле выбора даты
     */
    private EditText date;

    /**
     * Кнопка определить время и дату
     */
    private Button detectAuto;

    /**
     * Выпадающий список с маршрутами
     */
    private Spinner route;

    /**
     * Поле ввода количества вышедших пассажиров
     */
    private EditText left;

    /**
     * Полве ввода вошедших пассажиров
     */
    private EditText entered;

    /**
     * Выпадающий список со степенями нагруженности
     */
    private Spinner congestion;

    /**
     * Описание степени нагруженности
     */
    private TextView description;

    /**
     * Кнопка сохранить
     */
    private Button save;

    /**
     * Объект типа Calendar. Необходим для получения даты и времени.
     */
    private Calendar dateAndTime = Calendar.getInstance();

    String[] congestionId = {"1", "2", "3", "4", "5", "6"};
    String[] congestionDesc = {
            "Занято не более 1/3 мест для сидения",
            "Занято от 1/3 до 2/3 мест для сидения",
            "Все места для сидения полностью заняты, стоящих пассажиров очень мало",
            "Все места для сидения заняты, стоящих людей достаточно много",
            "Все места для сидения заняты, много стоящих людей, но есть просветы между людьми",
            "Предельное наполнение салона, не видно просветов между людьми",
    };

    /**
     * Переопределяемый метод, вызывается при создании активити.
     * Загружает фрагмент выбора остановки.
     *
     * @param savedInstanceState сохраненное состояние экземпляра
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);

        toolbar = getSupportActionBar();
        toolbar.setTitle("Сбор данных");

        goBack = (Button) findViewById(R.id.goBackButton);
        time = (EditText) findViewById(R.id.timeSelector);
        date = (EditText) findViewById(R.id.dateSelector);
        detectAuto = (Button) findViewById(R.id.detectDateTime);
        route = (Spinner) findViewById(R.id.routeSelector);
        left = (EditText) findViewById(R.id.leftCount);
        entered = (EditText) findViewById(R.id.enterCount);
        congestion = (Spinner) findViewById(R.id.congestionSelector);
        description = (TextView) findViewById(R.id.congestionDescription);
        save = (Button) findViewById(R.id.saveButton);

        addOnButtonClickListener();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DataCollectionActivity.this, android.R.layout.simple_spinner_item, congestionId);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        congestion.setAdapter(adapter);
        congestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Обработчик нажатий на элементы списка
             * @param adapterView Объект Spinner, в котором произошло событие выбора элемента
             * @param view Объект View внутри Spinnera, который представляет выбранный элемент
             * @param i Индекс выбранного элемента в адаптере
             * @param l Идентификатор строки того элемента, который был выбран
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                description.setText(congestionDesc[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setInitialDate();
        setInitialTime();
    }

    /**
     * Слушатель нажатий на кнопки и поля ввода
     */
    private void addOnButtonClickListener() {
        goBack.setOnClickListener(new View.OnClickListener() {
            /**
             * Нажатие на кнопку назад
             * @param view View кнопки назад
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            /**
             * Нажатие на поле выбора времени
             * @param view View поля выбора времени
             */
            @Override
            public void onClick(View view) {
                setTime();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            /**
             * Нажатие на поле выбора даты
             * @param view View поля выбора даты
             */
            @Override
            public void onClick(View view) {
                setDate();
            }
        });
        detectAuto.setOnClickListener(new View.OnClickListener() {
            /**
             * Нажатие на кнопку автоопределения времени
             * @param view View кнопки автоопределения времени
             */
            @Override
            public void onClick(View view) {
                dateAndTime = Calendar.getInstance();
                time.setText(DateUtils.formatDateTime(DataCollectionActivity.this,
                        dateAndTime.getTimeInMillis(),
                        DateUtils.FORMAT_SHOW_TIME));
                date.setText(DateUtils.formatDateTime(DataCollectionActivity.this,
                        dateAndTime.getTimeInMillis(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            /**
             * Нажатие на кнопку сохранить
             * @param view View кнопки сохранить
             */
            @Override
            public void onClick(View view) {
                String tmp = "";
                tmp = tmp + time.getText().toString() + "\n";
                tmp = tmp + date.getText().toString() + "\n";
                tmp = tmp + left.getText().toString() + "\n";
                tmp = tmp + entered.getText().toString() + "\n";
                tmp = tmp + congestionDesc[congestion.getSelectedItemPosition()];
                Toast.makeText(DataCollectionActivity.this, tmp, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Вызывается по нажатию на поле выбора даты, отображает окно для выбора даты.
     */
    public void setDate() {
        new DatePickerDialog(DataCollectionActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    /**
     * Вызывается по нажатию на поле выбора времени, отображает окно для выбора времени.
     */
    public void setTime() {
        new TimePickerDialog(DataCollectionActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    /**
     * Выводит в поле даты установленную пользователем дату.
     */
    private void setInitialDate() {

        date.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    /**
     * Выводит в поле времени установленное пользователем время.
     */
    private void setInitialTime() {

        time.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME));
    }

    /**
     * Установка обраточика выбора даты
     */
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDate();
        }
    };

    /**
     * Установка обработчика выбора времени
     */
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialTime();
        }
    };
}
