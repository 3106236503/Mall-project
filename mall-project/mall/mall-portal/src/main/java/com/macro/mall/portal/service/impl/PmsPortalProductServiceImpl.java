package com.macro.mall.portal.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.PortalProductDao;
import com.macro.mall.portal.domain.PmsPortalProductDetail;
import com.macro.mall.portal.domain.PmsProductCategoryNode;
import com.macro.mall.portal.service.PmsPortalProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 前台订单管理Service实现类
 * Created by macro on 2020/4/6.
 */
@Service
public class PmsPortalProductServiceImpl implements PmsPortalProductService {
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductAttributeMapper productAttributeMapper;
    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsProductLadderMapper productLadderMapper;
    @Autowired
    private PmsProductFullReductionMapper productFullReductionMapper;
    @Autowired
    private PortalProductDao portalProductDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(PmsPortalProductServiceImpl.class);


    @Override
    public List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort) {
        PageHelper.startPage(pageNum, pageSize);
        PmsProductExample example = new PmsProductExample();
        PmsProductExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        criteria.andPublishStatusEqualTo(1);
        if (StrUtil.isNotEmpty(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        if (brandId != null) {
            criteria.andBrandIdEqualTo(brandId);
        }
        if (productCategoryId != null) {
            criteria.andProductCategoryIdEqualTo(productCategoryId);
        }
        //1->按新品；2->按销量；3->价格从低到高；4->价格从高到低
        if (sort == 1) {
            example.setOrderByClause("id desc");
        } else if (sort == 2) {
            example.setOrderByClause("sale desc");
        } else if (sort == 3) {
            example.setOrderByClause("price asc");
        } else if (sort == 4) {
            example.setOrderByClause("price desc");
        }
        return productMapper.selectByExample(example);
    }

    @Override
    public List<PmsProductCategoryNode> categoryTreeList() {
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        List<PmsProductCategory> allList = productCategoryMapper.selectByExample(example);
        List<PmsProductCategoryNode> result = allList.stream()
                .filter(item -> item.getParentId().equals(0L))
                .map(item -> covert(item, allList))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public PmsPortalProductDetail detail(Long id) {
        LOGGER.info("开始获取商品详情，商品ID: {}", id);

        PmsPortalProductDetail result = new PmsPortalProductDetail();

        // 获取商品信息
        LOGGER.debug("查询商品基本信息，ID: {}", id);
        PmsProduct product = productMapper.selectByPrimaryKey(id);
        if (product == null) {
            LOGGER.error("商品不存在，ID: {}", id);
            throw new RuntimeException("商品不存在，ID: " + id);
        }
        LOGGER.debug("商品信息查询成功，商品名称: {}, 品牌ID: {}, 分类ID: {}",
                product.getName(), product.getBrandId(), product.getProductCategoryId());
        result.setProduct(product);

        // 获取品牌信息（增加空值检查）
        if (product.getBrandId() != null) {
            LOGGER.debug("查询品牌信息，品牌ID: {}", product.getBrandId());
            PmsBrand brand = brandMapper.selectByPrimaryKey(product.getBrandId());
            if (brand != null) {
                LOGGER.debug("品牌信息查询成功，品牌名称: {}", brand.getName());
                result.setBrand(brand);
            } else {
                LOGGER.warn("品牌信息不存在，品牌ID: {}", product.getBrandId());
            }
        } else {
            LOGGER.debug("商品无品牌信息，跳过品牌查询");
        }

        // 获取商品属性信息（增加空值检查）
        if (product.getProductAttributeCategoryId() != null) {
            LOGGER.debug("查询商品属性信息，属性分类ID: {}", product.getProductAttributeCategoryId());
            PmsProductAttributeExample attributeExample = new PmsProductAttributeExample();
            attributeExample.createCriteria().andProductAttributeCategoryIdEqualTo(product.getProductAttributeCategoryId());
            List<PmsProductAttribute> productAttributeList = productAttributeMapper.selectByExample(attributeExample);

            if (CollUtil.isNotEmpty(productAttributeList)) {
                LOGGER.debug("查询到 {} 个商品属性", productAttributeList.size());
                result.setProductAttributeList(productAttributeList);

                // 获取商品属性值信息
                LOGGER.debug("查询商品属性值信息");
                List<Long> attributeIds = productAttributeList.stream().map(PmsProductAttribute::getId).collect(Collectors.toList());
                PmsProductAttributeValueExample attributeValueExample = new PmsProductAttributeValueExample();
                attributeValueExample.createCriteria().andProductIdEqualTo(product.getId())
                        .andProductAttributeIdIn(attributeIds);
                List<PmsProductAttributeValue> productAttributeValueList = productAttributeValueMapper.selectByExample(attributeValueExample);

                if (CollUtil.isNotEmpty(productAttributeValueList)) {
                    LOGGER.debug("查询到 {} 个商品属性值", productAttributeValueList.size());
                    result.setProductAttributeValueList(productAttributeValueList);
                } else {
                    LOGGER.warn("未查询到商品属性值信息");
                }
            } else {
                LOGGER.warn("未查询到商品属性信息，属性分类ID: {}", product.getProductAttributeCategoryId());
            }
        } else {
            LOGGER.debug("商品无属性分类信息，跳过属性查询");
        }

        // 获取商品SKU库存信息
        LOGGER.debug("查询商品SKU库存信息");
        PmsSkuStockExample skuExample = new PmsSkuStockExample();
        skuExample.createCriteria().andProductIdEqualTo(product.getId());
        List<PmsSkuStock> skuStockList = skuStockMapper.selectByExample(skuExample);

        if (CollUtil.isNotEmpty(skuStockList)) {
            LOGGER.debug("查询到 {} 个SKU库存信息", skuStockList.size());
            result.setSkuStockList(skuStockList);
        } else {
            LOGGER.warn("未查询到SKU库存信息");
        }

        // 商品阶梯价格设置（增加空值检查）
        if (product.getPromotionType() != null && product.getPromotionType() == 3) {
            LOGGER.debug("查询商品阶梯价格信息，促销类型: 阶梯价格");
            PmsProductLadderExample ladderExample = new PmsProductLadderExample();
            ladderExample.createCriteria().andProductIdEqualTo(product.getId());
            List<PmsProductLadder> productLadderList = productLadderMapper.selectByExample(ladderExample);

            if (CollUtil.isNotEmpty(productLadderList)) {
                LOGGER.debug("查询到 {} 个阶梯价格设置", productLadderList.size());
                result.setProductLadderList(productLadderList);
            } else {
                LOGGER.warn("未查询到阶梯价格设置");
            }
        } else {
            LOGGER.debug("商品促销类型不是阶梯价格，跳过阶梯价格查询");
        }

        // 商品满减价格设置（增加空值检查）
        if (product.getPromotionType() != null && product.getPromotionType() == 4) {
            LOGGER.debug("查询商品满减价格信息，促销类型: 满减价格");
            PmsProductFullReductionExample fullReductionExample = new PmsProductFullReductionExample();
            fullReductionExample.createCriteria().andProductIdEqualTo(product.getId());
            List<PmsProductFullReduction> productFullReductionList = productFullReductionMapper.selectByExample(fullReductionExample);

            if (CollUtil.isNotEmpty(productFullReductionList)) {
                LOGGER.debug("查询到 {} 个满减价格设置", productFullReductionList.size());
                result.setProductFullReductionList(productFullReductionList);
            } else {
                LOGGER.warn("未查询到满减价格设置");
            }
        } else {
            LOGGER.debug("商品促销类型不是满减价格，跳过满减价格查询");
        }

        // 商品可用优惠券（增加空值检查）
        if (product.getProductCategoryId() != null) {
            try {
                LOGGER.debug("查询商品可用优惠券信息，分类ID: {}", product.getProductCategoryId());
                List<SmsCoupon> couponList = portalProductDao.getAvailableCouponList(product.getId(), product.getProductCategoryId());

                if (CollUtil.isNotEmpty(couponList)) {
                    LOGGER.debug("查询到 {} 个可用优惠券", couponList.size());
                    result.setCouponList(couponList);
                } else {
                    LOGGER.debug("未查询到可用优惠券");
                    result.setCouponList(new ArrayList<>());
                }
            } catch (Exception e) {
                LOGGER.error("查询优惠券信息时发生异常", e);
                result.setCouponList(new ArrayList<>());
            }
        } else {
            LOGGER.debug("商品无分类信息，跳过优惠券查询");
            result.setCouponList(new ArrayList<>());
        }

        LOGGER.info("商品详情获取完成，商品ID: {}, 商品名称: {}", id, product.getName());
        return result;
    }


    /**
     * 初始对象转化为节点对象
     */
    private PmsProductCategoryNode covert(PmsProductCategory item, List<PmsProductCategory> allList) {
        PmsProductCategoryNode node = new PmsProductCategoryNode();
        BeanUtils.copyProperties(item, node);
        List<PmsProductCategoryNode> children = allList.stream()
                .filter(subItem -> subItem.getParentId().equals(item.getId()))
                .map(subItem -> covert(subItem, allList)).collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }
}
