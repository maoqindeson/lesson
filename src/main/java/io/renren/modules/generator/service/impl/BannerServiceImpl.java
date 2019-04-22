package io.renren.modules.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.generator.dao.BannerDao;
import io.renren.modules.generator.dao.FeedDao;
import io.renren.modules.generator.entity.BannerEntity;
import io.renren.modules.generator.entity.FeedEntity;
import io.renren.modules.generator.entity.UserFeedEntity;
import io.renren.modules.generator.form.SearchForm;
import io.renren.modules.generator.service.BannerService;
import io.renren.modules.generator.service.FeedDataService;
import io.renren.modules.generator.service.FeedService;
import io.renren.modules.generator.service.UserFeedService;
import io.renren.modules.generator.utils.FeedData.*;
import io.renren.modules.generator.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("bannerService")
public class BannerServiceImpl extends ServiceImpl<BannerDao, BannerEntity> implements BannerService {


}
