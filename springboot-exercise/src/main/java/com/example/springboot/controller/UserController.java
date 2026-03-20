package com.example.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.entity.*;
import com.example.springboot.entity.dto.WxUser;
import com.example.springboot.service.*;
import com.example.springboot.utils.WeixinProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RestController
@CrossOrigin//解决跨域问题
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    @Autowired
    IEnergyService energyService;

    @Autowired
    IPurposeService purposeService;

    @Autowired
    IPartService partService;

    @Autowired
    IConsumeService consumeService;

    @Autowired
    WeixinProperties weixinProperties;

    //必需要自己去手动配置一下，去掉@Autowird句解不会报错，但功能会失效
    @Autowired
    private RestTemplate restTemplate;

    public String openid;

    public String session_key;


    @PostMapping("/wxLogin")
    public Result wxLogin(@RequestBody WxUser wxUser) {
        System.out.println(wxUser.getCode());
//        拼接后端发送请求的URL 例如 https://api.weixin.qq.com/sns/jscode2session?appid=wxa4de78832ea93858&secret=a2efb3b602d96b2dee615b7a4dee451a&js_code=0b1JwPkl2xqHkb4VEjml2vVdua3JwPkq&grant_type=authorization_code
        String jscode2sessionUrl = weixinProperties.getJscode2sessionUrl() + "?appid=" + weixinProperties.getAppid() + "&secret=" + weixinProperties.getSecret() + "&js_code=" + wxUser.getCode() + "&grant_type=authorization_code";
        System.out.println(jscode2sessionUrl);
        String responseBody = restTemplate.getForObject(jscode2sessionUrl, String.class);
        System.out.println(responseBody);

        //
        JSONObject jsonObject = JSON.parseObject(responseBody);//转换成object
        openid = jsonObject.get("openid").toString();//获取object中openid字段的值;
        session_key = jsonObject.get("session_key").toString();
        System.out.println(openid);
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getOpenid, openid);
        User user = new User();
        if (userService.getOne(userLambdaQueryWrapper) == null) {

            user.setOpenid(openid);
            user.setUsername("微信用户");
            user.setLevel(0);
            user.setExperience(0);
            user.setPurposeId(1);
            userService.save(user);
        }
        User one = userService.getOne(userLambdaQueryWrapper);
        one.setSession_key(session_key);
        one.setAppid(weixinProperties.getAppid());
        return Result.success(one);
    }

    @GetMapping("/getById")
    public Result getById(@RequestParam Integer userId) {
        User user = userService.getById(userId);
        LambdaQueryWrapper<Purpose> purposeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        purposeLambdaQueryWrapper.eq(Purpose::getId, user.getPurposeId());
        Purpose purpose = purposeService.getOne(purposeLambdaQueryWrapper);
        user.setPurpose(purpose.getName());
        return Result.success(user);
    }

    @GetMapping("/get")
    public Result getList() {
        return Result.success(userService.list());
    }

    @PostMapping("/login")
    public Result check(@RequestBody User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        queryWrapper.eq(User::getPassword, DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        User one = userService.getOne(queryWrapper);
        if (one != null) {
            return Result.success(one);
        } else {
            return Result.error();
        }
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        if(user.getPassword()==null||user.getPassword().length()==0){
            return Result.error("-1","用户名和密码不能为空");
        }
        userService.save(user);
        return Result.success();
    }

    @PostMapping("/confirm")
    public Result confirm(@RequestBody User user) {

        userService.saveOrUpdate(user);
        return Result.success();
    }

    @PostMapping("/purpose")
    public Result purpose(@RequestBody User user) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getId, user.getId());
        User user1 = userService.getOne(userLambdaQueryWrapper);
        user1.setPurposeId(user.getPurposeId());
        user1.setPartId(user.getPartId());
        userService.saveOrUpdate(user1);
        return Result.success();
    }


    @PostMapping("/send")
    public Result send() {
        return Result.success();
    }

    @GetMapping("/condition")
    public Result getCondition(@RequestParam Integer userId) {
        ZoneId zone = ZoneId.of("Asia/Shanghai"); // 请将 "时区代码" 替换为您所需的时区代码，例如 "Asia/Shanghai"
        ZonedDateTime now = ZonedDateTime.now(zone);
        LocalDate currentDate = now.toLocalDate();
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);

        ZonedDateTime startZonedDateTime = startDateTime.atZone(zone);
        ZonedDateTime endZonedDateTime = endDateTime.atZone(zone);
        LambdaQueryWrapper<Energy> energyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        energyLambdaQueryWrapper.eq(Energy::getUserId, userId)
                .between(Energy::getCreatetime, startZonedDateTime, endZonedDateTime);
        List<Energy> energyList = energyService.list(energyLambdaQueryWrapper);
        return Result.success(energyList);
    }

    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        userService.updateById(user);
        return Result.success();
    }

    //    每年的年龄加一岁
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void addAge() {
        List<User> users = userService.list();
        for (User user : users) {
            user.setAge(user.getAge() + 1);
            userService.updateById(user);
        }
    }

    //    每天早晨清除昨天的打卡记录
    @Scheduled(cron = "1 0 0 * * ?")
    public void cleanRecord() {
        List<User> users = userService.list();
        for (User user : users) {
            user.setRecord(false);
            userService.updateById(user);
        }
    }

    //    添加好友中的搜索
    @GetMapping("getByName")
    public Result getByName(@RequestParam String username, @RequestParam Integer id) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.like(User::getUsername, "%" + username + "%")
                .ne(User::getId, id);
        List<User> users = userService.list(userLambdaQueryWrapper);
        return Result.success(users);
    }


}
