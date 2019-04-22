package io.renren.modules.generator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.generator.entity.OrganizationEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.aspectj.weaver.ast.Or;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2018-11-27 09:51:20
 */
public interface OrganizationService extends IService<OrganizationEntity> {
     List<OrganizationEntity> getTree(String username);
     List<OrganizationEntity> getTreeByOrgId(Integer orgId, List<OrganizationEntity> list , String username);
     double getRateOfProgress(Integer orgId, String username);
     boolean checkOrg(Integer childId,Integer parentId);
     boolean checkEnableRead(Integer orgId,String username);
     boolean checkHasBuy(Integer orgId,String username);

}

