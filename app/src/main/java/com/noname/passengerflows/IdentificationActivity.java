package com.noname.passengerflows;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Активити для идентификации пользователя.
 * Вызывается, если файл с данными о пользователе невозможно прочитать или пользователь вышел из аккаунта.
 */
public class IdentificationActivity extends AppCompatActivity {
    /**
     * Экземпляр класса ActionBar для взаимодействия с панелью в верхней части приложения
     */
    private ActionBar toolbar;

    /**
     * Кнопка "Начать работу"
     */
    private Button begin;

    /**
     * Поле ввода имени
     */
    private EditText fullName;

    /**
     * Поле ввода номера телефона
     */
    private EditText phone;

    /**
     * Переопределяемый метод, вызывается при создании активити.
     *
     * @param savedInstanceState сохраненное состояние экземпляра
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);

        begin = (Button) findViewById(R.id.beginButton);
        fullName = (EditText) findViewById(R.id.fullNameInput);
        phone = (EditText) findViewById(R.id.phoneInput);
        toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle("Идентификация");
        }
        addOnCLickButtonListener();
    }

    /**
     * Слушатель нажатий на кнопки     *
     */
    private void addOnCLickButtonListener() {
        begin.setOnClickListener(new View.OnClickListener() {
            /**
             * Нажатие на кнопку "начать работу"
             * По нажатию на кнопку вызывается метод saveSettings()
             * @param view View кнопки "начать работу"
             */
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });
    }

    /**
     * Метод проверяет поля ввода на не пустоту.
     * В случае, когда хотя бы одно из полей ввода пустое - всплывает окошко с сообщением "Заполните все поля".
     * Если все поля заполнены - метод сохраняет настройки пользователя в общий файл настроек.
     */
    private void saveSettings() {
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (!fullName.getText().toString().isEmpty() && !phone.getText().toString().isEmpty()) {
            editor.putString("name", fullName.getText().toString());
            editor.putString("phone", phone.getText().toString());
            editor.apply();
            finish();
        } else Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
    }

    /**
     * Переопределяемый метод, вызывается при нажатии клавиши назад.
     * Вызывает метод openQuitDialog().
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        openQuitDialog();
    }

    /**
     * Строит диалоговое окно и выводит его на экран.
     * Заголовок - "Выход: Вы уверены?".
     * Кнопки - "Да" и "Нет".
     * По нажатию на кнопку "Да" происходит завершение работы приложения.
     * По нажатию на кнопку "Нет" приложение продолжает работу.
     * По нажатию на физическую кнопку устройства "Назад" приложение продолжает работу.
     */
    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                finishAffinity();
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }
}
