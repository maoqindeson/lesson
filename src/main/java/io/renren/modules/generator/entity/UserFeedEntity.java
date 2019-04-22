package io.renren.modules.generator.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
@Data
@TableName("tb_user_feed")
public class UserFeedEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private Integer feedId;
	private Integer orgId;
	private String feedResult;
	}

