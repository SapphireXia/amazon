package com.king.ruby.compoment;

import com.king.ruby.mapper.AmazonAsinidMapper;
import com.king.ruby.pojo.AmazonAsinid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.lang.StringUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Transactional
public class Common {

    @Autowired
    AmazonAsinidMapper amazonAsinidMapper;

    public List<AmazonAsinid> queryAllAsinId() {
        List<AmazonAsinid> amazonAsinids = amazonAsinidMapper.selectList(null);
        List<AmazonAsinid> collect = amazonAsinids.stream().filter(
                amazonAsinid -> !"1".equalsIgnoreCase(amazonAsinid.getUsed())
        ).collect(Collectors.toList());
        return collect;
    }
}
