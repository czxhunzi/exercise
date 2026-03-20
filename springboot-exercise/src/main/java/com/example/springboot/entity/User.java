package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")//mybaitsplus关联表名
public class User {
    @TableId(type = IdType.AUTO)//mybatisplus主键自增
    private Integer id;
    private String openid;
    private String username;
    private String password;
    private Integer age;
    private String sex;
    private double height;
    private double weight;
    private Integer purposeId;
    private Integer partId;
    private Integer level;
    private Integer experience;
    private Boolean record;
//    后来添加的
    @TableField(exist = false)
    private String purpose;
    @TableField(exist = false)
    private String session_key;
    @TableField(exist = false)
    private String appid;
}
