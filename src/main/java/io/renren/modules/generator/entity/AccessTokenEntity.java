package io.renren.modules.generator.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_access_token")
public class AccessTokenEntity extends BaseEntity {
    private String appid;
    private String accessToken;
}
