package io.renren.modules.generator.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
@Data
@TableName("tb_organization")
public class OrganizationEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer parentId;
    private Integer lastOrg;
    private Integer nextOrg;
    private String name;
    private int grade;
    private Integer activity;
    @TableField(exist = false)
    private List<OrganizationEntity> childList;
    @TableField(exist = false)
    private double finishRate;
    @TableField(exist = false)
    private boolean enableRead;
    @TableField(exist = false)
    private boolean hasPossessed;

}

