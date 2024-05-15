package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ApiService {
    private final RestTemplate restTemplate;

    @Value("${api.get}")
    private String apiGet;

    @Value("${api.put}")
    private String apiPut;

    @Value("${api.post}")
    private String apiPost;

    public String getAllData() {
         return getData() + "\n" + putData() + "\n" + postData();
    }
    public String getData() {
        try {
            ResponseEntity<String> response = restTemplate.exchange(apiGet, HttpMethod.GET, null, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "getData error: " + e.getMessage();
        }
    }

    public String putData() {
        try {
            HttpEntity<ApiRequest> request = new HttpEntity<>(new ApiRequest("PUT", "input1"));
            ResponseEntity<String> response = restTemplate.exchange(apiPut, HttpMethod.PUT, request, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            return "putData error: " + e.getMessage();
        }
    }

    public String postData() {
        try {
            HttpEntity<ApiRequest> request = new HttpEntity<>(new ApiRequest("POST", "input2"));
            ResponseEntity<String> response = restTemplate.exchange(apiPost, HttpMethod.POST, request, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            return "postData error: " + e.getMessage();
        }
    }
}
