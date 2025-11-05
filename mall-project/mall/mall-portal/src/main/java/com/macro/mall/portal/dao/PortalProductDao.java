package com.macro.mall.portal.dao;

import com.macro.mall.model.SmsCoupon;
import com.macro.mall.portal.domain.CartProduct;
import com.macro.mall.portal.domain.PromotionProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 前台购物车商品管理自定义Dao
 * Created by macro on 2018/8/2.
 */
@Mapper
public interface PortalProductDao {
    /**
     * 获取购物车商品信息
     */
    @Select("SELECT p.*, pc.name categoryName, pb.name brandName " +
            "FROM pms_product p " +
            "LEFT JOIN pms_product_category pc ON p.product_category_id = pc.id " +
            "LEFT JOIN pms_brand pb ON p.brand_id = pb.id " +
            "WHERE p.id = #{id}")
    CartProduct getCartProduct(@Param("id") Long id);

    /**
     * 获取促销商品信息列表
     */
    @Select({
            "<script>",
            "SELECT p.id, p.name, p.promotion_type, p.gift_growth, p.gift_point,",
            "sku.id as sku_id, sku.price, sku.sku_code, sku.promotion_price, sku.stock, sku.lock_stock",
            "FROM pms_product p",
            "LEFT JOIN pms_sku_stock sku ON p.id = sku.product_id",
            "WHERE p.id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<PromotionProduct> getPromotionProductList(@Param("ids") List<Long> ids);

    /**
     * 获取可用优惠券列表
     */
    @Select({
            "<script>",
            "SELECT * FROM sms_coupon WHERE use_type = 0",
            "OR (use_type = 1 AND id IN (",
            "SELECT coupon_id FROM sms_coupon_product_category_relation WHERE product_category_id = #{productCategoryId}",
            "))",
            "OR (use_type = 2 AND id IN (",
            "SELECT coupon_id FROM sms_coupon_product_relation WHERE product_id = #{productId}",
            "))",
            "</script>"
    })
    List<SmsCoupon> getAvailableCouponList(@Param("productId") Long productId, @Param("productCategoryId") Long productCategoryId);
}
