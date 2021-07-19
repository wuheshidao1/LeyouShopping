package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;


    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 通过商品分类来查询产品规格组（一对多）
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);

        return specGroupMapper.select(specGroup);
    }

    /**
     * 通过规格组id查询参数列表
     * 如果param中有属性为null，则不会把属性作为查询条件，因此该方法具备通用性，即可根据gid查询，也可根据cid查询。
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setGeneric(generic);
        record.setSearching(searching);
        return this.specParamMapper.select(record);
    }

    /**
     * 添加规格参数
     * @param specParam
     */
    public Boolean saveSpecParam(SpecParam specParam) {
        return specParamMapper.insert(specParam)==1;
    }

    /**
     * 更新规格参数
     * @param specParam
     */
    public Boolean updateSpecParam(SpecParam specParam) {
        return specParamMapper.updateByPrimaryKeySelective(specParam)==1;
    }

    /**
     * 删除规格参数
     * @param pid
     */
    public Boolean delSpecParam(Long pid) {
        SpecParam specParam = new SpecParam();
        specParam.setId(pid);
        return specParamMapper.delete(specParam)==1;
    }

    public void saveSpecGroupByCid(SpecGroup specGroup) {
        specGroup.setId(null);
        specGroupMapper.insert(specGroup);
    }


    /**
     * 根据cid来查询规格参数组和规格参数
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsWithParam(Long cid) {
        List<SpecGroup> groups = queryGroupsByCid(cid);

        groups.forEach(group->{
            List<SpecParam> params = queryParams(group.getId(), null, null, null);
            group.setParams(params);
        });

        return groups;
    }
}
