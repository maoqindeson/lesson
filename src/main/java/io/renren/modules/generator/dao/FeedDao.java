package io.renren.modules.generator.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.generator.entity.FeedEntity;
import io.renren.modules.generator.entity.UserEntity;
import io.renren.modules.generator.form.SearchForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
@Mapper
public interface FeedDao extends BaseMapper<FeedEntity> {
    FeedEntity getFirstFeedByOrgId(Integer orgId);
    @Cacheable(cacheNames = "feedList")
    List<FeedEntity> getAllFeedByOrgId(int orgId);
    List<FeedEntity> listByPage(SearchForm searchForm,@Param("orgId") Integer orgId);
    List<FeedEntity> listByPageAndId(@Param("id") Integer id ,@Param("pageSize") Integer pageSize );
    List<FeedEntity> listByPageAfterId(@Param("id") Integer id ,@Param("pageSize") Integer pageSize );
    List<FeedEntity> listByParentAndId(@Param("id") Integer id ,@Param("parentId") Integer parentId ,@Param("pageSize") Integer pageSize );
    List<FeedEntity> listByParentAfterId(@Param("id") Integer id ,@Param("parentId") Integer parentId ,@Param("pageSize") Integer pageSize );
    List<FeedEntity> getOldList(String username);
    List<FeedEntity> getOldListByOrgId(@Param("username") String username,@Param("orgId") Integer orgId );
    Integer countFeedsByOrgIds(List list);
    Integer getLastFeedIdByOrgIds(List<Integer> list);

    List<FeedEntity> selectPage(Page<FeedEntity> page);
}
