package com.example.springboot.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Food;
import com.example.springboot.entity.User;
import com.example.springboot.entity.dto.Suggest;
import com.example.springboot.service.ICategoryService;
import com.example.springboot.service.IFoodService;
import com.example.springboot.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
@CrossOrigin//解决跨域问题
@RequestMapping("/food")
public class FoodController {

    @Autowired
    IFoodService foodService;

    @Autowired
    IUserService userService;


    @GetMapping("/list")
    public Result getAll(){
        return Result.success(foodService.list());
    }

    @GetMapping("/getByCategoryId")
    public Result getByCategoryId(@RequestParam Integer categoryId){
        LambdaQueryWrapper<Food> foodLambdaQueryWrapper = new LambdaQueryWrapper<>();
        foodLambdaQueryWrapper.eq(Food::getCategoryId,categoryId);
        List<Food> foods = foodService.list(foodLambdaQueryWrapper);
        return Result.success(foods);
    }

    @GetMapping("/getById")
    public Result getById(@RequestParam Integer id){
        
        return Result.success(foodService.getById(id));
    }


    @GetMapping("/recommend")
    public Result recommend(@RequestParam Integer userId, @RequestParam Integer time){
        if(time==1){
            List<Food> foods = getAllFood(time, userId);
            List<Food> breakfast = getFood(foods);
            return Result.success(breakfast);
        }else if(time==2){
            List<Food> foods = getAllFood(time, userId);
            List<Food> lunch = getFood(foods);
            return Result.success(lunch);
        }else {
            List<Food> foods = getAllFood(time, userId);
            List<Food> dinner = getFood(foods);
            return Result.success(dinner);
        }


    }
    @GetMapping("/all")
    public Result Food(@RequestParam Integer userId, @RequestParam Integer time){
       List<Food> foods = getAllFood(time,userId);
       return Result.success(foods);
    }


    public List<Food> getAllFood(int time,int userId){
        LambdaQueryWrapper<Food> foodLambdaQueryWrapper = new LambdaQueryWrapper<>();
        User user = userService.getById(userId);
        foodLambdaQueryWrapper.eq(Food::getPurposeId,user.getPurposeId())
                .eq(Food::getTime,time);

        List<Food> food1 = foodService.list(foodLambdaQueryWrapper);
        return food1;
    }

//    随机出菜品
    public List<Food> getFood(List<Food> foods){
        List<Food> FoodList = new ArrayList<>();
        Random random = new Random();
        Set<Integer> selectedIndexes = new HashSet<>();
        int numSelections = 2;

        for (int i = 0; i < numSelections; i++) {
            int randomIndex;

            do {
                randomIndex = random.nextInt(foods.size());
            } while (selectedIndexes.contains(randomIndex));

            selectedIndexes.add(randomIndex);

            Food food = foods.get(randomIndex);
            FoodList.add(food);

        }
        return FoodList;
    }




}
