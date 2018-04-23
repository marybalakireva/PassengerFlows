package com.noname.passengerflows;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;

/**
 * Главная активити. Вызывается при открытии приложения.
 * Позволяет пользователю перемещаться между тремя вкладками(фрагментами),
 * каждая из которых представляет свой функционал.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Экземпляр класса ActionBar для взаимодействия с панелью в верхней части приложения
     */
    private ActionBar toolbar;

    /**
     * Фрагмент выбора остановок
     */
    private Fragment StopSelector;

    /**
     * Фрагмент настроек
     */
    private Fragment Settings;

    /**
     * Фрагмент экспорта
     */
    private Fragment Export;

    /**
     * Имя пользователя
     */
    public static String name;

    /**
     * Номер телефона пользователя
     */
    public static String phone;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        /**
         * Осуществляет переключение между вкладками.
         * @param item выбранный элемент.
         * @return true в случае успешной загрузки, false иначе.
         */
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    toolbar.setTitle("Выбор остановки");
                    loadFragment(StopSelector);
                    return true;
                case R.id.navigation_dashboard:
                    toolbar.setTitle("Экспорт");
                    loadFragment(Export);
                    return true;
                case R.id.navigation_notifications:
                    toolbar.setTitle("Настройки");
                    loadFragment(Settings);
                    return true;
            }
            return false;
        }
    };

    /**
     * Конструктор класса.
     * Создает объекты фрагментов.
     */
    public MainActivity() {
        StopSelector = new StopSelectorFragment();
        Settings = new SettingsFragment();
        Export = new ExportFragment();
    }

    /**
     * Загружает фрагмент в контейнер разметки.
     *
     * @param fragment Фрагмент
     */
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        //transaction.addToBackStack(null); //Пока что без бэкстека
        transaction.commit();
    }

    /**
     * Переопределяемый метод, вызывается при создании активити.
     * Проверяет предоставлены ли права на запись в память, если нет - запрашивает разрешение у пользователя.
     * Если права есть - отрисовывает основной интерфейс приложения.
     *
     * @param savedInstanceState сохраненное состояние экземпляра
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasPermissions()) {
            onCreateContinue();
        } else {
            setContentView(R.layout.no_permission_storage);
            requestPerms();
        }
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
                finish();
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

    /**
     * Метод проверяет наличие данных о пользователе в общем файле настроек.
     * В случае их отсутствие метод открывает активити для идентификации.
     */
    private void checkSettings() {
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        name = settings.getString("name", "");
        phone = settings.getString("phone", "");
        if (name.isEmpty() || phone.isEmpty()) {
            Intent intent = new Intent(this, IdentificationActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Переопределяемый метод.
     * Вызывается при возобновлении активити.
     * Переносит данные о пользователе из файла настроек в переменные.
     */
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        name = settings.getString("name", "");
        phone = settings.getString("phone", "");
    }

    //--------------------------------------------------------------------------------------------------

    /**
     * Метод выводит основной интерфейс приложения и вызывает фрагмент выбора остановки.
     * Запускается если пользователь предоставил все необходимые права
     */
    protected void onCreateContinue() {
        setContentView(R.layout.activity_main);

        checkSettings();

        toolbar = getSupportActionBar();

        loadFragment(StopSelector);
        toolbar.setTitle("Выбор остановки");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    /**
     * Код запроса
     */
    private static final int PERMISSION_REQUEST_CODE = 123;

    /**
     * Метод обрабатывающий нажатие кнопки "OK"
     *
     * @param view View запускающий данный метод
     */
    public void onClickPermission(View view) {
        if (!hasPermissions()) {
            requestPerms();
        } else {
            onCreateContinue();
        }
    }

    /**
     * Проверяет есть ли права
     *
     * @return есть права или нет
     */
    private boolean hasPermissions() {
        //int res = 0;
        //Массив с необходимыми правами,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions) {
            int res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Запрос на получение прав
     */
    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Метод проверяющий предоставил ли пользователь права
     * Если нет, то выводит сообщение об ошибке
     *
     * @param requestCode  код запроса
     * @param permissions  список прав
     * @param grantResults результат получения прав
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults) {
                    // Пользователь предоставил право
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                // Пользователь не предоставил права
                allowed = false;
                break;
        }

        if (allowed) {
            //Пользорватель предоставил все права
            onCreateContinue();
        } else {
            //Проверка, появлялось ли окно выбора у пользователя
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showNoStoragePermissionSnackbar();
                }
            }
        }

    }


    /**
     * Выводит сообщение о необходимости предоставить права записи в память телефона через настройки
     * андройда
     */
    public void showNoStoragePermissionSnackbar() {
        Snackbar.make(MainActivity.this.findViewById(R.id.no_permission), R.string.go_to_settings, Snackbar.LENGTH_LONG)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();

                        Toast.makeText(getApplicationContext(),
                                R.string.go_to_settings,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }

    /**
     * Метод вызывающий окно настроек
     */
    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    /**
     * Метод обрабатывающий возвращение из окна настроек
     * если пользователь предоставил права, то приложение начнёт работать
     * иначе нет
     *
     * @param requestCode код запроса
     * @param resultCode  результат
     * @param data        intent откуда получен результат
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            onCreateContinue();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
