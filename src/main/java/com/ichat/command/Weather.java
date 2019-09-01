package com.ichat.command;

import com.ichat.command.dto.WeatherResponse;
import com.ichat.command.dto.WeatherData;
import com.ichat.common.Util;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Weather extends AbstractStringCommand {

    private final String DEFAULT_ZIP_CODE = "11418";
    private final String OPEN_WEATHER_API_KEY = "dae66b1ca3bf0c1d18abf2a7560d08d9";
    private final String OPEN_WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5";
    private final String OPEN_WEATHER_CURRENT_ENDPOINT = "/weather";
    private final String OPEN_WEATHER_HOURLY_ENDPOINT = "/forecast";
    private final String OPEN_WEATHER_QUERY_PARAM_CITY = "q";
    private final String OPEN_WEATHER_QUERY_PARAM_ZIPCODE = "zip";

    private static final char LOCATION_PARAM = 'l';
    private static final char MODE_PARAM = 'm';
    private static final String MODE_CURRENT = "current";
    private static final String MODE_HOURLY = "hourly";

    private String CURRENT_WEATHER_HTML_TEMPLATE;
    private String HOURLY_WEATHER_HTML_TEMPLATE;
    private String WEATHER_ROW_TEMPLATE;

    private Util util = new Util();

    public Weather() {
        super();
        Unirest.config().setObjectMapper(new JacksonObjectMapper());
        loadTemplates();
    }

    @Override
    public String process(Map<String, String> paramArgMap) {
        WeatherResponse response = null;
        String url = OPEN_WEATHER_BASE_URL + OPEN_WEATHER_CURRENT_ENDPOINT;
        //use the arguments to generate a HTML representation of the weather
        if (MapUtils.isEmpty(paramArgMap)) {
            //no arguments - return the current weather for default location
            response = getWeatherFromAPI(url, null);
        } else {
            if (paramArgMap.containsKey(HELP_PARAM)) {
                //if help param is invoked, we will ignore any other commands
                return help();
            }
            Map<String, Object> queryParams = new HashMap<>();
            if (paramArgMap.containsKey(LOCATION_PARAM)) {
                String locationArg = paramArgMap.get(LOCATION_PARAM);
                Integer zip = Util.tryParseInt(locationArg);
                if (zip != null) {
                    queryParams.put(OPEN_WEATHER_QUERY_PARAM_ZIPCODE, zip);
                } else {
                    queryParams.put(OPEN_WEATHER_QUERY_PARAM_CITY, locationArg);
                }
            }
            if (paramArgMap.containsKey(MODE_PARAM)) {
                String modeArg = paramArgMap.get(MODE_PARAM);
                if (MODE_CURRENT.equals(modeArg)) {
                    url = OPEN_WEATHER_BASE_URL + OPEN_WEATHER_CURRENT_ENDPOINT;
                } else if (MODE_HOURLY.equals(modeArg)) {
                    url = OPEN_WEATHER_BASE_URL + OPEN_WEATHER_HOURLY_ENDPOINT;
                }
            }
            response = getWeatherFromAPI(url, queryParams);
        }
        return generateHtmlView(response);
    }

    @Override
    public Character getDefaultParameter() {
        return LOCATION_PARAM;
    }

    @Override
    protected void initializeParams() {
        super.initializeParams();
        this.addParameter(LOCATION_PARAM, buildHelpText(LOCATION_PARAM, "location: zip code or city name","look up weather for this location. Default."))
                .addParameter(MODE_PARAM, buildHelpText(MODE_PARAM, "mode: 'current', 'hourly'","'current' option fetches the current weather. 'hourly' fetches 5-day 3-hourly forecast."));
    }

    @Override
    public String getDescription() {
        return "Weather provides real time weather conditions for any given location in two formats: current weather and 5-day hourly forecast.";
    }

    private WeatherResponse getWeatherFromAPI(String url, Map<String, Object> queryParams) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        if (queryParams == null) {
            queryParams = new HashMap<>();
        }
        //always include API key and format params
        queryParams.put("appid", "dae66b1ca3bf0c1d18abf2a7560d08d9");
        queryParams.put("units", "imperial");

        //if no location is specified, use default location
        if (!queryParams.containsKey("zip") && !queryParams.containsKey("q")) {
            queryParams.put(OPEN_WEATHER_QUERY_PARAM_ZIPCODE, DEFAULT_ZIP_CODE);
        }

        WeatherResponse response = null;
        if (url.endsWith(OPEN_WEATHER_CURRENT_ENDPOINT)) {
            HttpResponse<WeatherData> weatherDataResponse = Unirest.get(url).queryString(queryParams).asObject(WeatherData.class);
            if (weatherDataResponse.isSuccess() && weatherDataResponse.getBody() != null) {
                response = new WeatherResponse();
                response.setList(Collections.singletonList(weatherDataResponse.getBody()));
                response.setHourlyData(Boolean.FALSE);
            }
        } else {
            HttpResponse<WeatherResponse> weatherResponse = Unirest.get(url).queryString(queryParams).asObject(WeatherResponse.class);
            response = weatherResponse.getBody(); //this will be null if the call failed
            response.setHourlyData(Boolean.TRUE);
        }
        return response;
    }

    private String generateHtmlView(WeatherResponse weatherResponse) {
        StringBuilder htmlBuilder = new StringBuilder();
        if (weatherResponse == null || CollectionUtils.isEmpty(weatherResponse.getList())) {
            htmlBuilder.append("<span style=\"font-style: italic;\">Weather information currently unavailable</span>");
        } else {
            StringBuilder rowBuilder = new StringBuilder();
            Date prevTime = null;
            String[] color = new String[]{"aliceblue", "paleturquoise"};
            int colorIdx = 0;
            for (WeatherData weatherData : weatherResponse.getList()) {
                String city = weatherData.getName();
                String time = Util.convertTime(weatherData.getDt(), "E MMM dd hh:mm a");
                Date date = new Date(TimeUnit.SECONDS.toMillis(weatherData.getDt()));
                double current = weatherData.getMain().getTemp();
                double min = weatherData.getMain().getTemp_min();
                double max = weatherData.getMain().getTemp_max();
                String status = weatherData.getWeather().get(0).getMain();
                String description = weatherData.getWeather().get(0).getDescription();

                prevTime = prevTime == null ? date : prevTime;

                if (weatherResponse.isHourlyData()) {
                    if (date.getDay() != prevTime.getDay()) {
                        prevTime = date;
                        colorIdx = (colorIdx + 1) % color.length;
                    }
                    rowBuilder.append(String.format(WEATHER_ROW_TEMPLATE, color[colorIdx], time, current, status + " - " + description));
                } else {
                    rowBuilder.append(String.format(CURRENT_WEATHER_HTML_TEMPLATE, city, time, current, min, max, status + " - " + description));
                }
            }
            String htmlResult = rowBuilder.toString();
            if (weatherResponse.isHourlyData()) {
                htmlResult = String.format(HOURLY_WEATHER_HTML_TEMPLATE,weatherResponse.getCity().getName(), htmlResult);
            }
            htmlBuilder.append(htmlResult);
        }
        return htmlBuilder.toString();
    }

    private void loadTemplates() {
        try {
            CURRENT_WEATHER_HTML_TEMPLATE = util.getResourceFileContent("weather/CurrentWeatherTemplate.txt");
            HOURLY_WEATHER_HTML_TEMPLATE = util.getResourceFileContent("weather/HourlyWeatherTemplate.txt");
            WEATHER_ROW_TEMPLATE = util.getResourceFileContent("weather/WeatherRow.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
