package io.renren.modules.generator.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
@Data
@TableName("tb_task")
public class TaskEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
   private String title;
   private String subtitle;
   private String content;
   private String introduce;
   private Integer orgId;
   private Integer productId;
   @TableField(exist = false)
   private String finishRate;
   @TableField(exist = false)
   private Integer hasBuy;
   @TableField(exist = false)
   private Integer hasBuyCount;
   @TableField(exist = false)
   private List<String> hasBuyAvatarUrls;
}

