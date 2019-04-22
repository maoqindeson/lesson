package io.renren.modules.generator.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.generator.aop.WebRecord;
import io.renren.modules.generator.entity.*;
import io.renren.modules.generator.form.SearchForm;
import io.renren.modules.generator.service.*;
import io.renren.modules.generator.utils.BaseResp;
import io.renren.modules.generator.utils.JWTUtil;
import io.renren.modules.generator.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/exercise")
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserExerciseService userExerciseService;

    /**
     * 查找单个习题
     */
    @WebRecord
    @RequestMapping("/getById")
    public BaseResp getById(HttpServletRequest request, Integer id) {
        if (null == id || 0 == id) {
            return BaseResp.error("id不能为空或0");
        }
        try {
            ExerciseEntity entity = exerciseService.getById(id);
            if (null == entity) {
                return BaseResp.error("找不到对应的记录,id为 : " + id);
            }
            return BaseResp.ok(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResp.error("找不到对应的记录,id为 : " + id);
    }

    /**
     * 阅读习题
     */
    @WebRecord
    @RequestMapping("/record")
    @RequiresAuthentication
    public BaseResp record(HttpServletRequest request, Integer exerciseId, String content) {
        String username = JWTUtil.getCurrentUsername(request);
        if (null == exerciseId || 0 == exerciseId) {
            return BaseResp.error("exerciseId不能为空");
        }
        UserExerciseEntity userExerciseEntity = userExerciseService.getOne(new QueryWrapper<UserExerciseEntity>().eq("username", username)
                .eq("exercise_id", exerciseId));
        if (null != userExerciseEntity) {
            return BaseResp.ok("exerciseId : " + exerciseId + "已记录");
        }
        ExerciseEntity exerciseEntity = exerciseService.getById(exerciseId);
        if (null==exerciseEntity){
            return BaseResp.error("找不到对应的记录,exercise_id为 : "+exerciseId);
        }
        List<UserExerciseEntity> list = userExerciseService.list(new QueryWrapper<UserExerciseEntity>().eq("username",username)
        .eq("exercise_id",exerciseId));
        if (null!=list&&!list.isEmpty()){
            return BaseResp.ok("该记录已存在");
        }
        Integer orgId = exerciseEntity.getOrgId();
        userExerciseEntity = new UserExerciseEntity();
        userExerciseEntity.setExerciseId(exerciseId);
        userExerciseEntity.setOrgId(orgId);
        userExerciseEntity.setUsername(username);
        userExerciseEntity.setHasComplete(1);
        if (!StringTools.isNullOrEmpty(content)) {
            userExerciseEntity.setContent(content);
        }
        boolean result = userExerciseService.saveOrUpdate(userExerciseEntity);
        if (!result) {
            return BaseResp.error("记录失败,exercise_id为 : " + exerciseId);
        }
        Map<String, Object> map = new HashMap<>();
        Map<String,Object> respMap = (Map<String, Object>) newList(request,orgId,10000).getData();
        Object newList = respMap.get("newList");
        map.put("newList",newList);
        return BaseResp.ok(map);
    }

    @WebRecord
    @RequestMapping("/newList")
    @RequiresAuthentication
    public BaseResp newList(HttpServletRequest request, Integer orgId, Integer limit) {
        String username = JWTUtil.getCurrentUsername(request);
        if (null == orgId || 0 == orgId) {
            return BaseResp.error("orgId不能为空或0");
        }
        try {
            if (null == limit || 0 == limit) {
                limit = 10;
            }
            Page page = new Page(1, limit);
            Map<String, Object> map = new HashMap<>();
            List<ExerciseEntity> newList = new ArrayList<>();
            //先查询用户之前练习记录,如果没有则默认加载
            UserExerciseEntity userExerciseEntity = userExerciseService.getOne(new QueryWrapper<UserExerciseEntity>().eq("username", username)
                    .eq("org_id", orgId).last("order by id desc limit 1"));
            if (null == userExerciseEntity) {
                newList = exerciseService.page(page, new QueryWrapper<ExerciseEntity>().eq("org_id", orgId).last("order by id asc ")).getRecords();
                if (null == newList || newList.isEmpty()) {
                    return BaseResp.ok("找不到对应的记录,orgId为 : " + orgId, new ArrayList<>());
                }
            } else {
                //否则继续往下加载
                Integer exerciseId = userExerciseEntity.getExerciseId();
                newList = exerciseService.page(page, new QueryWrapper<ExerciseEntity>().gt("id", exerciseId)
                        .eq("org_id", orgId).last("order by id asc ")).getRecords();
            }
            map.put("newList", newList);
            List<ExerciseEntity> oldList = exerciseService.getOldList(username);
            if (null==oldList||oldList.isEmpty()){
                map.put("oldList", new ArrayList<>());
            }else {
                map.put("oldList", oldList);
            }
            return BaseResp.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResp.error("找不到对应的记录,orgId为 : " + orgId);
    }

    /**
     * 查找单个习题
     */
    @WebRecord
    @RequestMapping("/saveExercise")
    public BaseResp saveExercise(@RequestBody ExerciseEntity exerciseEntity) {
        if (StringTools.isNullOrEmpty(exerciseEntity.getContent())) {
            return BaseResp.error("content为空");
        }
        if (StringTools.isNullOrEmpty(exerciseEntity.getType())) {
            return BaseResp.error("type为空");
        }
        if (null == exerciseEntity.getOrgId() || 0 == exerciseEntity.getOrgId()) {
            return BaseResp.error("orgId为空");
        }
        exerciseEntity.setCreatedAt(LocalDateTime.now());
        boolean result = exerciseService.save(exerciseEntity);
        if (!result) {
            return BaseResp.error("保存exercise失败");
        }
        return BaseResp.ok("保存exercise成功");
    }

}