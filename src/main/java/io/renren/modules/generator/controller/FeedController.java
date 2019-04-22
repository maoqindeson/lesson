package io.renren.modules.generator.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.modules.generator.aop.WebRecord;
import io.renren.modules.generator.entity.*;
import io.renren.modules.generator.form.SearchForm;
import io.renren.modules.generator.service.*;
import io.renren.modules.generator.utils.BaseResp;
import io.renren.modules.generator.utils.FeedData.FeedType;
import io.renren.modules.generator.utils.JWTUtil;
import io.renren.modules.generator.utils.ListUtil;
import io.renren.modules.generator.utils.StringTools;
import io.renren.modules.generator.utils.wechat.Keyword;
import io.renren.modules.generator.utils.wechat.TemplateSendData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/feed")
public class FeedController {
    @Value("${wechat.noticeTemplateId}")
    private String noticeTemplateId;
    @Autowired
    private FeedService feedService;
    @Autowired
    private UserFeedService userFeedService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private BannerService bannerService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private UserExerciseService userExerciseService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserFormidService userFormidService;

    /**
     * 查找单个feed
     */
    @WebRecord
    @RequestMapping("/getById")
    public BaseResp getById(HttpServletRequest request, Integer id) {
        if (null == id || 0 == id) {
            return BaseResp.error("id不能为空或0");
        }
        FeedEntity entity = feedService.getById(id);
        if (null == entity) {
            return BaseResp.error("找不到对应的记录,id为 : " + id);
        }
        return BaseResp.ok(entity);
    }

    /**
     * 记录
     */
    @WebRecord
    @RequestMapping("/record")
    @RequiresAuthentication
    public BaseResp record(HttpServletRequest request, Integer feedId, String feedResult, Integer limit) {
        String username = JWTUtil.getCurrentUsername(request);
        if (null == feedId || 0 == feedId) {
            return BaseResp.error("feedId不能为空或0");
        }
        FeedEntity entity = feedService.getById(feedId);
        if (null == entity) {
            return BaseResp.error("找不到对应的记录,feedId为 : " + feedId);
        }
        Integer orgId = entity.getOrgId();
        OrganizationEntity organizationEntity = organizationService.getById(orgId);
        //校验该课程是否用户已经购买有资格学习
        ProductEntity productEntity = productService.getOne(new QueryWrapper<ProductEntity>().eq("org_id", orgId));
        if (null == productEntity) {
            Integer parentOrgId = organizationEntity.getParentId();
            productEntity = productService.getOne(new QueryWrapper<ProductEntity>().eq("org_id", parentOrgId));
            if (null == productEntity) {
                return BaseResp.error("找不到对应orgId的产品,无法校验用户是否可以上课,orgId为 : " + orgId);
            }
        }
        Integer productId = productEntity.getId();
        List<MallOrderEntity> orderList = mallOrderService.list(new QueryWrapper<MallOrderEntity>().eq("username", username)
                .eq("product_id", productId).eq("order_status", 1));
        String currentOrgName = organizationEntity.getName();
        if (null == orderList || orderList.isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            map.put("newList", new ArrayList<>());
            map.put("oldList", new ArrayList<>());
            map.put("orgName", currentOrgName);
            map.put("productId", productId);
            map.put("orgId", productEntity.getOrgId());
            return BaseResp.ok("已经学完购买课程,如果需要继续,请继续购买课程", map);
        }

        Integer hasExercise = entity.getHasExercise();
        Integer exerciseId = entity.getExerciseId();
        //如果是有跳转习题,则记录一条习题task,并且推送一条模板消息
        if (1 == hasExercise && 0 != exerciseId) {
            List<UserExerciseEntity> list = userExerciseService.list(new QueryWrapper<UserExerciseEntity>().eq("username", username)
                    .eq("exercise_id", exerciseId));
            if (null == list || list.isEmpty()) {
                if (null == userExerciseService.getOne(new QueryWrapper<UserExerciseEntity>().eq("username", username)
                        .eq("exercise_id", exerciseId))) {
                    UserExerciseEntity userExerciseEntity = new UserExerciseEntity();
                    userExerciseEntity.setExerciseId(exerciseId);
                    userExerciseEntity.setOrgId(entity.getOrgId());
                    userExerciseEntity.setHasComplete(0);
                    userExerciseEntity.setUsername(username);
                    userExerciseEntity.setCreatedAt(LocalDateTime.now());
                    boolean result = userExerciseService.saveOrUpdate(userExerciseEntity);
                    if (!result) {
                        log.error("添加用户习题初始记录失败");
                    } else {
                        log.warn("添加用户习题初始记录成功");
                    }
                }
            }
            //推送模板消息,内容是pc网页地址
//            TemplateSendData data = new TemplateSendData();
//            data.setKeyword1(new Keyword("智习生"));
//            data.setKeyword2(new Keyword("打开"+entity.getContent()+"开始做习题"));
//            data.setKeyword3(new Keyword(currentOrgName));
//            data.setKeyword4(new Keyword("每日寄语"));
//            String page = "pages/index/index";
//            boolean result = userFormidService.sendDynamicNotice(username, data, noticeTemplateId, page);
        }
        String type = entity.getType();
        String correctResult = entity.getCorrectResult();
        UserFeedEntity userFeedEntity = new UserFeedEntity();
        //如果是需要回答的题目
        if (ArrayUtils.contains(FeedType.NEEDANSWER, type)) {
            if (StringTools.isNullOrEmpty(feedResult)) {
                return BaseResp.error("该feed为答题,请选择答案");
            } else {
                userFeedEntity.setFeedResult(feedResult);
            }
        }
        userFeedEntity.setCreatedAt(LocalDateTime.now());
        userFeedEntity.setFeedId(feedId);
        userFeedEntity.setOrgId(entity.getOrgId());
        userFeedEntity.setUsername(username);
        UserFeedEntity record = userFeedService.getOne(new QueryWrapper<UserFeedEntity>().eq("username", username).
                eq("feed_id", feedId));
        if (null == record) {
            boolean result = userFeedService.save(userFeedEntity);
            if (!result) {
                return BaseResp.error("插入userfeed记录失败");
            }
        }
        Map<String, Object> map = new HashMap<>();
        String orgName = "";
        String pointTo = entity.getPointTo();
        boolean hasRead = false;
        Integer nextFeedOrgId = 0;
        Integer nextFeedId = 0;
        if (pointTo.contains(",")) {
            List<FeedEntity> feedEntityList = feedService.getCacheList(orgId, feedId, limit, feedResult);
            FeedEntity nextFeed = feedEntityList.get(0);
            nextFeedId = nextFeed.getId();
        } else {
            nextFeedId = Integer.valueOf(entity.getPointTo());
        }
        FeedEntity nextFeed = feedService.getById(nextFeedId);
        if (null == nextFeed) {
            log.error("课程数据有误");
        }
        nextFeedOrgId = nextFeed.getOrgId();
        UserFeedEntity nextUserFeed = userFeedService.getOne(new QueryWrapper<UserFeedEntity>().eq("username", username).
                eq("feed_id", nextFeedId));
        if (null != nextUserFeed) {
            hasRead = true;
        }
        OrganizationEntity organizationEntity1 = organizationService.getById(nextFeedOrgId);
        if (null != organizationEntity1) {
            orgName = organizationEntity1.getName();
        } else {
            orgName = organizationService.getById(orgId).getName();
        }
        //如果是章节最后一个feed并且学过了,则是复习
        if (entity.getHasEnd() == 1 && hasRead) {
            map.put("hasEnd", 1);
            BaseResp baseResp = this.oldList(request, nextFeedOrgId);
            Map<String, Object> oldmap = (Map<String, Object>) baseResp.getData();
            List<FeedEntity> list = (List<FeedEntity>) oldmap.get("oldList");
            map.put("oldList", list);
            map.put("newList", new ArrayList<>());
        } else {
            BaseResp baseResp = newList(request, limit);
            Map<String, Object> respMap = (Map<String, Object>) baseResp.getData();
            List<FeedEntity> newList = (List<FeedEntity>) respMap.get("newList");

            if (null==newList||newList.isEmpty()){
                if (null!=respMap.get("productId")&&null!=respMap.get("orgId")){
                    Integer  pid  = (Integer) respMap.get("productId");
                    Integer oid = (Integer) respMap.get("orgId");
                    map.put("orgId",oid);
                    map.put("productId",pid);
                }
                map.put("orgName",currentOrgName);
                map.put("oldList", new ArrayList<>());
                map.put("newList", new ArrayList<>());
                return BaseResp.ok("已经学完体验课程,如果需要继续,请购买完整课程",map);
            }

            if (null == newList || newList.isEmpty()) {
                orgName = currentOrgName;
            }
            map.put("newList", newList);
            map.put("oldList", new ArrayList<>());
        }
        map.put("orgName", orgName);
        //如果是需要回答的题目
        if (ArrayUtils.contains(FeedType.NEEDANSWER, type)) {
            map.put("userAnswer", feedResult);
            if (!StringTools.isNullOrEmpty(correctResult)) {
                if (correctResult.equalsIgnoreCase(feedResult)) {
                    map.put("answerResult", 1);
                } else {
                    map.put("answerResult", 0);
                }
            } else {
                map.put("answerResult", 2);
            }
            //如果是选择题,需要返回用户的答案
            if (ArrayUtils.contains(FeedType.CHOICEQUESTION, type)) {
                String choices = entity.getChoices();
                String choiceArr[] = choices.split(";");
                for (String s : choiceArr) {
                    String prefix = StringUtils.substringBefore(s, ".");
                    if (feedResult.equalsIgnoreCase(prefix)) {
                        map.put("result", s);
                    }
                }
            }
        }
        return BaseResp.ok(map);
    }

    @WebRecord
    @RequestMapping("/oldList")
    @RequiresAuthentication
    public BaseResp oldList(HttpServletRequest request, Integer orgId) {
        String username = JWTUtil.getCurrentUsername(request);
        try {
            if (null == orgId) {
                return BaseResp.error("orgId不能为空");
            }
            //先查询用户是否有浏览记录
            List<FeedEntity> feedEntityList = feedService.getOldListByOrgId(username, orgId);
            if (null == feedEntityList || feedEntityList.isEmpty()) {
                return BaseResp.ok(new HashMap<>());
            }
            feedEntityList = feedService.transformFeedData(feedEntityList, username);
            OrganizationEntity organizationEntity = organizationService.getById(orgId);
            String orgName = "";
            if (null != organizationEntity && !StringTools.isNullOrEmpty(organizationEntity.getName())) {
                orgName = organizationEntity.getName();
            }
            Map<String, Object> map = new HashMap<>();
            map.put("oldList", feedEntityList);
            map.put("orgName", orgName);
            return BaseResp.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResp.ok(new HashMap<>());
    }

    /**
     * 加载新的feed列表
     */
    @WebRecord
    @RequestMapping("/newList")
    @RequiresAuthentication
    public BaseResp newList(HttpServletRequest request, Integer limit) {
        String username = JWTUtil.getCurrentUsername(request);
        //先查询用户是否有浏览记录
        UserFeedEntity userFeedEntity = userFeedService.getLastRecord(username);

        HashMap<String, Object> data = new HashMap<>();
        List<FeedEntity> feedEntityList = new ArrayList<>();
        if (null == limit || 0 == limit) {
            limit = 10;
        }
        Integer orgId = 0 ;
        String orgName = "";
        try {
            //如果没有则默认加载
            if (null == userFeedEntity) {
                orgId = 7;
                feedEntityList = feedService.getCacheList(orgId, 0, limit, null);
            } else  {
                //如果有则从上次结束的feed开始加载N条
                Integer feedId = userFeedEntity.getFeedId();
                String result = userFeedEntity.getFeedResult();
                //找出上次最后浏览的feed
                FeedEntity feedEntity = feedService.getById(feedId);
                orgId = feedEntity.getOrgId();
                //判断上次浏览的feed是否该章节的最后一个feed
                if (1 == feedEntity.getHasEnd()) {
                    //如果是则加载下一个feed,先找出下一个orgId,判断是否有购买下一章节内容
                    int nextOrgId = Integer.valueOf(feedEntity.getNextOrg());
                    boolean hasBuy = organizationService.checkHasBuy(nextOrgId,username);
                    if (!hasBuy){
                        Map<String,Object> map = new HashMap<>();
                        ProductEntity productEntity = productService.getByChildOrgId(orgId);
                        if (null!=productEntity){
                            map.put("productId",productEntity.getId());
                            map.put("orgId",productEntity.getOrgId());
                        }
                        map.put("newList",new ArrayList<>());
                        map.put("oldList",new ArrayList<>());
                        return BaseResp.ok("已经学完体验课程,如果需要继续,请购买完整课程",map);
                    }
                    feedEntityList = feedService.getCacheList(nextOrgId, 0, limit, null);
                } else {
                    if (feedEntity == null) {
                        return BaseResp.error("用户上次记录feedid有误");
                    }
                    feedEntityList = feedService.getCacheList(orgId, feedId, limit, result);
                }
            }
            OrganizationEntity organizationEntity = organizationService.getById(orgId);
            if (null != organizationEntity) {
                orgName = organizationEntity.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        data.put("orgName", orgName);
        feedEntityList = feedService.transformFeedData(feedEntityList, null);
        data.put("newList", feedEntityList);
        List<FeedEntity> oldFeedEntityList = new ArrayList<>();
        oldFeedEntityList = feedService.getOldListByOrgId(username, orgId);
        oldFeedEntityList = feedService.transformFeedData(oldFeedEntityList, username);
        data.put("oldList", oldFeedEntityList);
        return BaseResp.ok(data);
    }

    /**
     * 增加feed
     */
    @WebRecord
    @RequestMapping("/addFeed")
    public BaseResp addFeed(Integer orgId, Integer nextOrg, String type, String content, Integer intervalTime,
                            String pointTo, String loadType, Integer hasStart, Integer hasEnd, String choices, String correctResult) {
        //非空校验
        if (StringTools.isNullOrEmpty(type)) {
            return BaseResp.error("type不能为空");
        }
        if (StringTools.isNullOrEmpty(content)) {
            return BaseResp.error("content不能为空");
        }
        FeedEntity entity = new FeedEntity();
        entity.setContent(content);
        entity.setType(type);
        entity.setCreatedAt(LocalDateTime.now());
        if (null != intervalTime && 0 != intervalTime) {
            entity.setIntervalTime(intervalTime);
        }
        if (!StringTools.isNullOrEmpty(pointTo)) {
            entity.setPointTo(pointTo);
        }
        if (!StringTools.isNullOrEmpty(loadType)) {
            entity.setLoadType(loadType);
        } else {
            entity.setLoadType("not_node");
        }
        if (!StringTools.isNullOrEmpty(choices)) {
            entity.setChoices(choices);
        }
        if (!StringTools.isNullOrEmpty(correctResult)) {
            entity.setCorrectResult(correctResult);
        }
        if (null != orgId && 0 != orgId) {
            entity.setOrgId(orgId);
        }
        if (null != nextOrg && 0 != nextOrg) {
            entity.setNextOrg(nextOrg);
        }
        if (null != hasStart && 0 != hasStart) {
            entity.setHasStart(hasStart);
        }
        if (null != hasEnd && 0 != hasEnd) {
            entity.setHasEnd(hasEnd);
        }
        boolean result = feedService.save(entity);
        if (!result) {
            log.error("插入feed记录失败");
            return BaseResp.error("插入feed记录失败");
        }
        return BaseResp.ok("插入feed记录成功");
    }

    @WebRecord
    @RequestMapping("/getAllFeeds")
    public BaseResp getAllFeeds(Integer orgId, Integer startId, Integer limit) {
        Map<Integer, FeedEntity> map = feedService.getAllFeedByOrgId(orgId);
        return BaseResp.ok(map);
    }


    @WebRecord
    @RequestMapping("/banner")
    public BaseResp banner() {
        List<BannerEntity> list = bannerService.list(new QueryWrapper<>());
        return BaseResp.ok(list);
    }

    @WebRecord
    @RequestMapping("/task")
    @RequiresAuthentication
    public BaseResp task(HttpServletRequest request) {
        String username = JWTUtil.getCurrentUsername(request);
        try {
            List<Integer> idlist = mallOrderService.getUserProductByType(username, 1);
            List<TaskEntity> list = taskService.list();
            if (null != list && !list.isEmpty()) {
                for (TaskEntity entity : list) {
                    Integer orgId = entity.getOrgId();
                    double rate = organizationService.getRateOfProgress(orgId, username);
                    String finishRate = String.valueOf(rate)+"%";
                    entity.setFinishRate(finishRate);
                    if (idlist != null && !idlist.isEmpty()) {
                        for (Integer id : idlist) {
                            if (entity.getProductId() == id) {
                                entity.setHasBuy(1);
                            }
                        }
                    }
                    //查询该task已经购买的用户
                    //查找task对应的产品
                    ProductEntity productEntity = productService.getOne(new QueryWrapper<ProductEntity>().eq("org_id", orgId));
                    if (null != productEntity) {
                        Integer productId = productEntity.getId();
                        List<String> avatarUrls = mallOrderService.getHasBuyAvatarUrls(productId);
                        if (null != avatarUrls && !avatarUrls.isEmpty()) {
                            Integer count = avatarUrls.size();
                            if (count > 5) {
                                avatarUrls = avatarUrls.subList(0, 4);
                            }
                            entity.setHasBuyCount(count);
                            entity.setHasBuyAvatarUrls(avatarUrls);
                        }
                    }
                }
            }
            return BaseResp.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResp.ok();
    }


    @WebRecord
    @RequestMapping("/deleteUserFeedRecord")
    public BaseResp deleteUserFeedRecord(String username) {
        boolean result = userFeedService.remove(new QueryWrapper<UserFeedEntity>().eq("username", username));
        if (!result) {
            return BaseResp.error("删除失败,请重试");
        }
        return BaseResp.ok(result);
    }
}