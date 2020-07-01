package ru.adonixis.weatherapp.model;

public class WeatherModel {

    private String icon;
    private float temperature;
    private int time;

    public WeatherModel(String icon, float temperature, int time) {
        this.icon = icon;
        this.temperature = temperature;
        this.time = time;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}