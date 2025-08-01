package com.myblog;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MyBlogApplicationTests {

    @Value("${investigate.ack}")
    String access;
    @Value("${investigate.strategyId}")
    Integer strategyId;

    CloseableHttpClient httpclient = HttpClients.createDefault();

    @Test
    public void isLegal() throws UnsupportedEncodingException, URISyntaxException {

        String text = "操死你";
        URI uri = new URIBuilder("https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined")
                .addParameter("access_token",access).build();
        HttpPost httppost = new HttpPost(uri);
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> paramPairs = new ArrayList<>();
        paramPairs.add(new BasicNameValuePair("text", text));
        paramPairs.add(new BasicNameValuePair("strategyId", String.valueOf(strategyId)));
        httppost.setEntity(new UrlEncodedFormEntity(paramPairs,"UTF-8"));
        try {
            CloseableHttpResponse response = httpclient.execute(httppost);
            JSONObject jsonObject = JSONUtil.parseObj(response.getEntity().getContent());
            httpclient.close();
            response.close();
            int type = jsonObject.getInt("conclusionType");
            System.out.println(type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
