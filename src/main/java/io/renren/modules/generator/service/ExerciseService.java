package io.renren.modules.generator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.generator.entity.ExerciseEntity;

import java.util.List;

/**
 *
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
public interface ExerciseService extends IService<ExerciseEntity> {
    List<ExerciseEntity> getOldList(String username);
}

