package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import io.renren.modules.generator.dao.MallOrderDao;
import io.renren.modules.generator.entity.MallOrderEntity;
import io.renren.modules.generator.service.MallOrderService;

import java.util.List;


@Service("mallOrderService")
public class MallOrderServiceImpl extends ServiceImpl<MallOrderDao, MallOrderEntity> implements MallOrderService {


    @Override
    public List<Integer> getUserProductByType(String username, Integer orderStatus) {
        return baseMapper.getUserProductByType(username,orderStatus);
    }

    @Override
    public List<String> getHasBuyAvatarUrls(Integer productId) {
        return baseMapper.getHasBuyAvatarUrls(productId);
    }
}