package com.example.springboot.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Eat;
import com.example.springboot.entity.Food;
import com.example.springboot.entity.dto.Detail;
import com.example.springboot.service.ICategoryService;
import com.example.springboot.service.IEatService;
import com.example.springboot.service.IFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin//解决跨域问题
@RequestMapping("/eat")
public class EatController {
    @Autowired
    IEatService eatService;

    @Autowired
    IFoodService foodService;

    LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);


    @GetMapping("/list")
    public Result getAll() {
        return Result.success(eatService.list());
    }

    @PostMapping("/save")
    public Result getById(@RequestParam Integer userId, @RequestParam Integer foodId, @RequestParam Integer multiple) {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LambdaQueryWrapper<Eat> eatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        eatLambdaQueryWrapper.eq(Eat::getUserId, userId)
                .eq(Eat::getFoodId, foodId)
                .between(Eat::getTime, start, end);
        Eat one = eatService.getOne(eatLambdaQueryWrapper);
        if (one == null) {
            Eat eat = new Eat();
            eat.setUserId(userId);
            eat.setFoodId(foodId);
            eat.setMultiple(multiple);
            eat.setTime(new Date());
            eatService.save(eat);
        } else {
            one.setMultiple(one.getMultiple() + multiple);
            eatService.updateById(one);
        }

        return Result.success();
    }

    @GetMapping("/getEnergy")
    public Result getEnergy(@RequestParam Integer id) {
        Detail detail = getDetail(id);
        return Result.success(detail);

    }

    @GetMapping("/condition")
    public Result getCondition(@RequestParam Integer id) {

        LambdaQueryWrapper<Eat> eatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        eatLambdaQueryWrapper.eq(Eat::getUserId, id)
                .between(Eat::getTime, start, end);
        List<Eat> eatList = eatService.list(eatLambdaQueryWrapper);
        return Result.success(eatList);
    }

    public Detail getDetail(Integer id) {

        LambdaQueryWrapper<Eat> eatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        eatLambdaQueryWrapper.eq(Eat::getUserId, id)
                .between(Eat::getTime, start, end);
        List<Eat> eatList = eatService.list(eatLambdaQueryWrapper);
        Detail detail = new Detail();
        for (Eat eat : eatList) {
            LambdaQueryWrapper<Food> foodLambdaQueryWrapper = new LambdaQueryWrapper<>();
            foodLambdaQueryWrapper.eq(Food::getId, eat.getFoodId());
            Food food = foodService.getOne(foodLambdaQueryWrapper);
            if (eat.getMultiple() == 0) {
                detail.setEnergy(detail.getEnergy() + food.getEnergy() / 2);
                detail.setCarbohydrate(detail.getCarbohydrate() + food.getCarbohydrate() * 0.5);
                detail.setProtein(detail.getProtein() + food.getProtein() * 0.5);
                detail.setFat(detail.getFat() + food.getFat() * 0.5);
            } else {
                detail.setEnergy(detail.getEnergy() + food.getEnergy() * eat.getMultiple());
                detail.setCarbohydrate(detail.getCarbohydrate() + food.getCarbohydrate() * eat.getMultiple());
                detail.setProtein(detail.getProtein() + food.getProtein() * eat.getMultiple());
                detail.setFat(detail.getFat() + food.getFat() * eat.getMultiple());
            }

        }
        detail.setStandard(1500 - detail.getEnergy());
        return detail;
    }
}
