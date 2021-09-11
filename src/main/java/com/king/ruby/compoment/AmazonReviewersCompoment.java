package com.king.ruby.compoment;

import com.king.ruby.pojo.AmazonAsinid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AmazonReviewersCompoment {

    public static final Map<String, String> AREAURL = new HashMap<String, String>();

    static {
        AREAURL.put("US", "https://www.amazon.com/product-reviews/");
        AREAURL.put("UK", "https://www.amazon.co.uk/product-reviews/");
        AREAURL.put("DE", "https://www.amazon.de/product-reviews/");
        AREAURL.put("FR", "https://www.amazon.fr/product-reviews/");
        AREAURL.put("IT", "https://www.amazon.it/product-reviews/");
        AREAURL.put("ES", "https://www.amazon.es/product-reviews/");
        AREAURL.put("CA", "https://www.amazon.ca/product-reviews/");
        AREAURL.put("JP", "https://www.amazon.co.jp/product-reviews/");
    }

    public static String ASINID = "B08B8DKYPS";

    public static String suffixURLA = "/reviewerType=all_reviews/ref=cm_cr_arp_d_paging_btm_next_";

    public static String suffixURLB = "?pageNumber=";

    @Autowired
    private HttpClientCompoment httpClientCompoment;

    public void getReviewers(String productId) {
        String URLS = "";
        ASINID = productId;
        URLS = AREAURL.get("US") + ASINID + suffixURLA + 1 + suffixURLB + 1;
        int pageSize = httpClientCompoment.getPageReviewerSize(URLS);
        for (int i = 1; i < pageSize; i++) {
            URLS = AREAURL.get("US") + ASINID + suffixURLA + i + suffixURLB + i;
            httpClientCompoment.getHtml(URLS, ASINID);
        }
    }

    public void graspReviewers(AmazonAsinid amazonAsinid) {
        String URLS = "";
        ASINID = amazonAsinid.getAsinId();
        String areaUrl = AREAURL.get(amazonAsinid.getArea());
        URLS = areaUrl + ASINID + suffixURLA + 1 + suffixURLB + 1;
        int pageSize = httpClientCompoment.getPageReviewerSize(URLS);
        for (int i = 1; i < pageSize; i++) {
            URLS = areaUrl + ASINID + suffixURLA + i + suffixURLB + i;
            httpClientCompoment.getHtml(URLS, ASINID);
        }
        System.out.println(amazonAsinid.getAsinId() + "抓取完成");

    }

}