package io.renren.modules.generator.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.generator.entity.MallOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author leo
 * @email hujingleo01@163.com
 * @date 2019-03-27 18:26:02
 */
@Mapper
public interface MallOrderDao extends BaseMapper<MallOrderEntity> {
	List<Integer> getUserProductByType(@Param("username") String username,@Param("orderStatus") Integer orderStatus);
	List<String> getHasBuyAvatarUrls(Integer productId);
}
