package com.example.springboot.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rank {
    private Integer userId;
    private String  name;
    private Integer liveness;
}
