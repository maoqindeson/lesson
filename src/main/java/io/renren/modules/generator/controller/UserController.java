package io.renren.modules.generator.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.generator.aop.WebRecord;
import io.renren.modules.generator.entity.AccessTokenEntity;
import io.renren.modules.generator.entity.SceneDataEntity;
import io.renren.modules.generator.entity.UserEntity;
import io.renren.modules.generator.service.AccessTokenService;
import io.renren.modules.generator.service.SceneDataService;
import io.renren.modules.generator.service.UserService;
import io.renren.modules.generator.utils.*;
import io.renren.modules.generator.utils.wechat.WechatUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
@Data
@Slf4j
@RestController
@ConfigurationProperties(prefix = "wechat")
@RequestMapping("/user")
public class UserController {
    private String appId;//微信小程序appid
    private String appSecret;//微信小程序密钥
    @Autowired
    private UserService userService;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private WechatUtils wechatUtils;
    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SceneDataService sceneDataService;
    /**
     * 查找用户列表
     */
    @WebRecord
    @RequestMapping("/list")
    public BaseResp list(Integer pageIndex,Integer pageSize) {
        try {
            Page page = new Page<>(pageIndex, pageSize);
//            page.("created_at");
            IPage<UserEntity> list = userService.page(page,new QueryWrapper<UserEntity>().eq("gender",2));
            return BaseResp.ok(list);
        }catch (Exception e){
            e.printStackTrace();
        }
            return BaseResp.error();
    }
    //获取二维码
    @WebRecord
    @RequestMapping("/getSceneDataById")
    @ResponseBody
    public BaseResp saveSceneData(Integer id) throws Exception {
        SceneDataEntity entity = sceneDataService.getById(id);
        if (null==entity){
            return BaseResp.error("找不到对应的sceneData");
        }
        return BaseResp.ok(entity);
    }
    //获取二维码
    @WebRecord
    @RequestMapping("/shareCode")
    @ResponseBody
    public String shareCode(HttpServletResponse response) throws Exception {
        String content = StringTools.getRandomString(16);
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        os.write(qrCodeService.createQRCode(content));
        os.flush();
        os.close();
        return "success";
    }
    //获取小程序码
    @WebRecord
    @RequestMapping("getMiniProgramCode")
    public ResponseEntity<byte[]> getWXacodeJpg(String page ,String content) throws Exception {
        if (StringTools.isNullOrEmpty(content)){
            return null;
        }
        AccessTokenEntity accessTokenEntity = accessTokenService.getLatestToken(appId);
        if (null==accessTokenEntity||StringTools.isNullOrEmpty(accessTokenEntity.getAccessToken())){
            log.error("获取小程序码接口，获取accesstoken失败");
            return null;
        }
        String access_token = accessTokenEntity.getAccessToken();
        Map<String, Object> param = new HashMap<>();
//        String content = StringTools.encryStr(StringTools.genRandomStr(8));
        if (null==sceneDataService.getOne(new QueryWrapper<SceneDataEntity>().eq("content",content))){
            log.error("content非法 : "+content);
        }
        String url = "http://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + access_token;
        param.put("scene", content);
        log.warn("获取小程序码接口content为 : "+content);
        param.put("page", page);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        HttpEntity requestEntity = new HttpEntity(param, headers);
        try {
            return restTemplate.exchange(url, HttpMethod.POST, requestEntity, byte[].class, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取小程序码异常，异常信息为" + e.getMessage());
            return null;
        }
    }
    //刷新accesstoken
    @WebRecord
    @RequestMapping("/flushAccessToken")
    @ResponseBody
    public String flushAccessToken() {
            return accessTokenService.flushAccessToken();
    }

}