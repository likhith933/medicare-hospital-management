package com.medicare.controller;

import com.medicare.dto.PredictionRequest;
import com.medicare.exception.ApiResponse;
import com.medicare.service.PredictionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/predict")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @PostMapping("/disease")
    public ResponseEntity<ApiResponse<Map<String, Object>>> predictDisease(@Valid @RequestBody PredictionRequest request) {
        Map<String, Object> prediction = predictionService.predictDisease(request);
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Disease prediction generated successfully")
                .data(prediction)
                .build();
        return ResponseEntity.ok(response);
    }
}
