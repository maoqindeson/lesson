package io.renren.modules.generator.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.generator.entity.FeedEntity;
import io.renren.modules.generator.entity.OrganizationEntity;
import io.renren.modules.generator.form.SearchForm;
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
public interface OrganizationDao extends BaseMapper<OrganizationEntity> {
}
