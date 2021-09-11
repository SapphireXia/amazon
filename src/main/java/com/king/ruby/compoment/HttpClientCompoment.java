package com.king.ruby.compoment;

import com.king.ruby.mapper.AmazonReviewMapper;
import com.king.ruby.pojo.AmazonReview;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transaction;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Transactional
public class HttpClientCompoment {
    // 网站的地址
    public static String OriginURL = "https://www.amazon.com/product-reviews/B08V54TGD8/reviewerType=all_reviews/ref=cm_cr_arp_d_paging_btm_next_3?pageNumber=3";

    public static String prefix = "cm_cr-review_list";

    public static String suffix = "a-form-actions a-spacing-top-extra-large";

    // 名称
    public static String REG_NAME = "(?<=a-profile-name\">).+?(?=</span>)";

    // 星级
    public static String REG_STAR = "(?<=a-link-normal\" title=\").+?(?=\")";

    // 日期
    public static String REG_DATA = "(?<=review-date\">).+?(?=</span>)";

    // 是否 认证购买
    public static String REG_VP = "(?<=a-color-state a-text-bold\">).+?(?=</span>)";

    // 内容
    public static String REG_CONTENT1 = "(?<=review-text-content\">)[\\s\\S]*?(?=</span>)";

    public static String REG_CONTENT = "review-text-content\">[\\s\\S]*?</span>";

    // 标题
    public static String REG_TITLE = "<a data-hook=\"review-title[\\s\\S]*?</span>";

    public static ArrayList<String> NAME = new ArrayList<String>();

    public static ArrayList<String> STAR = new ArrayList<String>();

    public static ArrayList<String> DATA = new ArrayList<String>();

    public static ArrayList<String> VP = new ArrayList<String>();

    public static ArrayList<String> CONTENT = new ArrayList<String>();

    public static ArrayList<String> TITLE = new ArrayList<String>();

    @Autowired
    private AmazonReviewMapper amazonReviewMapper;

    public int getPageReviewerSize(String URLS) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpGet request = new HttpGet(URLS);
        String html = "";
        int pageTotal = 0;
        try {
            response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = response.getEntity();
                html = EntityUtils.toString(httpEntity, "utf-8");
                int lp =  html.indexOf("global reviews");
                int fp = html.lastIndexOf("|",lp);
                String value = html.substring(fp+1,lp);
                value = value.replace(",","");
                value = value.replaceAll("\r","");
                value = value.replaceAll("\n","");
                value = value.replace(" ","");
                pageTotal = Integer.parseInt(value);
            } else {
                //如果返回状态不是200，比如404（页面不存在）等，根据情况做处理，这里略
                System.out.println("返回状态不是200");
                System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //6.关闭
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
            if (pageTotal != 0) {
                return pageTotal / 10 + 1;
            }
            return 0;
        }
    }

    public String getHtml(String URLS , String asinId) {
        NAME.clear();STAR.clear();DATA.clear();
        VP.clear();CONTENT.clear();TITLE.clear();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpGet request = new HttpGet(URLS);
        String html = "";
        try {
            response = httpClient.execute(request);
            //4.判断响应状态为200，进行处理
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //5.获取响应内容
                HttpEntity httpEntity = response.getEntity();
                html = EntityUtils.toString(httpEntity, "utf-8");
                int fp = html.indexOf(prefix);
                int lp = html.indexOf(suffix);
                if (fp != -1 && lp != -1) {
                    html = html.substring(fp,lp);
                    getNeedData(html,asinId);
                }else{
                    html = "Error";
                }
            } else {
                //如果返回状态不是200，比如404（页面不存在）等，根据情况做处理，这里略
                System.out.println("返回状态不是200");
                System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            html = "Error";
        } catch (IOException e) {
            e.printStackTrace();
            html = "Error";
        } finally {
            //6.关闭
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
            return html;
        }
    }

    public void getNeedData(String htmls,String asinId) {
        matcherData(htmls);
        insertDataBase(asinId);
    }

    public void matcherData(String htmls) {
        Matcher matcher1 = Pattern.compile(REG_NAME).matcher(htmls);
        Matcher matcher2 = Pattern.compile(REG_STAR).matcher(htmls);
        Matcher matcher3 = Pattern.compile(REG_DATA).matcher(htmls);
        Matcher matcher4 = Pattern.compile(REG_VP).matcher(htmls);
        Matcher matcher5 = Pattern.compile(REG_CONTENT).matcher(htmls);
        Matcher matcher6 = Pattern.compile(REG_TITLE).matcher(htmls);
        while (matcher1.find()) {
            NAME.add(matcher1.group());
        }
        while (matcher2.find()) {
            STAR.add(matcher2.group().substring(0,1));
        }
        while (matcher3.find()) {
            DATA.add(formatDate(matcher3.group()));
        }
        while (matcher4.find()) {
            if(matcher4.group().equalsIgnoreCase("Verified Purchase")){
                VP.add("Yes");
            } else{
                VP.add("No");
            }
        }
        while (matcher5.find()) {
            CONTENT.add(formatTitle(matcher5.group()));
        }
        while (matcher6.find()) {
            TITLE.add(formatTitle(matcher6.group()));
        }
    }

    public String formatDate(String date) {
        String finalDate = "";
        try {
            date = date.replace("Reviewed in the United States on ", "");
            date = date.replace(",", "");
            String[] dates = date.split(" ");
            String st = dates[1] + "/" + dates[0] + "/" + dates[2];
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
            finalDate = sdf1.format(sdf.parse(st));
        } catch (Exception e) {
            System.err.println("日期转换错误");
        } finally {
            return finalDate;
        }
    }

    public String formatContent(String content) {
        content = content.replace("review-text-content\">","");
        content = content.replace("<span>","");
        content = content.replace("</span>","");
        content = content.replaceAll("\r","");
        content = content.replaceAll("\n","");
        return content;
    }

    public String formatTitle(String title) {
        int fp = title.indexOf("<span>");
        int lp = title.indexOf("</span>");
        if (fp != -1 && lp != -1) {
            return title.substring(fp+6, lp);
        } else {
            return "标题获取失败&&内容失败";
        }
    }

    public void insertDataBase(String asinId) {
        for(int i = 0; i< NAME.size(); i ++) {
            AmazonReview amazonReview = new AmazonReview();
            amazonReview.setAsinId(asinId);
            amazonReview.setReviewDate(DATA.get(i));
            amazonReview.setReviewNamer(NAME.get(i));
            amazonReview.setReviewStar(Integer.parseInt(STAR.get(i)));
            amazonReview.setReviewVerifiedPurchase((VP.get(i)));
            amazonReview.setReviewTitle(TITLE.get(i));
            amazonReview.setReviewContent(CONTENT.get(i));
            amazonReviewMapper.insert(amazonReview);
        }
    }

}
