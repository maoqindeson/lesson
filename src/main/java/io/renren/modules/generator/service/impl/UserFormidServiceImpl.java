package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.UserFormidDao;
import io.renren.modules.generator.entity.UserFormidEntity;
import io.renren.modules.generator.service.AccessTokenService;
import io.renren.modules.generator.service.UserFormidService;
import io.renren.modules.generator.utils.StringTools;
import io.renren.modules.generator.utils.wechat.TemplateSendData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.sf.json.JSONObject;
import java.net.URLDecoder;
import java.util.List;

@Slf4j
@Service("userFormidService")
public class UserFormidServiceImpl extends ServiceImpl<UserFormidDao, UserFormidEntity> implements UserFormidService {
    @Autowired
    private AccessTokenService accessTokenService;
    @Override
    public UserFormidEntity getByFormId(String form_id) {
        return baseMapper.getByFormId(form_id);
    }

    @Override
    public UserFormidEntity getByOpenId(String open_id) {
        return baseMapper.getByOpenId(open_id);
    }

    @Override
    public boolean sendDynamicNotice(String open_id, TemplateSendData data, String templateId, String page) {
        try {
            UserFormidEntity userFormidEntity = baseMapper.getByOpenId(open_id);
            if (null == userFormidEntity) {
                log.error("openid为 : " + open_id + "的用户找不到可用的form_id");
                return false;
            }
            if (StringTools.isNullOrEmpty(userFormidEntity.getFormId())) {
                log.error("openid为 : " + open_id + "的用户找不到可用的form_id");
                return false;
            }
            String form_id = userFormidEntity.getFormId();
            JSONObject json = new JSONObject();
            json.put("touser", open_id);
            json.put("template_id", templateId);
            json.put("form_id", form_id);
            json.put("page", page);
            log.warn("page : "+page);
            json.put("data", data);
            log.warn("通用模板消息参数为: " + json.toString());
            boolean wxTemplateSendUrlResult = accessTokenService.sendTemplateMessage(json.toString());
            //发送完删除无用formid记录
            this.removeById(userFormidEntity.getId());
            return wxTemplateSendUrlResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("通用模板消息发送失败,异常信息为: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getAvailableOpenId() {
        return baseMapper.getAvailableOpenId();
    }

    @Override
    public List<String> getPartialUser(Integer amount) {
        return baseMapper.getPartialUser(amount);
    }

    @Override
    public List<UserFormidEntity> getPartialUserInfo(Integer day, Integer limit) {
        List<UserFormidEntity> list = baseMapper.getPartialUserInfo(day,limit);
        if (null!=list&&!list.isEmpty()){
            for (UserFormidEntity entity : list){
                if (!StringTools.isNullOrEmpty(entity.getNickName())){
                    entity.setNickName(URLDecoder.decode(entity.getNickName()));
                }
            }
        }
        return list;
    }
}
