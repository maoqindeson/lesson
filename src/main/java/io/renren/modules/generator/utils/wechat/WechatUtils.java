package io.renren.modules.generator.utils.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.renren.modules.generator.utils.AlipayCore;
import io.renren.modules.generator.utils.HttpClientUtil;
import io.renren.modules.generator.utils.PubFun;
import io.renren.modules.generator.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Map;

@Slf4j
@Configuration
public class WechatUtils {

    private static int socketTimeout = 10000;// 连接超时时间，默认10秒
    private static int connectTimeout = 30000;// 传输超时时间，默认30秒

    /**
     * 获取accessToken
     *
     * @param appid
     * @param secret
     * @return accessToken 接口访问凭证
     */
    public String requireAccessToken(String appid, String secret) {
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET";
        requestUrl = requestUrl.replace("APPID", appid).replace("SECRET", secret);
        // 获取accessToken
        String result = HttpClientUtil.getGetResponse(requestUrl);
        if (!StringTools.isNullOrEmpty(result)) {
            JSONObject json = JSON.parseObject(result);
            if (null != json.get("access_token") && !StringTools.isNullOrEmpty(json.get("access_token").toString())) {
                String accessToken = json.get("access_token").toString();
                log.warn("请求微信获取accesstoken为 : " + accessToken);
                return accessToken;
            }
        }
        log.error("请求微信获取accesstoken失败,返回空");
        return null;
    }
    /**
     * 发送模板消息
     * @param
     * @return WeixinUserInfo
     */
    public static boolean sendTemplateMessage(String access_token ,String  sendJson) {
        // 拼接请求地址
//        access_token = getAccessToken("wxc9f8070bf847afdb","ff9abc64cb6ce1965deacbc9a40d6e65");
//        log.warn("accesstoken为: "+access_token);
//        String officialAccountsOpenId = "oMImQ0bVScpLH7-PVo4nfM7vjyog";
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN",access_token);
//        Miniprogram miniprogram = new Miniprogram();
//        miniprogram.setAppid("wxcb530c140be871b2");
//        miniprogram.setPagepath("pages/myWaitting/myWaitting");
//        JSONObject json = new JSONObject();
//        TemplateSendData data = new TemplateSendData();
//        data.setFirst(new Keyword("你好,你有一个新的会议","#173177"));
//        data.setKeyword1(new Keyword("名称","#173177"));
//        data.setKeyword2(new Keyword("时间","#173177"));
//        data.setKeyword3(new Keyword("地点地点地点地点地点地点地点地点地点地点地点地点地点地点地点地点地点地点地点地点地点","#173177"));
//        data.setRemark(new Keyword("请及时参加会议","#173177"));
//        json.put("touser", officialAccountsOpenId);
//        json.put("template_id", "JWcS4nKbNJQV0qyRLk1dLsduX6K-h4NQnTQTBKhOZNU");
////        json.put("url", "http://weixin.qq.com/download");
//        json.put("miniprogram", miniprogram);
//        json.put("data", data);
//        log.warn("请求微信模板消息参数为: " + json.toString());
        log.warn("开始请求微信模板消息: " + requestUrl);
        String wxTemplateSendUrlResult = HttpClientUtil.sendJsonHttpPost(requestUrl,sendJson);
//        String wxTemplateSendUrlResult = HttpClientUtil.sendJsonHttpPost(requestUrl,json.toJSONString());
        log.warn("请求微信模板消息结果为: " + wxTemplateSendUrlResult);
        JSONObject wxTemplateSendUrlResultJson = JSONObject.parseObject(wxTemplateSendUrlResult);
        if (null!=wxTemplateSendUrlResultJson&&wxTemplateSendUrlResultJson.get("errmsg").equals("ok")){
            log.warn("模板消息发送成功");
            return true;
        }else {
            log.error("模板消息发送失败,返回结果为: "+wxTemplateSendUrlResultJson.toJSONString());
        }
        log.error("模板消息发送失败");
        return false;
    }

    /**
     * 生成微信支付的签名
     *
     * @param wxPrePayEntity
     * @return
     */
    public static String createPaySign(WXPrePayEntity wxPrePayEntity, String key) {
        try {
            // 将bean转成map
            Map<String, String> beanMap = AlipayCore.convertBean(wxPrePayEntity);
            // 去掉空值
            Map<String, String> resultMap = AlipayCore.paraFilter(beanMap);
            String string1 = AlipayCore.createLinkString(resultMap);
            String stringSignTemp = string1 + "&key=" + key;
            String sign = PubFun.MD5(stringSignTemp).toUpperCase();
            return sign;
        } catch (Exception e) {
            log.error("生成微信支付签名异常，异常信息为：" + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 微信通知服务器支付结果之后，就要返回一个xml的信息，
     *
     * @param return_code SUCCESS
     * @param return_msg  OK
     * @return
     */
    public static void payResultBackXml(String return_code, String return_msg, HttpServletResponse response) {
        try {
            String xml = "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA["
                    + return_msg + "]]></return_msg></xml>";
            // String xml =
            // "<xml><return_code>"+return_code+"</return_code><return_msg>"+return_msg+"</return_msg></xml>";
            //System.out.println(xml);
            OutputStream stream = response.getOutputStream();// 获取一个向Response对象写入数据的流,当tomcat服务器进行响应的时候，会将Response中的数据写给浏览器
            stream.write(xml.getBytes("UTF-8"));
            stream.flush();
            stream.close();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        String access_token = getAccessToken("wxc9f8070bf847afdb","ff9abc64cb6ce1965deacbc9a40d6e65");
//        System.out.println("get accesstoken is "+ access_token);
//        sendTemplateMessage("","");
    }
}
