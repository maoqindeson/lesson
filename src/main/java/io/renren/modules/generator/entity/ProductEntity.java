package io.renren.modules.generator.entity;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * 
 * @author leo
 * @email hujingleo01@163.com
 * @date 2019-03-27 18:26:03
 */
@Data
@TableName("tb_product")
public class ProductEntity extends BaseEntity implements Serializable {
	private String name;
	private String introduce;
	private String imgUrls;
	private String bannerUrls;
	@TableField(exist = false)
	private List<String> imgUrl;
	@TableField(exist = false)
	private List<String> bannerUrl;
	private Integer orgId;
	private Integer trialOrg;
	@TableField(exist = false)
	private Integer hasBuy;
	@TableField(exist = false)
	private ProductPriceEntity price;
	@TableField(exist = false)
	private boolean enableSnapUp;
}
