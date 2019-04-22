package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.FeedDataDao;
import io.renren.modules.generator.entity.FeedDataEntity;
import io.renren.modules.generator.entity.FeedEntity;
import io.renren.modules.generator.service.FeedDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service("feedDataService")
public class FeedDataServiceImpl extends ServiceImpl<FeedDataDao, FeedDataEntity> implements FeedDataService {

    @Override
    @Cacheable(cacheNames = "feedDataList")
    public Map<Integer, FeedDataEntity> getAllFeedData() {
        log.warn("从数据库查询feedDataList");
        Map<Integer, FeedDataEntity> map = new HashMap<>();
        List<FeedDataEntity> list = baseMapper.getAllFeedData();
        for (FeedDataEntity entity : list) {
            map.put(entity.getId(), entity);
        }
        return map;
    }
}
