package io.renren.modules.generator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.generator.entity.UserFormidEntity;
import io.renren.modules.generator.utils.wechat.TemplateSendData;

import java.util.List;

/**
 * 
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-08-31 10:52:33
 */
public interface UserFormidService extends IService<UserFormidEntity> {
    UserFormidEntity getByFormId(String form_id);
    UserFormidEntity getByOpenId(String open_id);
    boolean sendDynamicNotice(String open_id, TemplateSendData data, String templateId, String page);
    List<String> getAvailableOpenId();
    List<String> getPartialUser(Integer amount);
    List<UserFormidEntity> getPartialUserInfo(Integer day, Integer limit);
}

