package io.renren.modules.generator.controller;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.modules.generator.service.FeedService;
import io.renren.modules.generator.utils.BaseResp;
import io.renren.modules.generator.utils.JWTUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.generator.entity.MallOrderEntity;
import io.renren.modules.generator.service.MallOrderService;

import javax.servlet.http.HttpServletRequest;


/**
 *
 *
 * @author leo
 * @email hujingleo01@163.com
 * @date 2019-03-27 18:26:02
 */
@RestController
@RequestMapping("/mallorder")
public class MallOrderController {
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private FeedService feedService;
    /**
     * 订单列表
     */
    @RequestMapping("/orderList")
    public BaseResp orderList(HttpServletRequest request){
        String username = JWTUtil.getCurrentUsername(request);
        List<MallOrderEntity> list = mallOrderService.list(new QueryWrapper<MallOrderEntity>().eq("username",username));
        return BaseResp.ok(list);
    }

    /**
     * 查询订单状态
     */
    @RequestMapping("/getOrderStatus")
    @RequiresAuthentication
    public BaseResp getOrderStatus(Integer orderId){
        if (null==orderId||0==orderId){
            return BaseResp.error();
        }
        MallOrderEntity entity = mallOrderService.getById(orderId);
        if (null==entity){
            return BaseResp.error("找不到该订单: "+orderId);
        }
        Integer orderStatus = entity.getOrderStatus();
        if (1==orderStatus){
            return BaseResp.ok("已支付",1);
        }else {
            return BaseResp.ok("待支付",0);
        }
    }

    /**
     * 订单列表
     */
    @RequestMapping("/testPage")
    public BaseResp testPage(Integer pageIndex , Integer pageSize){

        return BaseResp.ok();
    }
}
