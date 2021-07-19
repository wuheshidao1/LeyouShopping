package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//brand/page?key=&page=1&rows=5&sortBy=id&desc=false
@Controller
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 根据关键字key，当前页page，每页显示的条数rows，根据什么排序，desc排序的规则
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
            @RequestParam(value = "key", required = false)String key,
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy", required = false)String sortBy,
            @RequestParam(value = "desc", required = false)Boolean desc
    ){
        PageResult<Brand> result = this.brandService.queryBrandsByPage(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids){
        this.brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 更新品牌
     * @param brand
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand){
        this.brandService.updateBrand(brand);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 删除品牌
     * @param bid
     * @return
     */
    @GetMapping("/del/{bid}")
    public ResponseEntity<Void> delBrand(@PathVariable("bid")Long bid){
        this.brandService.delBrand(bid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    //params?cid=76
    /**
     * 通过分类id查询品牌
     * @param cid
     * @return
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid")Long cid){

        List<Brand> brands = brandService.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(brands)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(brands);
    }


    /**
     * 通过商品品牌id，查询商品品牌
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id")Long id){
        Brand brand = brandService.queryBrandById(id);
        if (brand==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }

}
