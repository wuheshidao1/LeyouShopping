package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父节点查询子节点
     * @param pid
     * @return
     */
    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        return this.categoryMapper.select(record);
    }

    /**
     * 通过品牌id查询分类id
     * @param bid
     * @return
     */
    public List<Category> queryByBrandId(Long bid) {
        return categoryMapper.queryByBrandId(bid);
    }

    /**
     * 通过spu中的三层id，来查询分类名称集合
     * @param ids
     * @return
     */
    public List<String> queryNamesByIds(List<Long> ids){
        List<Category> categories = categoryMapper.selectByIdList(ids);

        //通过流 来获取一个集合里面的对象，并将该集合里面的对象封装到新的集合里面，返回
        return categories.stream()
                .map(category -> category.getName())
                .collect(Collectors.toList());
    }
}
