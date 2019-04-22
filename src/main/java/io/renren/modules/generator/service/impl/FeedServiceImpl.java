package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.FeedDao;
import io.renren.modules.generator.entity.FeedDataEntity;
import io.renren.modules.generator.entity.FeedEntity;
import io.renren.modules.generator.entity.UserEntity;
import io.renren.modules.generator.entity.UserFeedEntity;
import io.renren.modules.generator.form.SearchForm;
import io.renren.modules.generator.service.FeedDataService;
import io.renren.modules.generator.service.FeedService;
import io.renren.modules.generator.service.UserFeedService;
import io.renren.modules.generator.service.UserService;
import io.renren.modules.generator.utils.FeedData.*;
import io.renren.modules.generator.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("feedService")
public class FeedServiceImpl extends ServiceImpl<FeedDao, FeedEntity> implements FeedService {

    @Autowired
    private FeedDataService feedDataService;
    @Autowired
    private UserFeedService userFeedService;
    @Override
    public Map<Integer, FeedEntity> getAllFeedByOrgId(int orgId) {
        Map<Integer, FeedEntity> map = new HashMap<>();
        List<FeedEntity> list = baseMapper.getAllFeedByOrgId(orgId);
        for (FeedEntity entity : list) {
            map.put(entity.getId(), entity);
        }
        return map;
    }

    @Override
    public List<FeedEntity> listByPage(SearchForm searchForm, Integer orgId) {
        Page page = new Page(searchForm.getPageIndex(),searchForm.getPageSize());
        return page(page,new QueryWrapper<FeedEntity>().eq("org_id",orgId)).getRecords();
//        return baseMapper.listByPage(searchForm, orgId);
    }


    @Override
    public List<FeedEntity> listByPageAndId(Integer id, Integer pageSize) {
        Page page = new Page();
        page.setSize(pageSize);
        return page(page,new QueryWrapper<FeedEntity>().ge("id",id)).getRecords();
//        return baseMapper.listByPageAndId(id, pageSize);
    }

    @Override
    public List<FeedEntity> listByPageAfterId(Integer id, Integer pageSize) {
        Page page = new Page();
        page.setSize(pageSize);
        return page(page,new QueryWrapper<FeedEntity>().gt("id",id)).getRecords();
    }

    @Override
    public List<FeedEntity> listByParentAndId(Integer id, Integer parentId, Integer pageSize) {
        Page page = new Page();
        page.setSize(pageSize);
        return page(page,new QueryWrapper<FeedEntity>().ge("id",id).eq("parent_id",parentId)
                .orderBy(true, true, "id")).getRecords();
//        return baseMapper.listByParentAndId(id, parentId, pageSize);
    }

    @Override
    public List<FeedEntity> listByParentAfterId(Integer id, Integer parentId, Integer pageSize) {
        Page page = new Page();
        page.setSize(pageSize);
        return page(page,new QueryWrapper<FeedEntity>().gt("id",id).eq("parent_id",parentId)
                .orderBy(true, true, "id")).getRecords();
//        return baseMapper.listByParentAfterId(id, parentId, pageSize);
    }

    @Override
    public List<FeedEntity> getOldList(String username) {
        return baseMapper.getOldList(username);
    }

    @Override
    public List<FeedEntity> getOldListByOrgId(String username,Integer orgId) {
        return baseMapper.getOldListByOrgId(username,orgId);
    }

    @Override
    public List<FeedEntity> getCacheList(Integer orgId, Integer lastId, Integer limit, String result) {
        String pointTo = "";
        FeedEntity lastEntity = new FeedEntity();
        Map<Integer, FeedEntity> map = this.getAllFeedByOrgId(orgId);
        List<FeedEntity> list = new ArrayList<>();
        if (lastId == 0) {
            FeedEntity fistFeed = baseMapper.getFirstFeedByOrgId(orgId);
            if (null == fistFeed) {
                log.error("没有" + orgId + "组织下的开始节点");
                return new ArrayList<>();
            }
            pointTo = String.valueOf(fistFeed.getId());
        } else {
            lastEntity = map.get(lastId);
            pointTo = lastEntity.getPointTo();
        }
        //如果不是节点
        if (!pointTo.contains(":")) {
            Integer nextId = Integer.valueOf(pointTo);
            int time = 0;
            while (time < limit) {
                FeedEntity entity = map.get(nextId);
                if (null == entity) {
                    break;
                }
                list.add(entity);
                String next = entity.getPointTo();
                if (StringTools.isNullOrEmpty(next) || next.contains(":")) {
                    break;
                }
                nextId = Integer.valueOf(next);
                time++;
            }
        } else {
            //对于多分支,则一定是指向分支点
            String points[] = pointTo.split(",");
            for (String s : points) {
                if (!StringTools.isNullOrEmpty(result) && s.toUpperCase().contains(result.toUpperCase())) {
                    String point = StringUtils.substringAfter(s, ":");
                    Integer nextId = Integer.valueOf(point);
                    //先查出所指向分支点,然后根据该feed的id作为parentId继续向下寻找feed
                    int time = 0;
                    while (time < limit) {
                        FeedEntity entity = map.get(nextId);
                        if (null == entity) {
                            break;
                        }
                        list.add(entity);
                        String next = entity.getPointTo();
                        //如果没有指向下一个点,则跳出
                        if (StringTools.isNullOrEmpty(next) || next.contains(":")) {
                            break;
                        }
                        nextId = Integer.valueOf(next);
                        time++;
                    }
                }
            }
        }
        return list;
    }

    @Override
    //将feed列表数据转换成前端需要的结构格式
    public List<FeedEntity> transformFeedData(List<FeedEntity> list,String username) {
        try {
            if (list == null || list.isEmpty()) {
                return null;
            }
            for (FeedEntity entity : list) {
                //根据feed 类型匹配数据接口
                String type = entity.getType();
                switch (type) {
                    //文本，图片，视频，判断题
                    case "text": case "picture": case "video" :
                        entity.setData(entity.getContent());
                        break;

                    //音频
                    case "audio":
                        Audio audio = new Audio();
                        audio.setAuthor(entity.getAuthor());
                        audio.setName(entity.getName());
                        audio.setPoster(entity.getPoster());
                        audio.setSrc(entity.getSrc());
                        entity.setData(audio);
                        break;
                    //录音
                    case "record":
                        Record record = new Record();
                        record.setAnswer(entity.getAnswer());
                        record.setText(entity.getText());
                        entity.setData(record);
                        break;
                    //填空题
                    case "full-question":
                        FQRecord fqRecord = new FQRecord();
                        fqRecord.setContent(entity.getContent());
                        if (!StringTools.isNullOrEmpty(username)){
//                            UserFeedEntity userFeedEntity = userFeedService.selectOne(new EntityWrapper<UserFeedEntity>().eq("username",username)
                            UserFeedEntity userFeedEntity = userFeedService.getOne(new QueryWrapper<UserFeedEntity>().eq("username",username)
                                    .eq("feed_id",entity.getId()));
                            if (null!=userFeedEntity&&!StringTools.isNullOrEmpty(userFeedEntity.getFeedResult())){
                                fqRecord.setAnswer(userFeedEntity.getFeedResult());
                            }
                        }
                        entity.setData(fqRecord);
                        break;
                    //判断题
                    case "tf-question":
                        TFRecord tfRecord = new TFRecord();
                        tfRecord.setContent(entity.getContent());
                        if (!StringTools.isNullOrEmpty(username)){
                            UserFeedEntity userFeedEntity = userFeedService.getOne(new QueryWrapper<UserFeedEntity>().eq("username",username)
                                    .eq("feed_id",entity.getId()));
                            if (null!=userFeedEntity&&!StringTools.isNullOrEmpty(userFeedEntity.getFeedResult())){
                                tfRecord.setChoiceResult(userFeedEntity.getFeedResult());
                            }
                        }
                        entity.setData(tfRecord);
                        break;
                    //单选多选题
                    case "sin-choice" : case "mul-choice":
                        List<Choice> choiceList = new ArrayList<>();
                        String choices = entity.getChoices();
                        if (StringTools.isNullOrEmpty(choices)){
                            log.error("feed为选择题但找不到选项");
                            break;
                        }
                        String choiceArr[] = choices.split(";");
                        for (String s : choiceArr) {
                            String prefix = StringUtils.substringBefore(s, ".");
                            String text = StringUtils.substringAfter(s, ".");
                            Choice choice = new Choice();
                            choice.setPrefix(prefix);
                            choice.setText(text);
                            choiceList.add(choice);
                        }
                        ChoiceRecord choiceRecord= new ChoiceRecord();
                        choiceRecord.setChoices(choiceList);
                        if (!StringTools.isNullOrEmpty(username)){
                            UserFeedEntity userFeedEntity = userFeedService.getOne(new QueryWrapper<UserFeedEntity>().eq("username",username)
                                    .eq("feed_id",entity.getId()));
                            if (null!=userFeedEntity&&!StringTools.isNullOrEmpty(userFeedEntity.getFeedResult())){
                                choiceRecord.setChoiceResult(userFeedEntity.getFeedResult());
                            }
                        }
                        entity.setData(choiceRecord);
                        break;
//                    //填空题
//                case "full-question":
//                    break;
                    default:
                        entity.setData(entity.getContent());
                }
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
            return null;
    }

    @Override
    public Integer countFeedsByOrgIds(List list) {
        return baseMapper.countFeedsByOrgIds(list);
    }

    @Override
    public List<FeedEntity> getByPage(int page , int pageSize) {
        Page<FeedEntity> p = new Page<>(page, pageSize);
      return baseMapper.selectPage(p);
    }

    @Override
    public Integer getLastFeedIdByOrgIds(List<Integer> list) {
        return baseMapper.getLastFeedIdByOrgIds(list);
    }


}
