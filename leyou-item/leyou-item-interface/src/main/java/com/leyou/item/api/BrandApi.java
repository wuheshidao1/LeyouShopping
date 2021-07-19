package com.leyou.item.api;


import com.leyou.item.pojo.Brand;

import org.springframework.web.bind.annotation.*;

import java.util.List;

//brand/page?key=&page=1&rows=5&sortBy=id&desc=false
@RequestMapping("/brand")
public interface BrandApi {

    /**
     * 通过商品品牌id，查询商品品牌
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Brand queryBrandById(@PathVariable("id")Long id);

}
