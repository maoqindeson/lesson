package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.UserFeedDao;
import io.renren.modules.generator.entity.FeedEntity;
import io.renren.modules.generator.entity.UserFeedEntity;
import io.renren.modules.generator.service.FeedService;
import io.renren.modules.generator.service.UserFeedService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("userFeedService")
public class UserFeedServiceImpl extends ServiceImpl<UserFeedDao, UserFeedEntity> implements UserFeedService {
    @Override
    public UserFeedEntity getLastRecord(String username) {
        return baseMapper.getLastRecord(username);
    }

    @Override
    public List<UserFeedEntity> getOldRecordList(String username) {
        return null;
    }

    @Override
    public Integer countFeedsByOrgIds(List list,String username) {
        return baseMapper.countFeedsByOrgIds(list,username);
    }
}
