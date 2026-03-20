package com.example.springboot.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Consume;
import com.example.springboot.mapper.ConsumeMapper;
import com.example.springboot.service.IConsumeService;
import org.springframework.stereotype.Service;

@Service
public class ConsumeServiceImpl extends ServiceImpl<ConsumeMapper, Consume> implements IConsumeService {
}
