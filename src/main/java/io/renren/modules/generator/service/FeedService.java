package io.renren.modules.generator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.generator.entity.FeedEntity;
import io.renren.modules.generator.entity.UserEntity;
import io.renren.modules.generator.form.SearchForm;

import java.util.List;
import java.util.Map;

/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
public interface FeedService extends IService<FeedEntity> {
    Map<Integer, FeedEntity> getAllFeedByOrgId(int orgId);

    List<FeedEntity> listByPage(SearchForm searchForm, Integer parentId);

    List<FeedEntity> listByPageAndId(Integer id, Integer pageSize);

    List<FeedEntity> listByPageAfterId(Integer id, Integer pageSize);

    List<FeedEntity> listByParentAndId(Integer id, Integer parentId, Integer pageSize);

    List<FeedEntity> listByParentAfterId(Integer id, Integer parentId, Integer pageSize);

    List<FeedEntity> getOldList(String username);

    List<FeedEntity> getOldListByOrgId(String username, Integer orgId);

    List<FeedEntity> getCacheList(Integer orgId, Integer lastId, Integer limit, String result);

    List<FeedEntity> transformFeedData(List<FeedEntity> list, String username);

    Integer countFeedsByOrgIds(List list);
    List<FeedEntity> getByPage(int page , int pageSize);
    Integer getLastFeedIdByOrgIds(List<Integer> list);
}

