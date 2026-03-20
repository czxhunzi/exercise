package com.example.springboot.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Friend;
import com.example.springboot.service.IFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin//解决跨域问题
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    IFriendService friendService;

    @GetMapping("/list")
    public Result getList(){
        return Result.success(friendService.list());
    }
//  添加好友
    @PostMapping("/addFriend")
    public Result addFriend(@RequestParam Integer user1Id, @RequestParam Integer user2Id){
        LambdaQueryWrapper<Friend> friendLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        friendLambdaQueryWrapper1.eq(Friend::getUser1Id,user1Id)
                .eq(Friend::getUser2Id,user2Id);
        Friend friend1 = friendService.getOne(friendLambdaQueryWrapper1);
        LambdaQueryWrapper<Friend> friendLambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        friendLambdaQueryWrapper2.eq(Friend::getUser1Id,user2Id)
                .eq(Friend::getUser2Id,user1Id);
        Friend friend2 = friendService.getOne(friendLambdaQueryWrapper2);
        if(friend1 == null && friend2 == null){
            Friend one = new Friend();
            one.setUser1Id(user1Id);
            one.setUser2Id(user2Id);
            one.setStatus(1);
            friendService.save(one);
            return Result.success();
        }else if(friend1 == null){//以前对方向它发过请求
            friend2.setStatus(2);
            friendService.updateById(friend2);
            return Result.success();
        }else {
            return Result.success();
        }
    }
//    查询好友
    @GetMapping("/friendList")
    public Result friendList(@RequestParam Integer userId){
//        LambdaQueryWrapper<Friend> friendLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        friendLambdaQueryWrapper.nested(i -> i.eq(Friend::getUser1Id,userId).or().eq(Friend::getUser2Id,userId)).eq(Friend::getStatus,3);
//        List<Friend> friends = friendService.list(friendLambdaQueryWrapper);

//        ArrayList<Integer> list = new ArrayList<>();
//        for (Friend friend : friends) {
//            if(friend.getUser1Id().equals(userId)){
//                list.add(friend.getUser2Id());
//            }else {
//                list.add(friend.getUser1Id());
//            }
//        }
        return Result.success(getFriendList(userId));
    }
//    查询好友申请
    @GetMapping("/friendRequest")
    public Result friendRequest(@RequestParam Integer userId){
        LambdaQueryWrapper<Friend> friendLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendLambdaQueryWrapper.and(i -> i.eq(Friend::getUser2Id,userId).ne(Friend::getStatus,3))
                .or(i -> i.eq(Friend::getUser1Id,userId).eq(Friend::getStatus,2));
        List<Friend> friends = friendService.list(friendLambdaQueryWrapper);
        return Result.success(getFriend(friends,userId));

    }

//    查询好友的方法别人也要用，把它抽象出来
    public ArrayList<Integer> getFriendList(Integer userId){
        LambdaQueryWrapper<Friend> friendLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendLambdaQueryWrapper.nested(i -> i.eq(Friend::getUser1Id,userId).or().eq(Friend::getUser2Id,userId)).eq(Friend::getStatus,3);
        List<Friend> friends = friendService.list(friendLambdaQueryWrapper);
        return getFriend(friends,userId);
    }

    public ArrayList<Integer> getFriend(List<Friend> friends, Integer userId){
        ArrayList<Integer> list = new ArrayList<>();
        for (Friend friend : friends) {
            if(friend.getUser1Id().equals(userId)){
                list.add(friend.getUser2Id());
            }else {
                list.add(friend.getUser1Id());
            }
        }
        return list;
    }

}
