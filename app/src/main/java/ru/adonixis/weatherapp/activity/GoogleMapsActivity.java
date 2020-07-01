package ru.adonixis.weatherapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.adonixis.weatherapp.R;
import ru.adonixis.weatherapp.model.WeatherResponse;
import ru.adonixis.weatherapp.viewmodel.GoogleMapViewModel;

import static ru.adonixis.weatherapp.util.Utils.showSnackbar;

public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final int MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final double SPB_LATITUDE = 59.950015;
    private static final double SPB_LONGITUDE = 30.316599;
    private GoogleMapViewModel googleMapViewModel;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng hereLocation;
    private GoogleMap googleMap;
    private ImageView ivWeatherIcon;
    private TextView tvSummary;
    private TextView tvTemperature;
    private CardView cardView;
    private View rootView;
    private LatLng spbLocation;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        googleMapViewModel = new ViewModelProvider(this).get(GoogleMapViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        rootView = findViewById(R.id.root_layout);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(GoogleMapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(GoogleMapsActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(GoogleMapsActivity.this)
                                .setTitle(R.string.access_location_title)
                                .setMessage(R.string.access_location_message)
                                .setPositiveButton(R.string.btn_positive, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ActivityCompat.requestPermissions(GoogleMapsActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                MY_PERMISSIONS_REQUEST_LOCATION );
                                    }
                                })
                                .create()
                                .show();


                    } else {
                        ActivityCompat.requestPermissions(GoogleMapsActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION );
                    }
                } else {
                    showLocationOnMap(googleMap);
                }
            }
        });

        ivWeatherIcon = findViewById(R.id.iv_weather_icon);
        tvSummary = findViewById(R.id.tv_summary);
        tvTemperature = findViewById(R.id.tv_temperature);
        cardView = findViewById(R.id.card_view);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                cardView.setVisibility(View.GONE);
            }
        };

        googleMapViewModel.getWeatherLiveData().observe(this, new Observer<WeatherResponse>() {
            @Override
            public void onChanged(@Nullable WeatherResponse weatherResponse) {
                if (weatherResponse != null) {
                    String icon = weatherResponse.getCurrentlyResponse().getIcon();
                    String iconUrl = getString(R.string.icon_url, icon);
                    Glide
                            .with(GoogleMapsActivity.this)
                            .load(iconUrl)
                            .fitCenter()
                            .placeholder(R.drawable.ic_placeholder)
                            .into(ivWeatherIcon);
                    String summary = weatherResponse.getCurrentlyResponse().getSummary();
                    tvSummary.setText(summary);
                    String temperature = Math.round(weatherResponse.getCurrentlyResponse().getTemperature()) + "°C";
                    tvTemperature.setText(temperature);

                    handler.removeCallbacks(runnable);
                    cardView.setVisibility(View.VISIBLE);
                    handler.postDelayed(runnable, 5000);
                }
            }
        });

        googleMapViewModel.getErrorMessageLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String errorMessage) {
                showErrorMessage(errorMessage);
            }
        });

        spbLocation = new LatLng(SPB_LATITUDE, SPB_LONGITUDE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                hereLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.access_location_title)
                        .setMessage(R.string.access_location_message)
                        .setPositiveButton(R.string.btn_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(GoogleMapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        } else {
            getLocation(googleMap);
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng pos) {
                cardView.setVisibility(View.GONE);
                googleMapViewModel.getWeather(getString(R.string.dark_sky_api_key), pos.latitude, pos.longitude);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLocation(GoogleMap googleMap) {
        Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocationGPS != null) {
            hereLocation = new LatLng(lastKnownLocationGPS.getLatitude(), lastKnownLocationGPS.getLongitude());
        } else {
            Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocationNetwork != null) {
                hereLocation = new LatLng(lastKnownLocationNetwork.getLatitude(), lastKnownLocationNetwork.getLongitude());
            } else {
                hereLocation = spbLocation;
            }
        }

        showLocationOnMap(googleMap);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        }
    }

    private void showLocationOnMap(GoogleMap googleMap) {
        if (googleMap != null && hereLocation != null) {
            if (marker != null) {
                marker.remove();
            }
            marker = googleMap.addMarker(new MarkerOptions()
                    .position(hereLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_near_me))
            );
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    cardView.setVisibility(View.GONE);
                    googleMapViewModel.getWeather(getString(R.string.dark_sky_api_key), marker.getPosition().latitude, marker.getPosition().longitude);
                    return true;
                }
            });
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hereLocation, 15f));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        getLocation(googleMap);
                    }

                } else {
                    hereLocation = spbLocation;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hereLocation, 15f));
                }
                return;
            }
        }
    }

    protected void showErrorMessage(String errorMessage) {
        showSnackbar(
                rootView,
                null,
                ContextCompat.getColor(this, R.color.red),
                Color.WHITE,
                errorMessage,
                Color.WHITE,
                getString(R.string.snackbar_action_hide),
                null
        );
    }
}
