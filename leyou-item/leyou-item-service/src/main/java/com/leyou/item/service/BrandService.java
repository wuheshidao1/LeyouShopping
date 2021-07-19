package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据条件查询品牌
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化查询条件对象  通用mapper的查询方法
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //根据name模糊查询，或者按首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }
        //github的分页插件
        PageHelper.startPage(page, rows);

        //添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        List<Brand> brands = brandMapper.selectByExample(example);
        //包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        //包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());

    }

    /**
     * 添加商品brand，并更新中间表
     *
     * @param brand
     * @param cids
     */
    public void saveBrand(Brand brand, List<Long> cids) {
        // 先新增brand
        boolean flag = brandMapper.insertSelective(brand) == 1;

        // 再新增中间表
        if (flag) {
            cids.forEach(cid -> {
                this.brandMapper.insertCategoryAndBrand(cid, brand.getId());
            });
        }
    }

    /**
     * 更新品牌
     * @param brand
     */
    public void updateBrand(Brand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    /**
     * 删除品牌
     * @param bid
     */
    public void delBrand(Long bid) {
        brandMapper.deleteByPrimaryKey(bid);
    }

    /**
     * 通过分类id来查询品牌。多对多
     * @param cid
     * @return
     */
    public List<Brand> queryBrandByCid(Long cid) {
        return brandMapper.queryBrandByCid(cid);
    }

    /**
     * 通过商品品牌id，查询商品品牌
     * @param id
     * @return
     */
    public Brand queryBrandById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
