package com.noname.passengerflows;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.DatePicker;
import android.app.DatePickerDialog;

import java.util.Calendar;

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
    private EditText dateBegin;

    /**
     * Поле выбора даты окончания экспорта
     */
    private EditText dateEnd;

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
        dateBegin = (EditText) view.findViewById(R.id.dateBegin);
        dateBegin.setOnClickListener(this);
        dateEnd = (EditText) view.findViewById(R.id.dateEnd);
        dateEnd.setOnClickListener(this);
        exportClick = (Button) view.findViewById(R.id.exportData);
        exportClick.setOnClickListener(this);
        formatSelector = (RadioGroup) view.findViewById(R.id.radioGroup);
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
                Toast.makeText(getContext(), "Экспортировано", Toast.LENGTH_SHORT).show();
                break;
            case R.id.dateBegin:
                activeField = dateBegin;
                setDate();
                break;
            case R.id.dateEnd:
                activeField = dateEnd;
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
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

}
