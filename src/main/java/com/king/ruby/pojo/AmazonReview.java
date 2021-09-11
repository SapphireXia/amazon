package com.king.ruby.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.sql.Date;

@Data
@ApiOperation("亚马逊获取评论")
public class AmazonReview {
    @TableId
    private int reviewId;
    private String asinId;
    private String reviewNamer;
    private int reviewStar;
    private String reviewDate;
    private String reviewVerifiedPurchase;
    private String reviewContent;
    private String reviewTitle;
}
