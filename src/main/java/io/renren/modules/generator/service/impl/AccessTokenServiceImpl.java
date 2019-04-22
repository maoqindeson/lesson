package io.renren.modules.generator.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.AccessTokenDao;
import io.renren.modules.generator.entity.AccessTokenEntity;
import io.renren.modules.generator.service.AccessTokenService;
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
@Service("accessTokenService")
@ConfigurationProperties(prefix = "wechat")
@Data
public class AccessTokenServiceImpl extends ServiceImpl<AccessTokenDao, AccessTokenEntity> implements AccessTokenService {
    private String appId;
    private String appSecret;
    @Autowired
    private WechatUtils wechatUtils;
    @Autowired
    private AccessTokenUpdateUtil accessTokenUpdateUtil;

    @Override
    public AccessTokenEntity getLatestToken(String appId) {
        return baseMapper.getLatestToken(appId);
    }

    @Override
    public  boolean sendTemplateMessage(String sendJson) {
        AccessTokenEntity accessTokenEntity = this.getLatestToken(appId);
        if (null==accessTokenEntity){
            log.error("数据库中accesstoken记录为空");
            return false;
        }
        String access_token = accessTokenEntity.getAccessToken();
//        String access_token = wechatUtils.requireAccessToken(appId,appSecret);
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", access_token);
        log.warn("开始请求微信模板消息: " + requestUrl);
        String wxTemplateSendUrlResult = HttpClientUtil.sendJsonHttpPost(requestUrl, sendJson);
        log.warn("首次请求微信模板消息结果为: " + wxTemplateSendUrlResult);
        JSONObject wxTemplateSendUrlResultJson = JSONObject.parseObject(wxTemplateSendUrlResult);
        if (null != wxTemplateSendUrlResultJson && wxTemplateSendUrlResultJson.get("errmsg").toString().equals("ok")) {
            log.warn("模板消息发送成功");
            return true;
        }
//        else if (wxTemplateSendUrlResultJson.get("errcode").toString().equalsIgnoreCase("40001")) {
//            log.error("模板消息发送失败,accesstoken失效,开始esstoken后重新发送");
//            access_token = wechatUtils.getAccessToken(appId,appSecret);
////            access_token = accessTokenUpdateUtil.scheduler();
//            if (StringTools.isNullOrEmpty(access_token)){
//                log.error("首次请求微信模板消息失败后,手动更新accesstoken失败");
//                return false;
//            }
//            requestUrl = requestUrl.replace("ACCESS_TOKEN", access_token);
//            log.warn("得到accesstoken为" + access_token);
//            wxTemplateSendUrlResult = HttpClientUtil.sendJsonHttpPost(requestUrl, sendJson);
//            log.warn("二次发送模板消息url为:" +requestUrl);
//            log.warn("二次发送模板消息结果为:" +wxTemplateSendUrlResult);
//            if (null != wxTemplateSendUrlResultJson && wxTemplateSendUrlResultJson.get("errmsg").toString().equals("ok")) {
//                log.warn("二次模板消息发送成功");
//                return true;
//            }else {
//                log.warn("二次模板消息发送失败,url为 :" +requestUrl);
//                log.warn("二次模板消息发送失败,返回结果为 :" +wxTemplateSendUrlResult);
//                return false;
//            }
//        }
        log.error("模板消息发送失败");
        return false;
    }

    @Override
    public String flushAccessToken() {
//        String accessToken = wechatUtils.getAccessToken(appId, appSecret);
        String accessToken = accessTokenUpdateUtil.scheduler();
        return accessToken;
    }
}
