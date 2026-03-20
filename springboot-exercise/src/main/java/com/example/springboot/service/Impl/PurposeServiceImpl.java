package com.example.springboot.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Purpose;
import com.example.springboot.mapper.PurposeMapper;
import com.example.springboot.service.IPurposeService;
import org.springframework.stereotype.Service;

@Service
public class PurposeServiceImpl extends ServiceImpl<PurposeMapper, Purpose> implements IPurposeService {
}
