package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.BannerDao;
import io.renren.modules.generator.dao.TaskDao;
import io.renren.modules.generator.entity.BannerEntity;
import io.renren.modules.generator.entity.TaskEntity;
import io.renren.modules.generator.service.BannerService;
import io.renren.modules.generator.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("taskService")
public class TaskServiceImpl extends ServiceImpl<TaskDao, TaskEntity> implements TaskService {
}
