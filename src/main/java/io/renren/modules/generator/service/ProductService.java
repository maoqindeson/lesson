package io.renren.modules.generator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.generator.entity.ProductEntity;

import java.util.Map;

/**
 *
 *
 * @author leo
 * @email hujingleo01@163.com
 * @date 2019-03-27 18:26:03
 */
public interface ProductService extends IService<ProductEntity> {
    ProductEntity getByChildOrgId(Integer orgId);

}

