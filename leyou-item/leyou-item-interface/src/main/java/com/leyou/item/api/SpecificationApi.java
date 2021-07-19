package com.leyou.item.api;


import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

//http://api.leyou.com/api/item/spec/groups/3
@RequestMapping("/spec")
public interface SpecificationApi {


    //我们在原来的根据 gid（规格组id)查询规格参数的接口上，添加一个参数：cid，即商品分类id。
    //
    //等一下， 考虑到以后可能还会根据是否搜索、是否为通用属性等条件过滤，我们多添加几个过滤条件

    /**http://api.leyou.com/api/item/spec/params?cid=76
     * 对原来的规格参数查询做了强化
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    @GetMapping("/params")
    public List<SpecParam> queryParams(
        @RequestParam(value = "gid",required = false)Long gid,
        @RequestParam(value = "cid",required = false)Long cid,
        @RequestParam(value = "generic",required = false)Boolean generic,
        @RequestParam(value = "searching",required = false)Boolean searching
    );


    @GetMapping("/group/param/{cid}")
    public List<SpecGroup> queryGroupsWithParam(@PathVariable("cid")Long cid);

}
