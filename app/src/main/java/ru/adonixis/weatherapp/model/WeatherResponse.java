package ru.adonixis.weatherapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {

    @SerializedName("currently")
    @Expose
    private CurrentlyResponse currentlyResponse;
    @SerializedName("daily")
    @Expose
    private DailyResponse dailyResponse;

    public CurrentlyResponse getCurrentlyResponse() {
        return currentlyResponse;
    }

    public void setCurrentlyResponse(CurrentlyResponse currentlyResponse) {
        this.currentlyResponse = currentlyResponse;
    }

    public DailyResponse getDailyResponse() {
        return dailyResponse;
    }

    public void setDailyResponse(DailyResponse daily) {
        this.dailyResponse = daily;
    }


    public static class CurrentlyResponse {

        @SerializedName("summary")
        @Expose
        private String summary;
        @SerializedName("icon")
        @Expose
        private String icon;
        @SerializedName("temperature")
        @Expose
        private Double temperature;

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

    }

    public static class DailyResponse {

        @SerializedName("data")
        @Expose
        private List<DataResponse> dataResponses = null;

        public List<DataResponse> getDataResponse() {
            return dataResponses;
        }

        public void setData(List<DataResponse> dataResponses) {
            this.dataResponses = dataResponses;
        }

    }

    public static class DataResponse {

        @SerializedName("time")
        @Expose
        private Integer time;
        @SerializedName("icon")
        @Expose
        private String icon;
        @SerializedName("temperatureHigh")
        @Expose
        private Float temperatureHigh;

        public Integer getTime() {
            return time;
        }

        public void setTime(Integer time) {
            this.time = time;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public Float getTemperatureHigh() {
            return temperatureHigh;
        }

        public void setTemperatureHigh(Float temperatureHigh) {
            this.temperatureHigh = temperatureHigh;
        }

    }

}

