package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.entity.OrganizationEntity;
import io.renren.modules.generator.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.python.jline.internal.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import io.renren.modules.generator.dao.ProductDao;
import io.renren.modules.generator.entity.ProductEntity;
import io.renren.modules.generator.service.ProductService;

@Slf4j
@Service("productService")
public class ProductServiceImpl extends ServiceImpl<ProductDao, ProductEntity> implements ProductService {
    @Autowired
    private OrganizationService organizationService;
    @Override
    public ProductEntity getByChildOrgId(Integer orgId) {
        if (null==orgId||0==orgId){
            return null;
        }
        List<OrganizationEntity> list = organizationService.list(new QueryWrapper<OrganizationEntity>().eq("grade",2));
        for (OrganizationEntity organizationEntity : list){
           Integer parentOrgId =  organizationEntity.getId();
            if (orgId!=parentOrgId&&organizationService.checkOrg(orgId,parentOrgId)){
                ProductEntity productEntity = this.getOne(new QueryWrapper<ProductEntity>().eq("org_id",parentOrgId));
                if (null==productEntity){
                    log.error("productService.getIdByOrgId根据orgId获取产品id失败,orgId : "+orgId);
                }else {
                    return productEntity;
                }
            }
        }
        return null;
    }
}