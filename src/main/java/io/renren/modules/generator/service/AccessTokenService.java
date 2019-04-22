package io.renren.modules.generator.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.generator.entity.AccessTokenEntity;

public interface AccessTokenService extends IService<AccessTokenEntity> {
//    String getLatestToken();
    AccessTokenEntity getLatestToken(String appId);
    boolean sendTemplateMessage(String sendJson);
    String flushAccessToken();
}
