package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.item.pojo.com.leyou.item.bo.SpuBo;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodService {
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;


    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //添加key过滤条件
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //添加saleable过滤条件
        if (saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }

        //先添加分页在进行查询
        PageHelper.startPage(page,rows);

        //先查询spu集合
        List<Spu> spus = spuMapper.selectByExample(example);

        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        //将Spu集合变为SpuBo集合,用stream函数
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();

            //拷贝工具，将spu全部拷贝到spuBo
            BeanUtils.copyProperties(spu, spuBo);

            //查询品牌名称(想查谁便从哪个mapper里面查询)
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());

            //查询分类名称
            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "/"));



            return spuBo;
        }).collect(Collectors.toList());

        //返回pageResult<spuBo>

        return new PageResult<SpuBo>(pageInfo.getTotal(),spuBos);
    }

    /**
     * 新增商品
     * 加入事务，防止产生垃圾数据
     * @param spuBo
     */
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        //先新增spu，得到spuid在新增其他的spuDetail，sku，stock等等

        //新增spu
        //得给spu的setSaleable,setCreateTime,setLastUpdateTime等等，设置默认字段
        spuBo.setId(null);//防止别人恶意注入，所以设置为null，数据库中会自增
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuMapper.insertSelective(spuBo);
        saveSkuAndStock(spuBo);

        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuDetail);

        sendMsg("insert",spuBo.getId());
    }

    private void sendMsg(String type,Long id) {
        try {
            amqpTemplate.convertAndSend("item."+type,id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增sku和stock
     * @param spuBo
     */
    private void saveSkuAndStock(SpuBo spuBo) {
        //因为skus和库存都是集合，所以要for循环新增
        spuBo.getSkus().forEach(sku -> {
            //新增sku
            //设置默认值
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);

            //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }


    /**
     * 通过spuid查询商品详情信息
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 通过spuid查询list<sku>
     * @param spuId
     * @return
     */
    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        skus.forEach(s -> {
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 更新商品
     * @param spuBo
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {

        //根据spuid 查询要删除的sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        List<Sku> skus = skuMapper.select(sku);
        skus.forEach(s -> {
            //删除stock
            stockMapper.deleteByPrimaryKey(s.getId());
            //删除sku
            skuMapper.delete(s);
        });

        saveSkuAndStock(spuBo);//提取的方法，保存sku和stock
        //更新spu和spuDetail
        spuBo.setCreateTime(null);//不能让别人随意更新
        spuBo.setLastUpdateTime(new Date());
        spuBo.setValid(null);
        spuBo.setSaleable(null);//不能让别人随意更新
        spuMapper.updateByPrimaryKeySelective(spuBo);
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
        sendMsg("update",spuBo.getId());
    }

    /**
     * 通过id查询spu
     * @param id
     * @return
     */
    public Spu querySpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据skuid查询sku
     * @param skuId
     * @return
     */
    public Sku querySkuBySkuId(Long skuId) {
        return skuMapper.selectByPrimaryKey(skuId);
    }
}
