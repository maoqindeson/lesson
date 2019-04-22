package io.renren.modules.generator.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.generator.entity.ProductEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author leo
 * @email hujingleo01@163.com
 * @date 2019-03-27 18:26:03
 */
@Mapper
public interface ProductDao extends BaseMapper<ProductEntity> {
	
}
