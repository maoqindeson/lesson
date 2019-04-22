package io.renren.modules.generator.utils.wechat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.modules.generator.entity.AccessTokenEntity;
import io.renren.modules.generator.entity.UserEntity;
import io.renren.modules.generator.entity.UserExerciseEntity;
import io.renren.modules.generator.service.AccessTokenService;
import io.renren.modules.generator.service.UserExerciseService;
import io.renren.modules.generator.service.UserFormidService;
import io.renren.modules.generator.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@Configuration
@EnableScheduling
public class ExerciseNoticeUtil {
    private final Logger log = LoggerFactory.getLogger(getClass());


    @Value("${wechat.appId}")
    private String appId;
    @Value("${wechat.appSecret}")
    private String appSecret;
    @Value("${wechat.noticeTemplateId}")
    private String noticeTemplateId;
    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private UserExerciseService userExerciseService;
    @Autowired
    private UserFormidService userFormidService;
    @Scheduled(cron = "0 */60 * * * ?")// 每10分钟执行一次提醒习题任务
    public Integer scheduler() {
        log.warn("开始执行习题未完成提醒定时任务" + new Date().toString());
        //检查需要提醒习题的用户
        List<UserExerciseEntity> list = userExerciseService.countNoticeList();
        Integer success = 0 ;
        Integer failure = 0 ;
        if (null!=list&&!list.isEmpty()){
            log.warn("查出来需要提醒做习题的记录有: "+list.size()+"条");
            for (UserExerciseEntity entity : list){
                String username = entity.getUsername();
                TemplateSendData data = new TemplateSendData();
                data.setKeyword1(new Keyword("智习生"));
                data.setKeyword2(new Keyword("完成第"+entity.getNoticeOrgId()+"习题"));
                data.setKeyword3(new Keyword("如何用python写爬虫"));
                data.setKeyword4(new Keyword("每日寄语"));
                String page = "pages/index/index";
               boolean result = userFormidService.sendDynamicNotice(username, data, noticeTemplateId, page);
               if (!result){
                   log.error("习题未完成提醒模板消息发送失败,openid为: "+username);
                   failure++;
               }else {
                   log.warn("习题未完成提醒模板消息发送成功,openid为: "+username);
                   Integer updatedResult = userExerciseService.updateHasNotice(1,entity.getExerciseId());
                   if (1!=updatedResult){
                       log.error("习题未完成提醒模板消息发送成功但更新发送状态失败,openid为:"+username);
                   }else {
                       log.warn("习题未完成提醒模板消息发送成功且更新发送状态成功,openid为:"+username);
                   }
                   success++;
               }
            }
        }
        log.warn("习题未完成提醒模板消息成功发送"+success+"条");
        return success;
    }
}
