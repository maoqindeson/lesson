package io.renren.modules.generator.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("tb_product_price")
public class ProductPriceEntity extends BaseEntity implements Serializable {
	private Integer productId;
	private BigDecimal price;
	private BigDecimal oldPrice;
	private BigDecimal trialPrice;
	private Integer activity;
}
