package io.renren.modules.generator.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.generator.entity.FeedEntity;
import io.renren.modules.generator.entity.UserFeedEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
@Mapper
public interface UserFeedDao extends BaseMapper<UserFeedEntity> {
    UserFeedEntity getLastRecord(String username);
    List<UserFeedEntity> getOldRecordList(String username);
    Integer countFeedsByOrgIds(@Param("list") List list,@Param("username") String username);
}
