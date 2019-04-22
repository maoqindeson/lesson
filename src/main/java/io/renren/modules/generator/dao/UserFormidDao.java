package io.renren.modules.generator.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.generator.entity.UserFormidEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-08-31 10:52:33
 */
@Mapper
public interface UserFormidDao extends BaseMapper<UserFormidEntity> {
	UserFormidEntity getByFormId(@Param("form_id") String form_id);
	UserFormidEntity getByOpenId(@Param("open_id") String open_id);
	List<String> getAvailableOpenId();
	List<String> getPartialUser(@Param("amount") Integer amount);
	List<UserFormidEntity> getPartialUserInfo(@Param("day") Integer day, @Param("limit") Integer limit);
}
