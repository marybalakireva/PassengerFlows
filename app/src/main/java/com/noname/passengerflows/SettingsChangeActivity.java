package com.noname.passengerflows;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.DialogInterface.OnCancelListener;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Активити для изменения настроек.
 * Вызывается по нажатию кнопки "Изменить данные" во фрагменте SettingsFragment.
 * Позволяет пользователю модифицировать сохраненные настройки.
 */
public class SettingsChangeActivity extends AppCompatActivity {
    /**
     * Кнопка "Сохранить"
     */
    private Button saveDataButton;

    /**
     * Поле ввода имени
     */
    private EditText fullNameField;

    /**
     * Полве ввода номера
     */
    private EditText phoneNumberField;

    /**
     * Диалоговое окно для подтверждения сохранения настроек
     */
    private AlertDialog.Builder saveDialog;

    /**
     * Переопределяемый метод, вызывается при создании активити.
     * Устанавливает слушатель кнопок, вызывает метод построения диалогового окна buildSaveDialog().
     *
     * @param savedInstanceState сохраненное состояние экземпляра
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_change);

        saveDataButton = (Button) findViewById(R.id.logOut);
        fullNameField = (EditText) findViewById(R.id.fullNameEdit);
        phoneNumberField = (EditText) findViewById(R.id.phoneNumberEdit);
        fullNameField.setText(MainActivity.name);
        phoneNumberField.setText(MainActivity.phone);
        addOnButtonClickListener();
        buildSaveDialog();
    }

    /**
     * Слушатель нажатий на кнопки
     */
    private void addOnButtonClickListener() {
        saveDataButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Нажатие на кнопку "сохранить"
             * По нажатию на кнопку поля ввода проверяются на не пустоту.
             * В случае, когда хотя бы одно из полей ввода пустое - всплывает окошко с сообщением "Заполните все поля".
             * Если все поля заполнены - вызывается диалоговое окно для подтверждения сохранения настроек.
             * @param view View кнопки "сохранить"
             */
            @Override
            public void onClick(View view) {
                if ((fullNameField.getText().toString().isEmpty()) || (phoneNumberField.getText().toString().isEmpty())) {
                    Toast.makeText(SettingsChangeActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveDialog.show();
            }
        });
    }

    /**
     * Метод конструирует диалоговое окно для подтверждения сохранения настроек.
     * Сообщение - "Сохранить?".
     * Кнопки - "Да" и "Нет".
     * По нажатию на кнопку "Да" происходит сохранение настроек и завершение работы активити.
     * По нажатию на кнопку "Нет" активити продолжает работу без изменений настроек.
     * По нажатию на физическую кнопку устройства "Назад" активити продолжает работу без изменений настроек.
     */
    private void buildSaveDialog() {
        saveDialog = new AlertDialog.Builder(this);
        saveDialog.setMessage("Сохранить?"); // сообщение
        saveDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                saveSettings();
            }
        });
        saveDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(SettingsChangeActivity.this, "Отмена", Toast.LENGTH_LONG).show();
            }
        });
        saveDialog.setCancelable(true);
        saveDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(SettingsChangeActivity.this, "Вы ничего не выбрали", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Метод сохраняет данные о пользователе в общий файл настроек.
     */
    private void saveSettings() {
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("name");
        editor.remove("phone");
        editor.putString("name", fullNameField.getText().toString());
        editor.putString("phone", phoneNumberField.getText().toString());
        editor.apply();
        finish();
    }
}
