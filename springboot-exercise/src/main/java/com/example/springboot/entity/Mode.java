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
@TableName("mode")//mybaitsplus关联表名
public class Mode {
    @TableId(type = IdType.AUTO)//mybatisplus主键自增
    private Integer id;
    private String name;
    private String img;
    private Integer energy;
    private String level;
}
