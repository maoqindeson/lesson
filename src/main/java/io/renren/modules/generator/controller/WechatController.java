package io.renren.modules.generator.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.modules.generator.aop.WebRecord;
import io.renren.modules.generator.entity.*;
import io.renren.modules.generator.service.*;
import io.renren.modules.generator.utils.*;
import io.renren.modules.generator.utils.wechat.PayUtil;
import io.renren.modules.generator.utils.wechat.WXPrePayEntity;
import io.renren.modules.generator.utils.wechat.WechatUtils;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Data
@RestController
@RequestMapping("wechat")
@ConfigurationProperties(prefix = "wechat")
public class WechatController {
    private String appId;
    private String appSecret;
    private String grantType;
    private String mchId;
    private String key;
    private String notifyUrl;
    private String TRADETYPE;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductPriceService productPriceService;
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private SceneDataService sceneDataService;

    /**
     * 小程序登录
     */
    @WebRecord
    @PostMapping("/login")
    public BaseResp login(@RequestBody WechatLoginForm wechatLoginForm) {
        log.warn("接收到微信授权登录参数为 : " + wechatLoginForm.toString());
        if (StringUtils.isBlank(wechatLoginForm.getCode())) {
            return BaseResp.error("code不能为空");
        }
        if (StringUtils.isBlank(wechatLoginForm.getAvatarUrl())) {
            return BaseResp.error("头像信息不能为空");
        }
        if (StringUtils.isBlank(wechatLoginForm.getGender())) {
            return BaseResp.error("性别信息不能为空");
        }
        if (StringUtils.isBlank(wechatLoginForm.getNickName())) {
            return BaseResp.error("昵称不能为空");
        }
        if (StringUtils.isBlank(wechatLoginForm.getEncryptedData())) {
            return BaseResp.error("用户信息密文不能为空");
        }
        if (StringUtils.isBlank(wechatLoginForm.getIv())) {
            return BaseResp.error("ivc参数不能为空");
        }
        String qrcode = wechatLoginForm.getQrcode();
        String avatarUrl = wechatLoginForm.getAvatarUrl();
        String gender = wechatLoginForm.getGender();
        String nickName = wechatLoginForm.getNickName();
        String code = wechatLoginForm.getCode();
        String param = "?grant_type=" + grantType + "&appid=" + appId + "&secret=" + appSecret + "&js_code=" + code;

        String url = "https://api.weixin.qq.com/sns/jscode2session" + param;
        log.warn("请求微信登录url : " + url);
        String result = HttpClientUtil.getGetResponse(url);
        if (StringTools.isNullOrEmpty(result)) {
            log.error("小程序登录接口返回结果为空");
            return BaseResp.error("小程序登录接口返回结果为空");
        }
        log.warn("小程序登录接口返回参数：{}", result);
        JSONObject rsJosn = JSON.parseObject(result);
        if (rsJosn.get("errcode") != null) {
            //返回异常信息
            log.error("小程序登陆返回异常信息：" + rsJosn.get("errmsg").toString());
            return BaseResp.error(rsJosn.get("errmsg").toString());
        }
        String sessionKey = null;
        Map<String, Object> map = new HashMap<>();
        String unionId = null;
        JSONObject userInfoJSON = null;
        String openId = null;
        if (null != rsJosn.get("session_key")) {
            sessionKey = rsJosn.get("session_key").toString();
        } else {
            log.error("小程序登录接口无法获得session_key");
            return BaseResp.error("小程序登录接口无法获得session_key");
        }
        if (null != rsJosn.get("openid")) {
            openId = rsJosn.get("openid").toString();
        } else {
            log.error("小程序登录接口无法获得openid");
            return BaseResp.error("小程序登录接口无法获得openid");
        }
        if (!StringTools.isNullOrEmpty(wechatLoginForm.getEncryptedData())) {
            //////////////// 2、对encryptedData加密数据进行AES解密其中包含这openid和unionid ////////////////
            try {
                log.warn("收到encryptedData为：" + wechatLoginForm.getEncryptedData() + "用户openid为" + openId + "昵称为" + nickName);
                String decryptResult = AesCbcUtil.decrypt(wechatLoginForm.getEncryptedData(), sessionKey, wechatLoginForm.getIv(), "UTF-8");
                if (null != decryptResult && decryptResult.length() > 0) {
                    userInfoJSON = JSON.parseObject(decryptResult);
                    log.warn("对encryptedData加密数据进行AES解密得到数据" + userInfoJSON);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("对encryptedData加密数据进行AES解密失败，encryptedData数据为：" + wechatLoginForm.getEncryptedData());
                return BaseResp.error("对encryptedData加密数据进行AES解密失败");
            }
        }
        if (null == userInfoJSON) {
            log.error("用户微信登陆encryptedData无法解密，用户encryptedData为" + wechatLoginForm.getEncryptedData());
        } else {
            if (null != userInfoJSON.get("unionId") && !StringTools.isNullOrEmpty(userInfoJSON.get("unionId").toString())) {
                unionId = userInfoJSON.get("unionId").toString();
            } else if (null != rsJosn.get("unionid") && !StringTools.isNullOrEmpty(rsJosn.get("unionid").toString())) {
                unionId = rsJosn.get("unionid").toString();
            } else {
                log.error("用户微信登陆无法获得unionid，暂用openid代替，用户openid为" + openId + "昵称为" + nickName);
                unionId = openId;
            }
        }
        try {
            //如果通过open_id能查出存在用户，则直接返回用户信息
            synchronized (this) {
                if (null == userService.getOne(new QueryWrapper<UserEntity>().eq("open_id", openId))) {
                    //抽空不全插入检查，唯一键等；
                    UserEntity userEntity = new UserEntity();
                    userEntity.setOpenId(openId);
                    userEntity.setUnionId(unionId);
                    userEntity.setUsername(openId);
                    userEntity.setAvatarUrl(avatarUrl);
                    userEntity.setGender(gender);
                    userEntity.setNickName(nickName);
                    userEntity.setCreatedAt(LocalDateTime.now());
                    if (!userService.save(userEntity)) {
                        log.error("登陆接口插入用户数据失败,用户openid为" + openId + "昵称为" + nickName);
                        return BaseResp.error("登陆接口插入用户数据失败,用户openid为" + openId + "昵称为" + nickName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("登陆接口插入用户数据异常" + e.getMessage() + "用户openid为" + openId + "昵称为" + nickName);
            return BaseResp.error("登陆接口插入用户数据异常,用户openid为" + openId + "昵称为" + nickName);
        }
        if (!StringTools.isNullOrEmpty(qrcode) && !StringTools.isNullOrEmpty(openId)) {
            //如果有传qrcode则为web端通过扫码登记登录态
            log.warn("web端通过扫码登记登录态");
            SceneDataEntity sceneDataEntity = sceneDataService.getOne(new QueryWrapper<SceneDataEntity>().eq("content", qrcode));
            if (null != sceneDataEntity) {
                sceneDataEntity.setStatus("login");
                sceneDataEntity.setUsername(openId);
                sceneDataEntity.setUpdatedAt(LocalDateTime.now());
                boolean sceneresult = sceneDataService.updateById(sceneDataEntity);
                if (!sceneresult) {
                    log.error("通过qrcode登陆成功,但更新登陆态失败");
                } else {
                    log.warn("通过qrcode登陆成功,更新登陆态成功");
                    //增加返回用户当前
                }
            }else {
                log.error("根据qrcode找不到对应的记录,qrcode为 : "+qrcode);
            }
        }
        String token = JWTUtil.sign(openId);
        map.put("openId", openId);
        map.put("unionId", unionId);
        map.put("sessionKey", sessionKey);
        map.put("token", token);
        return BaseResp.ok(map);
    }

    @WebRecord
    @PostMapping("pay")
    public BaseResp pay(HttpServletRequest request, Integer productId ,Integer productType) {
        String username = JWTUtil.getCurrentUsername(request);
        if (null == productId || 0 == productId) {
            return BaseResp.error("productId不能为空");
        }
        if (null==productType||productType==0){
            //默认是购买体验课
            productType=1;
        }
        try {
            ProductEntity productEntity = productService.getById(productId);
            if (productEntity == null) {
                return BaseResp.error("找不到对应商品的记录 : " + productId);
            }
            ProductPriceEntity productPriceEntity = productPriceService.getOne(new QueryWrapper<ProductPriceEntity>().eq("product_id", productId)
                    .eq("activity", 1));
            if (productPriceEntity == null) {
                return BaseResp.error("找不到对应商品的价格记录 : " + productId);
            }
            if (productPriceEntity.getPrice().compareTo(new BigDecimal("0")) <= 0) {
                return BaseResp.error("商品支付价格不能为0 ");
            }
            //如果购买的是体验课,则用体验价
            BigDecimal money =new BigDecimal("0");
            if (productType==1){
                money = productPriceEntity.getTrialPrice();
            }//否则是正课价格购买
            else if (productType==2){
                money = productPriceEntity.getPrice();
            }else {
                return BaseResp.ok("入参productType非法");
            }
            BigDecimal totalFee = money.multiply(new BigDecimal(100));
            String fee = totalFee.stripTrailingZeros().toPlainString();
            String productName = productEntity.getName();
            //生成的随机字符串
            String nonce_str = StringTools.getRandomStringByLength(32);
            String tradeno = StringTools.getTradeno();
            MallOrderEntity mallOrderEntity = new MallOrderEntity();
            mallOrderEntity.setUsername(username);
            mallOrderEntity.setOrderId(tradeno);
            mallOrderEntity.setCreatedAt(LocalDateTime.now());
            mallOrderEntity.setTradeno(tradeno);
            mallOrderEntity.setProductId(productId);
            mallOrderEntity.setProductType(productType);
            mallOrderEntity.setPayMoney(money);
//            mallOrderEntity.setOrderStatus(0);
            mallOrderEntity.setProductName(productName);
            boolean mallorderinsertresult = mallOrderService.save(mallOrderEntity);
            if (!mallorderinsertresult) {
                log.error("微信下单接口保存订单信息失败");
            }
            //获取客户端的ip地址
            String spbill_create_ip = IpUtil.getIpAddr(request);
            WXPrePayEntity wxPrePayEntity = new WXPrePayEntity();
            wxPrePayEntity.setAppid(appId);
            wxPrePayEntity.setMch_id(mchId);
            wxPrePayEntity.setNonce_str(nonce_str);
            wxPrePayEntity.setBody(productName);
            wxPrePayEntity.setOut_trade_no(tradeno);
            wxPrePayEntity.setTotal_fee(fee);
            wxPrePayEntity.setSpbill_create_ip(spbill_create_ip);
            wxPrePayEntity.setNotify_url(notifyUrl);
            wxPrePayEntity.setTrade_type(TRADETYPE);
            wxPrePayEntity.setOpenid(username);
            String mysign = WechatUtils.createPaySign(wxPrePayEntity, key);
            wxPrePayEntity.setSign(mysign);
            String xml = XmlUtil.toXml(wxPrePayEntity);
            xml = xml.replaceAll("io.renren.modules.generator.utils.wechat.WXPrePayEntity", "xml");
            xml = xml.replaceAll("__", "_");
            log.warn("调试模式_统一下单接口 请求XML数据：" + xml);
            //调用统一下单接口，并接受返回的结果
            String result = HttpsPostUtil.post("https://api.mch.weixin.qq.com/pay/unifiedorder", xml, "utf-8");
            log.warn("调试模式_统一下单接口 返回结果：" + result);
            //将解析结果存储在HashMap中
            Map map = PayUtil.doXMLParse(result);
            //返回状态码
            String return_code = (String) map.get("return_code");
            HashMap<String, Object> data = new HashMap<>();
            if (return_code.equalsIgnoreCase("SUCCESS")) {
                // 业务结果
                //返回的预付单信息
                String prepay_id = (String) map.get("prepay_id");
                data.put("nonceStr", nonce_str);
                data.put("package", "prepay_id=" + prepay_id);
                Long timeStamp = System.currentTimeMillis() / 1000;
                data.put("timeStamp", timeStamp.toString());
                String stringSignTemp = "appId=" + appId + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id + "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名
                String paySign = PayUtil.sign(stringSignTemp, "&key=" + key, "utf-8").toUpperCase();
                data.put("paySign", paySign);
                data.put("appId", appId);
                data.put("signType", "MD5");
                data.put("orderId", mallOrderEntity.getId());
                log.warn("订单：" + tradeno + ",商户id:" + mchId + "微信下单成功，返回结果===" + result);
                mallOrderEntity.setPrepayId(prepay_id);
                boolean updateResult = mallOrderService.updateById(mallOrderEntity);
                if (!updateResult) {
                    log.warn("更新订单prepayId失败");
                }
                return BaseResp.ok(data);
            }
            log.error("订单：" + tradeno + ",商户id:" + mchId + "微信下单失败，返回结果===" + result);
            return BaseResp.error("订单：" + tradeno + ",商户id:" + mchId + "微信下单失败，返回结果===" + result);

        } catch (Exception e) {
            log.error("小程序下单接口异常，异常信息为：" + e.getMessage());
            e.printStackTrace();
            return BaseResp.error("微信统一下单接口调用失败");
        }
    }

    @RequestMapping("notify")
    @ApiOperation("小程序支付结果回调接口")
    public String wxNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            String now = LocalDateTime.now().toString();
            log.warn(now + ": 微信回调开始");
            BufferedReader reader = null;
            reader = request.getReader();
            String line = "";
            String requestXml = null;
            StringBuffer inputString = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                inputString.append(line);
            }
            requestXml = inputString.toString();
            request.getReader().close();
            log.warn("异步通知的返回xml ： " + requestXml);
            Map notifyMap = XmlUtil.dom2Map(requestXml);
            // 判断结果
            if (StringTools.nil(requestXml)) {
                log.error("回调参数为Null或空字符串");
                WechatUtils.payResultBackXml("FAIL", "参数格式校验错误", response);
                return "success";
            }
            if ("FAIL".equals(notifyMap.get("result_code"))) {
                log.error("支付返回结果错误，原因：(" + notifyMap.get("err_code") + ")"
                        + notifyMap.get("err_code_des"));
                WechatUtils.payResultBackXml("FAIL", "参数格式校验错误", response);
                return "success";
            }
            Map<String, String> resultMap = AlipayCore.paraFilter(notifyMap);
            String string1 = AlipayCore.createLinkString(resultMap);
            String wxTempSign = string1 + "&key=" + key;
            wxTempSign = PubFun.MD5(wxTempSign).toUpperCase();
            if (!notifyMap.get("sign").equals(wxTempSign)) {
                log.error("签名不对！");
                log.error("微信发过来的： " + notifyMap.get("sign"));
                log.error("后台根据参数生成的: " + wxTempSign);
                WechatUtils.payResultBackXml("FAIL", "签名失败", response);
                return "success";
            }
            if ("SUCCESS".equalsIgnoreCase(notifyMap.get("return_code").toString())) {
                log.warn("开始更新用户购买课程记录的paystate状态使其可以上课");
                String tradeno = notifyMap.get("out_trade_no").toString();
                if (StringTools.isNullOrEmpty(tradeno)) {
                    log.error("支付回调out_trade_no参数为空,回调参数为 : " + requestXml);
                    return "sucess";
                }
                MallOrderEntity mallOrderEntity = mallOrderService.getOne(new QueryWrapper<MallOrderEntity>().eq("tradeno", tradeno));
                if (null == mallOrderEntity) {
                    log.error("支付回调attach找不到对应的记录,回调参数为: " + requestXml);
                    return "success";
                }
                mallOrderEntity.setUpdatedAt(LocalDateTime.now());
                mallOrderEntity.setOrderStatus(1);
                boolean updateResult = mallOrderService.updateById(mallOrderEntity);
                if (!updateResult) {
                    log.error("回调成功但更新记录支付状态失败,id : " + mallOrderEntity.getId());
                    return "success";
                }
                return "success";
            }
            log.warn(now + "微信回调签名验证成功");
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

}
