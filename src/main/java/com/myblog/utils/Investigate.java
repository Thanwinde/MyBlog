package com.myblog.utils;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nsh
 * @data 2025/5/1 19:15
 * @description
 **/
@Component
public class Investigate {
    @Value("${investigate.ack}")
    String access;
    @Value("${investigate.strategyId}")
    Integer strategyId;

    CloseableHttpClient httpclient = HttpClients.createDefault();

    public boolean isLegal(String text) throws IOException, URISyntaxException {
        URI uri = null;
            uri = new URIBuilder("https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined")
                    .addParameter("access_token",access).build();

        HttpPost httppost = new HttpPost(uri);
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> paramPairs = new ArrayList<>();
        paramPairs.add(new BasicNameValuePair("text", text));
        paramPairs.add(new BasicNameValuePair("strategyId", String.valueOf(strategyId)));

            httppost.setEntity(new UrlEncodedFormEntity(paramPairs,"UTF-8"));


            CloseableHttpResponse response = httpclient.execute(httppost);
            JSONObject jsonObject = JSONUtil.parseObj(response.getEntity().getContent());
            response.getEntity().getContent().close();
            int type = jsonObject.getInt("conclusionType");
            if(type == 3 || type == 2)
                return false;
            return true;

    }


}
