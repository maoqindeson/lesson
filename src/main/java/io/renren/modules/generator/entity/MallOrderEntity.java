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
 * @date 2019-03-27 18:26:02
 */
@Data
@TableName("tb_mall_order")
public class MallOrderEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String orderId;
	private String tradeno;
	private String username;
	private Integer productId;
	private Integer productType;
	private String productName;
	private BigDecimal payMoney;
	private String prepayId;
	private Integer orderStatus;
}
