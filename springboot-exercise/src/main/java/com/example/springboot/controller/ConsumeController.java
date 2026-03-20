package com.example.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Consume;
import com.example.springboot.entity.Energy;
import com.example.springboot.entity.Mode;
import com.example.springboot.entity.User;
import com.example.springboot.entity.dto.Home;
import com.example.springboot.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@RestController
@CrossOrigin//解决跨域问题
@RequestMapping("/consume")
public class ConsumeController {
    @Autowired
    IConsumeService consumeService;

    @Autowired
    IModeService modeService;

    @Autowired
    IUserService userService;

    @Autowired
    IEnergyService energyService;

    final int STEP = 70;

    LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);


    @GetMapping("/list")
    public Result getAll() {
        return Result.success(consumeService.list());
    }

    @PostMapping("/add")
    public Result add(@RequestParam Integer userId, @RequestParam Integer modeId, @RequestParam Integer time) {
//        添加经验
        User user = userService.getById(userId);
        if (user.getExperience() + time >= 100) {
            user.setLevel(user.getLevel() + 1);
            user.setExperience(user.getExperience() + time - 100);
            userService.updateById(user);
        } else {
            user.setExperience(user.getExperience() + time);
            userService.updateById(user);
        }
//        添加活跃度
        LambdaQueryWrapper<Energy> energyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        energyLambdaQueryWrapper.eq(Energy::getUserId, userId)
                .between(Energy::getCreatetime, start, end);
        Energy energy = energyService.getOne(energyLambdaQueryWrapper);
        energy.setLiveness(energy.getLiveness() + time);
        energyService.updateById(energy);


        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LambdaQueryWrapper<Consume> consumeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        consumeLambdaQueryWrapper.eq(Consume::getUserId, userId)
                .eq(Consume::getModeId, modeId)
                .between(Consume::getCreatetime, start, end);
        Consume one = consumeService.getOne(consumeLambdaQueryWrapper);
        if (one == null) {
            Consume consume = new Consume();
            consume.setUserId(userId);
            consume.setModeId(modeId);
            consume.setTime(time);
            consume.setCreatetime(new Date());
            consumeService.save(consume);
        } else {
            one.setTime(one.getTime() + time);
            consumeService.saveOrUpdate(one);
        }

        return Result.success();
    }

    @GetMapping("/getDay")
    public Result getDay(@RequestParam Integer userId, @RequestParam Integer index) {
        // 获取当前日期
        LocalDate now = LocalDate.now();

        // 获取指定星期的开始时间
        LocalDate Week = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusDays(index);
        LocalDateTime startOfDay = LocalDateTime.of(Week, LocalTime.MIN);
        // 获取指定星期的结束时间
        LocalDateTime endOfDay = LocalDateTime.of(Week, LocalTime.MAX);

        System.out.println("开始时间: " + startOfDay);
        System.out.println("结束时间: " + endOfDay);
        LambdaQueryWrapper<Consume> consumeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        consumeLambdaQueryWrapper.eq(Consume::getUserId, userId)
                .between(Consume::getCreatetime, startOfDay, endOfDay);
        List<Consume> consumeList = consumeService.list(consumeLambdaQueryWrapper);
        for (Consume consume : consumeList) {
            LambdaQueryWrapper<Mode> modeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            modeLambdaQueryWrapper.eq(Mode::getId, consume.getModeId());
            Mode mode = modeService.getOne(modeLambdaQueryWrapper);
            consume.setMode(mode.getName());
        }
        return Result.success(consumeList);
    }

    //获取今日的运动情况
    @GetMapping("/getToday")
    public Result getDay(@RequestParam Integer userId) {

        LambdaQueryWrapper<Consume> consumeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        consumeLambdaQueryWrapper.eq(Consume::getUserId, userId)
                .between(Consume::getCreatetime, start, end);
        List<Consume> consumeList = consumeService.list(consumeLambdaQueryWrapper);
        for (Consume consume : consumeList) {
            LambdaQueryWrapper<Mode> modeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            modeLambdaQueryWrapper.eq(Mode::getId, consume.getModeId());
            Mode mode = modeService.getOne(modeLambdaQueryWrapper);
            consume.setMode(mode.getName());
        }
        return Result.success(consumeList);
    }

    //获取选择日期的运动情况
    @GetMapping("/getChoiceDay")
    public Result getChoiceDay(@RequestParam Integer userId, @RequestParam String dateString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        LambdaQueryWrapper<Consume> consumeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        consumeLambdaQueryWrapper.ge(Consume::getCreatetime, date.atStartOfDay())
                .lt(Consume::getCreatetime, date.plusDays(1).atStartOfDay());
        List<Consume> consumeList = consumeService.list(consumeLambdaQueryWrapper);
        for (Consume consume : consumeList) {
            LambdaQueryWrapper<Mode> modeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            modeLambdaQueryWrapper.eq(Mode::getId, consume.getModeId());
            Mode mode = modeService.getOne(modeLambdaQueryWrapper);
            consume.setMode(mode.getName());
        }
        return Result.success(consumeList);
    }

    @PostMapping("/walk")
    public Result Walk(@RequestBody Map<String, Object> requestData) {
        int userId = (int) requestData.get("userId");
        List<Integer> dataArray = getDataArray(requestData);
//        System.out.println(userId);
//        System.out.println(dataArray);
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 获取本周的开始日期（星期一）
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 获取本周的结束日期（下一个星期一减一天）
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).minusDays(1);
        LambdaQueryWrapper<Consume> consumeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        consumeLambdaQueryWrapper.eq(Consume::getUserId, userId)
                .eq(Consume::getModeId, 1)
                .between(Consume::getCreatetime, startOfWeek, endOfWeek);
        List<Consume> consumeList = consumeService.list(consumeLambdaQueryWrapper);
        int index = 30;
        for (Consume consume : consumeList) {
            consume.setTime(dataArray.get(index) / STEP);
            index--;
            consumeService.updateById(consume);
        }
        return Result.success();
    }


    @PostMapping("/change")
    public Result Change(@RequestBody List<Consume> consumeList) {
        System.out.println(consumeList);
        Date createTime = consumeList.get(0).getCreatetime();
        System.out.println(createTime);
        // 将 Date 对象转换为 LocalDateTime 对象
        LocalDateTime localDateTime = LocalDateTime.ofInstant(createTime.toInstant(), ZoneId.systemDefault());
        // 获取某天的开始时间
        LocalDateTime startDateTime = localDateTime.with(LocalTime.MIN);
        // 获取某天的结束时间
        LocalDateTime endDateTime = localDateTime.with(LocalTime.MAX);
        System.out.println("开始时间: " + startDateTime);
        System.out.println("结束时间: " + endDateTime);
        for (Consume consume : consumeList) {
            LambdaQueryWrapper<Consume> consumeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            consumeLambdaQueryWrapper.eq(Consume::getUserId, consume.getUserId())
                    .eq(Consume::getModeId, consume.getModeId())
                    .between(Consume::getCreatetime, startDateTime, endDateTime);
            Consume one = consumeService.getOne(consumeLambdaQueryWrapper);
            if (one == null) {
                consume.setCreatetime(createTime);
                consumeService.save(consume);
            } else if (!Objects.equals(consume.getTime(), one.getTime())) {
                one.setTime(consume.getTime());
                consumeService.updateById(one);
            }
        }


        return Result.success();
    }


    @GetMapping("/getEnergy")
    public Result getEnergy(@RequestParam Integer userId) {
        Home home = getHome(userId);
        return Result.success(home);
    }

    @Scheduled(cron = "1 0 0 * * ?")
    public void saveWalk() {
        List<User> users = userService.list();
        for (User user : users) {
            Consume consume = new Consume();
            consume.setUserId(user.getId());
            consume.setModeId(1);
            consume.setTime(0);
            consume.setCreatetime(new Date());
            consumeService.save(consume);
        }
    }

    public Home getHome(Integer userId) {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LambdaQueryWrapper<Consume> consumeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        consumeLambdaQueryWrapper.eq(Consume::getUserId, userId)
                .between(Consume::getCreatetime, start, end);
        List<Consume> consumeList = consumeService.list(consumeLambdaQueryWrapper);
        Home home = new Home();
        for (Consume consume : consumeList) {
            LambdaQueryWrapper<Mode> modeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            modeLambdaQueryWrapper.eq(Mode::getId, consume.getModeId());
            Mode one = modeService.getOne(modeLambdaQueryWrapper);
            home.setPresent(home.getPresent() + one.getEnergy() * consume.getTime());
        }
        home.setTotal(1000 - home.getPresent());
        return home;
    }

    //    前端传来的数据强转为Integer不一定安全，使用泛型转化
    private List<Integer> getDataArray(Map<String, Object> requestData) {
        List<Integer> dataArray = new ArrayList<>();
        Object object = requestData.get("dataArray");
        if (object instanceof List) {
            List<?> list = (List<?>) object;
            for (Object item : list) {
                if (item instanceof Integer) {
                    dataArray.add((Integer) item);
                }
            }
        }
        return dataArray;
    }

}
