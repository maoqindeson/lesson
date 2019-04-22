
package io.renren.modules.generator.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.generator.entity.AccessTokenEntity;
import io.renren.modules.generator.entity.SettingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface SettingDao extends BaseMapper<SettingEntity> {
}
