package com.noname.passengerflows;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Фрагмент для выбора остановки.
 * Располагается внутри MainActivity.
 * Позволяет пользователю выбирать остановку из списка или на карте.
 * По нажатию на кнопку "Далее" открывает активити для сбора данных.
 *
 * @// TODO: 29.03.2018 Добавить кнопку "Далее" для перехода на активити сбора данных и описать ее.
 */
public class StopSelectorFragment extends Fragment implements View.OnClickListener {
    /**
     * Необходимый конструктор фрагмента, сгенерированный Android Studio.
     */
    public StopSelectorFragment() {
        // Required empty public constructor
    }

    /**
     * Кнопка открыть карту.
     */
    private Button openMapButton;

    /**
     * Кнопка далее
     */
    private Button openDataCollection;

    /**
     * Объект типа View. Необходим для прорисовки пользовательского интерфейса фрагмента.
     */
    private View view;

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
        view = inflater.inflate(R.layout.fragment_stop_selector, container, false);
        openMapButton = (Button) view.findViewById(R.id.openMapButton);
        openMapButton.setOnClickListener(this);
        openDataCollection = (Button) view.findViewById(R.id.openDataCollection);
        openDataCollection.setOnClickListener(this);
        return view;
    }

    /**
     * Переопределяемый метод.
     * Отвечает за обработку нажатий на кнопки.
     * При нажатии на кнопку "Открыть карту" вызывается активити с картой.
     * При нажатии на кнопку "Далее" вызовется активити для сбора данных.
     *
     * @param v Объект, на который нажал пользоваель(кнопка)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openMapButton:
                Intent map = new Intent(getContext(), MapsActivity.class);
                //Тут реально получить разрешение, т.е. из фрагмента?
                startActivity(map);
                break;
            case R.id.openDataCollection:
                Intent data = new Intent(getContext(), DataCollectionActivity.class);
                startActivity(data);
                break;
        }
    }
}
