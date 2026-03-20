package com.example.springboot.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Mode;
import com.example.springboot.service.IModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin//解决跨域问题
@RequestMapping("/mode")
public class ModeController {

    @Autowired
    IModeService modeService;

    @GetMapping("/list")
    public Result getAll(){
        return Result.success(modeService.list());
    }

    @GetMapping("/getById/{id}")
    public Result getById(@PathVariable Integer id){
        return Result.success(modeService.getById(id));
    }

    @GetMapping("/highList")
    public Result getHighList(){
        LambdaQueryWrapper<Mode> modeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        modeLambdaQueryWrapper.eq(Mode::getLevel, "high");
        return Result.success(modeService.list(modeLambdaQueryWrapper));
    }
}
