package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.Energy;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EnergyMapper extends BaseMapper<Energy> {
}
