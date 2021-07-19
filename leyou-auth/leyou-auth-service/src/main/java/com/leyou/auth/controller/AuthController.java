package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.netflix.client.http.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/accredit")
    public ResponseEntity<Void> accredit(
            @RequestParam("username")String username,
            @RequestParam("password")String password,
            HttpServletRequest request,
            HttpServletResponse response){//request和reponse都是可以直接设置的，并直接使用的
        //将token设置为cookie
        String token = authService.accredit(username,password);

        if (StringUtils.isBlank(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();//401
        }

        //通过common模块里面的CookieUtils来设置cookie
        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,this.jwtProperties.getExpire()*60);
        return ResponseEntity.ok(null);
    }

    /**
     * 通过前台的cookie信息，来通过jwtProperties的公钥进行解密。获得userInfo，在用其封装页面登录信息
     * @param token
     * @return
     */
    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verify(
            @CookieValue("LY_TOKEN")String token,
            HttpServletRequest request,
            HttpServletResponse response){
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
            if (userInfo==null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            //新生成一个token
            JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
            //重新设置一个cookie
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);


            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
