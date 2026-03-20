package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.Purpose;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurposeMapper extends BaseMapper<Purpose> {
}
