package io.renren.modules.generator.utils.wechat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.modules.generator.entity.AccessTokenEntity;
import io.renren.modules.generator.service.AccessTokenService;
import io.renren.modules.generator.utils.StringTools;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
@Data
@Service
@Configuration
@EnableScheduling
public class AccessTokenUpdateUtil {
    private final Logger log = LoggerFactory.getLogger(getClass());


    @Value("${wechat.appId}")
    private String appId;
    @Value("${wechat.appSecret}")
    private String appSecret;
    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private WechatUtils wechatUtils;

    @Scheduled(cron = "0 */60 * * * ?")// 每10分钟执行一次更新accesstoken
    public String scheduler() {
        log.warn("开始执行更新accesstoken定时任务" + new Date().toString());
        String access_token = wechatUtils.requireAccessToken(appId, appSecret);
        log.warn("得到accesstoken为" + access_token);
        int i = 0;
        while (i < 10) {
            i++;
            if (null == access_token) {
                log.error("得到accesstoken为null,重新尝试第" + i + "次");
                access_token = wechatUtils.requireAccessToken(appId, appSecret);
            } else {
                break;
            }
        }
        if (StringTools.isNullOrEmpty(access_token)) {
            access_token = wechatUtils.requireAccessToken(appId, appSecret);
        }
        boolean result = false;
        AccessTokenEntity accessTokenEntity = accessTokenService.getOne(new QueryWrapper<AccessTokenEntity>().eq("appid", appId));
        if (null == accessTokenEntity) {
            accessTokenEntity = new AccessTokenEntity();
            accessTokenEntity.setAppid(appId);
            accessTokenEntity.setAccessToken(access_token);
            accessTokenEntity.setCreatedAt(LocalDateTime.now());
            result = accessTokenService.save(accessTokenEntity);
            if (!result) {
                log.error("增加access_token失败,access_token为: " + access_token);
                return null;
            }
        } else {
            accessTokenEntity.setAccessToken(access_token);
            accessTokenEntity.setCreatedAt(LocalDateTime.now());
            result = accessTokenService.updateById(accessTokenEntity);
            if (!result) {
                log.error("更新access_token失败,access_token为: " + access_token);
                return null;
            }
        }
        return access_token;
    }
}
