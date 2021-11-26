package com.example.demo.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.DTO.AccessTokenEntity;
import com.example.demo.DTO.GithubUsers;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;


import static jdk.nashorn.internal.objects.NativeMath.log;

@Component
public class GithubProvider {

    public String getAccessTokenEntity(AccessTokenEntity accessTokenEntity){

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

//        https://gitee.com/oauth/token?grant_type=authorization_code&code={code}&client_id={client_id}&redirect_uri={redirect_uri}&client_secret={client_secret}
        RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accessTokenEntity));
//        System.out.println(body);
        Request request = new Request.Builder()
                .url("https://gitee.com/oauth/token")
                .post(body)
                .build();
//        System.out.println(request+"..................");
        try(Response response = client.newCall(request).execute()) {
            String string = response.body().string();
//            System.out.println(string);
            JSONObject object = JSON.parseObject(string);
            System.out.println(object);
            return object.getString("access_token");
        } catch (IOException e) {
            log("getAccessToken error,{}", accessTokenEntity);
        }
        return null;
    }


    public GithubUsers getUsers(String accessToken) throws IOException {
        OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://gitee.com/api/v5/user?access_token="+accessToken)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String string = response.body().string();
                GithubUsers githubUsers = JSON.parseObject(string, GithubUsers.class);

                return githubUsers;
        }
    }
}
