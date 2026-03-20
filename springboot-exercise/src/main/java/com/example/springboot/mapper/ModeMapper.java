package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.Mode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModeMapper extends BaseMapper<Mode> {
}
