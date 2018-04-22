package com.noname.passengerflows;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import static android.content.Context.MODE_PRIVATE;


/**
 * Фрагмент для работы с настройками.
 * Располагается внутри MainActivity.
 * Позволяет пользователю просматрировать и редактировать настройки.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    /**
     * Необходимый конструктор фрагмента, сгенерированный Android Studio.
     */
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Кнопка "Изменить данные"
     */
    private Button changeDataButton;

    /**
     * Кнопка "Выйти из аккаунта"
     */
    private Button logOutButton;

    /**
     * Объект типа View. Необходим для прорисовки пользовательского интерфейса фрагмента
     */
    private View view;

    /**
     * Поле ввода имени(только для чтения)
     */
    private EditText fullNameField;

    /**
     * phoneNumberField Поле ввода номера телефона(только для чтения)
     */
    private EditText phoneNumberField;

    /**
     * Переопределяемый метод.
     * Система вызывает этот метод при первом отображении пользовательского интерфейса фрагмента на дисплее.
     *
     * @param inflater           Объект типа LayoutInflater, который может быть использован для "раздувания" интерфейса пользователя.
     * @param container          Родительский View, к которому будет прикреплен интерфейс фрагмента.
     * @param savedInstanceState сохраненное состояние экземпляра.
     * @return готовый объект view, к которому уже привязян xml файл с интерфейсом и слушателем нажатий на кнопки.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        changeDataButton = (Button) view.findViewById(R.id.changeData);
        logOutButton = (Button) view.findViewById(R.id.logOut);
        fullNameField = (EditText) view.findViewById(R.id.fullNameEdit);
        phoneNumberField = (EditText) view.findViewById(R.id.phoneNumberEdit);
        changeDataButton.setOnClickListener(this);
        logOutButton.setOnClickListener(this);
        return view;
    }

    /**
     * Переопределяемый метод.
     * Отвечает за обработку нажатий на кнопки.
     * При нажатии на кнопку "Изменить данные" открывается активити для изменения данных.
     * При нажатии на кнопку "Выйти из аккаунта" открывается активити для идентификации, а сохраненные данные удаляются.
     *
     * @param v Объект, на который нажал пользоваель(кнопка)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeData:
                Intent settings = new Intent(getContext(), SettingsChangeActivity.class);
                startActivity(settings);
                break;
            case R.id.logOut:
                SharedPreferences preferences = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("name");
                editor.remove("phone");
                editor.apply();
                Intent logout = new Intent(getContext(), IdentificationActivity.class);
                startActivity(logout);
                break;
        }
    }

    /**
     * Переопределяемый метод.
     * Вызывается, когда фрагмент вновь становится видимым.
     * Устанавливает в поля ввода сохраненные данные о пользователе
     */
    @Override
    public void onResume() {
        super.onResume();
        fullNameField.setText(MainActivity.name);
        phoneNumberField.setText(MainActivity.phone);
    }
}
