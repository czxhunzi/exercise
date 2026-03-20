package com.example.springboot.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Detail {
    private Integer energy = 0;
    private Integer standard;
    private Double carbohydrate = 0.0;
    private Double protein = 0.0;
    private Double fat = 0.0;
}
