package com.leyou.user.pojo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 4,max = 30,message = "用户名必须在4-30位之间")
    private String username;// 用户名

    @Length(min = 4,max = 30,message = "用户名必须在4-30位之间")
    @JsonIgnore//对象序列化为json字符串时，忽略读属性
    private String password;// 密码

    @Pattern(regexp = "[1-9]\\d{7,10}@qq\\.com",message = "注册必须是qq邮箱")
    private String mail;// 电话

    private Date created;// 创建时间

    @JsonIgnore
    private String salt;// 密码的盐值

    public User() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}