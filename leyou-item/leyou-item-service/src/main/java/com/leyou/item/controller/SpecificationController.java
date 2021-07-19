package com.leyou.item.controller;


import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//http://api.leyou.com/api/item/spec/groups/3
@Controller
@RequestMapping("/spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 通过商品分类来查询产品规格组（一对多）
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid")Long cid){
        List<SpecGroup> groups = specificationService.queryGroupsByCid(cid);

        if (CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }


    ///item/spec/params?gid=
    /**
     * 通过规格组id查询参数列表
     * @param gid
     * @return
     */
/*    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> queryParams(@RequestParam("gid") Long gid){
        List<SpecParam> params = specificationService.queryParams(gid);

        if (CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }*/
    //
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
    public ResponseEntity<List<SpecParam>> queryParams(
        @RequestParam(value = "gid",required = false)Long gid,
        @RequestParam(value = "cid",required = false)Long cid,
        @RequestParam(value = "generic",required = false)Boolean generic,
        @RequestParam(value = "searching",required = false)Boolean searching
    ){
        List<SpecParam> params = specificationService.queryParams(gid,cid,generic,searching);
        if (CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    /**
     * 新增规格组
     * @param specGroup
     * @return
     */
    @PostMapping("/group")
    public ResponseEntity<Void> saveSpecGroupByCid(@RequestBody SpecGroup specGroup){
        specificationService.saveSpecGroupByCid(specGroup);
        return ResponseEntity.ok().build();
    }

    /**
     * 增加规格参数
     * @param specParam
     * @return
     */
    //{cid: 76, groupId: 1, segments: "0-10", numeric: true, searching: true, generic: false, unit: "dd"}
    @PostMapping("/param")
    public ResponseEntity<Void> saveSpecParam(@RequestBody SpecParam specParam){

        Boolean flag = specificationService.saveSpecParam(specParam);
        if (flag){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    /**
     * 更新规格参数
     * @param specParam
     * @return
     */
    //{id: 1, cid: 76, groupId: 1, name: "品牌", numeric: false, unit: "", generic: true, searching: false,…}
    @PutMapping("/param")
    public ResponseEntity<Void> updateSpecParam(@RequestBody SpecParam specParam){
        Boolean flag = specificationService.updateSpecParam(specParam);
        if (flag){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * 删除规格参数
     * @param pid
     * @return
     */
    @DeleteMapping("/param/{pid}")
    public ResponseEntity<Void> delSpecParam(@PathVariable("pid")Long pid){
        Boolean flag = specificationService.delSpecParam(pid);
        if (flag){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * 根据cid查询规格参数组,携带规格参数
     * @param cid
     * @return
     */
    @GetMapping("/group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid")Long cid){
        List<SpecGroup> groups = specificationService.queryGroupsWithParam(cid);

        if (CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(groups);
    }
}
