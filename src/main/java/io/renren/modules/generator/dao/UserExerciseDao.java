package io.renren.modules.generator.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.generator.entity.ExerciseEntity;
import io.renren.modules.generator.entity.UserExerciseEntity;
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
public interface UserExerciseDao extends BaseMapper<UserExerciseEntity> {
    List<UserExerciseEntity> countNoticeList();
    Integer updateHasNotice(@Param("hasNotice") Integer hasNotice,@Param("id") Integer id);
}
