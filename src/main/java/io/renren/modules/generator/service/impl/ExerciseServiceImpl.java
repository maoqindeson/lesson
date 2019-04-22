package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.ExerciseDao;
import io.renren.modules.generator.entity.ExerciseEntity;
import io.renren.modules.generator.service.ExerciseService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("exerciseService")
public class ExerciseServiceImpl extends ServiceImpl<ExerciseDao, ExerciseEntity> implements ExerciseService {
    @Override
    public List<ExerciseEntity> getOldList(String username) {
        return baseMapper.getOldList(username);
    }
}
