package io.renren.modules.generator.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.AccessTokenDao;
import io.renren.modules.generator.dao.SettingDao;
import io.renren.modules.generator.entity.AccessTokenEntity;
import io.renren.modules.generator.entity.SettingEntity;
import io.renren.modules.generator.service.AccessTokenService;
import io.renren.modules.generator.service.SettingService;
import io.renren.modules.generator.utils.HttpClientUtil;
import io.renren.modules.generator.utils.wechat.AccessTokenUpdateUtil;
import io.renren.modules.generator.utils.wechat.WechatUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * 获取微信接口凭证access_token
 */
@Slf4j
@Service("settingService")
@Data
public class SettingServiceImpl extends ServiceImpl<SettingDao, SettingEntity> implements SettingService {

}
