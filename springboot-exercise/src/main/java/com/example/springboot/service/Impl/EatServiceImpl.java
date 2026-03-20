package com.example.springboot.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Eat;
import com.example.springboot.mapper.EatMapper;
import com.example.springboot.service.IEatService;
import org.springframework.stereotype.Service;

@Service
public class EatServiceImpl extends ServiceImpl<EatMapper, Eat> implements IEatService {
}
