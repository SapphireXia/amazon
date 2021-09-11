package com.king.ruby.controller;

import com.king.ruby.compoment.AmazonReviewersCompoment;
import com.king.ruby.compoment.Common;
import com.king.ruby.mapper.AmazonAsinidMapper;
import com.king.ruby.mapper.AmazonReviewMapper;
import com.king.ruby.pojo.AmazonAsinid;
import com.king.ruby.pojo.AmazonReview;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@Api(value = "获取亚马逊的商品评论")
public class AmazonController {

    Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Autowired
    DataSource dataSource;

    @Autowired
    private AmazonReviewMapper amazonReviewMapper;

    @Autowired
    private AmazonAsinidMapper amazonAsinidMapper;

    @Autowired
    private AmazonReviewersCompoment amazonReviewersCompoment;

    @Autowired
    private Common common;

    @ApiOperation(value = "启动抓取亚马逊的评论")
    @RequestMapping(value = "/start", method = {RequestMethod.GET})
    public String start() {
        logger.info("now the method is run");
        List<AmazonAsinid> amazonAsinids = common.queryAllAsinId();
        logger.info(amazonAsinids.size());
        amazonAsinids.parallelStream().forEach(
           amazonAsinid -> {
               amazonReviewersCompoment.graspReviewers(amazonAsinid);
               amazonAsinid.setUsed("1");
               amazonAsinidMapper.updateById(amazonAsinid);
           }
        );
        return "抓取完成";
    }

//    @ApiOperation(value = "获取亚马逊的商品评论")
//    @RequestMapping(value = "/insert", method = {RequestMethod.POST})
//    public String selectAllSing() {
//        AmazonReview review = new AmazonReview();
//        review.setReviewDate("7/7/2021");
//        amazonReviewMapper.insert(review);
//        return "插入成功";
//    }

//    @ApiOperation(value = "获取亚马逊的商品评论")
    @RequestMapping(value = "/getReview", method = {RequestMethod.POST})
    public String selectAllSing(String productId) {
        amazonReviewersCompoment.getReviewers(productId);
        return "插入成功";
    }

//    @ApiOperation(value = "批量获取亚马逊的商品评论")
    @RequestMapping(value = "/getReviews", method = {RequestMethod.POST})
    public String getReviews(@RequestBody List<String> productIds) {
        productIds.parallelStream().forEach(
                productId -> amazonReviewersCompoment.getReviewers(productId)
        );
        return "插入成功";
    }

}
