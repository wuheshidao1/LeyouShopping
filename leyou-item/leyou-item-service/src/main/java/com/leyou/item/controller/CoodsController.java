package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.com.leyou.item.bo.SpuBo;
import com.leyou.item.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CoodsController {

    @Autowired
    private GoodService goodService;

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
    public ResponseEntity<PageResult<SpuBo>> querySpuBoByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    ){
        PageResult<SpuBo> pageResult = goodService.querySpuBoByPage(key,saleable,page,rows);
        if (CollectionUtils.isEmpty(pageResult.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 新增商品
     * http://api.leyou.com/api/item/goods
     * @param spuBo
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        goodService.saveGoods(spuBo);
        return ResponseEntity.ok().build();
    }


    /**
     * 通过spuid查询商品详情表，用于数据回显
     * http://api.leyou.com/api/item/spu/detail/196
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId")Long spuId){
        SpuDetail spuDetail = goodService.querySpuDetailBySpuId(spuId);
        if (spuDetail==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * http://api.leyou.com/api/item/sku/list?id=2
     * 通过spuid查询List<Sku>
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id")Long spuId){
        List<Sku> skus = goodService.querySkuBySpuId(spuId);
        if (CollectionUtils.isEmpty(skus)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }

    /**
     * http://api.leyou.com/api/item/goods
     * 更新商品信息
     * @param spuBo
     * @return
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){
        goodService.updateGoods(spuBo);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu = goodService.querySpuById(id);

        if (spu==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);
    }

    @GetMapping("/sku/{id}")
    public ResponseEntity<Sku> querySkuBySkuId(@PathVariable("id")Long skuId){
        Sku sku = goodService.querySkuBySkuId(skuId);

        if (sku==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(sku);
    }

}
