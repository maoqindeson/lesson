package io.renren.modules.generator.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.generator.aop.WebRecord;
import io.renren.modules.generator.entity.*;
import io.renren.modules.generator.service.*;
import io.renren.modules.generator.utils.*;
import io.renren.modules.generator.utils.wechat.ExerciseNoticeUtil;
import io.renren.modules.generator.utils.wechat.TemplateSendData;
import io.renren.modules.generator.utils.wechat.WechatUtils;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.coyote.http2.Setting;
import org.python.antlr.ast.Str;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
@Data
@Slf4j
@Controller
@ConfigurationProperties(prefix = "wechat")
@RequestMapping("/common")
public class CommonController {
    private String appId;//微信小程序appid
    private String appSecret;//微信小程序密钥
    private String pythonFilePath;//python文件保存目录
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
    @Autowired
    private UserFormidService userFormidService;
    @Autowired
    private ExerciseNoticeUtil exerciseNoticeUtil;
    @Autowired
    private SettingService settingService;
    @Autowired
    private FeedService feedService;
    @Autowired
    private UserExerciseService userExerciseService;

    //    @Autowired
//    private PythonInterpreter pythonInterpreter;
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

    @WebRecord
    @RequestMapping("/getContent")
    @ResponseBody
    public BaseResp getContent() {
        try {
            String content = StringTools.encryStr(StringTools.genRandomStr(6));
            SceneDataEntity entity = new SceneDataEntity();
            entity.setType("login");
            entity.setContent(content);
            entity.setCreatedAt(LocalDateTime.now());
            boolean result = sceneDataService.save(entity);
            if (!result) {
                log.error("保存scenedata失败");
                return BaseResp.error("保存scenedata失败");
            }
            return BaseResp.ok(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResp.error("获取content失败");
    }

    //获取小程序码
    @WebRecord
    @RequestMapping("/getMiniProgramCode")
    public ResponseEntity<byte[]> getWXacodeJpg(String page, String content) throws Exception {
        if (StringTools.isNullOrEmpty(content)) {
            return null;
        }
        AccessTokenEntity accessTokenEntity = accessTokenService.getLatestToken(appId);
        if (null == accessTokenEntity || StringTools.isNullOrEmpty(accessTokenEntity.getAccessToken())) {
            log.error("获取小程序码接口，获取accesstoken失败");
            return null;
        }
        String access_token = accessTokenEntity.getAccessToken();
        Map<String, Object> param = new HashMap<>();
        if (null == sceneDataService.getOne(new QueryWrapper<SceneDataEntity>().eq("content", content))) {
            log.error("content非法 : " + content);
        }
        String url = "http://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + access_token;
        param.put("scene", content);
        log.warn("获取小程序码接口content为 : " + content);
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

    //刷新accesstoken
    @WebRecord
    @RequestMapping("/checkScene")
    @ResponseBody
    public BaseResp checkScene(String content) {
        SceneDataEntity entity = sceneDataService.getOne(new QueryWrapper<SceneDataEntity>().eq("content", content));
        if (null == entity) {
            return BaseResp.ok("找不到对应的记录");
        }
        String status = entity.getStatus();
        if (!StringTools.isNullOrEmpty(status) && status.equalsIgnoreCase("login")) {
            LocalDateTime updatedAt = entity.getUpdatedAt();
            if (null == updatedAt) {
                return BaseResp.ok(null);
            }
            LocalDateTime createdAt = entity.getCreatedAt();
            long daysDiff = ChronoUnit.DAYS.between(createdAt, updatedAt);
            if (daysDiff > 0) {
                return BaseResp.ok(null);
            }
            Map<String, Object> map = new HashMap<>();
            String username = entity.getUsername();
            String token = JWTUtil.sign(username);
            map.put("token", token);
            UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("open_id", username));
            if (null != userEntity) {
                map.put("userInfo", userEntity);
            }
            return BaseResp.ok(map);
        }
        return BaseResp.ok(null);
    }

    @WebRecord
    @RequestMapping("/execPython")
    @ResponseBody
    public synchronized BaseResp execPython(String code) {
        String fileName = pythonFilePath + "output.py";
        String resultFileName = pythonFilePath + "pyresultttttt.txt";
        File pyFile = new File(fileName);
        File resultFile = new File(resultFileName);
        String result = "";
        try {
//            PythonInterpreter interpreter = new PythonInterpreter();
//            interpreter.exec(code);
            if (!pyFile.exists()) {
                pyFile.createNewFile();
            }
            if (!resultFile.exists()) {
                resultFile.createNewFile();
            }
            Files.write(Paths.get(fileName), code.getBytes());
            Process process = Runtime.getRuntime().exec("python " + fileName);
//            Process process = Runtime.getRuntime().exec("python " + fileName + "> "+resultFileName );
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            result = input.readLine();
            result = URLDecoder.decode(result, "utf8");
            input.close();
            ir.close();
            return BaseResp.ok("success", result);
        } catch (Exception e) {
            e.printStackTrace();
            Writer writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            String errorMsg = writer.toString().substring(0, 100);
            log.error("python代码执行错误:" + errorMsg);
            return BaseResp.ok(errorMsg);
        } finally {
            pyFile.delete();
        }
    }

    //    @WebRecord
    @PostMapping("/getString")
    @ResponseBody
    public BaseResp getString(Integer length, HttpServletRequest request) {
        String token = request.getHeader("token");
        String randomStr = StringTools.getRandomString(length);
        return BaseResp.ok(token, randomStr);
    }

    @PostMapping("/test/{length}")
    @ResponseBody
    public BaseResp test(@PathVariable Integer length, HttpServletRequest request) {
        String token = request.getHeader("token");
        String randomStr = StringTools.getRandomString(length);
        return BaseResp.ok(token, randomStr);
    }

    @WebRecord
    @GetMapping("/requestApi")
    @ResponseBody
    public BaseResp requestApi(String length, String token) {
        Map<String, String> paramMap = new HashMap<>();
        Map<String, String> headMap = new HashMap<>();
        paramMap.put("length", length);
        headMap.put("token", token);
        String result = HttpClientUtil.getPostResponse("http://47.107.102.196:8083/common/getString", paramMap, headMap);
        return BaseResp.ok(result);
    }

    @WebRecord
    @RequestMapping("/getObject")
    @ResponseBody
    public BaseResp getObject() {
        UserEntity entity = new UserEntity();
        entity.setUsername("leo");
        entity.setPassword("123123123");
        entity.setNickName("leo");
        entity.setCreatedAt(LocalDateTime.now());
        return BaseResp.ok(entity);
    }

    /**
     * 保存formid
     */
    @WebRecord
    @PostMapping
    @ResponseBody
    @RequestMapping("/saveFormid")
    public BaseResp saveFormid(HttpServletRequest request, String formId, String fromType) {
        if (StringTools.isNullOrEmpty(formId)) {
            log.warn("保存form_id接口缺少form_id参数");
            return BaseResp.ok("保存form_id接口缺少form_id参数");
        }
        if (null == request.getHeader("token") || null == JWTUtil.getCurrentUsername(request)) {
            log.warn("首页商品列表接口token校验失败，缺少token参数");
            return BaseResp.error(-3, "token invalid.");
        }
        String open_id = JWTUtil.getCurrentUsername(request);
        UserFormidEntity entity = new UserFormidEntity();
        entity.setFormidType(fromType);
        entity.setOpenId(open_id);
        entity.setFormId(formId);
        entity.setCreatedAt(LocalDateTime.now());
        try {
            if (null != userFormidService.getOne(new QueryWrapper<UserFormidEntity>().eq("form_id", formId))) {
                log.error("formId: " + formId + "已经存在");
                return BaseResp.ok("formId: " + formId + "已经存在");
            }
            boolean result = userFormidService.save(entity);
            if (!result) {
                log.error("保存用户formid失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("保存用户formid异常,异常信息为 : " + e.getMessage());
        }
        return BaseResp.ok();
    }

    /**
     *
     */
    @WebRecord
    @RequestMapping("/sendDynamicNotice")
    @ResponseBody
    public BaseResp sendDynamicNotice(@RequestBody TemplateSendData data, String openId, String page, String templateId) {
        page = page.replace("&amp;", "&");
        log.warn("change page is " + URLDecoder.decode(page));
        boolean b = userFormidService.sendDynamicNotice(openId, data, templateId, page);
        log.warn("page : " + page);
        if (b) {
            return BaseResp.ok("提现通知模板消息发送成功");
        } else {
            return BaseResp.ok("提现通知模板消息发送失败");
        }
    }

    @WebRecord
    @RequestMapping("/checkVersion")
    @ResponseBody
    public BaseResp checkVersion(HttpServletRequest request) {
        String version = request.getHeader("version");
        try {
            Integer v = Integer.valueOf(version);
            SettingEntity entity = settingService.getOne(new QueryWrapper<SettingEntity>().eq("setting_key", "version"));
            if (null == entity) {
                return BaseResp.ok(false);
            }
            Integer entityVersion = Integer.valueOf(entity.getSettingValue());
            if (v > entityVersion) {
                return BaseResp.ok(false);
            } else {
                return BaseResp.ok(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResp.ok(false);
    }

    @WebRecord
    @RequestMapping("/test")
    @ResponseBody
    public BaseResp test() {
        Integer success = 0;
        Integer failure = 0;
        List<FeedEntity> list = feedService.list();
        if (null != list && !list.isEmpty()) {
            for (FeedEntity feedEntity : list) {
                String content = feedEntity.getContent();
                Integer length = content.length();
                Integer intervalTime = 1000;
                if (0 < length && length <= 10) {
                    intervalTime = 1000;
                } else if (10 < length && length <= 20) {
                    intervalTime = 3000;
                } else if (20 < length && length <= 60) {
                    intervalTime = 4000;
                } else if (60 < length && length <= 100) {
                    intervalTime = 5000;
                } else if (length > 100) {
                    intervalTime = 10000;
                }
                feedEntity.setIntervalTime(intervalTime);
                boolean result = feedService.updateById(feedEntity);
                if (!result) {
                    log.error("更新feed的间隔时间失败,id为: " + feedEntity.getId());
                    failure++;
                } else {
                    success++;
                }
            }
        }
        return BaseResp.ok("成功更新feed间隔时间" + success + "个,失败" + failure + "个");
    }


    @WebRecord
    @RequestMapping("/testUpdateHasNotice")
    @ResponseBody
    public BaseResp testUpdateHasNotice() {
        Integer result = userExerciseService.updateHasNotice(1, 143);
        return BaseResp.ok(result);
    }

}