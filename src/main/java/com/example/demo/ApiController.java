package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ApiController {
    private final ApiService apiService;

    @GetMapping("get-all")
    public String getData() {
        return apiService.getAllData();
    }

    @GetMapping("test-get")
    public ApiResponse testGet() {
        return new ApiResponse(123, "Get Test Response");
    }

    @PutMapping("test-put")
    public ApiResponse testPut(@RequestBody ApiRequest input) {
        return new ApiResponse(123, "Put Test Response - "+input);
    }
    @PostMapping("test-post")
    public ApiResponse testPost(@RequestBody ApiRequest input) {
        return new ApiResponse(123, "Post Test Response - "+input);
    }
}
