package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.Map;
import io.renren.modules.generator.dao.ProductPriceDao;
import io.renren.modules.generator.entity.ProductPriceEntity;
import io.renren.modules.generator.service.ProductPriceService;


@Service("productPriceService")
public class ProductPriceServiceImpl extends ServiceImpl<ProductPriceDao, ProductPriceEntity> implements ProductPriceService {

}