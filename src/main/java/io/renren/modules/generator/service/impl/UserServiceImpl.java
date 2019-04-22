package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import io.renren.modules.generator.dao.UserDao;
import io.renren.modules.generator.dao.UserMapper;
import io.renren.modules.generator.entity.UserEntity;
import io.renren.modules.generator.service.UserService;
import org.springframework.stereotype.Service;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public int updateUser (UserEntity userEntity){

        int result = baseMapper.updateUser(userEntity);

        return result;

    }

}
