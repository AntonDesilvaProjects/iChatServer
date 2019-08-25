package com.ichat.command;

import com.ichat.command.dto.WeatherResponse;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
    @weather -l=richmond hill -m=hourly -h
*/
public class Weather implements Command<String> {
    private final String DEFAULT_ZIP_CODE = "11418";
    private final String OPEN_WEATHER_API_KEY = "dae66b1ca3bf0c1d18abf2a7560d08d9";
    private final String OPEN_WEATHER_BASE_URL = "https://www.api.openweathermap.org/data/2.5";
    private final String OPEN_WEATHER_CURRENT_ENDPOINT = "/weather";
    private final String OPEN_WEATHER_HOURLY_ENDPOINT = "/forecast/hourly";
    private final String OPEN_WEATHER_FORECAST_ENDPOINT = "/forecast";

    public static final Map<Character, String> PARAMS;
    static {
        Map<Character, String> tempParams = new HashMap<>();
        tempParams.put('l', "location: this can be a zip code");
        tempParams.put('m', "location: this can be a zip code");
        tempParams.put('a', "location: this can be a zip code");
        PARAMS = Collections.unmodifiableMap(tempParams);
    }

    @Override
    public String process(Map<String, String> paramArgMap) {
        //use the arguments to generate a HTML representation of the weather
        return "asked for weather: " + paramArgMap;
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public Set<Character> getParameters() {
        return Weather.PARAMS.keySet();
    }

    public static void main(String[] args) {
        Unirest.config().setObjectMapper(new JacksonObjectMapper());
       HttpResponse<WeatherResponse> r = Unirest.get("https://api.openweathermap.org/data/2.5" + "/weather").queryString("zip","11418").queryString("appid", "dae66b1ca3bf0c1d18abf2a7560d08d9").queryString("units", "imperial").asObject(WeatherResponse.class);
       System.out.println(r.getBody());
    }
}
