package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点的id查询子节点
     * @param pid
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam("pid") Long pid) {
        try {
            if (pid == null || pid < 0) {
                //参数不合法
                //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                return ResponseEntity.badRequest().build();//可以这样简写
            }
            List<Category> categories = categoryService.queryCategoriesByPid(pid);
            if (CollectionUtils.isEmpty(categories)) {
                //404 资源服务器未找到
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            //200 查询成功
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //500  服务器内部错误
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid){
        List<Category> categories = categoryService.queryByBrandId(bid);

        if (CollectionUtils.isEmpty(categories)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }

    /**
     * 通过ids来查询商品分类ids
     * @param ids
     * @return
     */
    @GetMapping
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids")List<Long> ids){
        List<String> names = categoryService.queryNamesByIds(ids);

        if (CollectionUtils.isEmpty(names)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }
}
