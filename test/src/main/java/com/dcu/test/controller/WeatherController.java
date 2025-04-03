package com.dcu.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import java.util.List;

@Controller
public class WeatherController {
    private static final String API_KEY = "5283762fb4305db433ecca45d263876c";
    private static final String URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=kr";
    private static final String GEO_URL = "http://ip-api.com/json"; // 사용자 IP 기반 위치 정보 API (예시)

    // 기본 날씨를 가져오는 엔드포인트 (기본적으로 내 위치의 날씨)
    @GetMapping("/weather")
    public String getWeather(Model model) {
        String city = getCityByIP(); // IP 기반으로 도시를 찾기
        return getWeatherForCity(city, model); // 해당 도시의 날씨 정보를 가져옴
    }

    // 특정 도시의 날씨를 가져오는 메서드
    private String getWeatherForCity(String city, Model model) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = String.format(URL, city, API_KEY);

        WeatherResponse weatherResponse = restTemplate.getForObject(apiUrl, WeatherResponse.class);

        if (weatherResponse != null) {
            model.addAttribute("weatherData", weatherResponse);
            model.addAttribute("cityName", city); // 도시 이름도 모델에 추가
            return "weather"; // weather.html 템플릿 호출
        } else {
            model.addAttribute("errorMessage", "날씨 정보를 불러올 수 없습니다.");
            return "weather"; // error 메시지와 함께 weather.html 템플릿 호출
        }
    }

    // 사용자 IP 기반으로 도시를 추출하는 메서드
    private String getCityByIP() {
        RestTemplate restTemplate = new RestTemplate();
        String geoApiUrl = GEO_URL;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(geoApiUrl, String.class);
            String responseBody = response.getBody();

            // JSON 파싱하여 도시 정보 추출 (ip-api에서 제공하는 JSON 응답 형식에 맞춰 파싱)
            JSONObject geoJson = new JSONObject(responseBody);
            return geoJson.getString("city"); // 도시 이름만 추출
        } catch (Exception e) {
            e.printStackTrace();
            return "Seoul"; // 기본값을 Seoul로 설정
        }
    }

    // 내부 DTO 클래스들
    @Getter
    @Setter
    public static class WeatherResponse {
        private MainData main;
        private List<WeatherData> weather;
        private WindData wind;
        private SysData sys;
    }

    @Getter
    @Setter
    public static class MainData {
        private double temp;
        @JsonProperty("feels_like")
        private double feelsLike;
        @JsonProperty("temp_min")
        private double tempMin;
        @JsonProperty("temp_max")
        private double tempMax;
        private int humidity;
        private int pressure;
    }

    @Getter
    @Setter
    public static class WeatherData {
        private String description;
    }

    @Getter
    @Setter
    public static class WindData {
        private double speed;
    }

    @Getter
    @Setter
    public static class SysData {
        private long sunrise;
        private long sunset;
    }
}
