package com.leyou.cart.service;

import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.api.GoodsApi;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsApi goodsClient;

    static final String KEY_PREFIX = "user:cart:";

    static final Logger logger = LoggerFactory.getLogger(CartService.class);

    public void addCart(Cart cart) {
        // 获取登录用户
        UserInfo user = LoginInterceptor.getUserInfo();
        // Redis的key
        String key = KEY_PREFIX + user.getId();
        // 获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        // 查询是否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        Boolean boo = hashOps.hasKey(skuId.toString());
        if (boo) {
            // 存在，获取购物车数据
            String json = hashOps.get(skuId.toString()).toString();
            cart = JsonUtils.parse(json, Cart.class);
            // 修改购物车数量
            cart.setNum(cart.getNum() + num);
        } else {
            // 不存在，新增购物车数据
            cart.setUserId(user.getId());
            // 其它商品信息，需要查询商品服务
            Sku sku = this.goodsClient.querySkuBySkuId(skuId);
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
        }
        // 将购物车数据写入redis
        hashOps.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
    }

    public List<Cart> queryCarts() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //判断用户是否有购物车记录
        if (!redisTemplate.hasKey(KEY_PREFIX+userInfo.getId())){
            return null;
        }
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        List<Object> cartsJson = hashOperations.values();

        //如果购物车集合为空，直接返回null
        if (CollectionUtils.isEmpty(cartsJson)){
            return null;
        }

        return cartsJson.stream().map(cartJson->JsonUtils.parse(cartJson.toString(),Cart.class)).collect(Collectors.toList());
    }

    public void updateNum(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //判断用户是否有购物车记录
        if (!this.redisTemplate.hasKey(KEY_PREFIX+userInfo.getId())){
            return;
        }

        Integer num = cart.getNum();

        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        String cartJson = hashOperations.get(cart.getSkuId().toString()).toString();

        cart = JsonUtils.parse(cartJson,Cart.class);

        cart.setNum(num);
        hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));

    }
}
