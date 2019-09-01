package com.ichat.command.dto;

import java.util.List;

public class WeatherResponse {
    public static class City {
        private String name;

        public String getName() {
            return name;
        }

        public City setName(String name) {
            this.name = name;
            return this;
        }
    }
    private List<WeatherData> list;
    private City city;
    private boolean hourlyData = Boolean.FALSE;

    public List<WeatherData> getList() {
        return list;
    }

    public WeatherResponse setList(List<WeatherData> list) {
        this.list = list;
        return this;
    }

    public City getCity() {
        return city;
    }

    public WeatherResponse setCity(City city) {
        this.city = city;
        return this;
    }

    public boolean isHourlyData() {
        return hourlyData;
    }

    public WeatherResponse setHourlyData(boolean hourlyData) {
        this.hourlyData = hourlyData;
        return this;
    }
}
