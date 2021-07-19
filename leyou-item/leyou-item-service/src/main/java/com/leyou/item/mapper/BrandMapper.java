package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {
    @Insert("insert into tb_category_brand(category_id,brand_id) values(#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid,@Param("bid") Long id);

    //@Select("select * from tb_brand where id in (select brand_id from tb_category_brand where category_id = #{cid})")
    //通过brand和tb_category_brand 相关联 查出b.id=cb.brand_id的tb_brand值，INNER JOIN 两边都不为空。在和tb_category_brand中的cid条件匹配
    @Select("SELECT b.* from tb_brand b INNER JOIN tb_category_brand cb on b.id=cb.brand_id where cb.category_id=#{cid}")
    List<Brand> queryBrandByCid(Long cid);
}
