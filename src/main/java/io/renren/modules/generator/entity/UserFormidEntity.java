package io.renren.modules.generator.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-08-31 10:52:33
 */
@Data
@TableName("tb_user_formid")
public class UserFormidEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String openId;
	private String formId;
	private String formidType;
	@TableField(exist = false)
	private String nickName;
}
