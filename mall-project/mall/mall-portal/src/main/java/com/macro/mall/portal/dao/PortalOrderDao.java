package com.macro.mall.portal.dao;

import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.portal.domain.OmsOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 前台订单管理自定义Dao
 * Created by macro on 2018/9/4.
 */
@Mapper
public interface PortalOrderDao {
    /**
     * 获取订单及下单商品详情
     */
    @Select("SELECT * FROM oms_order WHERE id = #{orderId}")
    OmsOrderDetail getDetail(@Param("orderId") Long orderId);

    /**
     * 修改 pms_sku_stock表的锁定库存及真实库存
     */
    @Update({
            "<script>",
            "<foreach collection='itemList' item='item' separator=';'>",
            "UPDATE pms_sku_stock",
            "SET stock = stock - #{item.productQuantity}, lock_stock = lock_stock - #{item.productQuantity}",
            "WHERE id = #{item.productSkuId}",
            "</foreach>",
            "</script>"
    })
    int updateSkuStock(@Param("itemList") List<OmsOrderItem> orderItemList);

    /**
     * 获取超时订单
     *
     * @param minute 超时时间（分）
     */
    @Select("SELECT * FROM oms_order WHERE create_time < DATE_SUB(NOW(), INTERVAL #{minute} MINUTE)")
    List<OmsOrderDetail> getTimeOutOrders(@Param("minute") Integer minute);

    /**
     * 批量修改订单状态
     */
    @Update({
            "<script>",
            "UPDATE oms_order SET status=#{status} WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int updateOrderStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 解除取消订单的库存锁定
     */
    @Update({
            "<script>",
            "<foreach collection='itemList' item='item' separator=';'>",
            "UPDATE pms_sku_stock",
            "SET lock_stock = lock_stock - #{item.productQuantity}",
            "WHERE id = #{item.productSkuId}",
            "</foreach>",
            "</script>"
    })
    int releaseSkuStockLock(@Param("itemList") List<OmsOrderItem> orderItemList);

    /**
     * 根据商品的skuId来锁定库存
     */
    @Update("UPDATE pms_sku_stock SET lock_stock = lock_stock + #{quantity} WHERE id = #{productSkuId} AND stock - lock_stock >= #{quantity}")
    int lockStockBySkuId(@Param("productSkuId") Long productSkuId, @Param("quantity") Integer quantity);

    /**
     * 根据商品的skuId扣减真实库存
     */
    @Update("UPDATE pms_sku_stock SET stock = stock - #{quantity}, lock_stock = lock_stock - #{quantity} WHERE id = #{productSkuId}")
    int reduceSkuStock(@Param("productSkuId") Long productSkuId, @Param("quantity") Integer quantity);

    /**
     * 根据商品的skuId释放库存
     */
    @Update("UPDATE pms_sku_stock SET lock_stock = lock_stock - #{quantity} WHERE id = #{productSkuId}")
    int releaseStockBySkuId(@Param("productSkuId") Long productSkuId, @Param("quantity") Integer quantity);
}
