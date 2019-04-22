package io.renren.modules.generator.utils;

import lombok.Data;

@Data
public class WechatLoginForm {
    private String nickName;
    private String avatarUrl;
    private String gender;
    private String encryptedData;
    private String iv;
    private String code;
    private String qrcode;
}
