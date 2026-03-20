package com.example.springboot.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private Integer id;
    private String sex;
    private Integer height;
    private Integer weight;
    private Integer purpose;
    private Integer part;
    private List<Integer> week;
    private List<Integer> mode;
}
