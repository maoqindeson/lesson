package io.renren.modules.generator.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_exercise")
public class ExerciseEntity extends BaseEntity implements Serializable {
    private Integer orgId;
    private Integer intervalTime;
    private String type;
    private String content;
    private Integer hasEnd;
    private Integer nextOrg;
    private Integer parentOrg;
    @TableField(exist = false)
    private String exerciseResult;
}
