package com.medicare.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequest {
    @NotEmpty(message = "Symptoms list cannot be empty")
    private List<String> symptoms;
}
