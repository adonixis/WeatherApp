package ru.adonixis.weatherapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import ru.adonixis.weatherapp.R;
import ru.adonixis.weatherapp.model.WeatherModel;

import static ru.adonixis.weatherapp.util.Utils.capitalize;
import static ru.adonixis.weatherapp.util.Utils.getDate;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherItemViewHolder> {

    private Context context;
    private final List<WeatherModel> weatherList;

    public WeatherAdapter(Context context, List<WeatherModel> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    public static class WeatherItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageWeatherIcon;
        private TextView tvTemperature;
        private TextView tvDate;

        WeatherItemViewHolder(View v) {
            super(v);
            imageWeatherIcon = itemView.findViewById(R.id.image_weather_icon);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }

    @Override
    public WeatherItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new WeatherItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WeatherItemViewHolder weatherItemViewHolder, final int position) {
        WeatherModel weather = weatherList.get(position);
        String iconUrl = context.getString(R.string.icon_url, weather.getIcon());
        Glide
                .with(context)
                .load(iconUrl)
                .fitCenter()
                .placeholder(R.drawable.ic_placeholder)
                .into(weatherItemViewHolder.imageWeatherIcon);

        String temperature = Math.round(weather.getTemperature()) + "°C";
        weatherItemViewHolder.tvTemperature.setText(temperature);
        weatherItemViewHolder.tvDate.setText(capitalize(getDate(weather.getTime()), 0, 2));
    }

    @Override
    public int getItemCount() {
        return weatherList == null ? 0 : weatherList.size();
    }

}