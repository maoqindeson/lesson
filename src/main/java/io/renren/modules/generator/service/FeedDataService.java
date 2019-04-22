package io.renren.modules.generator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.generator.entity.FeedDataEntity;
import io.renren.modules.generator.entity.FeedEntity;
import io.renren.modules.generator.form.SearchForm;

import java.util.List;
import java.util.Map;

/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
public interface FeedDataService extends IService<FeedDataEntity> {
    Map<Integer, FeedDataEntity> getAllFeedData();
}

