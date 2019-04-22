package io.renren.modules.generator.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_setting")
public class SettingEntity extends BaseEntity {
    private String settingKey;
    private String settingValue;
}
