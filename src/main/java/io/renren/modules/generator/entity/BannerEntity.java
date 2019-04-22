package io.renren.modules.generator.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
@Data
@TableName("tb_banner")
public class BannerEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String imgUrl;
    private String redirectUrl;
}

