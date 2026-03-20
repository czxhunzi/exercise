package com.example.springboot.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Energy;
import com.example.springboot.mapper.EnergyMapper;
import com.example.springboot.service.IEnergyService;
import org.springframework.stereotype.Service;

@Service
public class EnergyServiceImpl extends ServiceImpl<EnergyMapper, Energy> implements IEnergyService {
}
