package io.renren.modules.generator.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.modules.generator.entity.*;
import io.renren.modules.generator.service.*;
import io.renren.modules.generator.utils.BaseResp;
import io.renren.modules.generator.utils.JWTUtil;
import io.renren.modules.generator.utils.StringTools;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.parser.Entity;


/**
 *
 *
 * @author leo
 * @email hujingleo01@163.com
 * @date 2019-03-27 18:26:03
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductPriceService productPriceService;
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private SettingService settingService;
    @Autowired
    private UserService userService;
    /**
     * 产品列表
     */
    @RequestMapping("/list")
    public BaseResp list(){
        List<ProductEntity> list = productService.list();
        return BaseResp.ok(list);
    }

    /**
     * 用户产品列表
     */
    @RequestMapping("/userProductList")
    @RequiresAuthentication
    public BaseResp userProductList(HttpServletRequest request,Integer type){
        String username = JWTUtil.getCurrentUsername(request);
        if (null==type){
            type = 0;
        }
        List<Integer> list =mallOrderService.getUserProductByType(username,type);
        if (null==list||list.isEmpty()){
            return BaseResp.ok(new ArrayList<>());
        }
        List<ProductEntity> productEntities = (List<ProductEntity>) productService.listByIds(list);
        for (ProductEntity productEntity : productEntities){
            ProductPriceEntity price = productPriceService.getOne(new QueryWrapper<ProductPriceEntity>().eq("product_id",productEntity.getId()));
            productEntity.setPrice(price);
            productEntity.setHasBuy(type);
            String imgUrls = productEntity.getImgUrls();
            String bannerUrls = productEntity.getBannerUrls();
            List<String> imgUrl = Arrays.asList(imgUrls.split(","));
            List<String> bannerUrl = Arrays.asList(bannerUrls.split(","));
            productEntity.setImgUrl(imgUrl);
            productEntity.setBannerUrl(bannerUrl);
        }
        return BaseResp.ok(productEntities);
    }

    /**
     * 产品详情
     */
    @RequestMapping("/info")
    @RequiresAuthentication
    public BaseResp info(Integer id ,HttpServletRequest request){
        String username = JWTUtil.getCurrentUsername(request);
        UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("username",username));
        ProductEntity entity = productService.getById(id);
        ProductPriceEntity price = productPriceService.getOne(new QueryWrapper<ProductPriceEntity>().eq("product_id",id));
        entity.setPrice(price);
        List<MallOrderEntity> list = mallOrderService.list(new QueryWrapper<MallOrderEntity>().eq("product_id",entity.getId())
        .eq("order_status",1).eq("username",username));
        if (null==list||list.isEmpty()){
            entity.setHasBuy(0);
        }else {
            entity.setHasBuy(1);
        }
        for (MallOrderEntity mallOrderEntity: list){
            if (mallOrderEntity.getProductType()==2){
                entity.setHasBuy(2);
            }
        }
        try {
            String imgUrls = entity.getImgUrls();
            String bannerUrls = entity.getBannerUrls();
            List<String> imgUrl = Arrays.asList(imgUrls.split(","));
            List<String> bannerUrl = Arrays.asList(bannerUrls.split(","));
            entity.setImgUrl(imgUrl);
            entity.setBannerUrl(bannerUrl);
            //查询用户是否有限购抢购资格,注册后N天内可以抢购
            SettingEntity settingEntity = settingService.getOne(new QueryWrapper<SettingEntity>().eq("setting_key","snap_up_days"));
            if (null!=settingEntity){
                String value = settingEntity.getSettingValue();
                Integer days = Integer.valueOf(value);
                LocalDateTime createdAt = userEntity.getCreatedAt();
                long daysDiff = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
                if (daysDiff<=days){
                    entity.setEnableSnapUp(true);
                }else {
                    entity.setEnableSnapUp(false);
                }
            }
            return BaseResp.ok(entity);
        }catch (Exception e){
            e.printStackTrace();
        }
            return BaseResp.ok();
    }

}
