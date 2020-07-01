package ru.adonixis.weatherapp.network;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.adonixis.weatherapp.model.WeatherResponse;

public interface DarkSkyService {
    @GET("forecast/{apiKey}/{latitude},{longitude}?exclude=currently,hourly,minutely,alerts,flags&units=si&lang=ru")
    Observable<WeatherResponse> getDailyWeather(@Path("apiKey") String apiKey, @Path("latitude") double latitude, @Path("longitude") double longitude);

    @GET("forecast/{apiKey}/{latitude},{longitude}?exclude=daily,hourly,minutely,alerts,flags&units=si&lang=ru")
    Observable<WeatherResponse> getCurrentWeather(@Path("apiKey") String apiKey, @Path("latitude") double latitude, @Path("longitude") double longitude);
}