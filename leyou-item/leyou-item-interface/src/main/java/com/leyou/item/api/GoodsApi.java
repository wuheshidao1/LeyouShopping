package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.com.leyou.item.bo.SpuBo;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    /**
     * 通过spuid查询商品详情表，用于数据回显
     * http://api.leyou.com/api/item/spu/detail/196
     * feign的原理便是通过url拼接，/item-service/spu/detail/{spuId} 通过item微服务调用
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuId")Long spuId);


    //http://api.leyou.com/api/item/spu/page?key=&saleable=true&page=1&rows=5

    /**
     * 搜索词，上下架过滤 分页查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/spu/page")
    public PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows);


    /**
     * http://api.leyou.com/api/item/sku/list?id=2
     * 通过spuid查询List<Sku>
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public List<Sku> querySkuBySpuId(@RequestParam("id")Long spuId);



    @GetMapping("/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

    /**
     * 通过skuid查询sku
     * @param skuId
     * @return
     */
    @GetMapping("/sku/{id}")
    public Sku querySkuBySkuId(@PathVariable("id")Long skuId);

}
