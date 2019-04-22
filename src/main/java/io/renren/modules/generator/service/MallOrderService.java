package io.renren.modules.generator.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.generator.entity.MallOrderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author leo
 * @email hujingleo01@163.com
 * @date 2019-03-27 18:26:02
 */
public interface MallOrderService extends IService<MallOrderEntity> {
    List<Integer> getUserProductByType(String username,Integer orderStatus);
    List<String> getHasBuyAvatarUrls(Integer productId);
}

