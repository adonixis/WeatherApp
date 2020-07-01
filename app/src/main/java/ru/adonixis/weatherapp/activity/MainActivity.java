package ru.adonixis.weatherapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.adonixis.weatherapp.R;
import ru.adonixis.weatherapp.adapter.SectionsPagerAdapter;
import ru.adonixis.weatherapp.model.WeatherResponse;
import ru.adonixis.weatherapp.viewmodel.MainViewModel;

import static ru.adonixis.weatherapp.util.Utils.showSnackbar;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private View rootView;
    private ContentLoadingProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        rootView = findViewById(R.id.root_layout);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        ImageView ivWeatherIcon = findViewById(R.id.iv_weather_icon);
        TextView tvSummary = findViewById(R.id.tv_summary);
        TextView tvTemperature = findViewById(R.id.tv_temperature);
        progressBar = findViewById(R.id.progress_bar);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mainViewModel.getWeather(getString(R.string.dark_sky_api_key), tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }

        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GoogleMapsActivity.class);
                startActivity(intent);
            }
        });

        mainViewModel.getWeatherLiveData().observe(this, new Observer<WeatherResponse>() {
            @Override
            public void onChanged(@Nullable WeatherResponse weatherResponse) {
                if (weatherResponse != null) {
                    String icon = weatherResponse.getCurrentlyResponse().getIcon();
                    String iconUrl = getString(R.string.icon_url, icon);
                    Glide
                            .with(MainActivity.this)
                            .load(iconUrl)
                            .fitCenter()
                            .placeholder(R.drawable.ic_placeholder)
                            .into(ivWeatherIcon);
                    String summary = weatherResponse.getCurrentlyResponse().getSummary();
                    tvSummary.setText(summary);
                    String temperature = Math.round(weatherResponse.getCurrentlyResponse().getTemperature()) + "°C";
                    tvTemperature.setText(temperature);

                    progressBar.hide();
                }
            }
        });

        mainViewModel.getErrorMessageLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String errorMessage) {
                showErrorMessage(errorMessage);
            }
        });

        mainViewModel.getWeather(getString(R.string.dark_sky_api_key), 0);
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