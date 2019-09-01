package com.ichat.command.dto;

import java.util.List;

public class WeatherData {
    public static class Main {
        private double temp;
        private double temp_min;
        private double temp_max;

        public double getTemp() {
            return temp;
        }

        public Main setTemp(double temp) {
            this.temp = temp;
            return this;
        }

        public double getTemp_min() {
            return temp_min;
        }

        public Main setTemp_min(double temp_min) {
            this.temp_min = temp_min;
            return this;
        }

        public double getTemp_max() {
            return temp_max;
        }

        public Main setTemp_max(double temp_max) {
            this.temp_max = temp_max;
            return this;
        }

        @Override
        public String toString() {
            return "Main{" +
                    "temp=" + temp +
                    ", temp_min=" + temp_min +
                    ", temp_max=" + temp_max +
                    '}';
        }
    }
    public static class WeatherDescription {
        private String main;
        private String description;
        public String getMain() {
            return main;
        }

        public WeatherDescription setMain(String main) {
            this.main = main;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public WeatherDescription setDescription(String description) {
            this.description = description;
            return this;
        }

        @Override
        public String toString() {
            return "WeatherDescription{" +
                    "main='" + main + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
    private Main main;
    private List<WeatherDescription> weather;
    private String name;
    private long dt;

    public List<WeatherDescription> getWeather() {
        return weather;
    }

    public WeatherData setWeather(List<WeatherDescription> weather) {
        this.weather = weather;
        return this;
    }

    public Main getMain() {
        return main;
    }

    public WeatherData setMain(Main main) {
        this.main = main;
        return this;
    }

    public String getName() {
        return name;
    }

    public WeatherData setName(String name) {
        this.name = name;
        return this;
    }

    public long getDt() {
        return dt;
    }

    public WeatherData setDt(long dt) {
        this.dt = dt;
        return this;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "main=" + main +
                ", weather=" + weather +
                ", name='" + name + '\'' +
                ", dt=" + dt +
                '}';
    }
}