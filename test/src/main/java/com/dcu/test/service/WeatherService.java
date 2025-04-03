package com.dcu.test.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;

@Service
public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private static final String API_KEY = "5283762fb4305db433ecca45d263876c";
    private static final String URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=kr";
    private static final String GEO_URL = "http://ip-api.com/json"; // ip-api 사용 예시, 사용자 IP 기반 위치 파악

    public String getWeather(String city) {
        String apiUrl = String.format(URL, city, API_KEY);

        try {
            RestTemplate restTemplate = new RestTemplate();

            // 헤더 추가
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // GET 요청 수행
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // 응답 상태 코드 확인
            logger.error("응답 상태 코드: {}", response.getStatusCode());

            // 응답 본문 로깅
            String responseBody = response.getBody();
            logger.error("OpenWeather API 전체 응답: {}", responseBody);

            // JSON 파싱
            JSONObject jsonObject = new JSONObject(responseBody);

            // 온도 추출
            double temperature = jsonObject.getJSONObject("main").getDouble("temp");
            String temperatureStr = String.format("%.1f°C", temperature);

            // 날씨 설명 추출
            String weatherDescription = jsonObject.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("description");

            // 한글로 날씨 정보 포맷팅
            String weatherInfo = String.format("%s, %s", temperatureStr, weatherDescription);
            logger.error("파싱된 날씨 정보: {}", weatherInfo);

            return weatherInfo;
        } catch (Exception e) {
            logger.error("날씨 정보 요청 중 오류 발생: ", e);
            return "날씨 정보를 불러올 수 없습니다.";
        }
    }

    public String getWeatherByIP() {
        // 사용자의 IP 기반 위치 정보 요청 (ip-api 사용 예시)
        RestTemplate restTemplate = new RestTemplate();
        String geoApiUrl = GEO_URL;
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    geoApiUrl,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            String responseBody = response.getBody();
            logger.info("IP 기반 위치 정보: {}", responseBody);

            // JSON 파싱하여 도시 정보 추출
            JSONObject geoJson = new JSONObject(responseBody);
            String city = geoJson.getString("city"); // 예시로 도시만 추출

            // 해당 도시의 날씨 정보를 가져옴
            return getWeather(city);
        } catch (Exception e) {
            logger.error("사용자의 위치를 찾을 수 없습니다: ", e);
            return "위치 정보를 불러올 수 없습니다.";
        }
    }
}
