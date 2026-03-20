package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("food")//mybaitsplus关联表名
public class Food {
    @TableId(type = IdType.AUTO)//mybatisplus主键自增
    private Integer id;
    private String name;
    private Integer categoryId;
    private Integer purposeId;
    private String img;
    private Integer energy;
    private Double carbohydrate;
    private Double protein;
    private Double fat;
    private Integer time;
}
