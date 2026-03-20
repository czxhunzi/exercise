package com.example.springboot.entity.dto;

import com.example.springboot.entity.Food;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suggest {
    List<Food> breakfast;
    List<Food> lunch;
    List<Food> dinner;
}
