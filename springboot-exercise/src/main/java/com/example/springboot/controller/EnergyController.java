package com.example.springboot.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.entity.*;
import com.example.springboot.entity.dto.Detail;
import com.example.springboot.entity.dto.Echarts;
import com.example.springboot.entity.dto.Home;
import com.example.springboot.entity.dto.Rank;
import com.example.springboot.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin//解决跨域问题
@RequestMapping("/energy")
public class EnergyController {

    @Autowired
    IEnergyService energyService;

    @Autowired
    IUserService userService;

    @Autowired
    IPurposeService purposeService;

    @Autowired
    IConsumeService consumeService;

    @Autowired
    ConsumeController consumeController;

    @Autowired
    EatController eatController;

    @Autowired
    FriendController friendController;
    LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

    @GetMapping("/list")
    public Result getAll() {
        return Result.success(energyService.list());
    }


    @PostMapping("/consume")
    public Result consume(@RequestParam Integer id, @RequestParam Integer egy) {


        LocalDate today = LocalDate.now(); // 获取当前日期
        LambdaUpdateWrapper<Energy> energyLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        energyLambdaUpdateWrapper.eq(Energy::getUserId, id)
                .apply("DATE(createtime) = '" + today + "'") // 添加判断条件：createtime的日期等于今天
                .setSql("consume = consume + " + egy);
        energyService.update(energyLambdaUpdateWrapper);
        return Result.success();
    }


    //调试时当今天没有数据时它会爆空指针异常，改一下数据库的实际即可,之前的index的接口，现在我们已经不使用了
    @GetMapping("/echarts/{id}")
    public Result getEcharts(@PathVariable Integer id) {
        Echarts echarts = new Echarts();
        User user = userService.getById(id);
        LambdaQueryWrapper<Purpose> partLambdaQueryWrapper = new LambdaQueryWrapper<>();
        partLambdaQueryWrapper.eq(Purpose::getId, user.getPartId());
        Purpose purpose = purposeService.getOne(partLambdaQueryWrapper);
        echarts.setTotal(purpose.getEnergy());
        LambdaQueryWrapper<Energy> energyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        energyLambdaQueryWrapper.eq(Energy::getUserId, id);
        List<Energy> list = energyService.list(energyLambdaQueryWrapper);
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        for (Energy energy : list) {
            LocalDate createDate = energy.getCreatetime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (createDate.equals(today)) {
                // createtime 是今天
                echarts.setPresent(energy.getConsume());
            } else {
                // createtime 不是今天
            }
        }
        echarts.setTotal(echarts.getTotal() - echarts.getPresent());
        return Result.success(echarts);
    }

    @GetMapping("getById/{id}")
    public Result getById(@PathVariable Integer id) {
        ZoneId zone = ZoneId.of("Asia/Shanghai"); // 请将 "时区代码" 替换为您所需的时区代码，例如 "Asia/Shanghai"
        ZonedDateTime now = ZonedDateTime.now(zone);
        LocalDate currentDate = now.toLocalDate();
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);

        ZonedDateTime startZonedDateTime = startDateTime.atZone(zone);
        ZonedDateTime endZonedDateTime = endDateTime.atZone(zone);
        System.out.println(startZonedDateTime);
        System.out.println(endZonedDateTime);
        LambdaQueryWrapper<Energy> energyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        energyLambdaQueryWrapper.eq(Energy::getUserId, id)
                .between(Energy::getCreatetime, startZonedDateTime, endZonedDateTime)
                .orderByAsc(Energy::getCreatetime);
        return Result.success(energyService.list(energyLambdaQueryWrapper));
    }


    //    每天早上创建一个活跃度为0的energy
    @Scheduled(cron = "1 0 0 * * ?")
    public void create() {
        List<User> users = userService.list();
        for (User user : users) {
            Energy energy = new Energy();
            energy.setUserId(user.getId());
            energy.setConsume(0);
            energy.setIntake(0);
            energy.setCreatetime(new Date());
            energy.setLiveness(0);
            energyService.save(energy);
        }
    }

    //    在晚上的11点统计今天所有用户的消耗和摄入
    @Scheduled(cron = "59 59 23 * * ?") // 每天凌晨执行一次，留下一个疑问，好像每晚统一意义不大
    public void statistic() {
        List<User> users = userService.list();
        for (User user : users) {

            Home home = consumeController.getHome(user.getId());
            Detail detail = eatController.getDetail(user.getId());//我们不以注入的方式使用手动实例化时，它会抛出空指针异常
            LambdaQueryWrapper<Energy> energyLambdaQueryWrapper = new LambdaQueryWrapper<>();
            energyLambdaQueryWrapper.eq(Energy::getUserId, user.getId())
                    .between(Energy::getCreatetime, start, end);
            Energy energy = energyService.getOne(energyLambdaQueryWrapper);
            energy.setConsume(home.getPresent());
            energy.setIntake(detail.getEnergy());
            energyService.updateById(energy);
        }
    }

    @GetMapping("/getDay")
    public Result getDay(@RequestParam Integer userId, @RequestParam Integer index) {
        LocalDate now = LocalDate.now();

        // 获取指定星期的开始时间
        LocalDate Week = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusDays(index);
        LocalDateTime startOfDay = LocalDateTime.of(Week, LocalTime.MIN);
        // 获取指定星期的结束时间
        LocalDateTime endOfDay = LocalDateTime.of(Week, LocalTime.MAX);

        System.out.println("开始时间: " + startOfDay);
        System.out.println("结束时间: " + endOfDay);
        LambdaQueryWrapper<Energy> energyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        energyLambdaQueryWrapper.eq(Energy::getUserId, userId)
                .between(Energy::getCreatetime, startOfDay, endOfDay);
        Energy one = energyService.getOne(energyLambdaQueryWrapper);
        return Result.success(one);
    }

    //打卡的活跃度提交
    @GetMapping("putLiveness")
    public Result putLiveness(@RequestParam Integer userId) {
        User user = userService.getById(userId);
        user.setRecord(true);
        userService.updateById(user);
        increase(userId);
        return Result.success();

    }

    //     点击计时按钮和播放按钮就能获得活跃度
    @PostMapping("/addLiveness")
    public Result addLiveness(@RequestParam Integer userId) {
        increase(userId);
        return Result.success();
    }


    //     查询当天的活跃度
    @GetMapping("/getLiveness")
    public Result getLiveness(@RequestParam Integer userId) {
        LambdaQueryWrapper<Energy> energyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        energyLambdaQueryWrapper.eq(Energy::getUserId, userId)
                .between(Energy::getCreatetime, start, end);
        return Result.success(energyService.getOne(energyLambdaQueryWrapper));
    }

    @GetMapping("/rank")
    public Result rank(@RequestParam Integer index1, @RequestParam Integer index2, @RequestParam Integer userId) {
        if (index1 == 0) {
            return rankToday(index2, userId);
        } else if (index1 == 1) {
            return rankWeek(index2, userId);
        }
        return null;
    }


    public Result rankToday(Integer flag, Integer userId) {
        LambdaQueryWrapper<Energy> energyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        energyLambdaQueryWrapper.between(Energy::getCreatetime, start, end)
                .orderByDesc(Energy::getLiveness);
        List<Energy> energyList = energyService.list(energyLambdaQueryWrapper);
        List<Rank> ranks = new ArrayList<>();
        for (Energy energy : energyList) {
            Rank rank = new Rank();
            rank.setUserId(energy.getUserId());
            rank.setLiveness(energy.getLiveness());
            User user = userService.getById(energy.getUserId());
            rank.setName(user.getUsername());
            ranks.add(rank);
        }
        if (flag == 1) {
            List<Rank> newRanks = filter(userId, ranks);
            return Result.success(newRanks);
        }
        return Result.success(ranks);
    }


    public Result rankWeek(Integer flag, Integer userId) {
        // 获取当前日期时间
        LocalDateTime now = LocalDateTime.now();
        // 获取本周的周一日期
        LocalDate monday = now.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 获取本周的周天日期
        LocalDate sunday = now.toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        // 本周周一的开始时间
        LocalDateTime startOfWeek = LocalDateTime.of(monday, LocalTime.MIN);
        // 本周周天的结束时间
        LocalDateTime endOfWeek = LocalDateTime.of(sunday, LocalTime.MAX);
        System.out.println("本周周一开始时间：" + startOfWeek);
        System.out.println("本周周天结束时间：" + endOfWeek);
        LambdaQueryWrapper<Energy> energyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        energyLambdaQueryWrapper.between(Energy::getCreatetime, startOfWeek, endOfWeek);
        List<Energy> energyList = energyService.list(energyLambdaQueryWrapper);
        List<Rank> ranks = energyList.stream()
                .collect(Collectors.groupingBy(Energy::getUserId, Collectors.summingInt(Energy::getLiveness)))
                .entrySet()
                .stream()
                .map(entry -> new Rank(entry.getKey(), null, entry.getValue()))
                .collect(Collectors.toList());
        Collections.sort(ranks, Comparator.comparingInt(Rank::getLiveness).reversed());
        for (Rank rank : ranks) {
            User user = userService.getById(rank.getUserId());
            rank.setName(user.getUsername());
        }
        if (flag == 1) {
            List<Rank> newRanks = filter(userId, (ArrayList<Rank>) ranks);
            return Result.success(newRanks);
        }
        return Result.success(ranks);
    }

    //    在有要求是去掉不是好友的人
    public List<Rank> filter(Integer userId, List<Rank> ranks) {
        ArrayList<Integer> friendList = friendController.getFriendList(userId);
        friendList.add(userId);
        Iterator<Rank> iterator = ranks.iterator();
        while (iterator.hasNext()) {
            Rank rank = iterator.next();
            if (!friendList.contains(rank.getUserId())) {
                iterator.remove();
            }
        }
        return ranks;
    }

    //签到和点击播放和计时自动增加5活跃度
    public void increase(Integer userId) {
        LambdaQueryWrapper<Energy> energyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        energyLambdaQueryWrapper.eq(Energy::getUserId, userId)
                .between(Energy::getCreatetime, start, end);
        Energy one = energyService.getOne(energyLambdaQueryWrapper);
        one.setLiveness(one.getLiveness() + 5);
        energyService.updateById(one);
    }

}
