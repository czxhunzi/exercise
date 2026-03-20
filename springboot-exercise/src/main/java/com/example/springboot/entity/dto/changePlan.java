package com.example.springboot.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//修改计划
public class changePlan {
    private Integer id;
    private Integer modeId;
    private String mode;
    private Integer time;
}
