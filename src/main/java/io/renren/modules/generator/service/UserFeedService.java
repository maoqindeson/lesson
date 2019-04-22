package io.renren.modules.generator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.generator.entity.FeedEntity;
import io.renren.modules.generator.entity.UserFeedEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
public interface UserFeedService extends IService<UserFeedEntity> {
    UserFeedEntity getLastRecord(String username);
    List<UserFeedEntity> getOldRecordList(String username);
    Integer countFeedsByOrgIds(List list,String username);
}

