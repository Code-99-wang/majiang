package com.example.demo.Controller;

import com.example.demo.DTO.AccessTokenEntity;
import com.example.demo.DTO.GithubUsers;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mod.User;
import com.example.demo.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Value("${gitee.client.id}")
    private String clientId;

    @Value("${gitee.client.secret}")
    private String clientSecret;

    @Value("${gitee.redirect.uri}")
    private String redirectUri;

    @Autowired
    GithubProvider githubProvider;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           HttpServletResponse response) throws IOException {
        AccessTokenEntity accessTokenEntity = new AccessTokenEntity();
        accessTokenEntity.setCode(code);
        accessTokenEntity.setGrant_type("authorization_code");
        accessTokenEntity.setClient_secret(clientSecret);
        accessTokenEntity.setRedirect_uri(redirectUri);
        accessTokenEntity.setClient_id(clientId);
//        System.out.println(accessTokenEntity);
        String accessTokenEntity1 = githubProvider.getAccessTokenEntity(accessTokenEntity);
        GithubUsers githubUsers = githubProvider.getUsers(accessTokenEntity1);
        if(githubUsers != null){
            //登录成功，写cookie和session
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUsers.getName());
            user.setAccountId(String.valueOf(githubUsers.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            Cookie token1 = new Cookie("token", token);
            response.addCookie(token1);
            return "redirect:/";
        }else {
            //登录失败，重新登陆
            return "redirect:/";
        }
    }
}
