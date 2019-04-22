package io.renren.modules.generator.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.SceneDataDao;
import io.renren.modules.generator.entity.SceneDataEntity;
import io.renren.modules.generator.service.SceneDataService;
import org.springframework.stereotype.Service;


@Service("sceneDataService")
public class SceneDataServiceImpl extends ServiceImpl<SceneDataDao, SceneDataEntity> implements SceneDataService {
}
