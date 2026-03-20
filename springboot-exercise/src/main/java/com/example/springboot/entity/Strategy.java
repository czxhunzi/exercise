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
@TableName("strategy")//mybaitsplus关联表名
public class Strategy {
    @TableId(type = IdType.AUTO)//mybatisplus主键自增
    private Integer id;
    private Integer purposeId;
    private Integer partId;
    private Integer modeId;
    private Integer height;
    private Integer weight;
    private String sex;
}
