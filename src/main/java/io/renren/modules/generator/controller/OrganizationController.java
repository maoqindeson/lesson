package io.renren.modules.generator.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.modules.generator.aop.WebRecord;
import io.renren.modules.generator.entity.*;
import io.renren.modules.generator.form.SearchForm;
import io.renren.modules.generator.service.*;
import io.renren.modules.generator.utils.BaseResp;
import io.renren.modules.generator.utils.JWTUtil;
import io.renren.modules.generator.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/organization")
public class OrganizationController {
    @Autowired
    private FeedService feedService;
    @Autowired
    private UserFeedService userFeedService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private UserExerciseService userExerciseService;
    @Autowired
    private ExerciseService exerciseService;
    /**
     * 保存组织
     */
    @WebRecord
    @RequestMapping("/addOrganization")
    public BaseResp addOrganization(HttpServletRequest request, String name, Integer parentId, Integer grade) {
        if (StringTools.isNullOrEmpty(name)) {
            return BaseResp.error("name不能为空");
        }
        if (null == grade || 0 == grade) {
            return BaseResp.error("grade不能为空");
        }
        OrganizationEntity entity = new OrganizationEntity();
        entity.setActivity(1);
        entity.setName(name);
        entity.setGrade(grade);
        if (null != parentId && parentId != 0) {
            entity.setParentId(parentId);
        }
        entity.setCreatedAt(LocalDateTime.now());
        boolean result = organizationService.save(entity);
        if (!result) {
            return BaseResp.error("保存组织失败");
        }
        return BaseResp.ok("保存组织成功");
    }

    @WebRecord
    @RequestMapping("/list")
    @RequiresAuthentication
    public BaseResp list(Integer orgId,HttpServletRequest request) {
        String username = JWTUtil.getCurrentUsername(request);
        try {
            if (null != orgId && 0 != orgId) {
                return BaseResp.ok(organizationService.getTreeByOrgId(orgId, organizationService.list(new QueryWrapper<OrganizationEntity>()),username));
            }
            return BaseResp.ok(organizationService.getTree(username));
        }catch (Exception e){
            e.printStackTrace();
        }
        return BaseResp.ok();
    }

    @WebRecord
    @RequestMapping("/userExerciseOrgList")
    @RequiresAuthentication
    public BaseResp userExerciseOrgList(HttpServletRequest request) {
        String username = JWTUtil.getCurrentUsername(request);
        try {
            //查看用户之前习题做到哪里了
            UserExerciseEntity userExerciseEntity = userExerciseService.getOne(new QueryWrapper<UserExerciseEntity>().eq("username",username)
                    .last("order by exercise_id desc limit 1"));
            //如果习题记录为空,则默认加载orgid为10
            if (null==userExerciseEntity){
                return BaseResp.ok(organizationService.getTreeByOrgId(10, organizationService.list(new QueryWrapper<OrganizationEntity>()),username));
            }
            //否则查看用户进度
             Integer lastExerciseId = userExerciseEntity.getExerciseId();
            ExerciseEntity exerciseEntity = exerciseService.getById(lastExerciseId);
            if (exerciseEntity==null){
                return BaseResp.ok(organizationService.getTreeByOrgId(10, organizationService.list(new QueryWrapper<OrganizationEntity>()),username));
            }
            Integer hasEnd = exerciseEntity.getHasEnd();
            if (1==hasEnd){
                Integer nextOrgId = exerciseEntity.getNextOrg();
                return BaseResp.ok(organizationService.getTreeByOrgId(nextOrgId, organizationService.list(new QueryWrapper<OrganizationEntity>()),username));
            }else {
                Integer parentOrgId = exerciseEntity.getParentOrg();
                if (null==parentOrgId||0==parentOrgId){
                    log.error("parentOrgId有误,返回默认开始习题大纲");
                    return BaseResp.ok(organizationService.getTreeByOrgId(10, organizationService.list(new QueryWrapper<OrganizationEntity>()),username));
                }
                return BaseResp.ok(organizationService.getTreeByOrgId(parentOrgId, organizationService.list(new QueryWrapper<OrganizationEntity>()),username));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return BaseResp.ok();
    }

}