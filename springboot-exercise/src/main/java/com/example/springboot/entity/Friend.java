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
@TableName("friend")//mybaitsplus关联表名
public class Friend {
    @TableId(type = IdType.AUTO)//mybatisplus主键自增
    private Integer id;
    private Integer user1Id;
    private Integer user2Id;
    private Integer status;
}
