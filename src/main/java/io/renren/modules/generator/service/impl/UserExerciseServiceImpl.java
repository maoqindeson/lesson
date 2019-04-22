package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.ExerciseDao;
import io.renren.modules.generator.dao.UserExerciseDao;
import io.renren.modules.generator.entity.ExerciseEntity;
import io.renren.modules.generator.entity.UserExerciseEntity;
import io.renren.modules.generator.service.ExerciseService;
import io.renren.modules.generator.service.UserExerciseService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("userExerciseService")
public class UserExerciseServiceImpl extends ServiceImpl<UserExerciseDao, UserExerciseEntity> implements UserExerciseService {
    @Override
    public List<UserExerciseEntity> countNoticeList() {
        return baseMapper.countNoticeList();
    }

    @Override
    public Integer updateHasNotice(Integer hasNotice,Integer id) {
        return baseMapper.updateHasNotice(hasNotice,id);
    }
}
