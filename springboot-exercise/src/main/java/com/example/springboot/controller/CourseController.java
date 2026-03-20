package com.example.springboot.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Course;
import com.example.springboot.service.ICourseService;
import com.example.springboot.service.IModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin//解决跨域问题
@RequestMapping("/course")
public class CourseController {

    @Autowired
    ICourseService courseService;

    @GetMapping("/list")
    public Result getAll(){
        return Result.success(courseService.list());
    }

    @GetMapping("/condition/{id}")
    public Result Condition(@PathVariable Integer id){
        LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseLambdaQueryWrapper.eq(Course::getModeId,id);
        return Result.success(courseService.list(courseLambdaQueryWrapper));
    }
}
