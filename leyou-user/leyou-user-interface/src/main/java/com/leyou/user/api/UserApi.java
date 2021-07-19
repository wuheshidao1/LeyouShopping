package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {

    /**
     * 用户查询
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    public User queryUser(@RequestParam("username")String username, @RequestParam("password")String password);
}
