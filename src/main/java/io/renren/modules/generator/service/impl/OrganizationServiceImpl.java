package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.OrganizationDao;
import io.renren.modules.generator.entity.*;
import io.renren.modules.generator.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("organizationService")
public class OrganizationServiceImpl extends ServiceImpl<OrganizationDao, OrganizationEntity> implements OrganizationService {

    @Autowired
    private FeedService feedService;
    @Autowired
    private UserFeedService userFeedService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private ProductService productService;
    @Override
    public List<OrganizationEntity> getTree(String username) {
        List<OrganizationEntity> orgList = this.list(new QueryWrapper<OrganizationEntity>());
        List<OrganizationEntity> newList = new ArrayList<OrganizationEntity>();
        for (OrganizationEntity entity1 : orgList) {
            boolean mark = false;
            for (OrganizationEntity entity2 : orgList) {
                if (entity1.getParentId() != null && entity1.getParentId().equals(entity2.getId())) {
                    mark = true;
                    if (entity2.getChildList() == null) {
                        entity2.setChildList(new ArrayList<OrganizationEntity>());
                    }
                    entity2.getChildList().add(entity1);
                    break;
                }
            }
            if (!mark) {
                newList.add(entity1);
            }
        }
        for (OrganizationEntity entity : newList) {
            Integer orgId = entity.getId();
            Double rate = getRateOfProgress(orgId, username);
//            String finishRate = String.valueOf(rate)+"%";
            entity.setFinishRate(rate);
            boolean enableRead = checkEnableRead(orgId,username);
            boolean hasPossessed = checkHasBuy(orgId,username);
            entity.setHasPossessed(hasPossessed);
            entity.setEnableRead(enableRead);
        }
        return newList;
    }

    @Override
    public List<OrganizationEntity> getTreeByOrgId(Integer orgId, List<OrganizationEntity> list, String username) {
        if (list.size() == 0) {
            return null;
        }
        List<OrganizationEntity> newList = new ArrayList<>();
        for (OrganizationEntity organizationEntity : list) {
            if (organizationEntity.getParentId() == orgId) {
                newList.add(organizationEntity);
            }
        }
        for (OrganizationEntity organizationEntity : newList) {
            organizationEntity.setChildList(getTreeByOrgId(organizationEntity.getId(), list, username));
        }
        if (newList.size() == 0) {
            return null;
        }
        for (OrganizationEntity entity : newList) {
            orgId = entity.getId();
            Double rate = getRateOfProgress(orgId, username);
//            String finishRate = String.valueOf(rate)+"%";
            entity.setFinishRate(rate);
            boolean enableRead = checkEnableRead(orgId,username);
            boolean hasPossessed = checkHasBuy(orgId,username);
            entity.setHasPossessed(hasPossessed);
            entity.setEnableRead(enableRead);
        }
        return newList;
    }

    @Override
    public double getRateOfProgress(Integer orgId, String username) {
        List<OrganizationEntity> list = this.list(new QueryWrapper<OrganizationEntity>());
        List<OrganizationEntity> orgList = traverseTreeList(list, orgId);
        List<Integer> orgIdList = new ArrayList<>();
        orgIdList.add(orgId);
        for (Object entity : orgList) {
            OrganizationEntity org = (OrganizationEntity) entity;
            orgIdList.add(org.getId());
        }
        Integer total = feedService.countFeedsByOrgIds(orgIdList);
        UserFeedEntity userFeedEntity = userFeedService.getLastRecord(username);
        if (null==userFeedEntity){
            return 0;
        }
        Integer recordId = userFeedEntity.getFeedId();

        Integer endId = feedService.getLastFeedIdByOrgIds(orgIdList);
        if (null==endId||0==endId){
            return 0;
        }
        Integer residue = endId - recordId;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);
        double rate = 0;
        if (0!=total){
            if (residue<0){
                residue=0;
            }
            if (residue>=total){
                rate = 0;
            }else {
                rate = new BigDecimal((float)100-residue*100/total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//                rate = df.format(100 - residue * 100 / total) ;
            }
        }else {
            rate = 0;
        }
        return rate;
    }



    //检查是否父子org关系
    @Override
    public boolean checkOrg(Integer childId, Integer parentId) {
        List<OrganizationEntity> list = this.getTreeByOrgId(parentId,this.list(),null);
        if (null==list||list.isEmpty()){
            return false;
        }
        for (OrganizationEntity entity:list){
            if (entity.getId()==childId){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkEnableRead(Integer orgId, String username) {
        UserFeedEntity userFeedEntity = userFeedService.getLastRecord(username);
        OrganizationEntity organizationEntity = organizationService.getById(orgId);
        if (null==organizationEntity){
            log.error("checkEnableRead方法中出现非法orgId");
            return false;
        }
        if (null==organizationEntity.getLastOrg()||organizationEntity.getLastOrg()==0){
            return true;
        }
        if (null==userFeedEntity){
            return false;
        }
        Integer lastOrg = userFeedEntity.getOrgId();
        if (orgId==lastOrg){
            return true;
        }
        lastOrg = organizationEntity.getLastOrg();
        if (lastOrg==0){
            return true;
        }
        double rate = getRateOfProgress(lastOrg,username);
        if (rate>=100){
            return true;
        }
        return false;
    }

    //查询用户是否购买了某个章节的课程
    @Override
    public boolean checkHasBuy(Integer orgId, String username) {
        //首先看用户买了哪个产品
            List<MallOrderEntity> list = mallOrderService.list(new QueryWrapper<MallOrderEntity>().eq("username",username)
            .eq("order_status",1));
            if (null==list||list.isEmpty()){
                return false;
            }
            for (MallOrderEntity mallOrderEntity : list){
               Integer productId =  mallOrderEntity.getProductId();
               Integer buyOrg = 0 ;
               ProductEntity productEntity = productService.getById(productId);
               Integer type = mallOrderEntity.getProductType();
               if (1==type){
                buyOrg = productEntity.getTrialOrg();
               }else if (2==type){
                   buyOrg = productEntity.getOrgId();
               }
               if (buyOrg==orgId){
                   return true;
               }else {
                   boolean result = checkOrg(orgId,buyOrg);
                   if (result){
                       return true;
                   }
               }
            }
        return false;
    }

    public static List<OrganizationEntity> traverseTreeList(List<OrganizationEntity> list, Integer parentId) {
        List<OrganizationEntity> newList = new ArrayList<>();
        for (OrganizationEntity entity : list) {
            Integer pid = entity.getParentId();
            Integer orgId = entity.getId();
            if (parentId == pid) {
                newList.add(entity);
                List<OrganizationEntity> entityList = traverseTreeList(list, orgId);
                for (OrganizationEntity entity1 : entityList) {
                    newList.add(entity1);
                }
            }
        }
        return newList;
    }
}
