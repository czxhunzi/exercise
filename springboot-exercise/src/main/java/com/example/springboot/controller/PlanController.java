package com.example.springboot.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Course;
import com.example.springboot.entity.Mode;
import com.example.springboot.entity.Plan;
import com.example.springboot.entity.Strategy;
import com.example.springboot.entity.dto.Project;
import com.example.springboot.entity.dto.changePlan;
import com.example.springboot.service.*;
import com.example.springboot.utils.WeixinProperties;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@RestController
@CrossOrigin//解决跨域问题
@RequestMapping("/plan")
public class PlanController {
    @Autowired
    IPlanService planService;

    @Autowired
    IStrategyService strategyService;

    @Autowired
    ICourseService courseService;

    @Autowired
    IModeService modeService;

    @Autowired
    IUserService userService;

    @Autowired
    WeixinProperties weixinProperties;

    //必需要自己去手动配置一下，去掉@Autowird句解不会报错，但功能会失效
    @Autowired
    private RestTemplate restTemplate;

    final int MIDDLE = 150;

    final int HIGH = 75;

    final int STRONG = 2;

    @GetMapping("/list")
    public Result getAll() {
        return Result.success(planService.list());
    }

    @GetMapping("/getOne")
    public Result getOne(@RequestParam Integer id) {
        Plan plan = planService.getById(id);
        changePlan changePlan = new changePlan();
        changePlan.setId(plan.getId());
        changePlan.setModeId(plan.getModeId());
        changePlan.setTime(plan.getTime());
        changePlan.setMode(modeService.getById(plan.getModeId()).getName());
        return Result.success(changePlan);

    }

    @PostMapping("/update")
    public Result update(@RequestBody changePlan newPlan) {
        Plan plan = planService.getById(newPlan.getId());
        plan.setModeId(newPlan.getModeId());
        plan.setTime(newPlan.getTime());
        planService.updateById(plan);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
//        mybatis-plus默认不做更新操作，只能通过updateWrapper强制更新
        LambdaUpdateWrapper<Plan> planLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        planLambdaUpdateWrapper.eq(Plan::getId, id)
                .set(Plan::getModeId, null)
                .set(Plan::getTime, null)
                .set(Plan::getNotice, null);
        planService.update(planLambdaUpdateWrapper);
        return Result.success();
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

        LambdaQueryWrapper<Plan> planLambdaQueryWrapper = new LambdaQueryWrapper<>();
        planLambdaQueryWrapper.eq(Plan::getUserId, id)
                .between(Plan::getCreatetime, startZonedDateTime, endZonedDateTime);

        return Result.success(planService.list(planLambdaQueryWrapper));

    }

    @PostMapping("/newPlan")
    public Result newPlan(@RequestBody Project project) {
        double height = (double) project.getHeight() / 100;
        double weight = project.getWeight();
        double BMI = weight / (height * height);
        List<LocalDate> weekend = weekend();
        int days = project.getWeek().size();
        if (BMI < 24) {
            midPlan(project, 25);
            highPlan(project, 15);
        } else if (BMI >= 24 && BMI < 28) {
            midPlan(project, 40);
            highPlan(project, 25);
        } else {
            midPlan(project, 50);
            highPlan(project, 30);
        }
        return Result.success();
    }

    //白盒测试的代码
    public Result sportsPlan(@RequestBody Project project) {
        double height = (double) project.getHeight() / 100;
        double weight = project.getWeight();
        double BMI1 = weight / (height * height);
        double BMI2 = (weight * weight) / height;

        if(BMI1<25){
            midPlan(project,25);
            if(BMI2<50){
                midPlan(project,30);
                if(BMI2-BMI1<10){
                    midPlan(project,5);
                }else {
                    highPlan(project,5);
                }
            }else {
                highPlan(project,30);
            }
        }else {
            highPlan(project,50);
        }
        return Result.success();
    }

    @GetMapping("/test")
    public Result test() {
        double height = 175 / 100;
        double weight = 50;
        double BMI = weight / (height * height);
        System.out.println(BMI);
        List<LocalDate> weekend = weekend();
        System.out.println(weekend);
        System.out.println(transform(weekend.get(0)));
        return Result.success();
    }

    @PostMapping("/deletePlan")
    public Result deletePlan(@RequestParam Integer userId) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 获取本周的开始日期（星期一）
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 获取本周的结束日期（下一个星期一减一天）
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).minusDays(1);
        LambdaQueryWrapper<Plan> planLambdaQueryWrapper = new LambdaQueryWrapper<>();
        planLambdaQueryWrapper.eq(Plan::getUserId, userId)
                .between(Plan::getCreatetime, startOfWeek, endOfWeek);
        planService.remove(planLambdaQueryWrapper);
        return Result.success();
    }

    @PostMapping("/count/{id}/{time}")
    public Result count(@PathVariable Integer id, @PathVariable Integer time) {
        LocalDate today = LocalDate.now(); // 获取当前日期
        LambdaUpdateWrapper<Plan> planLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        planLambdaUpdateWrapper.setSql("time = time + " + time)
                .eq(Plan::getId, id);
        planService.update(planLambdaUpdateWrapper);
        return Result.success();
    }

    @GetMapping("/playVideo/{modeId}")
    public Result playVideo(@PathVariable Integer modeId) {
        LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseLambdaQueryWrapper.eq(Course::getModeId, modeId);
        List<Course> list = courseService.list(courseLambdaQueryWrapper);
        return Result.success(list);
    }

    @PostMapping("/notice")
    public Result Notice(@RequestParam Integer id, @RequestParam String date) {
        LambdaUpdateWrapper<Plan> planLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        planLambdaUpdateWrapper.eq(Plan::getId, id)
                .set(Plan::getNotice, date);
        planService.update(planLambdaUpdateWrapper);
        return Result.success();
    }

    //    添加新计划
    @PostMapping("/addPlan")
    public Result addPlan(@RequestBody Plan plan) {
        Plan plan1 = planService.getById(plan.getId());
        plan1.setModeId(plan.getModeId());
        plan1.setTime(plan.getTime());
        planService.updateById(plan1);
        return Result.success();
    }


    @Scheduled(fixedRate = 60000) // 每60秒执行一次
    @PostMapping("/try")
    public void Try() {
        // 获取当日的起止时间和终止时间
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LambdaQueryWrapper<Plan> planLambdaQueryWrapper = new LambdaQueryWrapper<>();
        planLambdaQueryWrapper.between(Plan::getCreatetime, start, end);

        List<Plan> plans = planService.list(planLambdaQueryWrapper);
        for (Plan plan : plans) {
            if (plan.getNotice() != null) {
                LocalDateTime time = deal(plan.getNotice());
                String openid = userService.getById(plan.getUserId()).getOpenid();
                ZoneId zoneId = ZoneId.systemDefault();
                Date date = Date.from(time.atZone(zoneId).toInstant());
                if (isSameHourAndMinute(new Date(), date)) {
                    sendMessage(openid);
                }
            }

        }

    }

    private void midPlan(Project project, int time) {
        List<LocalDate> weekend = weekend();
        for (int i = 0; i < 7; i++) {
            Random random = new Random();
            Plan plan = new Plan();
            LambdaQueryWrapper<Mode> modeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            modeLambdaQueryWrapper.eq(Mode::getLevel, "mid");
            List<Mode> modes = modeService.list(modeLambdaQueryWrapper);
            int randomIndex = random.nextInt(modes.size());
            plan.setUserId(project.getId());
            plan.setModeId(modes.get(randomIndex).getId());
            plan.setTime(time);
            plan.setCreatetime(transform(weekend.get(i)));
            planService.save(plan);
        }
    }

    private void highPlan(Project project, int time) {
        List<LocalDate> weekend = weekend();
        int days = project.getWeek().size();
        System.out.println(project.getWeek());
        for (int flag = 0; flag < 7; flag++) {
            boolean x = false;
            int i;
            for (i = 0; i < days; i++) {
                if (flag == project.getWeek().get(i)) {
                    x = true;
                    break;
                }
            }
            if (x) {
                Random random = new Random();
                Plan plan = new Plan();
                int randomIndex1 = random.nextInt(project.getMode().size());//记得改一下前端传来的只有高水平的运动
                plan.setUserId(project.getId());
                plan.setModeId(project.getMode().get(randomIndex1));
                plan.setTime(time);
                plan.setCreatetime(transform(weekend.get(project.getWeek().get(i))));
                planService.save(plan);
                LambdaQueryWrapper<Mode> modeLambdaQueryWrapper = new LambdaQueryWrapper<>();
                modeLambdaQueryWrapper.eq(Mode::getLevel, "high");
                List<Mode> modes = modeService.list(modeLambdaQueryWrapper);
                int randomIndex2 = random.nextInt(modes.size());
                while (Objects.equals(project.getMode().get(randomIndex1), modes.get(randomIndex2).getId())) {
                    randomIndex2 = random.nextInt(modes.size());
                }
                Plan plan1 = new Plan();//如果我们使用上面的plan，会抛异常java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '23' for key 'PRIMARY'
                plan1.setUserId(project.getId());
                plan1.setModeId(modes.get(randomIndex2).getId());
                plan1.setTime(time);
                plan1.setCreatetime(transform(weekend.get(project.getWeek().get(i))));
                planService.save(plan1);
            } else {
                Plan plan = new Plan();
                plan.setUserId(project.getId());
                plan.setCreatetime(transform(weekend.get(flag)));
                planService.save(plan);
                Plan plan1 = new Plan();
                plan1.setUserId(project.getId());
                plan1.setCreatetime(transform(weekend.get(flag)));
                planService.save(plan1);
            }
        }

    }


    //LocalDate转Date
    private Date transform(LocalDate localDate) {
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date date = Date.from(instant);
        return date;
    }


    //获取一个星期的日期
    private List<LocalDate> weekend() {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 获取本周的开始日期（星期一）
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 获取本周的结束日期（下一个星期一减一天）
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).minusDays(1);
        // 定义日期格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 存储每天日期的列表
        List<LocalDate> dates = new ArrayList<>();
        // 遍历每天的日期
        LocalDate date = startOfWeek;
        while (!date.isAfter(endOfWeek)) {
            dates.add(date);
            date = date.plusDays(1);
        }
        System.out.println(date);
        return dates;
    }

    // 判断两个时间是否相同（仅比较时、分、秒）
    // 判断两个时间的小时和分钟是否相同
    private boolean isSameHourAndMinute(Date time1, Date time2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(time1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(time2);

        int hour1 = cal1.get(Calendar.HOUR_OF_DAY);
        int minute1 = cal1.get(Calendar.MINUTE);

        int hour2 = cal2.get(Calendar.HOUR_OF_DAY);
        int minute2 = cal2.get(Calendar.MINUTE);

        return hour1 == hour2 && minute1 == minute2;
    }

    private LocalDateTime deal(String timeString) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();

        // 将字符串分割成小时和分钟
        String[] timeParts = timeString.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // 构建完整的日期时间对象
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, 0);
        return dateTime;
    }

    public void sendMessage(String openid) {
        String url = weixinProperties.getAccess() + "?grant_type=client_credential&appid=" + weixinProperties.getAppid() + "&secret=" + weixinProperties.getSecret();
        String responseBody = restTemplate.getForObject(url, String.class);
        System.out.println(responseBody);
        JSONObject jsonObject = JSON.parseObject(responseBody);//转换成object
        String access_token = jsonObject.get("access_token").toString();
        // 创建一个 JSONObject 对象表示 pushmsg 数据
        JSONObject msg = new JSONObject();
        // 设置 toUser 属性
        msg.put("touser", openid);
        // 设置 template_id 属性
        msg.put("template_id", "lnCQzBXb0Uy3FKKxTvgKPdrt5caLN1KW13KUrq2_Caw");
        // 创建一个内嵌的 JSONObject 对象表示 data 数据
        JSONObject data = new JSONObject();
        // 设置 thing1 属性
        JSONObject thing1 = new JSONObject();
        thing1.put("value", "你有待执行的运动计划");
        data.put("thing1", thing1);
        // 设置 time2 属性
        JSONObject time2 = new JSONObject();
        time2.put("value", "19:00");
        data.put("time2", time2);
        // 设置 data 属性
        msg.put("data", data);
        // 打印输出
        System.out.println(msg.toJSONString());

        // 将msg对象转换为JSON字符串
        String jsonData = JSON.toJSONString(msg);
        // 发送POST请求
        RestTemplate restTemplate = new RestTemplate();
        String url1 = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + access_token;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonData, headers);
        ResponseEntity<String> response = restTemplate.exchange(url1, HttpMethod.POST, httpEntity, String.class);
    }





}
