package com.leyou.user.service;


import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String key_prefix="user:verify:";

    public Boolean checkUser(String data, Integer type) {
        User user = new User();
        if (type == 1) {
            user.setUsername(data);
        } else {
            user.setMail(data);
        }

        return userMapper.selectCount(user) == 0;
    }


    public void sendVerifyCode(String mail) {
        if (StringUtils.isBlank(mail)){
            return;
        }

        //生成验证码
        String code = NumberUtils.generateCode(6);

        //发送消息到rabbitMQ
        Map<String,String> msg = new HashMap<>();
        msg.put("mail",mail);
        msg.put("code",code);
        amqpTemplate.convertAndSend("leyou.mail.exchange","verifycode.mail",msg);

        //把验证码保存到redis
        stringRedisTemplate.opsForValue().set(key_prefix+mail,code,5, TimeUnit.MINUTES);
    }


    /**
     * 注册
     * @param user
     * @param code
     */
    public void register(User user, String code) {
        //查询redis中的验证码
        String redisCode = stringRedisTemplate.opsForValue().get(key_prefix+user.getMail());

        //1.校验验证码
        if (!StringUtils.equals(code,redisCode)){
            return;
        }
        //2.生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //3.加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));

        //4.新增用户
        user.setId(null);
        user.setCreated(new Date());
        userMapper.insertSelective(user);
    }

    /**
     * 用户查询
     * @param username
     * @param password
     * @return
     */
    public User queryUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        //根据用户名查寻到盐
        User findUser = userMapper.selectOne(user);

        //判断user是否为空
        if (findUser==null){
            return null;
        }

        //再用盐和密码经行加密后，和数据库中的密码进行比较
        password = CodecUtils.md5Hex(password,findUser.getSalt());
        if (StringUtils.equals(password,findUser.getPassword())){
            return findUser;
        }
        return null;
    }
}
