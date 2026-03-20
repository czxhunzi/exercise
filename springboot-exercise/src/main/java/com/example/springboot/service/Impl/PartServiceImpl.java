package com.example.springboot.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Part;
import com.example.springboot.mapper.PartMapper;
import com.example.springboot.service.IPartService;
import org.springframework.stereotype.Service;

@Service
public class PartServiceImpl extends ServiceImpl<PartMapper, Part> implements IPartService {
}
