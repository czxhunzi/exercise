package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.IPartService;
import com.example.springboot.service.IPurposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin//解决跨域问题
@RequestMapping("/part")
public class PartController {
    @Autowired
    IPartService partService;

    @GetMapping("/list")
    public Result getAll(){
        return Result.success(partService.list());
    }

}
