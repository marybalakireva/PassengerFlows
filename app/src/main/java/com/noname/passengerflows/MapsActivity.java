package com.noname.passengerflows;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

import java.util.Date;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

/**
 * Активити с картой.
 * Позволяет работать с Google Maps API.
 * Используется для выбора остановки пользователем.
 * Выбрать остановку можно как вручную, так и определить ближайшую остановку с помощью геолокации.
 */
public class MapsActivity extends FragmentActivity implements
        OnMarkerClickListener,
        OnMapReadyCallback {

    /**
     * Объект типа GoogleMap
     */
    private GoogleMap mMap;

    /**
     * Контект данной активити
     */
    Context mapContext = MapsActivity.this;

    private LocationManager locationManager;
    private LocationListener locationListener;

    /**
     * Переопределяемый метод, вызывается при создании активити.
     * Проверяет права и выводит карту на экран смартфона.
     *
     * @param savedInstanceState сохраненное состояние экземпляра.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!hasPermissions()) {
            requestPerms();
        } else {
            onCreateContinue();
        }
    }

    /**
     * Метод получает дескриптор объекта GoogleMap
     *
     * @param map Экземпляр GoogleMap
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    //getNearestStop();
                    return false;
                }
            });
        } else {
            // Show rationale and request permission.
        }

        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(61.7, 34.3), 10));

        setUpClusterer();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /*
        По факту для получения координат достаточно лабуды ниже(с кеком)
        Вряд ли мне нужно будет динамически смотреть координаты, так что этого должно хватить.
        Вызвал - забыл.
        Листенер уже явно лишнее.
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            loc.getLatitude(); //Широта
            loc.getLongitude(); //Долгота
        }

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                showLocation(location);
            }

            @Override
            public void onProviderDisabled(String provider) {
                checkEnabled();
            }

            @Override
            public void onProviderEnabled(String provider) {
                checkEnabled();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                if (provider.equals(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(mapContext, "Status: " + String.valueOf(status), Toast.LENGTH_SHORT).show();
                } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                    Toast.makeText(mapContext, "Status: " + String.valueOf(status), Toast.LENGTH_SHORT).show();
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 2, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 2,
                    locationListener);
        } else {
            // Show rationale and request permission.
        }
    }

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(mapContext, formatLocation(location), Toast.LENGTH_SHORT).show();
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(mapContext, formatLocation(location), Toast.LENGTH_SHORT).show();
        }
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private void checkEnabled() {
        Toast.makeText(this, "Enabled: " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Enabled: " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER), Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the user clicks a marker.
     */


    /**
     * Обработчик нажатий на маркеры(вне кластеров)
     *
     * @param marker Выбранный маркер
     * @return true, если нужно отключить стандартное поведение при выборе маркера, false - иначе.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        //if (clickCount != null)
        {
            //clickCount = clickCount + 1;
            //marker.setTag(clickCount);
            //Toast.makeText(this,marker.getTitle() + " has been clicked " + clickCount + " times.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, marker.getTitle(), Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    /**
     * Группирует элементы (маркеры) кластера в зависимости от уровня масштабирования.
     */
    private ClusterManager<BusStop> mClusterManager;

    /**
     * Добавляет функции реакции на события внутри кластера(нажатие маркера, нажатие инфо-окна)
     * и вызывает addItems()
     */
    public void setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<BusStop>(mapContext, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterItemInfoWindowClickListener(
                new ClusterManager.OnClusterItemInfoWindowClickListener<BusStop>() {
                    /**
                     * Обработчик нажатий на инфо-окно маркера
                     * @param busStop Выбранный элемент
                     */
                    @Override
                    public void onClusterItemInfoWindowClick(BusStop busStop) {
                        dialogBuilder(busStop);
                    }
                });

        mClusterManager.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<BusStop>() {
                    /**
                     * Обработчик нажатий на маркер внутри кластера
                     * @param clusterItem Элемент кластера
                     * @return true, если нужно отключить стандартное поведение при выборе маркера, false - иначе.
                     */
                    @Override
                    public boolean onClusterItemClick(BusStop clusterItem) {

                        // if true, click handling stops here and do not show info view, do not move camera
                        // you can avoid this by calling:
                        // renderer.getMarker(clusterItem).showInfoWindow();
                        return false;
                    }
                });

        // Add cluster items (markers) to the cluster manager.
        mClusterManager.setRenderer(new OwnRendring(mapContext, mMap, mClusterManager));
        addItems();
    }

    /**
     * Добалвяет элементы(остановки) на карту используя класс BusStopParser
     */
    private void addItems() {
        BusStopParser.upload(this);
        for (int i = 0; i < BusStopParser.list.size(); i++) {
            BusStop stop = new BusStop(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                    BusStopParser.list.get(i).latitude,
                    BusStopParser.list.get(i).longitude,
                    BusStopParser.list.get(i).stopName);
            mClusterManager.addItem(stop);
        }
    }


    /**
     * Реализация объекта ClusterItem.
     * Включает конструкторы для создания нового объекта внутри кластера.
     */
    public class BusStop implements ClusterItem {
        private final LatLng mPosition;
        BitmapDescriptor icon;
        String title;
        String snippet;

        /**
         * Конструктор
         *
         * @param ic  Иконка
         * @param lat Широта
         * @param lng Долгота
         * @param tit Название
         * @param sni Подпись
         */
        public BusStop(BitmapDescriptor ic, Double lat, Double lng, String tit, String sni) {
            mPosition = new LatLng(lat, lng);
            icon = ic;
            title = tit;
            snippet = sni;
        }

        /**
         * Конструктор
         *
         * @param ic  Иконка
         * @param lat Широта
         * @param lng Долгота
         * @param tit Название
         */
        public BusStop(BitmapDescriptor ic, Double lat, Double lng, String tit) {
            mPosition = new LatLng(lat, lng);
            icon = ic;
            title = tit;
        }

        /**
         * Возвращает положение маркера
         *
         * @return Объекта LatLng (широта и долгота)
         */
        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        /**
         * Возвращает название маркера
         *
         * @return Название маркера
         */
        public String getTitle() {
            return title;
        }

        /**
         * Возвращает подпись к маркеру
         *
         * @return Подпись к маркеру
         */
        public String getSnippet() {
            return snippet;
        }

        /**
         * Возвращает иконку маркера
         *
         * @return Иконка маркера
         */
        public BitmapDescriptor getIcon() {
            return icon;
        }

    }

    /**
     * Класс управляет кластерами и маркерами перед их выводом на экран.
     */
    public class OwnRendring extends DefaultClusterRenderer<BusStop> {
        /**
         * Конструктор
         *
         * @param context        Контекст
         * @param map            Дескриптор карты
         * @param clusterManager Кластер
         */
        public OwnRendring(Context context, GoogleMap map,
                           ClusterManager<BusStop> clusterManager) {
            super(context, map, clusterManager);
        }


        /**
         * Определяет внешний вид маркеров
         *
         * @param item          Элемент кластера
         * @param markerOptions Опции отрисовки
         */
        protected void onBeforeClusterItemRendered(BusStop item, MarkerOptions markerOptions) {
            markerOptions.icon(item.getIcon());
            markerOptions.snippet(item.getSnippet());
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

    /**
     * Конструктор диалогового окна.
     * Вывод название остановки и предлагает выбрать ее или отказаться от выбора.
     *
     * @param clusterItem элемент кластера.
     */
    public void dialogBuilder(BusStop clusterItem) {
        AlertDialog.Builder ad = new AlertDialog.Builder(mapContext);
        ad.setTitle(clusterItem.getTitle());  // заголовок
        ad.setMessage("Выбрать эту остановку?"); // сообщение
        ad.setPositiveButton("Да", new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(mapContext, "Выезжаем",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.setNegativeButton("Нет", new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(mapContext, "Ноуп", Toast.LENGTH_LONG)
                        .show();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(mapContext, "Слабак",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.show();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Метод отрисовывает карту на экране телефона.
     */
    protected void onCreateContinue() {
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Код запроса
     */
    private static final int PERMISSION_REQUEST_CODE = 124;

    /**
     * Проверяет есть ли права
     *
     * @return есть права или нет
     */
    private boolean hasPermissions() {
        //int res = 0;
        //Массив с необходимыми правами,
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

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
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
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

        onCreateContinue();

    }
}
