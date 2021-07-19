package com.leyou.item.api;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/category")
public interface CategoryApi {
    /**
     * 通过ids来查询商品分类ids
     * @param ids
     * @return
     */
    @GetMapping
    public List<String> queryNamesByIds(@RequestParam("ids")List<Long> ids);
}
