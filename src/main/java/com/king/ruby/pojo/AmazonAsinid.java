package com.king.ruby.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
public class AmazonAsinid {
    @TableId
    private String asinId;
    private String area;
    private String used;
}
