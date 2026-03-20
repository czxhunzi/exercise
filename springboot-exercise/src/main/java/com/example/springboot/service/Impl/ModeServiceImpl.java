package com.example.springboot.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Mode;
import com.example.springboot.mapper.ModeMapper;
import com.example.springboot.service.IModeService;
import org.springframework.stereotype.Service;

@Service
public class ModeServiceImpl extends ServiceImpl<ModeMapper, Mode> implements IModeService {
}
