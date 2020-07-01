package ru.adonixis.weatherapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import ru.adonixis.weatherapp.model.WeatherResponse;
import ru.adonixis.weatherapp.network.DarkSkyService;
import ru.adonixis.weatherapp.network.ServiceFactory;

public class GoogleMapViewModel extends AndroidViewModel {

    private static final String TAG = "GoogleMapViewModel";
    private MutableLiveData<WeatherResponse> weatherLiveData = new MutableLiveData<>();;
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();;

    public GoogleMapViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<WeatherResponse> getWeatherLiveData() {
        return weatherLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public void getWeather(String apiKey, double latitude, double longitude) {
        DarkSkyService service = ServiceFactory.getDarkSkyService(getApplication());
        service.getCurrentWeather(apiKey, latitude, longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeatherResponse>() {
                    @Override
                    public final void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            if ((((HttpException) e).code() == 504)) {
                                Log.e(TAG, "Get weather failed: " + e);
                                errorMessageLiveData.setValue("Check your internet connection");
                                return;
                            }
                            ResponseBody body = ((HttpException) e).response().errorBody();
                            try {
                                JSONObject jObjError = new JSONObject (body.string());
                                String message = "";
                                if (jObjError.has("message")) {
                                    message = (String) jObjError.get("message");
                                } else if (jObjError.has("errors")) {
                                    JSONArray errors = (JSONArray) jObjError.get("errors");
                                    JSONObject error = (JSONObject) errors.get(0);
                                    JSONArray messages = (JSONArray) error.get("messages");
                                    message = (String) messages.get(0);
                                }
                                Log.e(TAG, "Get weather failed: " + message);
                                errorMessageLiveData.setValue(message);
                            } catch (JSONException | IOException ex) {
                                Log.e(TAG, "Get weather failed: ", ex);
                                errorMessageLiveData.setValue("Unknown error");
                            }
                        } else if (e instanceof IOException) {
                            Log.e(TAG, "Get weather failed: ", e);
                            errorMessageLiveData.setValue("Check your internet connection");
                        } else {
                            Log.e(TAG, "Get weather failed: ", e);
                            errorMessageLiveData.setValue("Unknown error");
                        }
                    }

                    @Override
                    public void onNext(WeatherResponse weatherResponse) {
                        weatherLiveData.setValue(weatherResponse);
                    }

                    @Override
                    public void onComplete() { }

                    @Override
                    public void onSubscribe(Disposable d) { }
                });
    }
}