package ru.adonixis.weatherapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import ru.adonixis.weatherapp.R;
import ru.adonixis.weatherapp.adapter.DividerItemDecoration;
import ru.adonixis.weatherapp.adapter.WeatherAdapter;
import ru.adonixis.weatherapp.model.WeatherModel;
import ru.adonixis.weatherapp.model.WeatherResponse;
import ru.adonixis.weatherapp.viewmodel.FragmentViewModel;

import static ru.adonixis.weatherapp.util.Utils.showSnackbar;

public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private FragmentViewModel fragmentViewModel;
    private RecyclerView recyclerWeather;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WeatherAdapter weatherAdapter;
    private List<WeatherModel> weatherList = new ArrayList<>();
    private View rootView;
    private int index = 0;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentViewModel = new ViewModelProvider(this).get(FragmentViewModel.class);

        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        rootView = root.findViewById(R.id.root_layout);

        recyclerWeather = root.findViewById(R.id.recycler_weather);
        recyclerWeather.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerWeather.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity());
        recyclerWeather.addItemDecoration(dividerItemDecoration);
        weatherAdapter = new WeatherAdapter(getContext(), weatherList);
        recyclerWeather.setAdapter(weatherAdapter);

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fragmentViewModel.getWeather(getString(R.string.dark_sky_api_key), index);
            }
        });
        swipeRefreshLayout.setRefreshing(true);

        fragmentViewModel.getWeatherLiveData().observe(getViewLifecycleOwner(), new Observer<WeatherResponse>() {
            @Override
            public void onChanged(@Nullable WeatherResponse weatherResponse) {
                if (weatherResponse != null) {
                    weatherList.clear();
                    int size = weatherResponse.getDailyResponse().getDataResponse().size();
                    for (int i = 0; i < size; i++) {
                        float temperature = weatherResponse.getDailyResponse().getDataResponse().get(i).getTemperatureHigh();
                        String icon = weatherResponse.getDailyResponse().getDataResponse().get(i).getIcon();
                        int time = weatherResponse.getDailyResponse().getDataResponse().get(i).getTime();
                        weatherList.add(new WeatherModel(
                                            icon,
                                            temperature,
                                            time
                                        )
                        );
                    }
                    weatherAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        fragmentViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String errorMessage) {
                showErrorMessage(errorMessage);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        fragmentViewModel.getWeather(getString(R.string.dark_sky_api_key), index);

        return root;
    }

    protected void showErrorMessage(String errorMessage) {
        showSnackbar(
                rootView,
                null,
                ContextCompat.getColor(getContext(), R.color.red),
                Color.WHITE,
                errorMessage,
                Color.WHITE,
                getString(R.string.snackbar_action_hide),
                null
        );
    }
}