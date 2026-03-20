package com.example.springboot.controller;

import com.example.springboot.entity.Plan;
import com.example.springboot.entity.dto.changePlan;
import com.example.springboot.service.IModeService;
import com.example.springboot.service.IPlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class PlanControllerTest {

    @Autowired
    IPlanService planService;

    @Autowired
    IModeService modeService;

    Integer id = null;

    @Test
    void getAll() {
        boolean flag = false;
        List<Plan> planList = planService.list();
        if(planList!=null&&planList.size()!=0){
            flag = true;
        }
        assertTrue(flag,"返回值为True断言失败");
    }

    @Test
    void getById() {
        assertNull(id,"初始值不为空");
        id = 160;
        Plan plan = planService.getById(id);
        Plan plan1 = planService.getById(id);
        assertEquals(plan,plan1,"2个值不相同");
        assertSame(plan,plan1,"2个值不是同一个对象");
        changePlan changePlan = new changePlan();
        changePlan.setId(plan.getId());
        changePlan.setModeId(plan.getModeId());
        changePlan.setTime(plan.getTime());
        changePlan.setMode(modeService.getById(plan.getModeId()).getName());
        assertNotNull(plan,"判断不为空断言失败");
    }
}