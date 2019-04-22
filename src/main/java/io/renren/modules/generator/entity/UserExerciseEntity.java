package io.renren.modules.generator.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_user_exercise")
public class UserExerciseEntity  extends BaseEntity implements Serializable {
    private String username;
    private Integer exerciseId;
    private Integer orgId;
    @TableField(exist = false)
    private String noticeOrgId;
    private String content;
    private Integer hasComplete;
    private Integer hasNotice;
}
