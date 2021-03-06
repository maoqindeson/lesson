package io.renren.modules.generator.utils.wechat;

import lombok.Data;

import java.io.Serializable;

/**
 * 
 * Description：微信预支付请求参数
 * @author leo-2017-03-13
 */
@Data
public class WXPrePayEntity implements Serializable
{
	private static final long serialVersionUID = -7712257468736467291L;
	private String appid;//微信分配的公众账号ID（企业号corpid即为此appId）
	private String mch_id;//微信支付分配的商户号
	private String device_info;//终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
	private String nonce_str;//随机字符串，不长于32位。
	private String sign;//签名
	private String body;//商品或支付单简要描述
	private String detail;//商品名称明细列表
	private String attach;//附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
	private String out_trade_no;//商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
	private String fee_type;//符合ISO 4217标准的三位字母代码，默认人民币：CNY，
	private String total_fee;//订单总金额，只能为整数，
	private String spbill_create_ip;//APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
	private String time_start;//订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。
	private String time_expire;//订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则注意：最短失效时间间隔必须大于5分钟
	private String goods_tag;//商品标记，代金券或立减优惠功能的参数
	private String notify_url;//接收微信支付异步通知回调地址
	private String trade_type;//取值如下：JSAPI，NATIVE，APP，WAP,
	private String product_id;//trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。
	private String limit_pay;//no_credit--指定不能使用信用卡支付
	private String openid;//用户标识
}
